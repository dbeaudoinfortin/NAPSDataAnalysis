package com.dbf.naps.data.loader.continuous;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.loader.LoadOptions;

public class ContinuousFileLoader implements Runnable {
	
	private static final Logger log = LoggerFactory.getLogger(ContinuousFileLoader.class);
	
	private final int threadId;
	private final LoadOptions config;
	private final File rawFile;
	
	public ContinuousFileLoader(int threadId, LoadOptions config, File rawFile) {
		this.threadId = threadId;
		this.config = config;
		this.rawFile = rawFile;
	}
	
	@Override
	public void run() {
		
		try {
			log.info(threadId + ":: Loading file " + rawFile + " into the database.");
			
		 } catch (Throwable t) {
			 log.error(threadId + ":: ERROR loading file " + rawFile + " into the database.", t);
			return; //Don't throw a runtime exception, let the other threads run
		 }
	}

}
