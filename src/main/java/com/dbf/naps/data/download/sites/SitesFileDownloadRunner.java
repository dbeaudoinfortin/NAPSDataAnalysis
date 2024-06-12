package com.dbf.naps.data.download.sites;

import java.net.URI;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.download.FileDownloadRunner;
import com.dbf.naps.data.download.options.DownloaderOptions;
import com.dbf.naps.data.globals.Constants;

public class SitesFileDownloadRunner extends FileDownloadRunner<DownloaderOptions> {
	
	private static final Logger log = LoggerFactory.getLogger(SitesFileDownloadRunner.class);
	
	public SitesFileDownloadRunner(int threadId, DownloaderOptions config, Path rawPath) {
		super(threadId, config, rawPath);
	}
	
	@Override
	public void run() {
		
		try {
			log.info(getThreadId() + ":: Downloading sites file.");
			URI uri = URI.create(Constants.URL_SITES_FULL);
			log.info(getThreadId() + ":: using URL " + uri);
			
			downloadFile(uri, resolveFilePath("sites.csv"));
		 } catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR downloading sites file.", t);
			return;
		 }
	}
}
