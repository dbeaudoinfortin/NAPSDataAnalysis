package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.excel.ExcelSheet;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.records.IntegratedDataRecord;
import com.dbf.naps.data.records.SampleRecord;
import com.dbf.naps.data.utilities.DataCleaner;

/**
 * Extends the base IntegratedFileLoadRunner class to add support for common sample metadata column
 * Includes PAH, HCB, VOC, PCDD, PCB, CARB, IC, ICPMS, etc.
 */
public class SampleMetaDataLoaderRunner extends IntegratedLoaderRunner {

	public SampleMetaDataLoaderRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String reportType, String units) {
		super(threadId, config, sqlSessionFactory, rawFile, reportType, units);
	}

	//Store these column indexes so we only have to look them up once for the entire sheet
	private Integer canisterIDCol;
	private Integer sampleIDCol;
	private Integer sampleVolumeCol;
	private Integer sampleDurationCol;
	private Integer sampleTypeCol;
	private Integer tspCol;
	private Integer massCol;
	private Integer speciationMassCol;
	private Integer dichotMassCol;
	private Integer startTimeCol;
	private Integer endTimeCol;
	private Integer cartridgeCol;
	private Integer mediaCol;
	
	@Override
	protected void processSheet(ExcelSheet sheet) {
		canisterIDCol = null;
		sampleIDCol = null;
		sampleVolumeCol = null;
		sampleDurationCol = null;
		sampleTypeCol = null;
		tspCol = null;
		massCol = null;
		speciationMassCol = null;
		dichotMassCol = null;
		startTimeCol = null;
		endTimeCol = null;
		cartridgeCol = null;
		mediaCol = null;
		super.processSheet(sheet);
	}
	
	@Override
	protected void preProcessRows() {
		//The column indexes for the sample metadata are different for every sheet.
		//Look them up only once and store the result.
		//These columns are not guaranteed to be present in every sheet.
		//getColumnIndex() will not throw an exception if the column doesn't exist.
		sampleVolumeCol = updateColumnIndex(sampleVolumeCol, "Sample Volume", "Actual Volume");
		canisterIDCol = updateColumnIndex(canisterIDCol, "Canister ID"); //May also be Canister ID#
		sampleIDCol = updateColumnIndex(sampleIDCol, "Sample ID"); //May also be Sample ID#
		sampleDurationCol = updateColumnIndex(sampleDurationCol, "Duration");
		tspCol = updateColumnIndex(tspCol, "TSP", "T.S.P"); //Alternate name on some sheets
		sampleTypeCol = updateColumnIndex(sampleTypeCol, "Sample Type", "Sampling Type");
		massCol = updateColumnIndex(massCol, "Mass");
		speciationMassCol = updateColumnIndex(speciationMassCol, "Speciation Mass");
		dichotMassCol = updateColumnIndex(dichotMassCol, "Dich/Partisol Mass");
		startTimeCol = updateColumnIndex(startTimeCol, "Start"); //Some are "Start_time", some are "Start Time"
		endTimeCol = updateColumnIndex(endTimeCol, "End");
		mediaCol = updateColumnIndex(mediaCol, "Media");
		cartridgeCol = updateColumnIndex(cartridgeCol, "Cart"); //Some are "Cart", some are "Cartridge"
		if (null == cartridgeCol) cartridgeCol = updateColumnIndex(true, cartridgeCol, "0.0","0"); //A couple cartridge columns have bad headers
	}
	
	private Integer updateColumnIndex(Integer oldValue, String... matchingColumnHeaders) {
		return updateColumnIndex(false, oldValue, matchingColumnHeaders);
	}
	
	private Integer updateColumnIndex(boolean exact, Integer oldValue, String... matchingColumnHeaders) {
		Integer newValue = getColumnIndex(exact, matchingColumnHeaders);
		return null == newValue ? oldValue : newValue;
	}
	
	@Override
	protected SampleRecord processSampleRecord() {
		SampleRecord sample = super.processSampleRecord();
		
		//Mass is not always present
		BigDecimal mass = (null == massCol) ? null : DataCleaner.extractDecimalData(getSheet().getCellContents(massCol, getRow()), true);
		BigDecimal speciationMass = (null == speciationMassCol) ? null : DataCleaner.extractDecimalData(getSheet().getCellContents(speciationMassCol, getRow()), true);
		BigDecimal dichotMass = (null == dichotMassCol) ? null : DataCleaner.extractDecimalData(getSheet().getCellContents(dichotMassCol, getRow()), true);
		
		BigDecimal tsp = (null == tspCol) ? null : DataCleaner.extractDecimalData(getSheet().getCellContents(tspCol, getRow()), true);
		BigDecimal sampleVol = (null == sampleVolumeCol) ? null : DataCleaner.extractDecimalData(getSheet().getCellContents(sampleVolumeCol, getRow()).replace("mL", ""), true); 	//A few data rows have units in the volume column
		
		Double sampleDuration = (null == sampleDurationCol) ? null : DataCleaner.extractDoubleData(getSheet().getCellContents(sampleDurationCol, getRow()), true);
		if (null == sampleDuration) {
			//When the duration is not explicitly recorded then we try to calculate it using the sart and end times
			Double startTime = (null == startTimeCol) ? null : DataCleaner.extractDoubleData(getSheet().getCellContents(startTimeCol, getRow()), true);
			Double endTime = (null == endTimeCol) ? null : DataCleaner.extractDoubleData(getSheet().getCellContents(endTimeCol, getRow()), true);
			sampleDuration = (startTime != null && endTime != null) ? (endTime - startTime) : null;
		}
		
		String cartridge = (null == cartridgeCol) ? null : getSheet().getCellContents(cartridgeCol, getRow()).trim();
		if("".equals(cartridge)) cartridge = null;
		
		String media = (null == mediaCol) ? null : getSheet().getCellContents(mediaCol, getRow()).trim();
		if("".equals(media)) media = null;
		
		String sampleType = null;
		if(null != sampleTypeCol) {
			sampleType = getSheet().getCellContents(sampleTypeCol, getRow()).trim();
			if(sampleType.isEmpty()) sampleType = null;
		}

		String canisterId = null;
		if(null != canisterIDCol) {
			canisterId = getSheet().getCellContents(canisterIDCol, getRow()).trim();
			if(canisterId.isEmpty()) canisterId = null;
		}
		
		String id = null;
		if (null != sampleIDCol) {
			String rawId = getSheet().getCellContents(sampleIDCol, getRow()).trim();
			if(rawId.startsWith(":")) rawId = rawId.substring(1); //Cleanup
			if(rawId.isEmpty()) rawId = null;
		}
		
		sample.setCanisterID(canisterId);
		sample.setType(sampleType);
		sample.setNapsID(id);
		sample.setVolume(sampleVol);
		sample.setDuration(sampleDuration);
		sample.setTSP(tsp);
		sample.setMedia(media);
		sample.setCartridge(cartridge);
		sample.setMass(mass);
		sample.setSpecMass(speciationMass);
		sample.setDichotMass(dichotMass);
		return sample;
	}
	
	@Override
	protected List<IntegratedDataRecord> processRow(Date date) {
		//Exclude all FB   records as these are sample blanks
		//Exclude all -999 records as these are missing data
		String sampleType = (null == sampleTypeCol) ? "" : getSheet().getCellContents(sampleTypeCol, getRow());
		sampleType.toUpperCase();
		if ("FB".equals(sampleType) || "TB".equals(sampleType) || sampleType.startsWith("-99")) return Collections.emptyList();
		
		String cartridge = (null == cartridgeCol) ? "" : getSheet().getCellContents(cartridgeCol, getRow());
		if("FB".equals(cartridge.toUpperCase())) return Collections.emptyList(); //Field blank
		if("CARB".equals(getReportType()) && "B".equals(cartridge.toUpperCase())) return Collections.emptyList(); //Dynamic blank
		
		String media = (null == mediaCol) ? "" : getSheet().getCellContents(mediaCol, getRow());
		if("FB".equals(media.toUpperCase())) return Collections.emptyList(); //Field blank
		
		return super.processRow(date);
	}
	
	@Override
	protected String getMethod(String rawPollutantName) {
		switch(getReportType()) {
		case "PCDD":
		case "PAH":
		case "HCB":
		case "PCB":
			return "GC-MS";
		case "CARBONYLS":
			return "HPLC";
		case "CARB":
			return "TOR";
		case "IC":
		case "NH4":
		case "NA":
		case "LEV":
			return "IC";
		case "WICPMS":
			return "WICPMS";
		case "ICPMS":
			return "ICPMS";
		case "VOC":
		case "VOC_4HR":
			return rawPollutantName.equals("Ethane") || rawPollutantName.equals("Acetylene") || rawPollutantName.equals("Ethylene")  ? "GC-FID" : "GC-MS";
		case "SPEC":
			return rawPollutantName.contains("OC") || rawPollutantName.contains("EC") ? "TOR" : "IC";  
		default:
			return null; //"N/A"
		}

		
	}
}
