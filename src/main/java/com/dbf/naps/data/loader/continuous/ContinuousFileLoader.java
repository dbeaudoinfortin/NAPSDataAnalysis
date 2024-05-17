package com.dbf.naps.data.loader.continuous;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.Compound;
import com.dbf.naps.data.loader.LoadOptions;

public class ContinuousFileLoader implements Runnable {
	
	private static final Logger log = LoggerFactory.getLogger(ContinuousFileLoader.class);
	
	private static final CSVFormat csvFormat;
	
	//Holds a mapping of NAPSID to SiteID, shared across threads
	private static final Map<Integer, Integer> siteIDLookup = new ConcurrentHashMap<Integer, Integer>(300);
	
	//Holds a mapping of Compound to PollutantID, shared across threads
	private static final Map<String, Integer> pollutantIDLookup = new ConcurrentHashMap<String, Integer>(20);
	
	private static final Long ONE_HOUR_MS = 60*60*1000L;
	
	static {
		csvFormat = CSVFormat.Builder.create()
				.setTrim(true)
				.setSkipHeaderRecord(true)
				.setIgnoreEmptyLines(true)
				.build();
	}
	
	//Note: SimpleDateFormat is not thread safe, must not be static
	private final SimpleDateFormat EARLY_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private final SimpleDateFormat LATE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
		
	private final int threadId;
	private final LoadOptions config;
	private final File rawFile;
	private final SqlSessionFactory sqlSessionFactory;
	
