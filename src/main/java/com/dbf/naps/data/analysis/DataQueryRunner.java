package com.dbf.naps.data.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
			
			//Only allow mixing unit if we are not aggregating data or we are grouping by pollutant
			if(!getConfig().getAggregateFunction().equals(AggregateFunction.COUNT) && !getConfig().getAggregateFunction().equals(AggregateFunction.NONE) 
					&& getConfig().getFields().stream().filter(f->f.equals(AggregationField.POLLUTANT)).count() == 0) {
				
				//Note: The schema makes it possible for two different data points measuring the same pollutant to use two different methods with two different unit.
				//However, in practise the units are consistent for all of the pollutants.
				List<String> units = session.getMapper(DataMapper.class).getDistinctUnits(
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
					//Continuous vs. Integrated
					getDataset());
				
				if(units.size() > 1) {
					
				}
			}
			
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
		
		List<String> headers = buildCSVHeader();
		CSVFormat format = CSVFormat.EXCEL
				.builder()
				.setTrim(false)
				.setHeader(headers.toArray(new String[headers.size()]))
				.setSkipHeaderRecord(false)
				.build();
			try(BufferedWriter writer = Files.newBufferedWriter(dataFile.toPath(), StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
				writer.write('\ufeff'); //Manually print the UTF-8 BOM
				try(CSVPrinter printer = new CSVPrinter(writer, format)){
					for(DataQueryRecord record : records) {
						printRecordToCSV(record, printer);
					}
				}
			}
			
		log.info(getThreadId() + ":: Completed writing to file " + dataFile + ".");
	}
	
	public void printRecordToCSV(DataQueryRecord record, CSVPrinter printer) throws IOException {
		record.printToCSV(printer, getConfig().getFields().size());
	}
	
	public List<String> buildCSVHeader() {
		//Dynamically build the CSV header based on the configuration
		List<String> headerStrings = getConfig().getFields().stream().map(f->f.name()).collect(Collectors.toList());
		
		switch (getConfig().getAggregateFunction()) {
		case NONE:
			headerStrings.add("VALUE");
			break;
		case COUNT:
			headerStrings.add("SAMPLE COUNT");
			break;
		default:
			final String functionString = getConfig().getAggregateFunction().name();
			headerStrings.add(functionString+ "(VALUES)");
			break;
		}
		return headerStrings;
	}

	protected abstract String getDataset();
}
