package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;
import com.dbf.naps.data.utilities.DataCleaner;

public class VOCFileLoadRunner extends IntegratedFileLoadRunner {

	public VOCFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}

	//Store the column indexes so we only have to look them up once for the entire sheet 
	private Integer sampleVolumeCol;
	private Integer sampleDurationCol;
	
	@Override
	protected List<IntegratedDataRecord> processRow(int row, Date date) {
		
		//The column indexes are different for every sheet.
		//Look them up only once and store the result.
		//getColumnIndex() will throw an exception if the column doesn't exist
		if (null == sampleVolumeCol) sampleVolumeCol = getColumnIndex("Sample Volume");
		if (null == sampleDurationCol) sampleDurationCol = getColumnIndex("Duration");
		BigDecimal sampleVol = DataCleaner.extractDecimalData(getSheet().getCellContents(sampleVolumeCol, row), true);
		Integer sampleDuration = DataCleaner.extractIntegerData(getSheet().getCellContents(sampleDurationCol, row));
		
		List<IntegratedDataRecord> records = super.processRow(row, date);
		for(IntegratedDataRecord record : records) {
			//Enhance the data with metadata specific to this dataset
			record.setVolume(sampleVol);
			record.setDuration(sampleDuration);
		}
        return records;
	}
		
}
