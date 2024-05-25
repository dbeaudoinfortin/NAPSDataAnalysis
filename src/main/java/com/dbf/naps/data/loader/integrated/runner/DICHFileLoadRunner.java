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
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataMapper;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;

public class DICHFileLoadRunner extends IntegratedFileLoadRunner {

	private static final Logger log = LoggerFactory.getLogger(DICHFileLoadRunner.class);
	
	public DICHFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}

	@Override
	protected void processFile() throws Exception {
		
		log.info(getThreadId() + ":: Starting to parse data from Excel workbook " + getRawFile() + ".");
		ExcelSheet sheet = ExcelSheetFactory.createSheet(getRawFile());

		List<IntegratedDataRecord> records = new ArrayList<IntegratedDataRecord>(100);
		
		//Track the site id to read it only once
		Integer siteID = null;
		
		//Flag to track if we have passed all the headers
		boolean dataRowReached = false;
		
		//The first row is skipped. It has information in the form of
		//Dichotomous Sampler Concentrations (ug/m3) at ST. JOHN'S - DUCKWORTH/ORDINANCE NAPS No. 10101
		for (int row = 0; row < sheet.rowCount(); row++) {
			
			//We need to find the actual column header row, which may be the second or third (or more) row
			if(!dataRowReached) {
				String firstCell = sheet.getCellContents(0, row);
				if(!firstCell.toUpperCase().equals("DATE")) continue;
				dataRowReached = true; //We can now start processing data on the next row
				
				//Sanity check. Confirm the last column is the NAPS ID
				String NAPSIDHeader = sheet.getCellContents(sheet.columnCount()-1, row);
				if(!"NAPS ID".equals(NAPSIDHeader.toUpperCase())) {
					throw new IllegalArgumentException("Could not parse NAPS ID using column " + (sheet.columnCount()-1) + " named " + NAPSIDHeader + ". Expected \"NAPS ID\"");
				}
				continue;
			}
			
			IntegratedDataRecord record = new IntegratedDataRecord();
			
			try {
				//First column contains the date in the form of 11-20-84
				Date date = sheet.getCellDate(0, row);
				if(null == date) continue; //We have reached padding at the end of the file
				record.setDatetime(date);
			} catch(NumberFormatException e) {
				log.warn("Expected a date for column 0, row " + row + ". Raw value is: " + sheet.getCellContents(0, row), e);
				continue; //This could be bad data or it could simply be a footer
			}	
			
			//Second column contains the coarse/fine flag
			record.setFine("F".equals(sheet.getCellContents(1, row).toUpperCase()));
			
			//Third column is MASS and is ignored (at least for now)
			
			//Some sheets are broken and are missing data at the end
			//Only read the site id on the first row since it will not change
			if(siteID == null) {
				siteID = getSiteID(sheet.getCellContents(sheet.columnCount()-1, row), row);
			}
			
			//Last column is the NAPS ID
			record.setSiteId(siteID);
			
            for (int col = 3; col < sheet.columnCount() -1; col++) {
            	String columnHeader = sheet.getCellContents(col, 1);
            	
            	//Ignore detection limit (at least for now)
            	if("D.L.".equals(columnHeader.toUpperCase())) continue;
     
            	String cellValue = sheet.getCellContents(col, row);
            	
            	//Ignore empty cells
            	if("".equals(cellValue)) continue;
            	
            	record.setPollutantId(getPollutantID(columnHeader, row));

            	try {
            		Double d = Double.parseDouble(cellValue);
					record.setData(new BigDecimal(d));
				} catch (NumberFormatException e){
					throw new IllegalArgumentException("Invalid raw data (" + cellValue + ") for column " + columnHeader + " on row " + row, e);
				}
            		
            	records.add(record);
            	
            	//Only the pollutant and raw data will change for the next record
            	record = new IntegratedDataRecord(record);
            	
				//Save everything to the database
				//For faster performance partition the list of records into
				if(records.size() == 100) loadRecords(records);
             }
         }

		
		//Might have some records left over
		loadRecords(records);
	}

	
	private void loadRecords(List<IntegratedDataRecord> records) {
		if(records.size() > 0) {
			log.info(getThreadId() + ":: Loading " + records.size() + " records into the database for file " + getRawFile() + ".");
			try(SqlSession session = getSqlSessionFactory().openSession(true)) {
				session.getMapper(IntegratedDataMapper.class).insertIntegratedDataBulk(records);
			} finally {
				records.clear();
			}
		}
	}

}
