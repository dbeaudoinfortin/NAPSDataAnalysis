package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;

public class VOCFileLoadRunner extends IntegratedFileLoadRunner {

	public VOCFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}

	@Override
	protected List<IntegratedDataRecord> processRow(int row, Date date) {
		List<IntegratedDataRecord> records = new ArrayList<IntegratedDataRecord>(100);
		
		//VOC data starts on column 2
		//Last 6 columns are metadata and are ignored
        for (int col = 1; col < getLastColumn()-5; col++) {
        	IntegratedDataRecord record = processSingleRecord(getSheet().getCellContents(col, getHeaderRowNumber()), getSheet().getCellContents(col, row), date);
        	if(null != record) records.add(record);
         }
        return records;
	}

	@Override
	protected String headerFirstColumn() {
		return "COMPOUND";  //This is0 sometimes COMPOUND and sometimes COMPOUNDS
	}
}
