package com.dbf.naps.data.download;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.BaseRunner;
import com.dbf.naps.data.download.options.DownloaderOptions;

public abstract class FileDownloadRunner<O extends DownloaderOptions> extends BaseRunner<O> {
	
	private static final Logger log = LoggerFactory.getLogger(FileDownloadRunner.class);
	
	private final Path downloadPath;
	
	public FileDownloadRunner(int threadId, O config, Path downloadPath) {
		super(threadId, config);
		this.downloadPath = downloadPath;
	}
	
	protected Path resolveFilePath(String fileName) throws IOException {
		Files.createDirectories(downloadPath);
		
		Path path = downloadPath.resolve(fileName.toString());
		File file = path.toFile();
		log.info(getThreadId() + ":: using file path " + path);
		
		if(file.exists()) {
			if(file.isDirectory()) {
				 log.error(getThreadId() + ":: directory found at file location " + path);
				 throw new IOException("Directory found at file location " + path);
			} else if (getConfig().isOverwriteFiles()) {
				log.info(getThreadId() + ":: existing file found at path " + path + ". Deleting file.");
				Files.delete(path);
			} else {
				log.info(getThreadId() + ":: existing file found at path " + path + ". Skipping file.");
				throw new IOException("Existing file found at path " + path);
			}
		}
		return path;
	}
	
	protected void downloadFile(URI url, Path filePath) throws IOException, InterruptedException
	{
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(url)
				.build();
		
		HttpClient client = HttpClient.newBuilder()
				.followRedirects(Redirect.ALWAYS)
				.build();
		
		HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(filePath));
		if (response.statusCode() >= 300 || response.statusCode() < 200) {
			Files.delete(filePath);
		}
		log.info(getThreadId() + ":: File download for URL " + url + " resulted in status code " + response.statusCode());
	}
}
