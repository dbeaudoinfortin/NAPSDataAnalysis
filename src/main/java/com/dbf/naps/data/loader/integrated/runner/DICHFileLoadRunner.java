package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.DateUtil;
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
	
	//Note: SimpleDateFormat is not thread safe, must not be static
	private final SimpleDateFormat EARLY_DATE_FORMAT = new SimpleDateFormat("MM-dd-YY");
	
	@Override
	protected void processFile() throws Exception {
		
		log.info(getThreadId() + ":: Starting to parse data from Excel workbook " + getRawFile() + ".");
		ExcelSheet sheet = ExcelSheetFactory.createSheet(getRawFile());
		
		//Sanity check. Confirm the last column is the NAPS ID
		String NAPSIDHeader = sheet.getCellContents(sheet.columnCount()-1, 1);
		if(!"NAPS ID".equals(NAPSIDHeader.toUpperCase())) {
			throw new IllegalArgumentException("Could not parse NAPS ID using column " + (sheet.columnCount()-1) + " named " + NAPSIDHeader + ". Expected \"NAPS ID\"");
		}
		
		List<IntegratedDataRecord> records = new ArrayList<IntegratedDataRecord>(100);
		
		//The first row is skipped. It has information in the form of
		//Dichotomous Sampler Concentrations (ug/m3) at ST. JOHN'S - DUCKWORTH/ORDINANCE NAPS No. 10101
		//The second row contains the column headers 
		for (int row = 2; row < sheet.rowCount(); row++) {
			
			IntegratedDataRecord record = new IntegratedDataRecord();
			
			//First column contains the date in the form of 11-20-84
			String rawDate = sheet.getCellContents(0, row);
			if("".equals(rawDate)) break; //We have reached padding at the end of the file
			
			try {
				if(!rawDate.contains("-")) {
					//This is a date likely in Excel's special 1900 format
					//TODO: The date could also be in 1904 format. We need to read the DATEMODE record to certain.
					//This isn't an issue because all XLS NAPS files in BIFF4 file format use the 1900 date format
					record.setDatetime(DateUtil.getJavaDate(Double.parseDouble(rawDate)));
				} else {
					record.setDatetime(EARLY_DATE_FORMAT.parse(rawDate));
				}
	        } catch (ParseException | NumberFormatException e) {
	        	throw new IllegalArgumentException("Could not parse date (" + rawDate + ") on row " + row + ". Expecting format " + EARLY_DATE_FORMAT, e);
	        }
			
			//Second column contains the coarse/fine flag
			record.setFine("F".equals(sheet.getCellContents(1, row).toUpperCase()));
			
			//Third column is MASS and is ignored (at least for now)
			//Last column is the NAPS ID
			record.setSiteId(getSiteID(sheet.getCellContents(sheet.columnCount()-1, row), row));
			
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
