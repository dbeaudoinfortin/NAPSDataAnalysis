package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;
import com.dbf.naps.data.utilities.DataCleaner;

/**
 * Extends the base IntegratedFileLoadRunner class to add support for speciation metadata.
 */
public class SpeciationFileLoadRunner extends IntegratedFileLoadRunner {

	public SpeciationFileLoadRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String method) {
		super(threadId, config, sqlSessionFactory, rawFile, method);
	}
	
	//Store these column indexes so we only have to look them up once for the entire sheet 
	private Integer massCol;
	private Integer speciationMassCol;
	private Integer startTimeCol;
	private Integer endTimeCol;
	private Integer cartridgeCol;
	private Integer mediaCol;
	
	@Override
	protected void preProcessRow() {
		//The column indexes for the speciation metadata are different for every sheet.
		//Look them up only once and store the result.
		//These columns are not guaranteed to be present in every sheet.
		//getColumnIndex() will not throw an exception if the column doesn't exist.
		if (null == massCol) massCol = getColumnIndex("Mass");
		if (null == speciationMassCol) speciationMassCol = getColumnIndex("Speciation Mass");
		if (null == startTimeCol) startTimeCol = getColumnIndex("Start"); //Some are "Start_time", some are "Start Time"
		if (null == endTimeCol) endTimeCol = getColumnIndex("End");
		if (null == cartridgeCol) cartridgeCol = getColumnIndex("Cart"); //Some are "Cart", some are "Cartridge"
		if (null == mediaCol) mediaCol = getColumnIndex("Media");
	}

	@Override
	protected List<IntegratedDataRecord> processRow(int row, Date date) {
		
		//Mas is not always present
		BigDecimal mass = (null == massCol) ? null : DataCleaner.extractDecimalData(getSheet().getCellContents(massCol, row), true);
		
		//TODO: This is separate from sample mass and should be its own column
		BigDecimal speciationMass = (null == speciationMassCol) ? null : DataCleaner.extractDecimalData(getSheet().getCellContents(speciationMassCol, row), true);
		
		//TODO: Handle the "Dich/Partisol Mass (ug/m3)" column?
		
		Double startTime = (null == startTimeCol) ? null : DataCleaner.extractDoubleData(getSheet().getCellContents(startTimeCol, row), true);
		Double endTime = (null == endTimeCol) ? null : DataCleaner.extractDoubleData(getSheet().getCellContents(endTimeCol, row), true);
		Double duration = (startTime != null && endTime != null) ? (endTime - startTime) : null;
		
		String cartridge = (null == cartridgeCol) ? null : getSheet().getCellContents(cartridgeCol, row);
		if("".equals(cartridge)) cartridge = null;
		if(null != cartridge  && "FB".equals(cartridge.toUpperCase())) return Collections.emptyList(); //Field blank
		
		String media = (null == mediaCol) ? null : getSheet().getCellContents(mediaCol, row);
		if("".equals(media)) media = null;
		if(null != media  && "FB".equals(media.toUpperCase())) return Collections.emptyList(); //Field blank
		
		List<IntegratedDataRecord> records = super.processRow(row, date);
		for(IntegratedDataRecord record : records) {
			//Enhance the data with metadata specific to this dataset
			record.setDuration(duration);
			record.setMedia(media);
			record.setCartridge(cartridge);
        	record.setMass(mass); 
		}
        return records;
	}
}