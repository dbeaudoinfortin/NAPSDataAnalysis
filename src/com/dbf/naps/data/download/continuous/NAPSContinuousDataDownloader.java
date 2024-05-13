package com.dbf.naps.data.download.continuous;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.Compound;
import com.dbf.naps.data.Constants;
import com.dbf.naps.data.NAPSActionBase;
import com.dbf.naps.data.download.DownloadOptions;

public class NAPSContinuousDataDownloader extends NAPSActionBase {

	private static final Logger log = LoggerFactory.getLogger(NAPSContinuousDataDownloader.class);

	private static DownloadOptions CONFIG = null;
	
	public static void main(String[] args) {
		log.info("Welcome! 🙂");
		
		try
		{
			initConfig(args);
			initThreadPool(CONFIG.getThreadCount());
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
			log.info("Command line usage:\n" + CONFIG.printOptions());
			System.exit(0);
		}
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
}
