package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.excel.ExcelSheet;
import com.dbf.excel.ExcelSheetFactory;
import com.dbf.naps.data.db.mappers.IntegratedDataMapper;
import com.dbf.naps.data.db.mappers.SampleMapper;
import com.dbf.naps.data.globals.Constants;
import com.dbf.naps.data.loader.FileLoaderRunner;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.Headers;
import com.dbf.naps.data.records.IntegratedDataRecord;
import com.dbf.naps.data.records.SampleRecord;
import com.dbf.naps.data.utilities.DataCleaner;

/**
 * Base class for the processing of integrated dataset files.
 * This class holds state during processing and is not thread-safe.
 * A new instance of a this class should be used for each dataset file.
 */
public class IntegratedLoaderRunner extends FileLoaderRunner {

	private static final Logger log = LoggerFactory.getLogger(IntegratedLoaderRunner.class);

	//Attributes applying to the entire sheet
	private final Set<String> validDataColumnHeaders = new HashSet<String>(50);
	private final Set<Integer> duplicateDataColumnIndexes = new HashSet<Integer>(5);
	private final String fileType;
	private Integer siteID; //Track the site id to read it only once
	private Integer headerRowNumber; //Track when we have reached the real header row
	private Integer siteIDColumn; //Track when we have reached the NAPS ID which represents the last column
	
	//State that changes during processing
	private ExcelSheet sheet;
	private int row;
	private int col;
	protected Integer sampleId;
	protected String defaultUnits;
	protected int miniumColIndex = 0;

