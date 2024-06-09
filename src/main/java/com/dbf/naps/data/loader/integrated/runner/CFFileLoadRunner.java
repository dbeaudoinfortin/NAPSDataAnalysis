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
 * Extends the base IntegratedFileLoadRunner class to add support for DICHOT & PART25 specific metadata.
 */
public class CFFileLoadRunner extends IntegratedFileLoadRunner {

	public CFFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String method) {
		super(threadId, config, sqlSessionFactory, rawFile, method);
	}

	@Override
	protected List<IntegratedDataRecord> processRow(Date date) {
		//TODO: It would be better to lookup the column index using the header instead of hard-coding the column index
		
		//Second column always contains the coarse/fine flag
		boolean fine = "F".equals(getSheet().getCellContents(1, getRow()).toUpperCase());
		
		//Third column is always mass
		//Mass is missing in a few cases and is allowed to be null
		BigDecimal mass = DataCleaner.extractDecimalData(getSheet().getCellContents(2, getRow()), true);
		
		List<IntegratedDataRecord> records = super.processRow(date);
		for(IntegratedDataRecord record : records) {
			//Enhance the data with metadata specific to this dataset
			record.setFine(fine);
        	record.setMass(mass);
		}
        return records;
	}
}