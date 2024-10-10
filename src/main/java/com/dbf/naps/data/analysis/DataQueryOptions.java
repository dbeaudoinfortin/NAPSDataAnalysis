package com.dbf.naps.data.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.exporter.ExporterOptions;

public abstract class DataQueryOptions extends ExporterOptions {

	private static final Logger log = LoggerFactory.getLogger(DataQueryOptions.class);

	private AggregateFunction aggregateFunction = AggregateFunction.AVG;
	private final List<AggregationField> dimensions = new ArrayList<AggregationField>();
	private final Set<Integer> months = new HashSet<Integer>();
	private final Set<String> provTerr = new HashSet<String>();
	private String siteName;
	private String cityName;
	
	//TODO: finish loading these options

	static {
		getOptions().addOption("a","aggregateFunction", true, "Data aggregation function.");
		getOptions().addRequiredOption("d1","dimension1", true, "Data field for dimension 1.");
		getOptions().addRequiredOption("d2","dimension2", true, "Data field for dimension 2.");
		getOptions().addOption("sid","months", true, "Comma-seperated list of months of the year, starting at 1 for January.");
		getOptions().addOption("sid","provTerr", true, "Comma-seperated list of 2-digit province & territory codes.");
		getOptions().addOption("sn","siteName", true, "NAPS site (station) name, partial match.");
		getOptions().addOption("cn","cityName", true, "City name, partial match.");
	}

	public DataQueryOptions(String[] args) throws IllegalArgumentException {
		super(args);
		loadFromArgs(args);
	}
	
	private void loadFromArgs(String[] args) throws IllegalArgumentException {
		CommandLine cmd = null;
		try {
			cmd = (new DefaultParser()).parse(getOptions(), args);
		}
		catch(ParseException e) {
			throw new IllegalArgumentException(e);
		}
		
		loadAggregateFunction(cmd);
		loadAggregationField(cmd, 1, true);
		loadAggregationField(cmd, 2, true); 
	}
	
	public void loadAggregationField(CommandLine cmd, int dimIndex, boolean mandatory) {
		final String field =  "dimension" + dimIndex;
		AggregationField aggregationField = null;
		
		if(cmd.hasOption(field)) {
			String rawValue = cmd.getOptionValue(field);
			try {
				aggregationField = AggregationField.valueOf(rawValue.toUpperCase()); 
			} catch(Exception e) {
				throw new IllegalArgumentException("Invalid data field for dimension " + dimIndex + " " + rawValue);
			}
			if(aggregationField.equals(AggregationField.HOUR) && !allowAggregationFieldHour()) {
				log.info("Cannot use 'HOUR' as a data field for dimension " + dimIndex + ".");
			}
			
			dimensions.add(dimIndex-1, aggregationField);
			log.info("Using data field " + aggregationField + " for dimension " + dimIndex + ".");
		} else if(mandatory) {
			throw new IllegalArgumentException("Missing data field for dimension " + dimIndex + ". Use the -d" + dimIndex + " argument.");
		} else {
			dimensions.add(null);
		}
	}
	
	private void loadAggregateFunction(CommandLine cmd) {
		if(cmd.hasOption("aggregateFunction")) {
			String rawValue = cmd.getOptionValue("aggregateFunction");
			try {
				aggregateFunction = AggregateFunction.valueOf(rawValue.toUpperCase()); 
			} catch(Exception e) {
				throw new IllegalArgumentException("Invalid aggregation function option: " + rawValue);
			}
			
			if(aggregateFunction.equals(AggregateFunction.NONE) && !allowAggregateFunctionNone()) {
				log.info("Aggregate function cannot be set to 'NONE'.");
			}
			log.info("Using aggregate function " + aggregateFunction);
		} else {
			log.info("Using default aggregate function: " + aggregateFunction);
		}
	}
	
	public abstract boolean allowAggregateFunctionNone();
	
	public abstract boolean allowAggregationFieldHour();

	public AggregateFunction getAggregateFunction() {
		return aggregateFunction;
	}

	public void setAggregateFunction(AggregateFunction aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}
	
	public List<AggregationField> getDimensions() {
		return dimensions;
	}
}
