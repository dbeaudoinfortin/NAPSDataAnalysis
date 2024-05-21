package com.dbf.naps.data.download.integrated;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.download.DownloaderOptions;
import com.dbf.naps.data.download.FileDownloadRunner;
import com.dbf.naps.data.globals.Constants;
import com.dbf.naps.data.utilities.ZipUtil;

public class IntegratedFileDownloadRunner extends FileDownloadRunner {
	
	private static final Logger log = LoggerFactory.getLogger(IntegratedFileDownloadRunner.class);
	
	private final String urlPath;
	private final String fileName;

	public IntegratedFileDownloadRunner(int year, String urlPath, String fileName, int threadId, DownloaderOptions config, Path downloadPath) {
		super(year, threadId, config, downloadPath);
		this.urlPath = urlPath;
		this.fileName = fileName;
	}
	
	@Override
	public void run() {
		
		try {
			log.info(getThreadId() + ":: Downloading file " + fileName + " for year " + getYear() + " at path " + urlPath);

			URI uri = URI.create(Constants.URL_INTEGRATED_BASE + urlPath);
			log.info(getThreadId() + ":: using URL " + uri);
			
			Path path = this.resolveFilePath(fileName);
			downloadFile(uri, resolveFilePath(fileName.toString()));
			
			if (fileName.toLowerCase().endsWith(".zip")) {
				ZipUtil.unzip(path, path.getParent());
				Files.delete(path);
			}
			
		 } catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR downloading file " + fileName + " for year " + getYear() + " at path " + urlPath, t);
			return; //Don't throw a runtime exception, let the other threads run
		 }
	}	
}
