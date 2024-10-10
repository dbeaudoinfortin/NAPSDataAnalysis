package com.dbf.naps.data.analysis.heatmap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.analysis.DataQueryOptions;

public class HeatMapOptions extends DataQueryOptions {

	private static final Logger log = LoggerFactory.getLogger(HeatMapOptions.class);

	private Double dataLowerBound;
	private Double dataUpperBound;
	
	static {
		getOptions().getOption("dimension1").setDescription("Data field for the heat map X-axis.");
		getOptions().getOption("dimension2").setDescription("Data field for the heat map Y-axis.");
		getOptions().addOption("ub","dataUpperBound", true, "Heat map colour upper bound (inclusive).");
		getOptions().addOption("lb","dataLowerBound", true, "Heat map colour lower bound (inclusive).");
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
		
		loadDataLowerBound(cmd); //Check me first!
		loadDataUpperBound(cmd);
	}
	
	private void loadDataLowerBound(CommandLine cmd) {
		if(cmd.hasOption("dataLowerBound")) {
			dataLowerBound = Double.parseDouble(cmd.getOptionValue("dataLowerBound"));
			if (dataLowerBound < 0) {
				throw new IllegalArgumentException("Invalid heat map colour lower bound: " + dataLowerBound);
			}
			log.info("Using heat map colour lower bound: " + dataLowerBound);
		} else {
			log.info("No explicit upper bound set for the heat map colour. The upper bound will be automatically calculated.");
		}
	}
	
	private void loadDataUpperBound(CommandLine cmd) {
		if(cmd.hasOption("dataUpperBound")) {
			dataUpperBound = Double.parseDouble(cmd.getOptionValue("dataUpperBound"));
			if (dataLowerBound >= dataUpperBound) {
				throw new IllegalArgumentException("Invalid heat map colour upper bound: " + dataUpperBound);
			}
			log.info("Using heat map colour upper bound: " + dataUpperBound);
		} else {
			log.info("No explicit lower bound set for the heat map colour. The lower bound will be automatically calculated.");
		}
	}

	public Double getDataLowerBound() {
		return dataLowerBound;
	}

	public void setDataLowerBound(Double dataLowerBound) {
		this.dataLowerBound = dataLowerBound;
	}

	public Double getDataUpperBound() {
		return dataUpperBound;
	}

	public void setDataUpperBound(Double dataUpperBound) {
		this.dataUpperBound = dataUpperBound;
	}
}
