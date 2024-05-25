package com.dbf.naps.data.download.integrated;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.download.YearlyFileDownloadRunner;
import com.dbf.naps.data.download.YearlyDownloaderOptions;
import com.dbf.naps.data.globals.Constants;
import com.dbf.naps.data.utilities.ZipUtil;

public class IntegratedFileDownloadRunner extends YearlyFileDownloadRunner {
	
	private static final Logger log = LoggerFactory.getLogger(IntegratedFileDownloadRunner.class);
	
	private static final Set<String> EXCLUDED_FILES = new HashSet<String>();
	static {
		EXCLUDED_FILES.add("S52603_DICH.XLS"); //Unknown site, every sheet has invalid data
		EXCLUDED_FILES.add("S106600_DICH.XLS"); //Unknown site but sheet has valid data
	}
	
	private final String urlPath;
	private final String fileName;

	public IntegratedFileDownloadRunner(int year, String urlPath, String fileName, int threadId, YearlyDownloaderOptions config, Path downloadPath) {
		super(year, threadId, config, downloadPath);
		this.urlPath = urlPath;
		this.fileName = fileName;
	}
	
	@Override
	public void run() {
		
		try {
			if(EXCLUDED_FILES.contains(fileName.toUpperCase())) {
				log.info(getThreadId() + ":: Skipping excluded file " + fileName + " for year " + getYear() + " at path " + urlPath);
				return;
			}
			
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
