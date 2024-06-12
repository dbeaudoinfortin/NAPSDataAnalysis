package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSessionFactory;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.IntegratedDataRecord;
import com.dbf.naps.data.loader.records.SampleRecord;
import com.dbf.naps.data.utilities.DataCleaner;

/**
 * Extends the base SampleMetaDataFileLoadRunner class to add support for a variable number of sheets
 * and other XLSX-specific formating.
 */
public class XLSX_LoaderRunner extends SampleMetaDataLoaderRunner {

	private static final List<String> VALID_SHEETS  = new ArrayList<String>();
	private static final List<Map.Entry<String, String>> VALID_METHODS = new ArrayList<Map.Entry<String, String>>(); //Must be ordered
	static {
		//Note: must be in all upper-case to match correctly
		VALID_SHEETS.add("PAH");
		VALID_SHEETS.add("PM2.5");
		VALID_SHEETS.add("ELEMENTS");
		VALID_SHEETS.add("METALS");
		VALID_SHEETS.add("IONS");
		VALID_SHEETS.add("VOLATILE");
		VALID_SHEETS.add("OCEC");
		VALID_SHEETS.add("BIOMASS");
		VALID_SHEETS.add("AMMONIA");
		VALID_SHEETS.add("ACIDIC");
		VALID_SHEETS.add("PAH");
		VALID_SHEETS.add("CARBONYLS");
		VALID_SHEETS.add("VOC");
		
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("VOC","GC_FID"));
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("OCEC","TOR"));
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("PM2.5","Microbalance"));
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("CARBONYLS","HPLC"));
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("(TP)","Microbalance"));
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("(G)","GC-MS"));
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("(TP+G)","GC-MS TP+G"));
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("_EDXRF", "ED-XRF"));
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("_EXDXRF", "ED-XRF"));
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("_ICPMS (Water-Soluble)", "ICPMS Water")); 
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("_ICPMS (Near-Total)", "ICPMS Acid")); 
		//Needs to come last so we don't match "IC" on "ICPMS"
		VALID_METHODS.add(new AbstractMap.SimpleEntry<String, String>("_IC","IC"));
	}
	
	public XLSX_LoaderRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String method, String units) {
		super(threadId, config, sqlSessionFactory, rawFile, method, units);
	}
	
	private Integer samplerRow;
	private Integer cartridgeRow;
	private Integer mediumRow;
	private Integer unitsRow;
	
	//Some sheets have more than one sample per row.
	//Keep track of the previous sampler cell value.
	private String samplerValue = null;
	private String cartridgeValue;
	private String mediumValue;
	
	//Contains the units (eg. "ng/m³") for each of the data columns. Cached for faster lookup
	private final Map<Integer, String> columnUnits = new HashMap<Integer, String>(50);
	
	@Override
	protected void preProcessRows() {
		super.preProcessRows();
		
		//Some of the metadata is stored in rows above the main data column headers
		for(int row = 0; row < this.getHeaderRowNumber(); row++) {
			String cell = getSheet().getCellContents(0, row).trim().toUpperCase();
			switch (cell) {
			case "SAMPLER":
				samplerRow = row;
				break;
			case "CARTRIDGE":
				cartridgeRow = row;
				break;
			case "MEDIUM":
				mediumRow = row;
				break;
			case "UNITS":
				unitsRow = row;
				break;
			}
		}
	}
	
	@Override
	protected IntegratedDataRecord processDataRecord(String columnHeader, String cellValue, Date date) {
		//Strip the abbreviation out of the column header
		columnHeader = DataCleaner.replaceColumnHeaderAbbreviation(columnHeader);
		
		if(null != samplerRow) {
			String newSamplerValue = getSheet().getCellContents(getColumn(), samplerRow).trim();
			if(null == samplerValue) {
				samplerValue = newSamplerValue;
			} else if(!samplerValue.equals(newSamplerValue)) {
				//The sample has changed and we now need to insert a new sample record for the next data record
				sampleId = null;
				samplerValue = newSamplerValue;
				
				//There is now a whole new set of column for reading metadata. We need to reset all of the metadata columns
				miniumColIndex = getColumn();
				super.preProcessRows();
						
				if(null != cartridgeRow) {
					cartridgeValue = getSheet().getCellContents(getColumn(), cartridgeRow).trim();
					if(cartridgeValue.equals("N/A")) cartridgeValue = "";
					if(cartridgeValue.isEmpty()) cartridgeValue = null;
				}
				
				if(null != mediumRow) {
					mediumValue = getSheet().getCellContents(getColumn(), mediumRow).trim();
					if(mediumValue.isEmpty()) mediumValue = null;
				}
			}
		}
		//Override the default units using the value explicitly defined above the column header
		this.units = getUnits();
		return super.processDataRecord(columnHeader, cellValue, date);
	}
	
	@Override
	protected SampleRecord processSampleRecord() {
		SampleRecord sample = super.processSampleRecord();
		//Values that may be set by a metadata row above the main header row, rather than by a column next to the data
		if(null == sample.getCartridge()) {
			sample.setCartridge(cartridgeValue);
		}
		if(null == sample.getMedia()) {
			sample.setMedia(mediumValue);
		}
		return sample;
	}
	
	@Override
	protected void setMethod() {
		method = null;
		String sheetNameUpper = getSheet().getName().toUpperCase();
		
		//Other sheets may have the same data but using a different method
		for (Map.Entry<String, String> methodEntry: VALID_METHODS) {
			if(sheetNameUpper.contains(methodEntry.getKey())) {
				method = methodEntry.getValue();
				break;
			}
		}
		
		if(method == null) 
			throw new IllegalArgumentException("Unable to determine the method for the sheet " + this.getSheet().getName());
	}
	
	@Override
	protected List<String> getMatchingSheetNames() {
		return VALID_SHEETS;
	}
	
	@Override
	protected boolean ignoreDuplicateColumns(){
		//We want to allow dupliciate data columns only if there is a valid
		//sampler metdata row to distinguish between the multiple data points
		return samplerRow == null;
	}
	
	private String getUnits() {
		return columnUnits.computeIfAbsent(getColumn(), col-> {
			String units = getSheet().getCellContents(col, unitsRow).trim();
			if(units.isEmpty()) {
				throw new IllegalArgumentException("Unable to locate the units for column " + col);
			}
			units = units.replace("m3", "m³");
			return units;
		});
	}
}
