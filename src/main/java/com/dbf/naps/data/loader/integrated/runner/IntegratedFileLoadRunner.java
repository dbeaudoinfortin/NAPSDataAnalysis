package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.excel.ExcelSheet;
import com.dbf.excel.ExcelSheetFactory;
import com.dbf.naps.data.loader.FileLoadRunner;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataMapper;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;
import com.dbf.naps.data.utilities.DataCleaner;

public abstract class IntegratedFileLoadRunner extends FileLoadRunner {

	private static final Logger log = LoggerFactory.getLogger(IntegratedFileLoadRunner.class);
	
	private static final List<String> DEFAULT_IGNORED_HEADERS = new ArrayList<String>();
	
	static {
		DEFAULT_IGNORED_HEADERS.add("%"); //% Recovery
		DEFAULT_IGNORED_HEADERS.add("SAMPLE"); //Sample Volume, Sample Type, Sample Date & Sample ID
		DEFAULT_IGNORED_HEADERS.add("TSP"); //Total suspended particles
		DEFAULT_IGNORED_HEADERS.add("T.S.P"); //Total suspended particles
		DEFAULT_IGNORED_HEADERS.add("D.L."); //Detection limit
		DEFAULT_IGNORED_HEADERS.add("TOTAL"); //TOTAL PAH
		DEFAULT_IGNORED_HEADERS.add("C/F"); //Coarse/Fine
		DEFAULT_IGNORED_HEADERS.add("MASS"); //Sample Mass
		DEFAULT_IGNORED_HEADERS.add("SURROGATE"); //Surrogate Recovery
		DEFAULT_IGNORED_HEADERS.add("48 H"); //Not sure why this is a column
		DEFAULT_IGNORED_HEADERS.add("CANISTER"); //Canister ID#
		DEFAULT_IGNORED_HEADERS.add("START"); //Start Time
		DEFAULT_IGNORED_HEADERS.add("DURATION"); //Duration
	}
	
