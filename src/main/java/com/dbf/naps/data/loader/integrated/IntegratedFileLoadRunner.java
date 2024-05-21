package com.dbf.naps.data.loader.integrated;

import java.io.File;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.loader.FileLoadRunner;
import com.dbf.naps.data.loader.LoaderOptions;

public class IntegratedFileLoadRunner extends FileLoadRunner {

	private static final Logger log = LoggerFactory.getLogger(IntegratedFileLoadRunner.class);
	
	public IntegratedFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}
	
	@Override
	public void run() {
		log.info(getThreadId() + ":: Starting to load file " + getRawFile() + " into the database.");
		
		try {
			
		 } catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR loading file " + getRawFile() + " into the database.", t);
			return; //Don't throw a runtime exception, let the other threads run
		 }
		log.info(getThreadId() + ":: Done loading file " + getRawFile() + " into the database.");
	}
	
}
