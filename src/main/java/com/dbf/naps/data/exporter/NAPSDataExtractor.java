package com.dbf.naps.data.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.NAPSDBAction;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.records.DataRecordGroup;
import com.dbf.naps.data.utilities.DataCleaner;
import com.dbf.naps.data.utilities.Utils;
import com.dbf.utils.stacktrace.StackTraceCompactor;

public abstract class NAPSDataExtractor<O extends ExtractorOptions> extends NAPSDBAction<O> {

	private static final Logger log = LoggerFactory.getLogger(NAPSDataExtractor.class);
		
	public NAPSDataExtractor(String[] args) {
		super(args);
	}
	
	//TODO: Create an options class and add an option to automatically zip the export files
	
	@Override
	protected void run() {
		log.info("Welcome! 🙂");
		
		try
		{
			super.run();
			exportData();
		} catch (Throwable t) {
			log.error("Unexpected failure.\n" + StackTraceCompactor.getCompactStackTrace(t));
		}
		
		log.info("Goodbye! 🙂");
		end();
	}

	private void exportData() throws IOException {
		List<Future<?>> futures = new ArrayList<Future<?>>();
		final Path exportPath = getOptions().getDataPath();
		
		log.info("Exporting data to the path " + exportPath);

		File exportPathFile = exportPath.toFile();
		if(exportPathFile.exists()) {
			if(!exportPathFile.isDirectory()) {
				log.error("The export path is not valid: " + exportPath);
				return;
			}
		} else {
			log.info("Creating the export path " + exportPath);
			exportPathFile.mkdirs();
		}
		
		log.info("Calculating file data groups based on the provided arguments.");
		if(getOptions().isFilePerYear() || getOptions().isFilePerPollutant() || getOptions().isFilePerSite())
		{
			final List<DataRecordGroup> dataGroups = getDataGroups();
			
			if(getOptions().isGenerateJSDataMap() && getOptions().isFilePerYear() && getOptions().isFilePerPollutant() && getOptions().isFilePerSite()) {
				log.info("Generating JS data map to " + exportPath);
				
				//Generate a multi-dimensional JavaScript lookup table
				Map<String, Map<Integer, Set<Integer>>> dataMap = new HashMap<String, Map<Integer, Set<Integer>>>();
				dataGroups.stream().forEach(r->{
					dataMap.computeIfAbsent(r.getPollutantName().replace("/", "_"), p->new HashMap<Integer, Set<Integer>>())
						.computeIfAbsent(r.getYear(), y->new TreeSet<Integer>())
						.add(r.getSiteID());
				});
				
				String jsFormattedDataMap = Utils.convertToJsObjectNotation(getDataset().toLowerCase() + "DataMap", dataMap);
				if(getOptions().isVerbose()) {
					log.debug("JavaScript Data Map:");
					log.debug(jsFormattedDataMap);
				}
				
				Path dataMapPath = exportPath.resolve(getDataset().toLowerCase() + "_datamap.js");
				Files.write(dataMapPath, jsFormattedDataMap.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				log.info("JS data map has been written to " + exportPath);
			}
			
					
			log.info(dataGroups.size() + " file(s) will be created.");
			for(DataRecordGroup group : dataGroups) {
				processFile(futures, group.getYear(), group.getPollutantName(), group.getSiteID());
			}
		} else {
			processFile(futures, null, null, null);
		}

		log.info(futures.size() + " task(s) have been created. Waiting for completion...");
		waitForTaskCompletion(futures);
	}
	
	protected List<DataRecordGroup> getDataGroups() {
		try(SqlSession session = getSqlSessionFactory().openSession(true)) {
			return session.getMapper(DataMapper.class).getExportDataGroups(
					getOptions().getYearStart(), getOptions().getYearEnd(), getOptions().getPollutants(),  getOptions().getSites(), //Per-file filters
					getOptions().isFilePerYear(), getOptions().isFilePerPollutant(), getOptions().isFilePerSite(), //Grouping
					null, null, null, null, null, null, null, null, null, null,	//Filtering
					getDataset());
		}
	}
	
	private void processFile(List<Future<?>> futures, Integer year, String pollutant, Integer site) {
		String fileName = getBaseFilename(year, pollutant, site) + getFileExtension();
		futures.add(submitTask(processFile(getOptions().getDataPath().resolve(DataCleaner.sanatizeFileName(fileName.toString())).toFile(), year, pollutant, site)));	
	}
	
	private String getBaseFilename(Integer year, String pollutant, Integer site) {
		StringBuilder fileName = new StringBuilder();
		appendFilename(fileName, year, pollutant, site);
		return fileName.toString();
	}
	
	protected void appendFilename(StringBuilder fileName, Integer year, String pollutant, Integer site) {
		if(getOptions().getFileName() != null) {
			//Using a custom filename
			fileName.append(getOptions().getFileName());
		} else {
			fileName.append(getDataset());
		}
		
		if(null != pollutant) {
			fileName.append("_");
			fileName.append(pollutant);
		}
		if(null != site) {
			fileName.append("_");
			fileName.append(site);
		}
		if(null != year) {
			fileName.append("_");
			fileName.append(year);
		}
	}
	
	@Override
	protected List<Class<?>> getDBMappers() {
		return List.of(DataMapper.class);
	}
	
	protected abstract Runnable processFile(File dataFile, Integer specificYear, String specificPollutant, Integer specificSite);
	 
	protected abstract String getDataset();
	
	protected abstract String getFileExtension();
}
