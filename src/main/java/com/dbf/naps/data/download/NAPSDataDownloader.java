package com.dbf.naps.data.download;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.NAPSActionBase;
import com.dbf.naps.data.download.options.YearlyDownloaderOptions;

public abstract class NAPSDataDownloader extends NAPSActionBase<YearlyDownloaderOptions> {

	private static final Logger log = LoggerFactory.getLogger(NAPSDataDownloader.class);

	public NAPSDataDownloader(String[] args) {
		super(args);
	}

	protected void run() {
		log.info("Welcome! 🙂");

		try
		{
			downloadFiles();
		} catch (Throwable t) {
			log.error("Unexpected failure.", t);
		}

		log.info("Goodbye! 🙂");
		System.exit(0);
	}

	private void downloadFiles() {
		List<Future<?>> futures = new ArrayList<Future<?>>();

		final Path rawPath = getDownloadPath();
		rawPath.toFile().mkdir();

		for (int year = getOptions().getYearStart(); year <= getOptions().getYearEnd(); year++) {
			futures.addAll(submitTasks(processYear(year, rawPath)));
		}

		waitForTaskCompletion(futures);
	}

	public Class<YearlyDownloaderOptions> getOptionsClass(){
		return YearlyDownloaderOptions.class;
	}

	protected abstract Path getDownloadPath();

	protected abstract List<Runnable> processYear(int year, Path downloadPath);
}
