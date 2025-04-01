package com.dbf.naps.data.analysis.query;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVPrinter;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.analysis.DataAnalysisRecord;
import com.dbf.naps.data.analysis.DataAnalysisRunner;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.utilities.Utils;

public class ExtendedDataQueryRunner extends DataAnalysisRunner<ExtendedDataQueryOptions> {
	
	private final String dataSet;
	
	public ExtendedDataQueryRunner(int threadId, ExtendedDataQueryOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite, String dataSet) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
		this.dataSet = dataSet;
	}
	
	@Override
	public List<DataAnalysisRecord> runQuery(SqlSession session){
		
		Collection <Integer> years = getSpecificYear() != null ? List.of(getSpecificYear()) : Utils.getYearList(getConfig().getYearStart(), getConfig().getYearEnd());
		Collection <String> pollutants  = getSpecificPollutant() != null ? List.of(getSpecificPollutant()) : getConfig().getPollutants();
		Collection <Integer> sites = getSpecificSite() != null ? List.of(getSpecificSite()) : getConfig().getSites();
		
		return session.getMapper(DataMapper.class).getQueryData(
				//Grouping	
				getConfig().getFields(), getConfig().getAggregateFunction(),
				//Per-file filters
				years, pollutants,sites,
				//Method filters
				getConfig().getMethods(), getConfig().getReportTypes(),
				//Basic filters
				getConfig().getMonths(), getConfig().getDaysOfMonth(), getConfig().getDaysOfWeek(),
				getConfig().getSiteName(), getConfig().getCityName(),
				getConfig().getProvTerr().stream().map(p->p.name()).toList(),
				//Advanced site filters
				getConfig().getSiteType().stream().map(s->s.name()).toList(),
				getConfig().getUrbanization().stream().map(u->u.name()).toList(),
				//Advanced filters
				getConfig().getValueUpperBound(), getConfig().getValueLowerBound(),
				//Additional Columns
				getConfig().isSampleCount(), getConfig().isStdDevPop(), getConfig().isStdDevSmp(),
				//Having conditions
				getConfig().getResultUpperBound(), getConfig().getResultLowerBound(), getConfig().getMinSampleCount(),
				//Continuous vs. Integrated
				getDataset(),
				//AQHI
				getConfig().isAQHI());
	}
	
	@Override
	protected void writeToFile(List<DataAnalysisRecord> records, String queryUnits, String title, File dataFile) throws IOException {
		
		final Set<QueryOutputTypes> outputTypes = getConfig().getOutputTypes();
		
		if(outputTypes == null || outputTypes.isEmpty()) {
			//The default it to write a CSV file
			writeToCSVFile(records, queryUnits, title, dataFile);
		} else {
			for (QueryOutputTypes output : outputTypes) {
				switch (output) {
				case CSV:
					writeToCSVFile(records, queryUnits, title, dataFile);
					break;
				case JSON:
					File jsonFile = new File(dataFile.getParent(), dataFile.getName().replace(".csv", ".json"));
					this.checkFile(jsonFile);
					writeToJSONFile(records, queryUnits, title, jsonFile, false);
					break;
				case JSON_SLIM:
					File jsonSlimFile = new File(dataFile.getParent(), dataFile.getName().replace(".csv", outputTypes.contains(QueryOutputTypes.JSON) ? "_slim.json" : ".json"));
					this.checkFile(jsonSlimFile);
					writeToJSONFile(records, queryUnits, title, jsonSlimFile, true);
					break;
				default:
					break;
				}
			}
		}
	}
	
	@Override
	public void printRecordToCSV(DataAnalysisRecord record, CSVPrinter printer) throws IOException {
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
