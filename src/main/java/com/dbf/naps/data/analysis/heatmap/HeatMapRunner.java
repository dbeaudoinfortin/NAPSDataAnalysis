package com.dbf.naps.data.analysis.heatmap;

import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.DBRunner;
import com.dbf.naps.data.db.mappers.DataMapper;

public abstract class HeatMapRunner extends DBRunner<HeatMapOptions> {
	
	private static final Logger log = LoggerFactory.getLogger(HeatMapRunner.class);
	
	private File dataFile;
	private Integer specificYear;
	private String specificPollutant;
	private Integer specificSite;
		
	public HeatMapRunner(int threadId, HeatMapOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory);
		this.dataFile = dataFile;
		this.specificYear = specificYear;
		this.specificPollutant = specificPollutant;
		this.specificSite = specificSite;
	}
	
	@Override
	public void run() {
		
		try {
			log.info(getThreadId() + ":: Starting Heat Map generation for file " + dataFile + ".");
			generateHeatMap();
			log.info(getThreadId() + ":: Completed Heat Map generation for file " + dataFile + ".");
		 } catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR Heat Map generation for file " + dataFile + ".", t);
			return;
		 }
	}

	public void generateHeatMap() {		
		if(dataFile.isDirectory()) {
			throw new IllegalArgumentException("File path is a directory: " + dataFile);
		} else if (dataFile.isFile()) {
			if(getConfig().isOverwriteFiles()) {
				log.warn(getThreadId() + ":: Deleting existing file " + dataFile + ".");
				dataFile.delete();
			} else {
				throw new IllegalArgumentException("Cannot output to path \"" + dataFile +"\". The file already exists and the overwrite flag is set to false.");
			}
		}
		
		List<HeatMapRecord> records;
		try(SqlSession session = getSqlSessionFactory().openSession(true)) {
			records = session.getMapper(getDataMapper()).getHeatMapData(
				getConfig().getXField(),
				getConfig().getYField(),
				getConfig().getAggregateFunction(),
				specificYear != null ? List.of(specificYear) : IntStream.range(getConfig().getYearStart(), getConfig().getYearEnd() + 1).boxed().toList(),
				specificPollutant != null ? List.of(specificPollutant) : getConfig().getPollutants(),
				specificSite != null ? List.of(specificSite) : getConfig().getSites());
		}
		
		if(records == null || records.isEmpty()) {
			log.info(getThreadId() + ":: No data records found for " + dataFile + ". Skipping file.");
			return;
		}
		
		for(HeatMapRecord record : records) {
			log.info(record.getX().toString() + "\t" + record.getY().toString() + "\t\t" + record.getValue().toString());
		}
	}
	
	protected abstract Class<? extends DataMapper> getDataMapper();
	
	protected abstract String getDataset();
}
