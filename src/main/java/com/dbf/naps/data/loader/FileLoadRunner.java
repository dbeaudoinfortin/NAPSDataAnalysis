package com.dbf.naps.data.loader;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.globals.PollutantMapping;
import com.dbf.naps.data.utilities.DataCleaner;

public abstract class FileLoadRunner implements Runnable {
	
	private static final Logger log = LoggerFactory.getLogger(FileLoadRunner.class);
	
	//Holds a mapping of NAPSID to SiteID, shared across threads
	private static final Map<Integer, Integer> siteIDLookup = new ConcurrentHashMap<Integer, Integer>(300);
	
	//Holds a mapping of PollutantName to PollutantID, shared across threads
	private static final Map<String, Integer> pollutantIDLookup = new ConcurrentHashMap<String, Integer>(200);
	
	//Holds a mapping of lookupKey (dataset, report_type, method) to MethodID, shared across threads
	private static final Map<String, Integer> methodIDLookup = new ConcurrentHashMap<String, Integer>(50);
	
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
					DataMapper mapper = session.getMapper(DataMapper.class);
					mapper.insertSitePartial(NAPSID, cityName, provTerr.toUpperCase(), DataCleaner.parseLatitude(latitudeRaw), DataCleaner.parseLongitude(longitudeRaw));
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
	
	protected Integer getPollutantID(String rawPollutantName) {
		
		//If one thread stamps overrides the data of another it's no big deal
		return pollutantIDLookup.computeIfAbsent(rawPollutantName, pollutantName -> {
			Integer pollutantID = null;
			pollutantName = PollutantMapping.lookupPollutantName(pollutantName);
			//May or may not insert, let the DB manage contention
			try(SqlSession session = sqlSessionFactory.openSession(true)) {
				DataMapper mapper = session.getMapper(DataMapper.class);
				mapper.insertPollutant(pollutantName);
				pollutantID = mapper.getPollutantID(pollutantName);
			}
			if(null == pollutantID) { //Sanity check, should be impossible
				throw new IllegalArgumentException("Could not find a matching pollutant ID with name " + rawPollutantName);
			}
			return pollutantID;
		});
	}
	
	protected Integer getMethodID(String dataset, String reportType, String method, String units) {
		String lookupKey = dataset + "_" + reportType + "_" + method + "_" + units;
		
		//If one thread stamps overrides the data of another it's no big deal
		return methodIDLookup.computeIfAbsent(lookupKey, key -> {
			Integer methodID = null;

			//May or may not insert, let the DB manage contention
			try(SqlSession session = sqlSessionFactory.openSession(true)) {
				DataMapper mapper = session.getMapper(DataMapper.class);
				mapper.insertMethod(dataset, reportType, method, units);
				methodID = mapper.getMethodID(dataset, reportType, method, units);
			}
			if(null == methodID) { //Sanity check, should be impossible
				throw new IllegalArgumentException("Could not find a matching method ID using lookup key: " + key);
			}
			return methodID;
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
