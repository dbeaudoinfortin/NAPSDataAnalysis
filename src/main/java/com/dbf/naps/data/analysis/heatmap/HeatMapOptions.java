package com.dbf.naps.data.analysis.heatmap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.analysis.DataQueryOptions;

public abstract class HeatMapOptions extends DataQueryOptions {

	private static final Logger log = LoggerFactory.getLogger(HeatMapOptions.class);

	private Double colourLowerBound;
	private Double colourUpperBound;
	private int colourGradient = 1;
	
	private boolean generateCSV = false;
	
	static {
		//The parent class' static initialiser will be called first.
		Option dim1 = getOptions().getOption("group1");
		Option dim2 = getOptions().getOption("group2");
		dim1.setDescription("Data field for the heat map X-axis.");
		dim2.setDescription("Data field for the heat map Y-axis.");
		dim1.setRequired(true);
		dim2.setRequired(true);
		
		getOptions().addOption("cub","colourUpperBound", true, "Heat map colour upper bound (inclusive).");
		getOptions().addOption("clb","colourLowerBound", true, "Heat map colour lower bound (inclusive).");
		getOptions().addOption("cg","colourGradient", true, "Heat map colour gradient choice. Values are 1-" + (HeatMapGradient.getGradientCount()) + " (inclusive).");
		getOptions().addOption("csv","generateCSV", false, "Generate a corresponding CSV file containing the raw data for each heat map.");	
	}

	public HeatMapOptions(String[] args) throws IllegalArgumentException {
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
		
		loadColourLowerBound(cmd); //Check me first!
		loadColourUpperBound(cmd);
		loadGenerateCSV(cmd);
		loadGradient(cmd);
	}
	
	private void loadGradient(CommandLine cmd) {
		if(cmd.hasOption("colourGradient")) {
			colourGradient = Integer.parseInt(cmd.getOptionValue("colourGradient"));
			if (colourGradient < 1 || colourGradient > HeatMapGradient.getGradientCount()) {
				throw new IllegalArgumentException("Heat map colour gradient : " + colourGradient + ". Must be between 1 and " + HeatMapGradient.getGradientCount() + " (inclusive).");
			}
			log.info("Using heat map colour gradient " + colourGradient + ".");
		} else {
			log.info("Using the default heat map colour gradient " + colourGradient + ".");
		}
	}
	
	private void loadGenerateCSV(CommandLine cmd) {
		generateCSV = cmd.hasOption("generateCSV");
		log.info("Generate CSV file flag is set to " + generateCSV);
	}
	
	private void loadColourLowerBound(CommandLine cmd) {
		if(cmd.hasOption("colourLowerBound")) {
			colourLowerBound = Double.parseDouble(cmd.getOptionValue("colourLowerBound"));
			if (colourLowerBound < 0) {
				throw new IllegalArgumentException("Invalid heat map colour lower bound: " + colourLowerBound);
			}
			log.info("Using heat map colour lower bound: " + colourLowerBound);
		} else {
			log.info("No explicit lower bound set for the heat map colour. The lower bound will be automatically calculated.");
		}
	}
	
	private void loadColourUpperBound(CommandLine cmd) {
		if(cmd.hasOption("colourUpperBound")) {
			colourUpperBound = Double.parseDouble(cmd.getOptionValue("colourUpperBound"));
			if (colourLowerBound != null && (colourLowerBound >= colourUpperBound)) {
				throw new IllegalArgumentException("Invalid heat map colour upper bound: " + colourUpperBound);
			}
			log.info("Using heat map colour upper bound: " + colourUpperBound);
		} else {
			log.info("No explicit upper bound set for the heat map colour. The upper bound will be automatically calculated.");
		}
	}
	
	public boolean allowAggregateFunctionNone() {return false;}
	public boolean isAggregationMandatory() {return true;}

	public boolean isGenerateCSV() {
		return generateCSV;
	}

	public Double getColourLowerBound() {
		return colourLowerBound;
	}

	public Double getColourUpperBound() {
		return colourUpperBound;
	}

	public int getColourGradient() {
		return colourGradient;
	}
}
