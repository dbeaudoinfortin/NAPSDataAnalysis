package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;
import com.dbf.naps.data.utilities.DataCleaner;

public class PAHFileLoadRunner extends IntegratedFileLoadRunner {

	public PAHFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}

	//Store these column indexes so we only have to look them up once for the entire sheet 
	private Integer sampleVolumeCol;
	private Integer tspCol;
	
	@Override
	protected List<IntegratedDataRecord> processRow(int row, Date date) {
		
		//The column indexes for sample vol and TSP are different for every sheet.
		//Look them up only once and store the result.
		//getColumnIndex() will throw an exception if the column doesn't exist
		if (null == sampleVolumeCol) sampleVolumeCol = getColumnIndex("Sample Volume");
		if (null == tspCol) tspCol = getColumnIndex("TSP", "T.S.P"); //Alternate name on some sheets

		BigDecimal sampleVol = DataCleaner.extractDecimalData(getSheet().getCellContents(sampleVolumeCol, row), true);
		BigDecimal tsp = DataCleaner.extractDecimalData(getSheet().getCellContents(tspCol, row), true);
		
		List<IntegratedDataRecord> records = super.processRow(row, date);
		for(IntegratedDataRecord record : records) {
			//Enhance the data with metadata specific to this dataset
			record.setVolume(sampleVol);
        	record.setTSP(tsp);
		}
        return records;
	}
}
