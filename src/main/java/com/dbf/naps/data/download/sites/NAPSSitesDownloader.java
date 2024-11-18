package com.dbf.naps.data.download.sites;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.NAPSActionBase;
import com.dbf.naps.data.download.NAPSDataDownloader;
import com.dbf.naps.data.download.options.DownloaderOptions;
import com.dbf.utils.stacktrace.StackTraceCompactor;

public class NAPSSitesDownloader extends NAPSActionBase<DownloaderOptions> {

	private static final Logger log = LoggerFactory.getLogger(NAPSDataDownloader.class);

	public static void main(String[] args) {
		NAPSSitesDownloader sitesDownloader = new NAPSSitesDownloader(args);
		sitesDownloader.run();
	}

	public NAPSSitesDownloader(String[] args) {
		super(args);
	}

	protected void run() {
		log.info("Welcome! 🙂");

		try
		{
			downloadSiteFile();
		} catch (Throwable t) {
			log.error("Unexpected failure.\n" + StackTraceCompactor.getCompactStackTrace(t));
		}

		log.info("Goodbye! 🙂");
		end();
	}

	private void downloadSiteFile() {
		getOptions().getDownloadPath().toFile().mkdir();

		List<Future<?>> futures = new ArrayList<Future<?>>();
		futures.add(submitTask(new SitesFileDownloadRunner(getThreadID(), getOptions(), getOptions().getDownloadPath())));
		waitForTaskCompletion(futures);
	}

	@Override
	public Class<DownloaderOptions> getOptionsClass(){
		return DownloaderOptions.class;
	}

}