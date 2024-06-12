package com.dbf.naps.data.download.continuous;

import java.net.URI;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.download.YearlyFileDownloadRunner;
import com.dbf.naps.data.download.options.YearlyDownloaderOptions;
import com.dbf.naps.data.globals.Constants;
import com.dbf.naps.data.globals.continuous.Compound;

public class ContinuousFileDownloadRunner extends YearlyFileDownloadRunner {
	
	private static final Logger log = LoggerFactory.getLogger(ContinuousFileDownloadRunner.class);
	
	private final Compound compound;
	
	public ContinuousFileDownloadRunner(int year, Compound compound, int threadId, YearlyDownloaderOptions config, Path rawPath) {
		super(year, threadId, config, rawPath);
		this.compound = compound;
	}
	
	@Override
	public void run() {
		
		try {
			log.info(getThreadId() + ":: Downloading file for year " + getYear() + " and compound " + compound);
			
			StringBuilder fileName = new StringBuilder();
			fileName.append(compound);
			fileName.append("_");
			fileName.append(getYear());
			fileName.append(".csv");
			
			StringBuilder url = new StringBuilder();
			url.append(Constants.URL_CONTINUOUS_BASE);
			url.append(getYear());
			url.append(Constants.URL_CONTINUOUS_SUFFIX);
			url.append(fileName);

			URI uri = URI.create(url.toString());
			log.info(getThreadId() + ":: using URL " + uri);
			
			downloadFile(uri, resolveFilePath(fileName.toString()));
		 } catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR downloading file for year " + getYear() + " and compound " + compound, t);
			return; //Don't throw a runtime exception, let the other threads run
		 }
	}
}
