package com.dbf.naps.data.analysis;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVPrinter;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.analysis.query.ExtendedDataQueryOptions;
import com.dbf.naps.data.db.mappers.DataMapper;

public class ExtendedDataQueryRunner extends DataQueryRunner<ExtendedDataQueryOptions> {
	
	private final String dataSet;
	
	public ExtendedDataQueryRunner(int threadId, ExtendedDataQueryOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite, String dataSet) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
		this.dataSet = dataSet;
	}
	
	@Override
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
				getConfig().isSampleCount(), getConfig().isStdDevPop(), getConfig().isStdDevSmp(),
				//Having conditions
				getConfig().getResultUpperBound(), getConfig().getResultLowerBound(), getConfig().getMinSampleCount(),
				//Continuous vs. Integrated
				getDataset());
	}
	
	@Override
	public void printRecordToCSV(DataQueryRecord record, CSVPrinter printer) throws IOException {
		record.printToCSV(printer, getConfig().getFields().size(), getConfig().isSampleCount(), getConfig().isStdDevPop(), getConfig().isStdDevSmp());
	}
	
	@Override
	public List<String> buildCSVHeader() {
		List<String> headerStrings = super.buildCSVHeader();
		if(getConfig().isSampleCount()) headerStrings.add("SAMPLE COUNT");
		if(getConfig().isStdDevPop()) headerStrings.add("POPULATION STANDARD DEVIATION");
		if(getConfig().isStdDevSmp()) headerStrings.add("SAMPLE STANDARD DEVIATION");
		return headerStrings;
	}

	@Override
	protected String getDataset() {
		return dataSet;
	}
}
