package com.dbf.naps.data.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.NAPSDBAction;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.db.mappers.IntegratedDataMapper;
import com.dbf.naps.data.records.DataRecordGroup;
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
		
		log.info("Calculating file data groups based on the provided arguments.");
		if(getOptions().isFilePerYear() || getOptions().isFilePerPollutant() || getOptions().isFilePerSite())
		{
			List<DataRecordGroup> dataGroups = getDataGroups(getOptions().getYearStart(), getOptions().getYearEnd(),
					getOptions().getPollutants(),  getOptions().getSites(),
					getOptions().isFilePerYear(), getOptions().isFilePerPollutant(), getOptions().isFilePerSite());
					
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
	
	protected List<DataRecordGroup> getDataGroups(int startYear, int endYear, Collection<String> pollutants,
			Collection<Integer> sites, boolean groupByYear, boolean groupByPollutant, boolean groupBySite) {
		try(SqlSession session = getSqlSessionFactory().openSession(true)) {
			return session.getMapper(DataMapper.class).getDataGroups(startYear, endYear, pollutants,
					sites, groupByYear, groupByPollutant, groupBySite, getDataset());
		}
	}
	
	private void processFile(List<Future<?>> futures, Integer year, String pollutant, Integer site) {
		StringBuilder fileName = new StringBuilder(getDataset());
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
		fileName.append(".csv");
		futures.add(submitTask(processFile(getOptions().getDataPath().resolve(DataCleaner.sanatizeFileName(fileName.toString())).toFile(), year, pollutant, site)));	
	}
	
	protected Runnable processFile(File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		return new ExporterRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite, getDataset());
	}
	
	public Class<ExporterOptions> getOptionsClass(){
		return ExporterOptions.class;
	}
	
	@Override
	protected List<Class<?>> getDBMappers() {
		return List.of(DataMapper.class);
	}
	
	protected abstract String getDataset();
}
