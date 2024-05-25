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

public abstract class IntegratedFileLoadRunner extends FileLoadRunner {

	private static final Logger log = LoggerFactory.getLogger(IntegratedFileLoadRunner.class);
	
	public IntegratedFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}
	
	private ExcelSheet sheet;
	private Integer siteID; //Track the site id to read it only once
	private Integer headerRowNumber; //Track when we have reached the real header row
	
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
				String firstCell = sheet.getCellContents(0, row);
				if(!firstCell.toUpperCase().equals("DATE") && !firstCell.toUpperCase().equals("COMPOUND")) continue;
				headerRowNumber = row; //We can now start processing data on the next row
				
				//Sanity check. Confirm the last column is the NAPS ID
				String NAPSIDHeader = sheet.getCellContents(sheet.columnCount()-1, headerRowNumber);
				if(!"NAPS ID".equals(NAPSIDHeader.toUpperCase())) {
					throw new IllegalArgumentException("Could not parse NAPS ID using column " + (sheet.columnCount()-1) + " named " + NAPSIDHeader + ". Expected \"NAPS ID\"");
				}
				continue;
			}
			
			Date date;
			try {
				//First column contains the date in the form of 11-20-84
				date = sheet.getCellDate(0, row);
				if(null == date) continue; //We have reached padding at the end of the file
			} catch(IllegalArgumentException e) {
				log.warn("Expected a date for column 0, row " + row + ". Raw value is: " + sheet.getCellContents(0, row), e);
				continue; //This could be bad data or it could simply be a footer
			}	
			
			//Some sheets are broken and are missing data at the end
			//Only read the site id on the first row since it will not change
			//Last column is the NAPS ID
			if(siteID == null) {
				siteID = getSiteID(sheet.getCellContents(sheet.columnCount()-1, row), row);
			}
			
			records.addAll(processRow(row, date));
			
			//Save everything to the database
			//For faster performance, do it in bulk
			if(records.size() >= 100) loadRecords(records);
         }
		
		//Might have some records left over
		loadRecords(records);
	}
	
	protected abstract List<IntegratedDataRecord> processRow(int row, Date date);

	protected IntegratedDataRecord processSingleRecord(String columnHeader, String cellValue, Date date) {
        	
    	//Ignore empty cells, but not zeros
    	if("".equals(cellValue)) return null;
    	
    	IntegratedDataRecord record = new IntegratedDataRecord();
		record.setDatetime(date);
		record.setSiteId(siteID);
    	record.setPollutantId(getPollutantID(columnHeader));

    	//Treat less-than as zeros (below detection limit)
    	if (cellValue.startsWith("<")) {
    		record.setData(new BigDecimal(0));
    	} else {
    		try {
        		Double d = Double.parseDouble(cellValue);
    			record.setData(new BigDecimal(d));
    		} catch (NumberFormatException e){
    			throw new IllegalArgumentException("Invalid raw data (" + cellValue + ") for column " + columnHeader + ".", e);
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

	protected ExcelSheet getSheet() {
		return sheet;
	}

	protected Integer getSiteID() {
		return siteID;
	}

	protected Integer getHeaderRowNumber() {
		return headerRowNumber;
	}
}
