package com.dbf.naps.data.download;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.Compound;
import com.dbf.naps.data.Constants;

public class NAPSDownloader {

	private static final Logger log = LoggerFactory.getLogger(NAPSDownloader.class);
	
	private static ThreadPoolExecutor  THREAD_POOL = null; 
	private static final AtomicInteger THREAD_ID_COUNTER = new AtomicInteger(0);
	
	private static DownloadOptions CONFIG = null;
	
	public static void main(String[] args) {
		log.info("Welcome! 🙂");
		
		try
		{
			initConfig(args);
			initThreadPool();
			downloadContinousFiles();
			
		} catch (Throwable t) {
			log.error("Unexpected failure.", t);
		}
		
		log.info("Goodbye! 🙂");
	}
	
	private static void initConfig(String[] args) {
		try {
			CONFIG = new DownloadOptions(args);
		} catch (IllegalArgumentException e) {
			log.error("Error reading command line options: ", e);
			log.info("Command line usage:\n" + DownloadOptions.printOptions());
			System.exit(0);
		}
	}
	
	private static void initThreadPool() { 
		log.info("Initializing thread pool with a size of " + CONFIG.getThreadCount());
		THREAD_POOL = new ThreadPoolExecutor(CONFIG.getThreadCount(),CONFIG.getThreadCount(),100l,TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
	}
	
	private static void downloadContinousFiles() {

		List<Future<?>> futures = new ArrayList<Future<?>>();
		
		final Path rawPath = CONFIG.getDownloadPath().resolve(Constants.FILE_PATH_CONTINUOUS);
		rawPath.toFile().mkdir();
		
		for (int year = CONFIG.getYearStart(); year <= CONFIG.getYearEnd(); year++) {
			for (Compound compound : Compound.values()) {
				futures.add(THREAD_POOL.submit(new ContinuousFileDownload(year, compound, THREAD_ID_COUNTER.getAndIncrement(), CONFIG, rawPath)));
			}
		}

		waitForTaskCompletion(futures);
	}
	
	private static void waitForTaskCompletion(List<Future<?>> futures) {
		futures.forEach(f->{
			try {
				f.get();
			} catch (ExecutionException | InterruptedException e) {
				throw new RuntimeException("Failed to wait for completion of tasks.", e); 
			}
		});
	}

}
