package com.dbf.naps.data.analysis.query;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.analysis.AggregateFunction;
import com.dbf.naps.data.analysis.DataAnalysisOptions;

public abstract class ExtendedDataQueryOptions extends DataAnalysisOptions {

	private static final Logger log = LoggerFactory.getLogger(ExtendedDataQueryOptions.class);

	private boolean sampleCount = false;
	private boolean stdDevPop = false;
	private boolean stdDevSmp = false;
	
	private final Set<QueryOutputTypes> outputTypes = new HashSet<QueryOutputTypes>();
	
	static {
		//TODO: Support more than one aggregation in this query mode. For example, I want average, min, and max
		getOptions().addOption("g3","group3", true, "Data field for optional level 3 grouping");
		getOptions().addOption("g4","group4", true, "Data field for optional level 4 grouping");
		getOptions().addOption("g5","group5", true, "Data field for optional level 5 grouping");
		getOptions().addOption("sc","showSampleCount", false, "Include the sample count (number of samples or data points) in the result set.");
		getOptions().addOption("stdDevPop","showStdDevPop", false, "Include the population standard deviation in the result set.");
		getOptions().addOption("stdDevSmp","showStdDevSamp", false, "Include the sample standard deviation in the result set.");
		getOptions().addOption("ot","outputTypes", true, "Comma-separated list of file output types (" + QueryOutputTypes.ALL_VALUES + "). Defaults to CSV if unspecified.");
	}

	public ExtendedDataQueryOptions(String[] args) throws IllegalArgumentException {
		super(args);
		loadFromArgs(args);
	}
	
	private void loadFromArgs(String[] args) throws IllegalArgumentException {
		CommandLine cmd = null;
		try {
			cmd = getParser().parse(getOptions(), args);
		}
		catch(ParseException e) {
			throw new IllegalArgumentException(e);
		}
		
		loadAggregationField(cmd, 3, false);
		loadAggregationField(cmd, 4, false);
		loadAggregationField(cmd, 5, false);
		
		loadStdDevPop(cmd);
		loadStdDevSmp(cmd);
		
		loadOutputTypes(cmd);
		
		checkAQHIFields(); //Need to re-check to support g3-g5
		
		sampleCount = cmd.hasOption("showSampleCount");
		log.info("Will" + (sampleCount ? "" : " not") +  " include sample count in the result set.");
	}
	
	private void loadOutputTypes(CommandLine cmd) {
		if(cmd.hasOption("outputTypes")) {
			for(String outputRaw : cmd.getOptionValue("outputTypes").split(",")) {
				String outputTrimmed = outputRaw.trim().toUpperCase();
				if (outputTrimmed.isEmpty()) continue;
				
				QueryOutputTypes outputEnum;
				try {
					outputEnum = QueryOutputTypes.valueOf(outputTrimmed);
				} catch (Exception e) {
					throw new IllegalArgumentException("Invalid output type: " + outputRaw + ". Possible values are: " + QueryOutputTypes.ALL_VALUES);
				}
				outputTypes.add(outputEnum);
			}
			if(outputTypes.isEmpty()) 
				throw new IllegalArgumentException("Must specify at least one output type. Possible values are: " + QueryOutputTypes.ALL_VALUES);
			
			log.info("Using the following output types: " + outputTypes);
		} else {
			log.info("Using the default output type of CSV.");
		}
	}
	
	private void loadStdDevPop(CommandLine cmd) {
		if(cmd.hasOption("showStdDevPop")) {
			if(getAggregateFunction().equals(AggregateFunction.NONE)) {
				throw new IllegalArgumentException("Population standard deviation requires the use of an aggregation function. Use the -a argument.");
			}
			stdDevPop = true;
		}
		log.info("Will" + (stdDevPop ? "" : " not") +  " include population standard deviation in the result set.");
	}
	
	private void loadStdDevSmp(CommandLine cmd) {
		if(cmd.hasOption("showStdDevSamp")) {
			if(getAggregateFunction().equals(AggregateFunction.NONE)) {
				throw new IllegalArgumentException("Sample standard deviation requires the use of an aggregation function. Use the -a argument.");
			}
			stdDevSmp = true;
		}
		log.info("Will" + (stdDevSmp ? "" : " not") +  " include sample standard deviation in the result set.");
	}
	
	public boolean allowAggregateFunctionNone() {return true;}
	public boolean isAggregationMandatory() {return false;}

	public boolean isSampleCount() {
		return sampleCount;
	}

	public boolean isStdDevPop() {
		return stdDevPop;
	}

	public boolean isStdDevSmp() {
		return stdDevSmp;
	}
	
	public Set<QueryOutputTypes> getOutputTypes() {
		return outputTypes;
	}
}
