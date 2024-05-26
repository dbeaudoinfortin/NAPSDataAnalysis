package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;

public class PCDDFileLoadRunner extends IntegratedFileLoadRunner {

	public PCDDFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}

	@Override
	protected List<IntegratedDataRecord> processRow(int row, Date date) {
		List<IntegratedDataRecord> records = new ArrayList<IntegratedDataRecord>(40);
		
		//PCDD data starts on column 2
		//Last 7 columns are metadata and are ignored
        for (int col = 1; col < getLastColumn()-6; col++) {
        	String columnHeader = getSheet().getCellContents(col, getHeaderRowNumber());
        	
        	//Ignore derived columns
        	if(columnHeader.toUpperCase().startsWith("TOTAL")) continue;
        	if(columnHeader.toUpperCase().startsWith("SURROGATE")) continue;
        	
        	IntegratedDataRecord record = processSingleRecord(columnHeader, getSheet().getCellContents(col, row), date);
        	if(null != record) records.add(record);
         }
        return records;
	}

	@Override
	protected String headerFirstColumn() {
		return "CONGENER";
	}
}
