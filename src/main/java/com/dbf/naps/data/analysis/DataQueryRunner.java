package com.dbf.naps.data.analysis;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.FileRunner;
import com.dbf.naps.data.db.mappers.DataMapper;

public abstract class DataQueryRunner<O extends DataQueryOptions> extends FileRunner<O> {
	
	private static final Logger log = LoggerFactory.getLogger(DataQueryRunner.class);
	
	public DataQueryRunner(int threadId, O config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}
	
	@Override
	public void run() {		
		List<DataQueryRecord> records = null;
		try {
			checkFile();
			records = queryData();
			if(records.isEmpty()) return;
		} catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR executing data query for file " + getDataFile() + ".", t);
			return;
		}
		
		try {
			writeToFile(records, getDataFile());
		 } catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR writing to file " + getDataFile() + ".", t);
			return;
		 }
	}
	
	private List<DataQueryRecord> queryData() {
		log.info(getThreadId() + ":: Starting data query for file " + getDataFile() + ".");
		List<DataQueryRecord> records;
		try(SqlSession session = getSqlSessionFactory().openSession(true)) {
			records = runQuery(session);
		}
		if(records == null || records.isEmpty()) {
			log.info(getThreadId() + ":: No data records found for " + getDataFile() + ". Skipping file.");
			return Collections.emptyList();
		}
		log.info(getThreadId() + ":: Completed data query for file " + getDataFile() + ". Query returned " + records.size() + " record(s).");
		return records;
	}
	
	public List<DataQueryRecord> runQuery(SqlSession session){
		return session.getMapper(DataMapper.class).getQueryData(
				//Grouping	
				getConfig().getFields(), getConfig().getAggregateFunction(),
				//Per-file filters
				getSpecificYear() != null ? List.of(getSpecificYear()) : IntStream.range(getConfig().getYearStart(), getConfig().getYearEnd() + 1).boxed().toList(),
				getSpecificPollutant() != null ? List.of(getSpecificPollutant()) : getConfig().getPollutants(),
				getSpecificSite() != null ? List.of(getSpecificSite()) : getConfig().getSites(),
				//Basic filters
				getConfig().getMonths(),getConfig().getDays(),
				getConfig().getSiteName(), getConfig().getCityName(),
				getConfig().getProvTerr().stream().map(p->p.name()).toList(),
				//Advanced filters
				getConfig().getValueUpperBound(), getConfig().getValueLowerBound(),
				//Additional Columns
				false, false, false,
				//Having conditions
				getConfig().getResultUpperBound(), getConfig().getResultLowerBound(), getConfig().getMinSampleCount(),
				//Continuous vs. Integrated
				getDataset());
	}
	
	public void writeToFile(List<DataQueryRecord> records, File dataFile) throws IOException {
		log.info(getThreadId() + ":: Starting writing to file " + dataFile + ".");
		
		
		//Write to CSV here
		log.info(getThreadId() + ":: Completed writing to file " + dataFile + ".");
	}

	protected abstract String getDataset();
}
