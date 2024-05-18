package com.dbf.naps.data.download;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.NAPSActionBase;

public abstract class NAPSDataDownloader extends NAPSActionBase {

	private static final Logger log = LoggerFactory.getLogger(NAPSDataDownloader.class);

	protected DownloadOptions CONFIG = null;
	
	protected void run(String[] args) {
		log.info("Welcome! 🙂");
		
		try
		{
			initConfig(args);
			initBase(CONFIG);
			downloadFiles();
		} catch (Throwable t) {
			log.error("Unexpected failure.", t);
		}
		
		log.info("Goodbye! 🙂");
	}
	
	private void initConfig(String[] args) {
		try {
			CONFIG = new DownloadOptions(args);
		} catch (IllegalArgumentException e) {
			log.error("Error reading command line options: ", e);
			log.info("Command line usage:\n" + DownloadOptions.printOptions());
			System.exit(0);
		}
	}

	private void downloadFiles() {
		List<Future<?>> futures = new ArrayList<Future<?>>();
		
		final Path rawPath = getDownloadPath();
		rawPath.toFile().mkdir();
		
		for (int year = CONFIG.getYearStart(); year <= CONFIG.getYearEnd(); year++) {
			futures.addAll(submitTasks(processYear(year, rawPath)));
		}

		waitForTaskCompletion(futures);
	}
	
	protected abstract Path getDownloadPath();
	
	protected abstract List<Runnable> processYear(int year, Path downloadPath);
}
