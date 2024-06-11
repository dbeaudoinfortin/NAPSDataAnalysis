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
 * Extends the base VOCFileLoadRunner class to add support for sample metadata specific to Carbonyls
 */
public class CarbonylsFileLoadRunner extends VOCFileLoadRunner {

	public CarbonylsFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String method, String units) {
		super(threadId, config, sqlSessionFactory, rawFile, method, units);
	}

	//Store these column indexes so we only have to look them up once for the entire sheet 
	private Integer sampleTypeCol;
	private Integer startTimeCol;
	private Integer stopTimeCol;
	
	@Override
	protected void preProcessRow() {
		if (null == sampleTypeCol) sampleTypeCol = getColumnIndex("Sample Type");
		if (null == startTimeCol) startTimeCol = getColumnIndex("Start");
		if (null == stopTimeCol) stopTimeCol = getColumnIndex("Stop");
	}
	
	@Override
	protected List<IntegratedDataRecord> processRow(Date date) {

		//Exclude all -999 records as these are missing data
		String sampleType = (null == sampleTypeCol) ? "" : getSheet().getCellContents(sampleTypeCol, getRow());
		if (sampleType.startsWith("-99")) return Collections.emptyList();
		
		Double startTime = (null == startTimeCol) ? null : DataCleaner.extractDoubleData(getSheet().getCellContents(startTimeCol, getRow()), true);
		Double endTime = (null == stopTimeCol) ? null : DataCleaner.extractDoubleData(getSheet().getCellContents(stopTimeCol, getRow()), true);
		Double duration = (startTime != null && endTime != null) ? (endTime - startTime) : null;
		
		List<IntegratedDataRecord> records = super.processRow(date);
		for(IntegratedDataRecord record : records) {
			//Enhance the data with metadata specific to this dataset
			record.setDuration(duration);
		}
        return records;
	}
}
