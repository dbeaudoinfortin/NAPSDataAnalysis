package com.dbf.naps.data.analysis.heatmap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.analysis.AggregateFunction;
import com.dbf.naps.data.analysis.AggregationField;
import com.dbf.naps.data.exporter.ExporterOptions;

public class HeatMapOptions extends ExporterOptions {

	private static final Logger log = LoggerFactory.getLogger(HeatMapOptions.class);

	private AggregateFunction aggregateFunction = AggregateFunction.AVG;
	private AggregationField  xField;
	private AggregationField  yField;
	
	static {
		getOptions().addOption("a","aggregateFunction", true, "Data aggregation function.");
		getOptions().addRequiredOption("x","xDataField", true, "Data field for the X-axis.");
		getOptions().addRequiredOption("y","yDataField", true, "Data field for the Y-axis.");
	}

	public HeatMapOptions(String[] args) throws IllegalArgumentException {
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
		loadAggregationField(cmd, "x");
		loadAggregationField(cmd, "y"); 
	}
	
	private void loadAggregationField(CommandLine cmd, String axis) {
		final String field = axis + "DataField";
		AggregationField aggregationField = null;
		
		String rawValue = cmd.getOptionValue(field);
		try {
			aggregationField = AggregationField.valueOf(rawValue.toUpperCase()); 
			
		} catch(Exception e) {
			throw new IllegalArgumentException("Invalid data field for the " + axis.toUpperCase() + "-axis: " + rawValue);
		}
		
		log.info("Using " + axis.toUpperCase() + "-axis data field " + aggregationField);
		
		if(axis.equals("x")) {
			xField = aggregationField; 
		}else { 
			yField = aggregationField;
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
			log.info("Using aggregate function " + aggregateFunction);
		} else {
			log.info("Using default aggregate function: " + aggregateFunction);
		}
	}

	public AggregateFunction getAggregateFunction() {
		return aggregateFunction;
	}

	public AggregationField getXField() {
		return xField;
	}

	public AggregationField getYField() {
		return yField;
	}

	public void setAggregateFunction(AggregateFunction aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}

	public void setXField(AggregationField xField) {
		this.xField = xField;
	}

	public void setYField(AggregationField yField) {
		this.yField = yField;
	}

}
