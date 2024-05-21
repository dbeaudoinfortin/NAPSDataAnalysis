package com.dbf.naps.data.download.integrated;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.download.NAPSCatalogue;
import com.dbf.naps.data.download.NAPSCatalogue.PathContent;
import com.dbf.naps.data.download.NAPSDataDownloader;
import com.dbf.naps.data.globals.Constants;
import com.google.gson.Gson;

public class NAPSIntegratedDataDownloader extends NAPSDataDownloader {
	
	private static final Logger log = LoggerFactory.getLogger(NAPSIntegratedDataDownloader.class);

	private static final Gson gson = new Gson();
	
	public NAPSIntegratedDataDownloader(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSIntegratedDataDownloader dataDownloader = new NAPSIntegratedDataDownloader(args);
		dataDownloader.run();
	}
		
	@Override
	protected Path getDownloadPath() {
		return getOptions().getDownloadPath().resolve(Constants.FILE_PATH_INTEGRATED);
	}

	@Override
	protected List<Runnable> processYear(int year, Path downloadPath) {
		List<Runnable> tasks = new ArrayList<Runnable>(10);
		
		log.info("Searching for integrated data files for year " + year + ".");
		
		try {
			
			//Some of the file names are non-unique so we need to put every year in its own directory
			Path yearPath = downloadPath.resolve(""+ year);
			
			StringBuilder url = new StringBuilder();
			url.append(Constants.URL_INTEGRATED_LISTING_BASE);
			url.append(year);
			url.append(Constants.URL_INTEGRATED_LISTING_SUFFIX);
	
			URI uri = URI.create(url.toString());
			log.info("Calling URL " + uri);
						
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(uri)
					.build();
			
			HttpClient client = HttpClient.newBuilder()
					.followRedirects(Redirect.ALWAYS)
					.build();
			
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
			log.info("Request for year " + year + " resulted in status code " + response.statusCode());
			if (response.statusCode() != 200) {
				log.warn("Nothing to do for year " + year + ".");
				return tasks;
			}
			
			NAPSCatalogue catalogue = gson.fromJson(response.body(), NAPSCatalogue.class);
			
			log.info("Found " + catalogue.getPathContents().size() + " file(s) for year " + year + ".");
			for(PathContent content: catalogue.getPathContents()) {
				if (content.isDirectory()) continue; //TODO: Support recursive directories??
				tasks.add(new IntegratedFileDownloadRunner(year, content.getPath(), content.getName(), getThreadID(), getOptions(), yearPath));
			}
			
		} catch (Exception e) {
			log.error("Failed to lookup integrated data files for year " + year + ".");
			//Don't throw an exception so that we can try processing the other years
		}
		
		return tasks;
	}
}
