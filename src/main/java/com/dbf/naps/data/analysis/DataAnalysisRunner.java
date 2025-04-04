package com.dbf.naps.data.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.FileRunner;
import com.dbf.naps.data.analysis.query.json.JsonListRecord;
import com.dbf.naps.data.analysis.query.json.JsonMapRecord;
import com.dbf.naps.data.analysis.query.json.JsonMultiRecord;
import com.dbf.naps.data.analysis.query.json.JsonRecord;
import com.dbf.naps.data.analysis.query.json.JsonReport;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.globals.DayOfWeekMapping;
import com.dbf.naps.data.globals.MonthMapping;
import com.dbf.naps.data.globals.ProvTerr;
import com.dbf.naps.data.globals.ProvinceTerritoryMapping;
import com.dbf.naps.data.globals.SiteType;
import com.dbf.naps.data.globals.SiteTypeMapping;
import com.dbf.naps.data.globals.Urbanization;
import com.dbf.naps.data.globals.UrbanizationMapping;
import com.dbf.naps.data.utilities.Utils;
import com.dbf.utils.stacktrace.StackTraceCompactor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class DataAnalysisRunner<O extends DataAnalysisOptions> extends FileRunner<O> {
	
	private static final Logger log = LoggerFactory.getLogger(DataAnalysisRunner.class);
	
	private static final int JSON_SLIM_DATA_PRECISION = 4;
	private static final Gson gsonPretty  = new GsonBuilder().setPrettyPrinting().create();
	private static final Gson gsonCompact = new GsonBuilder().create();
	
	public DataAnalysisRunner(int threadId, O config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}
	
	@Override
	public void run() {
		String queryUnits = null;
		String title = null;
		List<DataAnalysisRecord> records = null;
		try {
			checkFile();
			
			queryUnits = determineUnits();
			if(null == queryUnits) return; //We have determine that there is no data or the units are mixed
			if(!queryUnits.isEmpty()) {
				log.info(getThreadId() + ":: Will use the units " + queryUnits + " for the file " + getDataFile() + ".");
			}
			
			title = getReportTitle(queryUnits, true);
			log.info(getThreadId() + ":: Will use the report title \"" + title + "\" for the file " + getDataFile() + ".");
			
			records = queryData();
			if(records.isEmpty()) return;
		} catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR executing data query for file " + getDataFile() + ".\n" + StackTraceCompactor.getCompactStackTrace(t));
			return;
		}
		
		try {
			writeToFile(records, queryUnits, title, getDataFile());
		 } catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR writing to file " + getDataFile() + ".\n" + StackTraceCompactor.getCompactStackTrace(t));
			return;
		 }
	}
	
	private String determineUnits() {
		log.info(getThreadId() + ":: Determining units for file " + getDataFile() + ".");
		//Only allow mixing unit if we are not aggregating data, or we are grouping by pollutant, or we are calculating AQHI (which uses mixed units by design).
		//Note: The schema makes it possible for two different data points measuring the same pollutant to use two different methods with two different unit.
		//However, in practise the units are consistent for all of the pollutants.
		if(!getConfig().isAQHI() 
				&& !getConfig().getAggregateFunction().equals(AggregateFunction.COUNT)
				&& !getConfig().getAggregateFunction().equals(AggregateFunction.NONE) 
				&& getConfig().getFields().stream().filter(f->f.equals(AggregationField.POLLUTANT)).count() == 0) {
			
			Collection <Integer> years = getSpecificYear() != null ? List.of(getSpecificYear()) : Utils.getYearList(getConfig().getYearStart(), getConfig().getYearEnd());
			Collection <String> pollutants = getSpecificPollutant() != null ? List.of(getSpecificPollutant()) : getConfig().getPollutants();
			Collection <Integer> sites = getSpecificSite() != null ? List.of(getSpecificSite()) : getConfig().getSites();
			
			try(SqlSession session = getSqlSessionFactory().openSession(true)) {	
				List<String> allUnits = session.getMapper(DataMapper.class).getDistinctUnits(
					//Per-file filters
					years, pollutants, sites,
					//Method filters
					getConfig().getMethods(), getConfig().getReportTypes(),
					//Basic filters
					getConfig().getMonths(), getConfig().getDaysOfMonth(), getConfig().getDaysOfWeek(),
					getConfig().getSiteName(), getConfig().getCityName(),
					getConfig().getProvTerr().stream().map(p->p.name()).toList(),
					//Advanced site filters
					getConfig().getSiteType().stream().map(s->s.name()).toList(),
					getConfig().getUrbanization().stream().map(u->u.name()).toList(),
					//Advanced data filters
					getConfig().getValueUpperBound(), getConfig().getValueLowerBound(),
					//Continuous vs. Integrated
					getDataset());
				
				if(allUnits.size() == 0) {
					log.info(getThreadId() + ":: No data records found for " + getDataFile() + ". Skipping file.");
					return null;
				}
				else if(allUnits.size() > 1) {
					log.warn(getThreadId() + ":: WARNING: Cannot aggregate data with mixed units. Make sure all the selected pollutants are measured using the same units.");
					return null;
				}
				return allUnits.get(0);
			}
		}
		return "";
	}
	
	private List<DataAnalysisRecord> queryData() {
		log.info(getThreadId() + ":: Starting data query for file " + getDataFile() + ".");
		
		List<DataAnalysisRecord> records;
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
	
	public List<DataAnalysisRecord> runQuery(SqlSession session){
		
		Collection <Integer> years = getSpecificYear() != null ? List.of(getSpecificYear()) : Utils.getYearList(getConfig().getYearStart(), getConfig().getYearEnd());
		Collection <String> pollutants  = getSpecificPollutant() != null ? List.of(getSpecificPollutant()) : getConfig().getPollutants();
		Collection <Integer> sites = getSpecificSite() != null ? List.of(getSpecificSite()) : getConfig().getSites();
		
		return session.getMapper(DataMapper.class).getQueryData(
				//Grouping	
				getConfig().getFields(), getConfig().getAggregateFunction(),
				//Per-file filters
				years, pollutants, sites,
				//Method filters
				getConfig().getMethods(), getConfig().getReportTypes(),
				//Basic filters
				getConfig().getMonths(), getConfig().getDaysOfMonth(), getConfig().getDaysOfWeek(),
				getConfig().getSiteName(), getConfig().getCityName(),
				getConfig().getProvTerr().stream().map(p->p.name()).toList(),
				//Advanced site filters
				getConfig().getSiteType().stream().map(s->s.name()).toList(),
				getConfig().getUrbanization().stream().map(u->u.name()).toList(),
				//Advanced data filters
				getConfig().getValueUpperBound(), getConfig().getValueLowerBound(),
				//Additional Columns
				false, false, false,
				//Having conditions
				getConfig().getResultUpperBound(), getConfig().getResultLowerBound(), getConfig().getMinSampleCount(),
				//Continuous vs. Integrated
				getDataset(),
				//AQHI
				getConfig().isAQHI());
	}
	
	protected String getReportTitle(String units, boolean longTitle) {
		
		//Predefined custom title"
		if(getConfig().getTitle() != null) {
			//Empty string is perfectly fine
			return getConfig().getTitle();
		}
		
		StringBuilder title = new StringBuilder();
		
		if(AggregateFunction.COUNT.equals(getConfig().getAggregateFunction())) {
			if(getConfig().isAQHI()) {
				title.append("Count of AQHI Records");
			} else {
				title.append("Number of Samples");
			}
		} else {
			switch(getConfig().getAggregateFunction()) {
			case COUNT: //Handled above
				break;
			case AVG:
				title.append("Average ");
				break;
			case MAX:
				title.append("Maximum ");
				break;
			case MIN:
				title.append("Minimum ");
				break;
			case SUM:
				title.append("Sum of ");
				break;
			case P50:
				title.append("50th Percentile of ");
				break;
			case P95:
				title.append("95th Percentile of ");
				break;
			case P98:
				title.append("98th Percentile of ");
				break;
			case P99:
				title.append("99th Percentile of ");
				break;
			case NONE:
				break; //Handled below
			}
			if(getConfig().isAQHI()) {
				title.append("AQHI");
			} else {
				title.append("Concentration");
			}
		}
		
		if(!getConfig().isAQHI() && units != null && !units.isEmpty()
				&& !getConfig().getAggregateFunction().equals(AggregateFunction.NONE)
				&& !getConfig().getAggregateFunction().equals(AggregateFunction.COUNT)) {
			title.append(" (");
			title.append(units);
			
			if(getConfig().getMethods() != null && !getConfig().getMethods().isEmpty()) {
				title.append(" by ");
				Utils.prettyPrintStringList(getConfig().getMethods(), title);
			}
			title.append(")");
		}
		
		if(!getConfig().isAQHI()) {
			title.append(" of ");
			
			if(getSpecificPollutant() != null) {
				title.append(getSpecificPollutant());
			} else if(getConfig().getPollutants() == null || getConfig().getPollutants().isEmpty()) {
				title.append("All Pollutants");
			} else {
				Utils.prettyPrintStringList(getConfig().getPollutants(), title);
			}
		}
		
		title.append(" for ");
		
		if(getSpecificSite() != null) {
			//Adjectives don't matter if there is only one site.
			//If the site doesn't match the adjectives then there will be no data and thus no report
			title.append("NAPS Site ");
			title.append(getSpecificSite());
		} else if (getConfig().getSites() == null || getConfig().getSites().isEmpty()) {
			//Now the adjectives matter
			if(getConfig().getSites() == null || getConfig().getSites().isEmpty()) {
				title.append("All ");
			}
			
			boolean needSpace = false;
			if(getConfig().getUrbanization() != null && !getConfig().getUrbanization().isEmpty() && getConfig().getUrbanization().size() != Urbanization.values().length) {
				Utils.prettyPrintStringList(UrbanizationMapping.getUrbanizationStrings(getConfig().getUrbanization()), title, false);
				needSpace = true;
			}
			
			if(getConfig().getSiteType() != null && !getConfig().getSiteType().isEmpty() && getConfig().getSiteType().size() != SiteType.values().length) {
				if(needSpace) title.append(", ");
				Utils.prettyPrintStringList(SiteTypeMapping.getSiteTypeStrings(getConfig().getSiteType()), title, false);
				needSpace = true; //Regardless, we still need a space afterwards
			}
			
			if(needSpace) title.append(" ");
			title.append("NAPS Sites");
		} else if(getConfig().getSites().size() == 1) {
			title.append("NAPS Site ");
			title.append(getConfig().getSites().iterator().next());
		} else {
			//I'm not sure it makes to sense to have adjectives here. Why would someone explicitly define a list of site IDs but then want to filter the sites down further?
			//Regardless, it makes for a very awkward English sentence, so I'll omit it.
			title.append("NAPS Sites ");
			Utils.prettyPrintStringList(getConfig().getSites().stream().sorted().map(s->s.toString()).toList(), title, false);
		}

		if(getConfig().getSiteName() != null && !getConfig().getSiteName().isEmpty()) {
			title.append(" Named \"");
			title.append(getConfig().getSiteName());
			title.append("\"");
		}
		
		if(getConfig().getCityName() != null && !getConfig().getCityName().isEmpty()) {
			title.append(" From the Town/City \"");
			title.append(getConfig().getCityName());
			title.append("\"");
		}
		
		if(getConfig().getProvTerr() != null && !getConfig().getProvTerr().isEmpty() && getConfig().getProvTerr().size() != ProvTerr.values().length) {
			if(longTitle) {
				//Not sure why I need this part before the province name
				title.append(" in the ");
				if(getConfig().getProvTerr().size() == 1) {
					title.append("Province/Territory of ");
				} else {
					title.append("Provinces/Territories of ");
				}
			} else {
				title.append(" in ");
			}
			
			Utils.prettyPrintStringList(ProvinceTerritoryMapping.getProvTerrStrings(getConfig().getProvTerr()), title, false);
		}
		
		title.append(", Spanning ");
		
		if(getConfig().getDaysOfWeek() != null && !getConfig().getDaysOfWeek().isEmpty() && getConfig().getDaysOfWeek().size() != 7){
			Utils.prettyPrintStringList(DayOfWeekMapping.getDayOfWeekStrings(getConfig().getDaysOfWeek()), title, false);
			title.append(" of ");
		}
		
		if(getConfig().getDaysOfMonth() != null && !getConfig().getDaysOfMonth().isEmpty() && getConfig().getDaysOfMonth().size() != 31){
			title.append("Day");
			if(getConfig().getDaysOfMonth().size() > 1) title.append("s");
			title.append(" ");
			Utils.prettyPrintStringList(getConfig().getDaysOfMonth().stream().sorted().map(d->d.toString()).toList(), title, false);
			title.append(" of ");
		}
		
		if(getConfig().getMonths() != null && !getConfig().getMonths().isEmpty() && getConfig().getMonths().size() != 12){
			Utils.prettyPrintStringList(MonthMapping.getMonthStrings(getConfig().getMonths()), title, false);
			title.append(" of ");
		}
		
		if(getSpecificYear() != null) {
			title.append("the Year ");
			title.append(getSpecificYear());
		} else if(Utils.isAllYears(getConfig().getYearStart(), getConfig().getYearEnd())) {
			title.append("All Years");
		} else if(getConfig().getYearStart() == getConfig().getYearEnd()) {
			title.append("the Year ");
			title.append(getConfig().getYearStart());
		} else {
			title.append("the Years ");
			title.append(getConfig().getYearStart());
			title.append(" to ");
			title.append(getConfig().getYearEnd());
		}
		
		if(longTitle) {
			if(getConfig().getFields() != null && !getConfig().getFields().isEmpty()) {
				title.append(", Grouped by ");
				Utils.prettyPrintStringList(getConfig().getFields().stream().map(f->f.getPrettyName()).toList(), title, false);
			}
		}

		return title.toString();
	}
	
	protected void writeToFile(List<DataAnalysisRecord> records, String queryUnits, String title, File dataFile) throws IOException {
		writeToCSVFile(records, queryUnits, title, dataFile);	
	}
	
	protected void writeToCSVFile(List<DataAnalysisRecord> records, String queryUnits, String title, File dataFile) throws IOException {
		log.info(getThreadId() + ":: Starting writing to CSV file " + dataFile + ".");
		List<String> headers = buildCSVHeader();
		CSVFormat format = CSVFormat.EXCEL
				.builder()
				.setTrim(false)
				.setHeader(headers.toArray(new String[headers.size()]))
				.setSkipHeaderRecord(false)
				.build();
		try(BufferedWriter writer = Files.newBufferedWriter(dataFile.toPath(), StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
			writer.write('\ufeff'); //Manually print the UTF-8 BOM
			try (CSVPrinter titlePrinter = new CSVPrinter(writer, CSVFormat.EXCEL)) { //No header!
		        if(title != null && !title.isEmpty()) {
		        	titlePrinter.printRecord(title);
		        	writer.newLine();
		        }
		        try(CSVPrinter printer = new CSVPrinter(writer, format)){
					for(DataAnalysisRecord record : records) {
						printRecordToCSV(record, printer);
					}
				}
		    }
		}
		log.info(getThreadId() + ":: Completed writing to CSV file " + dataFile + ".");
	}
	
	protected void writeToJSONFile(List<DataAnalysisRecord> records, String queryUnits, String title, File dataFile, boolean slim) throws IOException {
		log.info(getThreadId() + ":: Starting writing to JSON file " + dataFile + ".");
		
		String jsonOutput = null;
		if(slim) {
			jsonOutput = gsonCompact.toJson(writeToSlimJsonFile(records, dataFile));
		} else {
			jsonOutput = gsonPretty.toJson(writeToFatJsonFile(records, queryUnits, title, dataFile));
		}
		
		if(getConfig().isVerbose()) {
			log.info(getThreadId() + ":: Converted data to JSON:\n" + (jsonOutput.length() > 2000 ? jsonOutput.substring(0,2000) : jsonOutput));
		}
		
		FileUtils.writeStringToFile(dataFile, jsonOutput, Charset.defaultCharset());
		log.info(getThreadId() + ":: Completed writing to JSON file " + dataFile + ".");
	}

	//TODO: Great candidate for a unit test
	@SuppressWarnings("unchecked")
	private Object writeToSlimJsonFile(List<DataAnalysisRecord> records, File dataFile) throws IOException {
		final int fieldCount = getConfig().getFields().size();
		
		if(fieldCount == 0) {  //There is no top-level aggregation
			if(records.size() == 1) {
				return records.get(0).getPreciseValue(JSON_SLIM_DATA_PRECISION); //Since we are aggregating with no group, there us only one value
			}
			return records.stream().map(r->r.getPreciseValue(JSON_SLIM_DATA_PRECISION)).toList();
		} 
			
		final HashMap<Object, Object> baseJSON = new HashMap<Object, Object>();
		for(DataAnalysisRecord dataRecord: records) {
			//Reset to the tree root of the data for each data record
			Object jsonRecord = baseJSON;
			
			//Iterate through all of the layers of the tree until we find the leaf node,
			//which is a List that we can add our data to.
			for(int fieldIndex = 0; fieldIndex <= fieldCount; fieldIndex++) {
				if(fieldIndex == fieldCount) {
					//We have reached the leaf node and we are ready to add the value to the list
					((ArrayList<Object>) jsonRecord).add(dataRecord.getPreciseValue(JSON_SLIM_DATA_PRECISION));
				} else {
					final Object dataField = dataRecord.getField(fieldIndex);
					//There is another layer of grouping and we need to locate the sub map
					final Map<Object, Object> jsonMapRecord = ((Map<Object, Object>) jsonRecord);
					jsonRecord = jsonMapRecord.get(dataField);
					if(null == jsonRecord) {
						//The sub-map doesn't exist because this is time we have seen this data group
						if(fieldIndex == fieldCount -1)  {
							//This is the last grouping so we need to put the actual data
							jsonMapRecord.put(dataField, dataRecord.getPreciseValue(JSON_SLIM_DATA_PRECISION)); //Don't include too many digits);
							break;
						} 
						//There are more levels of grouping, create a sub-map
						jsonRecord = new HashMap<Object, Object>();
						jsonMapRecord.put(dataField, jsonRecord);
					}
				}
			}
		}
		return baseJSON;
	}
	
	//TODO: Great candidate for a unit test
	@SuppressWarnings("unchecked")
	private Object writeToFatJsonFile(List<DataAnalysisRecord> records, String queryUnits, String title, File dataFile) throws IOException {
		final JsonReport baseJSON = new JsonReport();
		if(StringUtils.isNotEmpty(title)) baseJSON.setTitle(title);
		if(StringUtils.isNotEmpty(queryUnits)) baseJSON.setUnits(queryUnits);
		
		final List<AggregationField> fields = getConfig().getFields();
		final int fieldCount = fields.size();
		
		if(fieldCount == 0) { //There is no top-level aggregation
			if(records.size() == 1) { //There is only a single data point
				baseJSON.setData(records.get(0).toJsonSingleRecord(getValueFieldName())); //Since we are aggregating with no grouping, there us only one value
			} else {
				baseJSON.setData(new JsonListRecord(getValueFieldName(), records.stream().map(r->r.toJsonSingleRecord()).toList()));
			}
			return baseJSON;
		}
		
		baseJSON.setData(new JsonMapRecord<Object>(fields.get(0).name()));
		for(DataAnalysisRecord dataRecord: records) {
			//Reset to the tree root of the data for each data record
			JsonRecord jsonRecord = baseJSON.getData();
			
			//Iterate through all of the layers of the tree until we find the leaf node,
			//which is a List that we can add our data to.
			for(int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
				final Object dataField = dataRecord.getField(fieldIndex);
				//There is another layer of grouping and we need to locate the sub map
				final Map<Object, JsonRecord> jsonMapRecord = ((JsonMapRecord<Object>) jsonRecord).getValues();
				jsonRecord = (JsonMultiRecord) jsonMapRecord.get(dataField);
				if(null == jsonRecord) {
					//The sub-map doesn't exist because this is time we have seen this data group
					if(fieldIndex == fieldCount -1)  {
						//This is the last grouping so we need to set the actual data
						jsonRecord = dataRecord.toJsonSingleRecord(getValueFieldName());
					} else {
						//There are more levels of grouping, create a sub-map
						jsonRecord = new JsonMapRecord<Object>(fields.get(fieldIndex+1).name());
					}
					jsonMapRecord.put(dataField, jsonRecord);
				}
			}
		}
		return baseJSON;	
	}
	
	protected void printRecordToCSV(DataAnalysisRecord record, CSVPrinter printer) throws IOException {
		record.printToCSV(printer, getConfig().getFields().size());
	}
	
	protected List<String> buildCSVHeader() {
		//Dynamically build the CSV header based on the configuration
		List<String> headerStrings = getConfig().getFields().stream().map(f->f.name()).collect(Collectors.toList());
		headerStrings.add(getValueFieldName());
		return headerStrings;
	}
	
	private String getValueFieldName() {
		switch (getConfig().getAggregateFunction()) {
		case NONE:
			return "VALUE";
		case COUNT:
			return "SAMPLE COUNT";
		default:
			final String functionString = getConfig().getAggregateFunction().name();
			return functionString+ "(VALUES)";
		}
	}

	protected abstract String getDataset();
}
