package com.dbf.naps.data.loader.sites;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.globals.ProvinceTerritoryMapping;
import com.dbf.naps.data.loader.DataMapper;
import com.dbf.naps.data.loader.FileLoaderRunner;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.records.SiteRecord;
import com.dbf.naps.data.utilities.DataCleaner;

public class SitesLoaderRunner extends FileLoaderRunner {
	
	private static final Logger log = LoggerFactory.getLogger(SitesLoaderRunner.class);
	
	private static final CSVFormat csvFormat;

	static {
		csvFormat = CSVFormat.Builder.create()
				.setTrim(true)
				.setSkipHeaderRecord(true)
				.setIgnoreEmptyLines(true)
				.build();
	}
	
	public SitesLoaderRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}
	
	@Override
	public void processFile() throws Exception {
		log.info(getThreadId() + ":: Starting CSV parsing for file " + getRawFile() + ".");
		
		//Since there aren't too many sites we can safely hold them all in memory before inserting them into the DB
		List<SiteRecord> records = new ArrayList<SiteRecord>(800);
		
		//Load all the rows into memory. Let's assume we don't run out of memory. :) 
		try (Reader reader = new FileReader(getRawFile(), StandardCharsets.ISO_8859_1); CSVParser parser = csvFormat.parse(reader)) {
			for(CSVRecord line : parser) {
				if(line.size() < 41) continue; //Header lines
				
				if(line.get(0).toUpperCase().startsWith("NAPS") || line.get(0).toUpperCase().startsWith("IDENT")) {
					//These are the real header rows, in both French and English
					log.info(getThreadId() + ":: Skipping non-data row " + line.getRecordNumber() + " in file " + getRawFile() + ".");
					continue;
				}
					
				SiteRecord record = new SiteRecord();
				
				try {
					record.setNAPSId(Integer.parseInt(line.get(0)));
				} catch (NumberFormatException e){
					log.info(getThreadId() + ":: Skipping non-data row " + line.getRecordNumber() + " in file " + getRawFile() + ".");
					continue;
				}
				
				record.setStationName(line.get(1));
				record.setCityName(line.get(4));
				record.setProvTerr(ProvinceTerritoryMapping.getCode(line.get(5)));
				record.setLatitude(DataCleaner.parseLatitude(line.get(8)));
				record.setLongitude(DataCleaner.parseLongitude(line.get(9)));
				
				String elevationRaw = line.get(10);
				if(!"".equals(elevationRaw)) record.setElevation(Integer.parseInt(elevationRaw));
				
				record.setSiteType(line.get(30));
				record.setUrbanization(line.get(31));
				record.setNeighbourhood(line.get(32));
				record.setLandUse(line.get(33));
				record.setScale(line.get(34));
				records.add(record);
			}
		}
		
		//Load them all into the DB in one shot
		loadRecords(records);
	}
	
	private void loadRecords(List<SiteRecord> records) {
		if(records.size() > 0) {
			log.info(getThreadId() + ":: Loading " + records.size() + " records into the database for file " + getRawFile() + ".");
			try(SqlSession session = getSqlSessionFactory().openSession(true)) {
				//Shouldn't time out, there aren't that many records
				for(SiteRecord site: records) {
					session.getMapper(DataMapper.class).insertSiteFull(site);
				}
			}
		}
	}
}
