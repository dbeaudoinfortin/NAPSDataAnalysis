package com.dbf.naps.data.loader.integrated.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.globals.PollutantMapping;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.integrated.Headers;
import com.dbf.naps.data.records.SampleRecord;

/**
 * Extends the base IntegratedFileLoadRunner class to add support for DICHOT & PART25 specific metadata.
 */
public class XLS_SimpleLoaderRunner extends IntegratedLoaderRunner {

	public XLS_SimpleLoaderRunner(int threadId, LoaderOptions config, SqlSessionFactory sqlSessionFactory, File rawFile, String method, String units) {
		super(threadId, config, sqlSessionFactory, rawFile, method, units);
	}
	
	//Store these column indexes so we only have to look them up once for the entire sheet 
	private Integer sampleCFCol;
	
	private static final Set<String> EDXRF_POLLUTANTS = new HashSet<String>(50);
	private static final Set<String> IC_POLLUTANTS = new HashSet<String>(50);
	
	static {
		EDXRF_POLLUTANTS.add("Aluminum");
		EDXRF_POLLUTANTS.add("Silicon");
		EDXRF_POLLUTANTS.add("Sulphur");
		EDXRF_POLLUTANTS.add("Potassium");
		EDXRF_POLLUTANTS.add("Calcium");
		EDXRF_POLLUTANTS.add("Titanium");
		EDXRF_POLLUTANTS.add("Vanadium");
		EDXRF_POLLUTANTS.add("Chromium");
		EDXRF_POLLUTANTS.add("Manganese");
		EDXRF_POLLUTANTS.add("Iron");
		EDXRF_POLLUTANTS.add("Nickel");
		EDXRF_POLLUTANTS.add("Zinc");
		EDXRF_POLLUTANTS.add("Selenium");
		EDXRF_POLLUTANTS.add("Bromine");
		EDXRF_POLLUTANTS.add("Rubidium");
		EDXRF_POLLUTANTS.add("Strontium");
		EDXRF_POLLUTANTS.add("Cadmium");
		EDXRF_POLLUTANTS.add("Tin");
		EDXRF_POLLUTANTS.add("Antimony");
		EDXRF_POLLUTANTS.add("Cesium");
		EDXRF_POLLUTANTS.add("Barium");
		EDXRF_POLLUTANTS.add("Lead");

		IC_POLLUTANTS.add("Acetate");
		IC_POLLUTANTS.add("Formate");
		IC_POLLUTANTS.add("Oxalate");
		IC_POLLUTANTS.add("Phosphate");
		IC_POLLUTANTS.add("Nitrite");
		IC_POLLUTANTS.add("Methanesulphonic Acid");
		IC_POLLUTANTS.add("Propionate");
		IC_POLLUTANTS.add("Bromide");
		IC_POLLUTANTS.add("Fluoride");
		IC_POLLUTANTS.add("Sulphate");
		IC_POLLUTANTS.add("Ammonium");
		IC_POLLUTANTS.add("Nitrate");
		IC_POLLUTANTS.add("Sodium");
		IC_POLLUTANTS.add("Lithium");
		IC_POLLUTANTS.add("Potassium");
		IC_POLLUTANTS.add("Magnesium");
		IC_POLLUTANTS.add("Chloride");
		IC_POLLUTANTS.add("Manganese");
		IC_POLLUTANTS.add("Calcium");
		IC_POLLUTANTS.add("Strontium");
		IC_POLLUTANTS.add("Barium");
		IC_POLLUTANTS.add("Levoglucosan");
		IC_POLLUTANTS.add("Arabitol");
		IC_POLLUTANTS.add("Mannosan");
		IC_POLLUTANTS.add("Mannitol");
		IC_POLLUTANTS.add("Galactosan");
	}
	
	@Override
	protected void preProcessRows() {
		sampleCFCol = getColumnIndex("C/F", "F/C");
		if(null == sampleCFCol)
			throw new IllegalArgumentException("Could not located sample C/F column.");
	}
	
	@Override
	protected Integer getPollutantID(String rawPollutantName) {
		//Mass actually represents the PM2.5 or PM2.5-10 reading, depending on the Coarse/Fine flag.
		if("MASS".equals(rawPollutantName.toUpperCase())) {
			rawPollutantName = isFineRow() ? "PM2.5" : "PM2.5-10";
		}
		return super.getPollutantID(rawPollutantName);
	}
	
	@Override
	protected SampleRecord processSampleRecord() {
		SampleRecord sample = super.processSampleRecord();
		sample.setFine(isFineRow());
		return sample;
	}
	
	@Override
	protected String getMethod(String rawPollutantName) {
		final String pollutantName = PollutantMapping.lookupPollutantName(rawPollutantName);
		if("MASS".equals(pollutantName.toUpperCase()))
			return "Microbalance";
		
		boolean isEDXRF = EDXRF_POLLUTANTS.contains(pollutantName);
		boolean isIC = IC_POLLUTANTS.contains(pollutantName);
		
		if(isEDXRF && isIC) {
			//This is a work around for ambiguities in the data files.
			//Some elements are both in the EDXRF and IC lists depending
			//only whether they are in ionic form or not. However, the data
			//files don't make it clear. Assume the fully written out form is ionic
			if(rawPollutantName.equals(pollutantName)) {
				isEDXRF = false;
			} else {
				isIC = false;
			}
		}
		
		if (EDXRF_POLLUTANTS.contains(pollutantName))
			return "EDXRF";
		if (IC_POLLUTANTS.contains(pollutantName))
			return "IC";
		return null; //"N/A"
	}
	
	/*
	 * These files have a special exception for mass because it doesn't represent the sample mass,
	 * but rather the PM2.5 or PM10 reading, depending on the Coarse/Fine flag.
	 */
	@Override
	protected List<String> getIgnoredColumnList() {
		List<String> ignoredColumns = new ArrayList<String>(Headers.DEFAULT_IGNORED_HEADERS);
		ignoredColumns.remove("MASS");
		return ignoredColumns;
	}
	
	private boolean isFineRow() {
		return "F".equals(getSheet().getCellContents(sampleCFCol, getRow()).toUpperCase());
	}
}