package com.dbf.naps.data.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.NAPSDBAction;
import com.dbf.naps.data.utilities.DataCleaner;

public abstract class NAPSDataExporter extends NAPSDBAction<ExporterOptions> {

	private static final Logger log = LoggerFactory.getLogger(NAPSDataExporter.class);
		
	public NAPSDataExporter(String[] args) {
		super(args);
	}
	
	protected void run() {
		log.info("Welcome! 🙂");
		
		try
		{
			super.run();
			exportData();
		} catch (Throwable t) {
			log.error("Unexpected failure.", t);
		}
		
		log.info("Goodbye! 🙂");
		System.exit(0);
	}

	private void exportData() throws IOException {
		List<Future<?>> futures = new ArrayList<Future<?>>();
		final Path exportPath = getOptions().getDataPath();
		
		log.info("Exporting data to the path " + exportPath);

		if(!exportPath.toFile().isDirectory()) {
			log.error("The export path is not valid: " + exportPath);
			return;
		}
		
		createTasksPerYear(futures);
		log.info(futures.size() + " task(s) have been created. Waiting for completion...");
		waitForTaskCompletion(futures);
	}
	
	private void createTasksPerYear(List<Future<?>> futures) {
		if (getOptions().isFilePerYear()) {
			//Not every year specified might actually exist in the database
			Set<Integer> allYears = null;
			for (int year = getOptions().getYearStart(); year <= getOptions().getYearEnd(); year++) {
				if (allYears.contains(year)) createTasksPerPollutant(futures, year);
			}
		} else {
			createTasksPerPollutant(futures, null);
		}
	}
	
	private void createTasksPerPollutant(List<Future<?>> futures, Integer year) {
		if (getOptions().isFilePerPollutant()) {
			//Not every pollutant specified might actually exist in the database
			Set<String> allPollutants = null;
			for(String pollutant : getOptions().getPollutants()) {
				if (allPollutants.contains(pollutant)) createTasksPerSite(futures, year, pollutant);
			}
		} else {
			createTasksPerSite(futures, year, null);
		}
	}
	
	private void createTasksPerSite(List<Future<?>> futures, Integer year, String pollutant) {
		if (getOptions().isFilePerSite()) {
			//Not every site specified might actually exist in the database
			Set<Integer> allSites = null;
			for(Integer site : getOptions().getSites()) {
				if (allSites.contains(site)) processFile(futures, year, pollutant, site);
			}
		} else {
			processFile(futures, year, pollutant, null);
		}
	}
	
	private void processFile(List<Future<?>> futures, Integer year, String pollutant, Integer site) {
		StringBuilder fileName = new StringBuilder(getFilePrefix());
		if(null != pollutant) {
			fileName.append("_");
			fileName.append("pollutant");
		}
		if(null != site) {
			fileName.append("_");
			fileName.append("site");
		}
		if(null != year) {
			fileName.append("_");
			fileName.append("year");
		}
		fileName.append(".csv");
		
		futures.add(submitTask(processFile(getOptions().getDataPath().resolve(DataCleaner.sanatizeFileName(fileName.toString())).toFile(), year, pollutant, site)));	
	}
	
	protected abstract Runnable processFile(File dataFile, Integer year, String pollutant, Integer site);
	
	protected abstract String getFilePrefix();
	
	public Class<ExporterOptions> getOptionsClass(){
		return ExporterOptions.class;
	}
}
