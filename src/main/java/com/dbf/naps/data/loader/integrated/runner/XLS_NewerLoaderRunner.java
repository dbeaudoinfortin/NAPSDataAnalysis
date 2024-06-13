package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.records.IntegratedDataRecord;
import com.dbf.naps.data.utilities.DataCleaner;

public class XLS_NewerLoaderRunner extends SampleMetaDataLoaderRunner {

	public XLS_NewerLoaderRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String method, String units) {
		super(threadId, config, sqlSessionFactory, rawFile, method, units);
	}
	
	@Override
	protected IntegratedDataRecord processDataRecord(String columnHeader, String cellValue, Date date) {
		//Strip the units out of the headers
		columnHeader = DataCleaner.replaceColumnHeaderUnits(columnHeader); //This uses caching
		return super.processDataRecord(columnHeader, cellValue, date);
	}
	
	@Override
	protected List<String> getMatchingSheetNames() {
		return Collections.singletonList("DATA"); //Must be in all-caps
	}
}
