package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;
import com.dbf.naps.data.utilities.DataCleaner;

/**
 * Extends the base IntegratedFileLoadRunner class to add support for common sample metadata column
 */
public class SampleMetaDataFileLoadRunner extends IntegratedFileLoadRunner {

	public SampleMetaDataFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String method, String units) {
		super(threadId, config, sqlSessionFactory, rawFile, method, units);
	}

	//Store these column indexes so we only have to look them up once for the entire sheet 
	private Integer sampleVolumeCol;
	private Integer sampleDurationCol;
	private Integer tspCol;
	
	@Override
	protected void preProcessRow() {
		//The column indexes for the sample metadata are different for every sheet.
		//Look them up only once and store the result.
		//These columns are not guaranteed to be present in every sheet.
		//getColumnIndex() will not throw an exception if the column doesn't exist.
		if (null == sampleVolumeCol) sampleVolumeCol = getColumnIndex("Sample Volume");
		if (null == sampleDurationCol) sampleDurationCol = getColumnIndex("Duration");
		if (null == tspCol) tspCol = getColumnIndex("TSP", "T.S.P"); //Alternate name on some sheets
	}
	
	@Override
	protected List<IntegratedDataRecord> processRow(Date date) {

		List<IntegratedDataRecord> records = super.processRow(date);
		
		BigDecimal sampleVol = (null == sampleVolumeCol) ? null : DataCleaner.extractDecimalData(getSheet().getCellContents(sampleVolumeCol, getRow()), true);
		Double sampleDuration = (null == sampleDurationCol) ? null : DataCleaner.extractDoubleData(getSheet().getCellContents(sampleDurationCol, getRow()), true);
		BigDecimal tsp = (null == tspCol) ? null : DataCleaner.extractDecimalData(getSheet().getCellContents(tspCol, getRow()), true);
		
		for(IntegratedDataRecord record : records) {
			//Enhance the data with metadata specific to this dataset
			record.setVolume(sampleVol);
			record.setDuration(sampleDuration);
        	record.setTSP(tsp);
		}
        return records;
	}
}
