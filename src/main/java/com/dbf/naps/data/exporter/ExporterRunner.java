package com.dbf.naps.data.exporter;

import java.io.File;

import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.DBRunner;
import com.dbf.naps.data.db.mappers.DataMapper;

public class ExporterRunner extends DBRunner<ExporterOptions> {
	
	private static final Logger log = LoggerFactory.getLogger(ExporterRunner.class);
	
	private File dataFile;
	private Class<? extends DataMapper> mapperClass;
	
	public ExporterRunner(int threadId, ExporterOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Class<? extends DataMapper> mapperClass) {
		super(threadId, config, sqlSessionFactory);
		this.dataFile = dataFile;
		this.mapperClass = mapperClass;
	}

	@Override
	public void run() {
		log.info(getThreadId() + ":: Starting export of file " + dataFile + ".");
		
		
		
		log.info(getThreadId() + ":: Completed export of file " + dataFile + ".");
	}
	
}
