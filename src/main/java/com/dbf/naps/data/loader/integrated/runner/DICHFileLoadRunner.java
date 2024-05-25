package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;

public class DICHFileLoadRunner extends IntegratedFileLoadRunner {

	public DICHFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile) {
		super(threadId, config, sqlSessionFactory, rawFile);
	}

	@Override
	protected List<IntegratedDataRecord> processRow(int row, Date date) {
		
		List<IntegratedDataRecord> records = new ArrayList<IntegratedDataRecord>(40);
		
		//Second column contains the coarse/fine flag
		boolean fine = "F".equals(getSheet().getCellContents(1, row).toUpperCase());
		
		//Third column is mass
		BigDecimal mass = null;
		String rawMassValue = getSheet().getCellContents(2, row);
		//Mass is missing in a few cases and is allowed to be null
		if (rawMassValue != null && !rawMassValue.equals("")) {
			try {
	    		Double d = Double.parseDouble(rawMassValue);
	    		mass = new BigDecimal(d);
			} catch (NumberFormatException e){
				throw new IllegalArgumentException("Invalid sample mass (" + rawMassValue + ") for column 2, on row " + row, e);
			}
		}
		
		//DICHOT data starts only on column 4
		//Last column is NAPS ID and is also ignored
        for (int col = 3; col < getSheet().columnCount()-1; col++) {
        	String columnHeader = getSheet().getCellContents(col, getHeaderRowNumber());
        	
        	//Ignore detection limit (at least for now)
        	if("D.L.".equals(columnHeader.toUpperCase())) continue;
        	
        	IntegratedDataRecord record = processSingleRecord(columnHeader, getSheet().getCellContents(col, row), date);
        	if(null != record) {
        		record.setFine(fine);
            	record.setMass(mass);
        		records.add(record);
        	}
        }
        return records;
	}
}