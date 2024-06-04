package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
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
	protected List<IntegratedDataRecord> processRow(int row, Date date) {

		//Exclude all FB records as these are sample blanks
		String sampleType = (null == sampleTypeCol) ? "" : getSheet().getCellContents(sampleTypeCol, row);
		if ("FB".equals(sampleType.toUpperCase())) return Collections.emptyList();

		return super.processRow(row, date);
	}
	
	@Override
	protected IntegratedDataRecord processSingleRecord(String columnHeader, String cellValue, Date date) {
		//Strip the abbreviation out of the column header
		//TODO: we really should not being doing this on every single row, it's redundant
		columnHeader = DataCleaner.replaceColumnHeaderAbbreviation(columnHeader);
		return super.processSingleRecord(columnHeader, cellValue, date);
	}
}
