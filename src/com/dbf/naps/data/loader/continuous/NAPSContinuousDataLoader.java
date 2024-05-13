package com.dbf.naps.data.loader.continuous;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.Constants;
import com.dbf.naps.data.NAPSActionBase;
import com.dbf.naps.data.loader.LoadOptions;

public class NAPSContinuousDataLoader extends NAPSActionBase {

	private static final Logger log = LoggerFactory.getLogger(NAPSContinuousDataLoader.class);
	
	private static ThreadPoolExecutor  THREAD_POOL = null; 
	private static final AtomicInteger THREAD_ID_COUNTER = new AtomicInteger(0);
	
	private static LoadOptions CONFIG = null;
	
	public static void main(String[] args) {
		log.info("Welcome! 🙂");
		
		try
		{
			initConfig(args);
			initDB();
			initThreadPool(CONFIG.getThreadCount());
			loadContinousFiles();
			
		} catch (Throwable t) {
			log.error("Unexpected failure.", t);
		}
		
		log.info("Goodbye! 🙂");
	}
	
	private static void initConfig(String[] args) {
		try {
			CONFIG = new LoadOptions(args);
		} catch (IllegalArgumentException e) {
			log.error("Error reading command line options: ", e);
			log.info("Command line usage:\n" + CONFIG.printOptions());
			System.exit(0);
		}
	}
	
	private static void initDB() {
		//TODO: Implement this
	}
	
	private static void loadContinousFiles() {

		List<Future<?>> futures = new ArrayList<Future<?>>();
		
		final Path rawPath = CONFIG.getDataPath().resolve(Constants.FILE_PATH_CONTINUOUS);
		
		//TODO: implement this
		//futures.add(THREAD_POOL.submit(new ContinuousFileLoader(THREAD_ID_COUNTER.getAndIncrement(), CONFIG, rawFile)));
		waitForTaskCompletion(futures);
	}

}
