package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.records.SampleRecord;
import com.dbf.naps.data.utilities.DataCleaner;

/**
 * Extends the base IntegratedFileLoadRunner class to add support for DICHOT & PART25 specific metadata.
 */
public class XLS_SimpleLoaderRunner extends IntegratedLoaderRunner {

	public XLS_SimpleLoaderRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String method, String units) {
		super(threadId, config, sqlSessionFactory, rawFile, method, units);
	}
	
	//Store these column indexes so we only have to look them up once for the entire sheet 
	private Integer sampleCFCol;
	private Integer sampleMassCol;
	
	@Override
	protected void preProcessRows() {
		sampleCFCol = getColumnIndex("C/F", "F/C");
		sampleMassCol = getColumnIndex("MASS");
		if(null == sampleCFCol)
			throw new IllegalArgumentException("Could not located sample C/F column.");
		if(null == sampleMassCol)
			throw new IllegalArgumentException("Could not located sample mass column.");
	}
	
	@Override
	protected SampleRecord processSampleRecord() {
		SampleRecord sample = super.processSampleRecord();
		sample.setFine("F".equals(getSheet().getCellContents(sampleCFCol, getRow()).toUpperCase()));
		//Mass is missing in a few cases and is allowed to be null
		sample.setMass(DataCleaner.extractDecimalData(getSheet().getCellContents(sampleMassCol, getRow()), true));
		return sample;
	}
}