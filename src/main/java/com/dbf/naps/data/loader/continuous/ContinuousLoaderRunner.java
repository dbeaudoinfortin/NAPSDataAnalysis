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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.mappers.ContinuousDataMapper;
import com.dbf.naps.data.globals.continuous.Compound;
import com.dbf.naps.data.loader.FileLoaderRunner;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.records.ContinuousDataRecord;

public class ContinuousLoaderRunner extends FileLoaderRunner {
	
	private static final Logger log = LoggerFactory.getLogger(ContinuousLoaderRunner.class);
	
	private static final CSVFormat csvFormat;
	
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
		
	public ContinuousLoaderRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}
	
	@Override
	public void processFile() throws Exception {
		log.info(getThreadId() + ":: Starting CSV parsing for file " + getRawFile() + ".");
		List<ContinuousDataRecord> records = new ArrayList<ContinuousDataRecord>(100);

		//Load all the rows into memory. Let's assume we don't run out of memory. :) 
		try (Reader reader = new FileReader(getRawFile(), StandardCharsets.ISO_8859_1); CSVParser parser = csvFormat.parse(reader)) {
			for(CSVRecord line : parser) {
				if(line.size() < 31 || line.get(0).toLowerCase().startsWith("poll")) {
					//These are header lines
					log.info(getThreadId() + ":: Skipping non-data row " + line.getRecordNumber() + " in file " + getRawFile() + ".");
					continue;
				}
			
				//More sanity checks, the line needs to start with a known pollutant
				String compoudString = line.get(0).replace(".", "").toUpperCase(); //PM2.5 -> PM25
				if(!Compound.contains(compoudString)) continue;
				
				boolean isPM25 = compoudString.equals(Compound.PM25.name());
				if(!((isPM25 && line.size() == 32) || (!isPM25 &&  line.size() == 31))) {
					throw new IllegalArgumentException("Wrong number of columns (" + line.size() + ") on row " + line.getRecordNumber() + ". Expected " + (isPM25? "32.":"31."));
				}
				
				int columnOffset = isPM25 ? 1:0;
				String method = null;
				if(isPM25) {
					//Only PM25 has a specific method
					method = line.get(1); 
				}
				
				//The units are expected to be consistent for each compound type
				String units = null;
				if (compoudString.equals(Compound.CO.name())) {
					units = "ppm";
				} else if (compoudString.equals(Compound.SO2.name())
						|| compoudString.equals(Compound.O3.name())
						|| compoudString.equals(Compound.NOX.name())
				        || compoudString.equals(Compound.NO2.name())
				        || compoudString.equals(Compound.NO.name())) {
					units = "ppb";
				} else if(isPM25 || compoudString.equals(Compound.PM10.name())) {
					units = "µg/m³";
				}
				
				//We create 24 records per CSV line, 1 per hour
				for(int hour = 0; hour < 24; hour++) {
					ContinuousDataRecord record = new ContinuousDataRecord();
					record.setPollutantId(getPollutantID(compoudString));
					record.setMethodId(getMethodID("Continuous", compoudString, method, units));
					record.setSiteId(getSiteID(
							line.get(1 + columnOffset),
							line.get(2 + columnOffset),
							line.get(3 + columnOffset),
							line.get(4 + columnOffset),
							line.get(5 + columnOffset),
							line.getRecordNumber()));
					
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

					//Add the hour component
					record.setHour(hour + 1);
					record.getDatetime().setTime(record.getDatetime().getTime() + (hour * ONE_HOUR_MS));
					
					String data = line.get(7 + hour + columnOffset);
					//There are a couple odd ball values in the data set, such -9999 and -99
					if(!getConfig().isIncludeNulls() && data.startsWith("-99")) continue;
					try {
						record.setData(new BigDecimal(data));
					} catch (NumberFormatException e){
						throw new IllegalArgumentException("Invalid raw data (" + data + ") for hour " + (hour+1) + " on row " + line.getRecordNumber(), e);
					}

					records.add(record);
					
					//Save everything to the database
					//For faster performance, do it in bulk
					if(records.size() == 200) loadRecords(records);
				}
			}
		}
		
		//Might have some records left over
		loadRecords(records);
	}
	
	private void loadRecords(List<ContinuousDataRecord> records) {
		if(records.size() > 0) {
			log.info(getThreadId() + ":: Loading " + records.size() + " records into the database for file " + getRawFile() + ".");
			try(SqlSession session = getSqlSessionFactory().openSession(true)) {
				session.getMapper(ContinuousDataMapper.class).insertContinuousDataBulk(records);
			} finally {
				records.clear();
			}
		}
	}
	
}
