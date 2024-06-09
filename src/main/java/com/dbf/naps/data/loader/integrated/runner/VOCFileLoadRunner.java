package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.util.Date;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;
import com.dbf.naps.data.utilities.DataCleaner;

/**
 * Extends the base IntegratedFileLoadRunner class to add support for carbonyls
 */
public class VOCFileLoadRunner extends IntegratedFileLoadRunner {

	public VOCFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String method) {
		super(threadId, config, sqlSessionFactory, rawFile, method);
	}
	
	@Override
	protected IntegratedDataRecord processSingleRecord(String columnHeader, String cellValue, Date date) {
		//Strip the units out of the headers
		columnHeader = DataCleaner.replaceColumnHeaderUnits(columnHeader); //This uses caching
		return super.processSingleRecord(columnHeader, cellValue, date);
	}
}
