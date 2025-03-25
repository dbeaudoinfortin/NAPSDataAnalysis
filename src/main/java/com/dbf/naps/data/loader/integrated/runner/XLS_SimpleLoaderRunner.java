package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.Headers;
import com.dbf.naps.data.records.SampleRecord;

/**
 * Extends the base IntegratedFileLoadRunner class to add support for DICHOT & PART25 specific metadata.
 */
public class XLS_SimpleLoaderRunner extends IntegratedLoaderRunner {

	public XLS_SimpleLoaderRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String method, String units) {
		super(threadId, config, sqlSessionFactory, rawFile, method, units);
	}
	
	//Store these column indexes so we only have to look them up once for the entire sheet 
	private Integer sampleCFCol;
	
	@Override
	protected void preProcessRows() {
		sampleCFCol = getColumnIndex("C/F", "F/C");
		if(null == sampleCFCol)
			throw new IllegalArgumentException("Could not located sample C/F column.");
	}
	
	@Override
	protected Integer getPollutantID(String rawPollutantName) {
		//Mass actually represents the PM2.5 or PM2.5-10 reading, depending on the Coarse/Fine flag.
		if("MASS".equals(rawPollutantName.toUpperCase())) {
			rawPollutantName = isFineRow() ? "PM2.5" : "PM2.5-10";
		}
		return super.getPollutantID(rawPollutantName);
	}
	
	@Override
	protected SampleRecord processSampleRecord() {
		SampleRecord sample = super.processSampleRecord();
		sample.setFine(isFineRow());
		return sample;
	}
	
	/*
	 * These files have a special exception for mass because it doesn't represent the sample mass,
	 * but rather the PM2.5 or PM10 reading, depending on the Coarse/Fine flag.
	 */
	@Override
	protected List<String> getIgnoredColumnList() {
		List<String> ignoredColumns = new ArrayList<String>(Headers.DEFAULT_IGNORED_HEADERS);
		ignoredColumns.remove("MASS");
		return ignoredColumns;
	}
	
	private boolean isFineRow() {
		return "F".equals(getSheet().getCellContents(sampleCFCol, getRow()).toUpperCase());
	}
}