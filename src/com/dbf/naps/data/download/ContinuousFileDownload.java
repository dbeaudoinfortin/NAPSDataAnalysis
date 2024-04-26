package com.dbf.naps.data.download;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.Compound;
import com.dbf.naps.data.Constants;

public class ContinuousFileDownload implements Runnable {
	
	private static final Logger log = LoggerFactory.getLogger(ContinuousFileDownload.class);
	
	private final int year;
	private final Compound compound;
	private final int threadId;
	private final DownloadOptions config;
	private final Path rawPath;
	
	public ContinuousFileDownload(int year, Compound compound, int threadId, DownloadOptions config, Path rawPath) {
		this.year = year;
		this.compound = compound;
		this.threadId = threadId;
		this.config = config;
		this.rawPath = rawPath;
	}
	@Override
	public void run() {
		
		try {
			log.info(threadId + ":: Downloading file for year " + year + " and compound " + compound);
			
			StringBuilder fileName = new StringBuilder();
			fileName.append(compound);
			fileName.append("_");
			fileName.append(year);
			fileName.append(".csv");
			
			StringBuilder url = new StringBuilder();
			url.append(Constants.URL_CONTINUOUS_BASE);
			url.append(year);
			url.append(Constants.URL_CONTINUOUS_SUFFIX);
			url.append(fileName);

			URI uri = URI.create(url.toString());
			log.info(threadId + ":: using URL " + uri);
			
			Path path = rawPath.resolve(fileName.toString());
			File file = path.toFile();
			log.info(threadId + ":: using file path " + path);
			
			if(file.exists()) {
				if(file.isDirectory()) {
					 log.error(threadId + ":: ERROR directory found at file location " + path);
					 return;
				} else if (config.isOverwriteFiles()) {
					log.info(threadId + ":: existing file found at path " + path + ". Deleting file.");
					Files.delete(path);
				} else {
					log.info(threadId + ":: existing file found at path " + path + ". Skipping file.");
					return;
				}
			}
			
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(uri)
					.build();
			
			HttpClient client = HttpClient.newBuilder()
					.followRedirects(Redirect.ALWAYS)
					.build();
			
			HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(path));
			if (response.statusCode() >= 300 || response.statusCode() < 200) {
				Files.delete(path);
			}
			log.info(threadId + ":: File download for year " + year + " and compound " + compound + " resulted in status code " + response.statusCode());
			
		 } catch (Throwable t) {
			 log.error(threadId + ":: ERROR downloading file for year " + year + " and compound " + compound, t);
			return; //Don't throw a runtime exception, let the other threads run
		 }
	}

}
