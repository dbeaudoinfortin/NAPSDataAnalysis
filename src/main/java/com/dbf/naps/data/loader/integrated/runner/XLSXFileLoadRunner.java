package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;
import com.dbf.naps.data.utilities.DataCleaner;

/**
 * Extends the base IntegratedFileLoadRunner class to add support for common sample metadata column
 */
public class XLSXFileLoadRunner extends IntegratedFileLoadRunner {

	private static final List<String> VALID_SHEETS  = new ArrayList<String>();
	private static final List<String> VALID_METHODS = new ArrayList<String>();
	static {
		//Note: must be in all upper-case to match correctly
		VALID_SHEETS.add("PAH");
		VALID_SHEETS.add("PM2.5");
		VALID_SHEETS.add("ELEMENTS");
		VALID_SHEETS.add("METALS");
		VALID_SHEETS.add("IONS");
		VALID_SHEETS.add("VOLATILE");
		VALID_SHEETS.add("OCEC");
		VALID_SHEETS.add("BIOMASS");
		VALID_SHEETS.add("AMMONIA");
		VALID_SHEETS.add("ACIDIC");
		VALID_SHEETS.add("PAH");
		VALID_SHEETS.add("CARBONYLS");
		VALID_SHEETS.add("VOC");
		
		VALID_METHODS.add("EDXRF");
		VALID_METHODS.add("ICPMS"); //Needs to come before "IC", so we don't match on IC first
		VALID_METHODS.add("IC");
	}
	
	public XLSXFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String method) {
		super(threadId, config, sqlSessionFactory, rawFile, method);
	}

	//Store these column indexes so we only have to look them up once for the entire sheet 
	private Integer sampleTypeCol;
	
	@Override
	protected void preProcessRow() {
		if (null == sampleTypeCol) sampleTypeCol = getColumnIndex("Sampling Type");
	}
	
	@Override
	protected List<IntegratedDataRecord> processRow(Date date) {

		//Exclude all FB records as these are sample blanks
		String sampleType = (null == sampleTypeCol) ? "" : getSheet().getCellContents(sampleTypeCol, getRow());
		sampleType.toUpperCase();
		
		if ("FB".equals(sampleType) || "TB".equals(sampleType)) return Collections.emptyList();

		return super.processRow(date);
	}
	
	@Override
	protected IntegratedDataRecord processSingleRecord(String columnHeader, String cellValue, Date date) {
		//Strip the abbreviation out of the column header
		columnHeader = DataCleaner.replaceColumnHeaderAbbreviation(columnHeader); 
		return super.processSingleRecord(columnHeader, cellValue, date);
	}
	
	@Override
	protected void setMethod() {
		super.setMethod();
		String sheetNameUpper = getSheet().getName().toUpperCase();
		
		//Other sheets may have the same data but using a different method
		for (String method : VALID_METHODS) {
			if(sheetNameUpper.contains(method)) {
				this.method = this.method + "_" + method;
				break;
			}
		}
	}
	
	@Override
	protected List<String> getMatchingSheetNames() {
		return VALID_SHEETS;
	}
}