	public IntegratedFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}
	
	private ExcelSheet sheet;
	private Integer siteID; //Track the site id to read it only once
	private Integer headerRowNumber; //Track when we have reached the real header row
	private Integer lastColumn; //Track when we have reached the NAPS ID which represents the last column
	
	@Override
	protected void processFile() throws Exception {
		log.info(getThreadId() + ":: Starting to parse data from Excel workbook " + getRawFile() + ".");
		sheet = ExcelSheetFactory.createSheet(getRawFile());

		List<IntegratedDataRecord> records = new ArrayList<IntegratedDataRecord>(100);
		
		//The first row is skipped. It has information in the form of
		//Dichotomous Sampler Concentrations (ug/m3) at ST. JOHN'S - DUCKWORTH/ORDINANCE NAPS No. 10101
		for (int row = 0; row < sheet.rowCount(); row++) {
			
			//We need to find the actual column header row, which may be the second or third (or more) row
			if(null == headerRowNumber) {
				if(!matchesFirstColumnHeaders(sheet.getCellContents(0, row))) continue;
				headerRowNumber = row; //We can now start processing data on the next row
				
				//Sanity check. The last column may not be the NAPS ID. We need to confirm it.
				for(int col = sheet.columnCount()-1; col > 2; col--) {
					if (sheet.getCellContents(col, headerRowNumber).equals("NAPS ID")) {
						lastColumn = col;
						break;
					}
				}
				if(null == lastColumn) throw new IllegalArgumentException("Could not locate the NAPS ID column.");
				
				//Done with header validation, ready to process the first row of data
				continue;
			}
			
			Date date;
			try {
				//First column contains the date in the form of 11-20-84
				date = sheet.getCellDate(0, row);
				if(null == date) continue; //We have reached padding at the end of the file
			} catch(IllegalArgumentException e) {
				log.warn("Expected a date for column 0, row " + row + ". Raw value is: " + sheet.getCellContents(0, row));
				continue; //This could be bad data or it could simply be a footer
			}	
			
			//Some sheets are broken and are missing data at the end
			//Only read the site id on the first row since it will not change
			//Last column is the NAPS ID
			if(siteID == null) {
				siteID = getSiteID(sheet.getCellContents(lastColumn, row), row);
			}
			
			records.addAll(processRow(row, date));
			
			//Save everything to the database
			//For faster performance, do it in bulk
			if(records.size() >= 100) loadRecords(records);
         }
		
		if(null == headerRowNumber) {
			//Oh no! We never found the header. Either the sheet format is seriously broken or there is a bug in the header detection logic.
			log.error(getThreadId() + ":: Starting to parse data from Excel workbook " + getRawFile() + ".");
			throw new IllegalArgumentException("Could not locate the NAPS ID column.");
		}
		
		//Might have some records left over
		loadRecords(records);
	}
	
	/*
	 * All possible column headers for the first column of data.
	 * This is used to determine when we have passed all of the introductory headers and are ready to process data
	 * Must be in upper case will be compared with a startsWith()
	 */
	protected String[] getFirstColumnHeaders() {
		return new String[] {"COMPOUND","DATE"}; //This is sometimes COMPOUND and sometimes COMPOUNDS
	}
	
	private boolean matchesFirstColumnHeaders(String cellValue) {
		cellValue = cellValue.toUpperCase();
		for(String firstColumnHeader : getFirstColumnHeaders()) {
			if(cellValue.startsWith(firstColumnHeader)) return true;
		}
		return false;	
	}
	
	protected List<IntegratedDataRecord> processRow(int row, Date date){
		List<IntegratedDataRecord> records = new ArrayList<IntegratedDataRecord>(50);
		
		//Data is expected to start on column 2
		//Last column is NAPS ID and is ignored
        for (int col = 1; col < getLastColumn(); col++) {
        	String columnHeader = getSheet().getCellContents(col, getHeaderRowNumber());
        	if(isColumnIgnored(columnHeader)) continue;
        	
        	IntegratedDataRecord record = processSingleRecord(getSheet().getCellContents(col, getHeaderRowNumber()), getSheet().getCellContents(col, row), date);
        	if(null != record) records.add(record);
         }
        return records;
	}
	
	private boolean isColumnIgnored(String columnHeader) {
		columnHeader = columnHeader.toUpperCase();
		for(String ignoredHeader : getIgnoredColumnList()) {
			if (columnHeader.startsWith(ignoredHeader)) return true;
		}
		return false;
	}
	
	/*
	 * Returns a List of Strings that contain all of the column headers for columns that should be ignored.
	 * All entries must be in upper case
	 * All entries will be compared with a startsWith()
	 * Blank columns headers are automatically ignored and should not be included. 
	 */
	protected List<String> getIgnoredColumnList() {
		return DEFAULT_IGNORED_HEADERS;
	}

	protected IntegratedDataRecord processSingleRecord(String columnHeader, String cellValue, Date date) {
        
		//Ignore empty headers. These are blank columns used as separators.
    	if("".equals(columnHeader)) return null;
    	
    	cellValue = cellValue.trim();
    	
    	//Ignore empty cells, but not zeros
    	if("".equals(cellValue) || "N.M.".equals(cellValue)|| "-".equals(cellValue)) return null;
    	
    	//Looks like someone may have copied and pasted a bad formula from another spreadsheet ☺
    	if(cellValue.startsWith("ERROR")) {
    		log.warn(getThreadId() + ":: Bad data for column " + columnHeader + " (" + cellValue + ") in file " + getRawFile() + ".");
    		return null;
    	}
    
    	IntegratedDataRecord record = new IntegratedDataRecord();
		record.setDatetime(date);
		record.setSiteId(siteID);
    	record.setPollutantId(getPollutantID(columnHeader));

    	//Treat less-than as zeros (below detection limit)
    	if (cellValue.startsWith("<") || "N.D.".equals(cellValue)) {
    		record.setData(new BigDecimal(0));
    	} else {
    		try {
    			record.setData(DataCleaner.extractDecimalData(cellValue, false)); //Set ignore error to false so we get the full exception details
    		} catch (IllegalArgumentException e){
    			log.error(getThreadId() + ":: Invalid raw data (" + cellValue + ") for column " + columnHeader + ", in file " + getRawFile() + ".", e);
    			return null; //We still want to try processing subsequent records. Don't throw an exception.
    		}
    	}
    	
        return record;
	}
	
	protected void loadRecords(List<IntegratedDataRecord> records) {
		if(records.size() > 0) {
			log.info(getThreadId() + ":: Loading " + records.size() + " records into the database for file " + getRawFile() + ".");
			try(SqlSession session = getSqlSessionFactory().openSession(true)) {
				session.getMapper(IntegratedDataMapper.class).insertIntegratedDataBulk(records);
			} finally {
				records.clear();
			}
		}
	}
	
	protected Integer getColumnIndex(String... columnHeaders) {
		for(String columnHeader : columnHeaders) {
			columnHeader = columnHeader.toUpperCase();
			for(int col = 0; col < sheet.columnCount(); col++) {
				//Do a startsWith() check because some columns have variations, like "TSP" and "TSP (µg/m³)"
				if(sheet.getCellContents(col, getHeaderRowNumber()).toUpperCase().startsWith(columnHeader)) {
					return col;
				}
			}
		}
		throw new IllegalArgumentException("Could not locate a data column with the names \"" + columnHeaders + "\".");
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
		return lastColumn;
	}
}
