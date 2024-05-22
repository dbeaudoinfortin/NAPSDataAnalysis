package com.dbf.naps.data.loader;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileLoadRunner implements Runnable {
	
	private static final Logger log = LoggerFactory.getLogger(FileLoadRunner.class);
	
	//Holds a mapping of NAPSID to SiteID, shared across threads
	private static final Map<Integer, Integer> siteIDLookup = new ConcurrentHashMap<Integer, Integer>(300);
	
	//Holds a mapping of Compound to PollutantID, shared across threads
	private static final Map<String, Integer> pollutantIDLookup = new ConcurrentHashMap<String, Integer>(20);
	
	private final int threadId;
	private final LoaderOptions config;
	private final File rawFile;
	private final SqlSessionFactory sqlSessionFactory;
	
	public FileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		this.threadId = threadId;
		this.config = config;
		this.rawFile = rawFile;
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	@Override
	public void run() {
		log.info(getThreadId() + ":: Starting to load file " + getRawFile() + " into the database.");
		
		try {
			processFile();
		 } catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR loading file " + getRawFile() + " into the database.", t);
			return; //Don't throw a runtime exception, let the other threads run
		 }
		log.info(getThreadId() + ":: Done loading file " + getRawFile() + " into the database.");
	}
	
	protected abstract void processFile() throws Exception;
	
	protected Integer getSiteID(String napsID, String cityName, String provTerr, String latitudeRaw, String longitudeRaw, long recordNumber) {
		try {
			final Integer NAPSID = Integer.parseInt(napsID);
			//If one thread stamps overrides the data of another it's no big deal
			return siteIDLookup.computeIfAbsent(NAPSID, key -> {
				Integer siteID = null;
				
				//May or may not insert, let the DB manage contention
				try(SqlSession session = sqlSessionFactory.openSession(true)) {
					BigDecimal latitude, longitude;
					try {
						latitude = new BigDecimal(latitudeRaw);
					} catch (NumberFormatException e){
						throw new IllegalArgumentException("Invalid latitude (" + latitudeRaw + ") on row " + recordNumber, e);
					}
					try {
						longitude = new BigDecimal(longitudeRaw);
					} catch (NumberFormatException e){
						throw new IllegalArgumentException("Invalid longitude (" + longitudeRaw + ") on row " + recordNumber, e);
					}
					
					if (longitude.longValue() < -188L) {
						//Some of the longitude data is bad
						String longitudeCorrected = longitudeRaw.substring(1);
						if(longitudeCorrected.startsWith("1")) {
							//Is it over 100?
							longitudeCorrected = "-" + longitudeCorrected.substring(0,3) + "." + longitudeCorrected.substring(3);
						} else {
							longitudeCorrected = "-" + longitudeCorrected.substring(0,2) + "." + longitudeCorrected.substring(2);
						}
						try {
							longitude = new BigDecimal(longitudeCorrected);
						} catch (NumberFormatException e) {
							throw new IllegalArgumentException("Invalid longitude (" + longitudeRaw + ") on row " + recordNumber, e);
						}
					}
					
					DataMapper mapper = session.getMapper(DataMapper.class);
					mapper.insertSite(NAPSID, cityName, provTerr.toUpperCase(), latitude, longitude);
					siteID = mapper.getSiteID(NAPSID);
				}
				
				if( null == siteID) {
					throw new IllegalArgumentException("Could not find matching Site ID for NAPS ID (" + napsID + ") on row " + recordNumber);
				}
				return siteID;
			});
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("Invalid NAPS ID (" + napsID + ") on row " + recordNumber, e);
		}
	}
	
	protected Integer getSiteID(String napsID, long recordNumber) {
		try {
			Integer NAPSID;
			if(napsID.contains(".")) {
				//Try as a double
				NAPSID = (int) Double.parseDouble(napsID);
			} else {
				NAPSID = Integer.parseInt(napsID);
			}

			//If one thread stamps overrides the data of another it's no big deal
			return siteIDLookup.computeIfAbsent(NAPSID, key -> {
				Integer siteID = null;
				
				try(SqlSession session = sqlSessionFactory.openSession(true)) {
					siteID = session.getMapper(DataMapper.class).getSiteID(NAPSID);
				}
				
				if(null == siteID) {
					throw new IllegalArgumentException("Could not find matching Site ID for NAPS ID (" + napsID + ") on row " + recordNumber);
				}
				return siteID;
			});
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("Invalid NAPS ID (" + napsID + ") on row " + recordNumber, e);
		}
	}
	
	protected Integer getPollutantID(String compound, long recordNumber) {
		//If one thread stamps overrides the data of another it's no big deal
		return pollutantIDLookup.computeIfAbsent(compound, key -> {
			Integer pollutantID = null;
			//May or may not insert, let the DB manage contention
			try(SqlSession session = sqlSessionFactory.openSession(true)) {
				DataMapper mapper = session.getMapper(DataMapper.class);
				mapper.insertPollutant(compound);
				pollutantID = mapper.getPollutantID(compound);
			}
			if(null == pollutantID) {
				throw new IllegalArgumentException("Could not find matching Pollutant ID for compound (" + compound + ") on row " + recordNumber);
			}
			return pollutantID;
		});
	}

	public int getThreadId() {
		return threadId;
	}

	public LoaderOptions getConfig() {
		return config;
	}

	public File getRawFile() {
		return rawFile;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
}