	public ContinuousFileLoader(int threadId, LoadOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		this.threadId = threadId;
		this.config = config;
		this.rawFile = rawFile;
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	//Deprecated Date functions since Java 1.1, probably safe to ignore :)
	@SuppressWarnings("deprecation") 
	@Override
	public void run() {
		log.info(threadId + ":: Starting to load file " + rawFile + " into the database.");
		
		try {
			log.info(threadId + ":: Starting CSV parsing for file " + rawFile + ".");
			List<ContinuousDataRecord> records = new ArrayList<ContinuousDataRecord>(100);
			
			//Load all the rows into memory. Let's assume we don't run out of memory. :) 
			try (Reader reader = new FileReader(rawFile, StandardCharsets.ISO_8859_1); CSVParser parser = csvFormat.parse(reader)) {
				for(CSVRecord line : parser) {
					if(line.size() < 31) continue; //Header line
					
					if(line.get(0).toLowerCase().startsWith("poll")) continue; //This is the real header row
					
					//More sanity checks, the line need to start with a known pollutant
					String compoudString = line.get(0).replace(".", ""); //PM2.5 -> PM25
					if(!Compound.contains(compoudString)) continue;
					
					boolean isPM25 = compoudString.equalsIgnoreCase(Compound.PM25.name());
					if(!((isPM25 && line.size() == 32) || (!isPM25 &&  line.size() == 31))) {
						throw new IllegalArgumentException("Wrong number of columns (" + line.size() + ") on row " + line.getRecordNumber() + ". Expected " + (isPM25? "32.":"31."));
					}
					
					int columnOffset = isPM25 ? 1:0;
					if(isPM25) compoudString += "_" + line.get(1); //Append the method to the compound
					
					//We create 24 records per CSV line, 1 per hour
					for(int hour = 0; hour < 24; hour++) {
						ContinuousDataRecord record = new ContinuousDataRecord();
						record.setPollutantId(getPollutantID(compoudString, line.getRecordNumber()));
						record.setSiteId(getSiteID(line, columnOffset));
						
						String date = line.get(6 + columnOffset);
						if (date.contains("-")) { //Date might be in more than 1 format
				        	try {
								record.setDatetime(LATE_DATE_FORMAT.parse(date));
					        } catch (ParseException | NumberFormatException e) {
					        	throw new IllegalArgumentException("Could not parse date (" + date + ") on row " + line.getRecordNumber() + ". Expecting format " + LATE_DATE_FORMAT, e);
					        }
						} else {
							try {
								record.setDatetime(EARLY_DATE_FORMAT.parse(date));
					        } catch (ParseException | NumberFormatException e) {
						        throw new IllegalArgumentException("Could not parse date (" + date + ") on row " + line.getRecordNumber() + ". Expecting format " + EARLY_DATE_FORMAT, e);
					        }
						}
						
				        record.setDay(record.getDatetime().getDate());
						record.setDayOfWeek(record.getDatetime().getDay());
						record.setHour(hour + 1);
						record.setMonth(record.getDatetime().getMonth()+1);
						record.setYear(record.getDatetime().getYear() + 1900); //This is really lazy, oh well
	
						//Add the hour component
						record.getDatetime().setTime(record.getDatetime().getTime() + (hour * ONE_HOUR_MS));
						
						String data = line.get(7 + hour + columnOffset);
						//There are a couple odd ball values in the data set, such -9999 and -99
						if(!config.isIncludeNulls() && data.startsWith("-99")) continue;
						try {
							record.setData(new BigDecimal(data));
						} catch (NumberFormatException e){
							throw new IllegalArgumentException("Invalid raw data (" + data + ") for hour " + (hour+1) + " on row " + line.getRecordNumber(), e);
						}
	
						records.add(record);
						
						//Save everything to the database
						//For faster performance partition the list of records into
						if(records.size() == 100) loadRecords(records);
					}
				}
			}
			
			//Might have some records left over
			loadRecords(records);

		 } catch (Throwable t) {
			 log.error(threadId + ":: ERROR loading file " + rawFile + " into the database.", t);
			return; //Don't throw a runtime exception, let the other threads run
		 }
		log.info(threadId + ":: Done loading file " + rawFile + " into the database.");
	}
	
	private void loadRecords(List<ContinuousDataRecord> records) {
		if(records.size() > 0) {
			log.info(threadId + ":: Loading " + records.size() + " records into the database for file " + rawFile + ".");
			try(SqlSession session = sqlSessionFactory.openSession(true)) {
				session.getMapper(ContinuousDataMapper.class).insertContinuousDataBulk(records);
			} finally {
				records.clear();
			}
		}
	}
	
	private Integer getSiteID(CSVRecord line, int columnOffset) {
		try {
			final Integer NAPSID = Integer.parseInt(line.get(1 + columnOffset));
			//If one thread stamps overrides the data of another it's no big deal
			return siteIDLookup.computeIfAbsent(NAPSID, key -> {
				Integer siteID = null;
				
				//May or may not insert, let the DB manage contention
				try(SqlSession session = sqlSessionFactory.openSession(true)) {
					BigDecimal latitude, longitude;
					try {
						latitude = new BigDecimal(line.get(4 + columnOffset));
					} catch (NumberFormatException e){
						throw new IllegalArgumentException("Invalid latitude (" + line.get(4 + columnOffset) + ") on row " + line.getRecordNumber(), e);
					}
					try {
						longitude = new BigDecimal(line.get(5 + columnOffset));
					} catch (NumberFormatException e){
						throw new IllegalArgumentException("Invalid longitude (" + line.get(5 + columnOffset) + ") on row " + line.getRecordNumber(), e);
					}
					
					if (longitude.longValue() < -188L) {
						//Some of the data is bad
						String s = line.get(5 + columnOffset).substring(1);
						if(s.startsWith("1")) {
							//Is over 100
							s = "-" + s.substring(0,3) + "." + s.substring(3);
						} else {
							s = "-" + s.substring(0,2) + "." + s.substring(2);
						}
						try {
							longitude = new BigDecimal(s);
						} catch (NumberFormatException e) {
							throw new IllegalArgumentException("Invalid longitude (" + line.get(5 + columnOffset) + ") on row " + line.getRecordNumber(), e);
						}
					}
					
					ContinuousDataMapper mapper = session.getMapper(ContinuousDataMapper.class);
					session.getMapper(ContinuousDataMapper.class).insertSite(NAPSID, line.get(2 + columnOffset), line.get(3 + columnOffset).toUpperCase(), latitude, longitude);
					siteID = mapper.getSiteID(NAPSID);
				}
				
				if( null == siteID) {
					throw new IllegalArgumentException("Could not find matching Site ID for NAPS ID (" + line.get(1 + columnOffset) + ") on row " + line.getRecordNumber());
				}
				return siteID;
			});
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("Invalid NAPS ID (" + line.get(1 + columnOffset) + ") on row " + line.getRecordNumber(), e);
		}
	}
	
	private Integer getPollutantID(String compound, long recordNumber) {
		//If one thread stamps overrides the data of another it's no big deal
		return pollutantIDLookup.computeIfAbsent(compound, key -> {
			Integer pollutantID = null;
			//May or may not insert, let the DB manage contention
			try(SqlSession session = sqlSessionFactory.openSession(true)) {
				ContinuousDataMapper mapper = session.getMapper(ContinuousDataMapper.class);
				mapper.insertPollutant(compound);
				pollutantID = mapper.getPollutantID(compound);
			}
			if(null == pollutantID) {
				throw new IllegalArgumentException("Could not find matching Pollutant ID for compound (" + compound + ") on row " + recordNumber);
			}
			return pollutantID;
		});
	}
}