	public IntegratedLoaderRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String fileType, String units) {
		super(threadId, config, sqlSessionFactory, rawFile);
		this.fileType = fileType;
		this.defaultUnits = units;
	}
	
	/**
	 * Main entry-point method for processing the workbook.
	 */
	@Override
	protected void processFile() throws Exception {
		log.info(getThreadId() + ":: Starting to parse data from Excel workbook " + getRawFile() + ".");
		List<ExcelSheet> sheets = ExcelSheetFactory.getSheets(getRawFile(), getMatchingSheetNames(), getExcludedSheetNames());
		log.info(getThreadId() + ":: Found " + sheets.size() + " matching sheet(s).");
		
		for(ExcelSheet sheet : sheets) {
			processSheet(sheet);
		}
	}
	
	/**
	 * Returns a list of all sheet names that explicitly should be processed.
	 * By default this is just the file type. EG. PAH, PM25, CARB, etc.
	 * Provided as a method so that it can be overridden by sub-classes
	 */
	protected List<String> getMatchingSheetNames() {
		return Collections.singletonList(fileType);
	}
	
	/**
	 * Returns a list of all sheet names that should be ignored (not processed).
	 * Provided as a method so that it can be overridden by sub-classes.
	 */
	protected List<String> getExcludedSheetNames() {
		return Headers.DEFAULT_IGNORED_SHEETS;
	}
	
	/**
	 * Allows sub-classes to implement their own logic to determine the method.
	 * By default the method is null, so this does nothing here.
	 */
	protected String getMethod() {
		return null;
	}
	
	/**
	 * Processing of a single sheet of the workbook.
	 */
	protected void processSheet(ExcelSheet sheet) {
		this.sheet = sheet;
		
		//Reset stateful attributes
		validDataColumnHeaders.clear();
		duplicateDataColumnIndexes.clear();
		headerRowNumber = null;
		siteIDColumn = null;
		sampleId = null;
		miniumColIndex = 0;
		
		log.info(getThreadId() + ":: Processing sheet" + (sheet.getName() == null ? "" : " " + sheet.getName()) + ".");
		List<IntegratedDataRecord> records = new ArrayList<IntegratedDataRecord>(100);
		
		//The first row is skipped. It has information in the form of:
		//"Dichotomous Sampler Concentrations (ug/m3) at ST. JOHN'S - DUCKWORTH/ORDINANCE NAPS No. 10101"
		for (row = 0; row < sheet.rowCount(); row++) {
			
			//We need to find the actual column header row, which may be the second or third (or more) row
			if(null == headerRowNumber) { //The header row isn't located yet
				if(!matchesFirstColumnHeaders(sheet.getCellContents(0, row))) continue;
				headerRowNumber = row; //We can now start processing data on the next row
				
				//Validate all of the column headers
				for(int col = sheet.columnCount()-1; col >= 0; col--) {
					String columnHeader = sheet.getCellContents(col, headerRowNumber).trim();
					String columnHeaderUpper = columnHeader.toUpperCase();
					if(!isColumnIgnored(columnHeaderUpper)) {
						//This column should contain data. We will want to read from it.
						if(!validDataColumnHeaders.add(columnHeader)) {
							log.warn("Duplicate data column, index " + col + ", name \"" + columnHeader + "\", for sheet " + sheet.getName() + ", in file " + this.getRawFile());
							duplicateDataColumnIndexes.add(col);
						}
					}
		
					//Sanity check. The last column may not be the NAPS ID. We need to find it.
					if (columnHeaderUpper.equals("NAPS ID") || columnHeaderUpper.equals("NAPS SITE ID")) {
						siteIDColumn = col;
					}
				}
				
				if(null == siteIDColumn) {
					//Some of the PM25 files are just quick summaries and don't contain full data. Add a quick sanity check.
					if(fileType.equals("PM2.5") && sheet.getCellContents(0, 0).toUpperCase().startsWith("SAMPLING DATE")) {
						return;
					}
					throw new IllegalArgumentException("Could not locate the NAPS ID column.");
				}
				
				//Done with header validation, run sheet-wide pre-processing once
				preProcessRows();
				//Ready to process the first row of data
				continue;
			}
			
			int dateColumn = siteIDColumn == 0 ? 1 : 0;
			
			Date date;
			try {
				//First column contains the date in the form of 11-20-84, unless the first column is being used as the NAPS Site ID
				date = sheet.getCellDate(dateColumn, row);
			} catch(IllegalArgumentException e) {
				if(!sheet.getCellContents(dateColumn, row).startsWith("Sampling")) {
					log.error("Expected a date for column " + dateColumn + ", row " + row + ". Raw value is: " + sheet.getCellContents(dateColumn, row));
				}
				continue; //This could be bad data or it could simply be a footer
			}
			
			if(null == date) continue; //We have reached padding at the end of the file
			
			if(date.getTime() < 0 || date.after(new Date())) {
				//Date can't be before 1970, since the data starts in 1972
				throw new IllegalArgumentException("Invalid date \"" +  date + "\" for column " + dateColumn + ", row " + row + ".");
			}
			
			//Some sheets are broken and are missing data at the end
			//Only read the site id on the first row since it will not change
			//Last column is the NAPS ID
			if(siteID == null) {
				String napsID = sheet.getCellContents(siteIDColumn, row);
				
				//Special snowflake
				if (sheet.getName() != null && sheet.getName().toUpperCase().contains("SHOULD BE 90228")) {
					napsID = "90228";
				}
				siteID = getSiteID(napsID, row);
			}
			
			records.addAll(processRow(date));
			
			//Save everything to the database
			//For faster performance, do it in bulk
			if(records.size() >= 200) loadRecordsIntoDB(records);
         }
		
		if(null == headerRowNumber) {
			//Oh no! We never found the header. Either the sheet format is seriously broken or there is a bug in the header detection logic.
			log.error(getThreadId() + ":: Starting to parse data from Excel workbook " + getRawFile() + ".");
			throw new IllegalArgumentException("Could not locate the NAPS ID column.");
		}
		
		//Might have some records left over
		loadRecordsIntoDB(records);
	}
	
	/**
	 * All possible column headers for the first column of data.
	 * This is used to determine when we have passed all of the introductory headers and are ready to process data.
	 * Must be in upper case will be compared with a startsWith().
	 */
	protected String[] getFirstColumnHeaders() {
		 //COMPOUND is sometimes "COMPOUND" and sometimes "COMPOUNDS"
		return new String[] {"COMPOUND","DATE","CONGENER","SAMPLING", "NAPS SITE ID", "NAPS ID"};
	}
	
	/**
	 * Checks if a given cell value (the raw string value) matches any of the possible first column headers.
	 * This is used to determine when we have passed all of the introductory headers and are ready to process data.
	 * Comparisons are done in upper case and using startsWith().
	 * 
	 */
	private boolean matchesFirstColumnHeaders(String cellValue) {
		cellValue = cellValue.toUpperCase();
		for(String firstColumnHeader : getFirstColumnHeaders()) {
			if(cellValue.startsWith(firstColumnHeader)) return true;
		}
		return false;	
	}
	
	/**
	 * Allows sub-classes to insert their own logic to process things that are sheet-wide.
	 * Called only once, after the header row is processed and before any of the data rows are processed.
	 */
	protected void preProcessRows() {}
	
	/**
	 * Create a sample object for each row of each sheet.
	 * Provided as a method so that it can be overridden by sub-classes.
	 * Called  once per data row.
	 * By default the sample is an empty record.
	 */
	protected SampleRecord processSampleRecord() {
		return new SampleRecord();
	}
	
	//Holds the data records used in processRow()
	//This is defined as a class variable to avoid reallocating it every time
	private final List<IntegratedDataRecord> singleRowRecords = new ArrayList<IntegratedDataRecord>(50);
	
	/**
	 * Called once per row to process the data on the provided row.
	 */
	protected List<IntegratedDataRecord> processRow(Date date) {
		sampleId = null; //always reset the sample Id on every row
		
		//Reset the records list
		singleRowRecords.clear();
		
		//Data is expected to start on column 2
		//Last column is NAPS ID and is ignored
        for (col = 1; col < getLastColumn(); col++) {
        	if(ignoreDuplicateColumns() && duplicateDataColumnIndexes.contains(col)) continue;
        	
        	String columnHeader = getSheet().getCellContents(col, getHeaderRowNumber()).trim();
        	if(!validDataColumnHeaders.contains(columnHeader)) continue;
        	
        	IntegratedDataRecord record = processDataRecord(columnHeader, getSheet().getCellContents(col, row), date);
        	if(null != record && (getConfig().isIncludeNulls() || record.getData() != null))
        	{
	        	//Only process the sample record once we are sure we have a valid data record.
	        	//Otherwise we might end up with orphaned sample records.
	        	if(null == sampleId) {
	        		SampleRecord sample = processSampleRecord();
	        		try(SqlSession session = getSqlSessionFactory().openSession(true)) {
	        			session.getMapper(SampleMapper.class).insertSample(sample);
	    			}
	        		sampleId = sample.getId();
	        		if (null == sampleId)
	        			throw new RuntimeException("Failed to insert a new sample record into the database.");
	        	}
	        	record.setSampleId(sampleId);
	        	singleRowRecords.add(record);
        	}
         }
        return singleRowRecords;
	}
	
	/**
	* Allows sub-classes to override the duplicate column behaviour
	* By default we always ignore duplicated data.
	*/
	protected boolean ignoreDuplicateColumns(){
		return true;
	}
	
	/**
	 * Checks if a column should be ignored by checking the column header with the list of ignored headers.
	 * Note: columnHeader should be in all caps
	 * See DEFAULT_IGNORED_HEADERS
	 * 
	 */
	private boolean isColumnIgnored(String columnHeader) {
		if(columnHeader.equals("") || columnHeader.equals("0") || columnHeader.equals("0.0") ) return true;

		//ID is a special case that uses an exact match otherwise we might match 
		//a compound that either start or end with the letter "id"
		if (columnHeader.equals("ID")) return true;
		
		for(String ignoredHeader : getIgnoredColumnList()) {
			if (columnHeader.startsWith(ignoredHeader) || columnHeader.endsWith(ignoredHeader)) return true;
		}
		return false;
	}
	
	/**
	 * Returns a List of Strings that contain all of the column headers for columns that should be ignored.
	 * Provided as a method so that it can be overridden by sub-classes.
	 * All entries must be in upper case.
	 * All entries will be compared with a startsWith() and endsWith().
	 * Blank columns headers are automatically ignored and should not be included. 
	 * For the default list of ignored headers see DEFAULT_IGNORED_HEADERS
	 */
	protected List<String> getIgnoredColumnList() {
		return Headers.DEFAULT_IGNORED_HEADERS;
	}

	/**
	 * Called once per cell to process the raw data and produce an IntegratedDataRecord.
	 */
	protected IntegratedDataRecord processDataRecord(String columnHeader, String cellValue, Date date) {
        
		//Ignore empty headers. These are blank columns used as separators.
    	if(columnHeader.isEmpty()) return null;
    	
    	cellValue = cellValue.trim();

    	//Looks like someone may have copied and pasted a bad formula from another spreadsheet ☺
    	if(cellValue.startsWith("ERROR")) {
    		log.warn(getThreadId() + ":: Bad data on row " + row + " for column " + columnHeader + " (" + cellValue + ") in file " + getRawFile() + ".");
    		return null;
    	}

    	IntegratedDataRecord record = new IntegratedDataRecord();
		record.setDatetime(date);
		record.setSiteId(siteID);
    	record.setPollutantId(getPollutantID(columnHeader));

    	//Ignore empty cells, but not zeros
    	if(!"N.M.".equals(cellValue)) {
	    	//Treat less-than as zeros (below detection limit)
	    	if (cellValue.startsWith("<") || "N.D.".equals(cellValue)) {
	    		record.setData(new BigDecimal(0));
	    	} else {
	    		try {
	    			record.setData(DataCleaner.extractDecimalData(cellValue, false)); //Set ignore error to false so we get the full exception details
	    		} catch (IllegalArgumentException e){
	    			log.warn(getThreadId() + ":: Invalid raw data (" + cellValue + ") on row " + row + " for column " + columnHeader + ", in file " + getRawFile() + ".", e.getMessage());
	    			return null; //We still want to try processing subsequent records. Don't throw an exception.
	    		}
	    	}
    	}
    	
    	//We want to standardise on µg/m³, otherwise we won't be able to aggregate data for reporting
    	String units = getUnits();
    	if("ng/m³".equals(units)) {
    		//It's normal for the data to be null here. It means there was no measurement
    		if(record.getData() != null) {
    			record.setData(record.getData().divide(Constants.bigDecimal1000));
    		}
    		units = "µg/m³";
    	}
    	record.setMethodId(getMethodID("Integrated", fileType, getMethod(), units));
        return record;
	}
	
	/**
	 * Inserts or updates the provided IntegratedDataRecord records into the database.
	 */
	protected void loadRecordsIntoDB(List<IntegratedDataRecord> records) {
		if(records.size() > 0) {
			log.info(getThreadId() + ":: Loading " + records.size() + " records into the database for file " + getRawFile() + ".");
			try(SqlSession session = getSqlSessionFactory().openSession(true)) {
				session.getMapper(IntegratedDataMapper.class).insertIntegratedDataBulk(records);
			} finally {
				records.clear();
			}
		}
	}
	
	/**
	 * Determine a data column's index based on the provided column header.
	 * This uses a startsWith() approach to comparisons because some columns have variations, like "TSP" and "TSP (µg/m³)"
	 * Returns null if the column cannot is not found.
	 */
	protected Integer getColumnIndex(String... matchingColumnHeaders) {
		return getColumnIndex(false, matchingColumnHeaders);
	}
	
	/**
	 * Determine a data column's index based on the provided column header.
	 * Returns null if the column cannot is not found.
	 * @param exact - true means an equals() approach is used. false means a startsWith() approach is used
	 */
	protected Integer getColumnIndex(boolean exact, String... matchingColumnHeaders) {
		for(String matchingColumnHeader : matchingColumnHeaders) {
			matchingColumnHeader = matchingColumnHeader.toUpperCase();
			for(int col = miniumColIndex; col < sheet.columnCount(); col++) {
				String columnHeader = sheet.getCellContents(col, getHeaderRowNumber()).toUpperCase();
				if((exact && columnHeader.equals(matchingColumnHeader)) || (!exact && columnHeader.startsWith(matchingColumnHeader))) {
					return col;
				}
			}
		}
		log.debug("Could not locate a data column with the names \"" + matchingColumnHeaders + "\".");
		return null;
	}

	protected ExcelSheet getSheet() {
		return sheet;
	}

	protected Integer getSiteID() {
		return siteID;
	}

	protected Integer getHeaderRowNumber() {
		return headerRowNumber;
	}

	protected Integer getLastColumn() {
		//When the NAPS Site ID column is at the end then it is considered the end of valid data
		//Otherwise, we have no good way of knowing when the data ends, so we assume the last column
		return siteIDColumn < 2 ? sheet.columnCount()-1 : siteIDColumn;
	}
	
	protected int getRow() {
		return row;
	}
	
	protected int getColumn() {
		return col;
	}
	
	protected String getUnits() {
		return defaultUnits;
	}
}
