package com.dbf.naps.data.analysis.heatmap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.heatmaps.HeatMapGradient;
import com.dbf.naps.data.analysis.DataAnalysisOptions;

public abstract class HeatMapOptions extends DataAnalysisOptions {

	private static final Logger log = LoggerFactory.getLogger(HeatMapOptions.class);

	private Double colourLowerBound;
	private Double colourUpperBound;
	private int colourGradient = 1;
	
	private boolean generateCSV = false;
	private boolean generateJSON = false;
	private boolean gridLines = false;
	private boolean gridValues = false;
	private int digits = 4;
	private double fontScale = 1.0;
	
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
		getOptions().addOption("cg","colourGradient", true, "Heat map colour gradient choice. Values are 1-" + (HeatMapGradient.getCannedGradientCount()) + " (inclusive).");
		getOptions().addOption("csv","generateCSV", false, "Generate a corresponding CSV file containing the raw data for each heat map.");
		getOptions().addOption("json","generateJSON", false, "Generate a corresponding JSON file containing the raw data for each heat map.");	
		getOptions().addOption("gl","gridLines", false, "Include grid lines on the heat map.");
		getOptions().addOption("gv","gridValues", false, "Include grid values on the heat map.");	
		getOptions().addOption("ld","legendDecimals", true, "Number of decimal digits to use for the legend, 0 to 20 (inclusive). Default is 4.");
		getOptions().addOption("fts","fontScale", true, "Relative font size. Must be greater than 0 and no more than 10. Default is 1.");	
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
		loadGenerateJSON(cmd);
		loadGridLines(cmd);
		loadGridValues(cmd);
		loadFontScale(cmd);
		loadDigits(cmd);
		loadGradient(cmd);
	}
	
	private void loadDigits(CommandLine cmd) {
		if(cmd.hasOption("legendDecimals")) {
			digits = Integer.parseInt(cmd.getOptionValue("legendDecimals"));
			if (digits < 0) {
				throw new IllegalArgumentException("Invalid number of legend decimals, must be at least zero: " + digits);
			}
			if (digits > 20) {
				throw new IllegalArgumentException("Invalid number of legend decimals, must be no more than 20: " + digits);
			}
			log.info("Using " + digits + " legend decimal(s).");
		} else {
			log.info("Using the default number legend decimals: " + digits);
		}
	}
	
	private void loadFontScale(CommandLine cmd) {
		if(cmd.hasOption("fontScale")) {
			fontScale = Double.parseDouble(cmd.getOptionValue("fontScale"));
			if (fontScale <= 0.0) {
				throw new IllegalArgumentException("Invalid font scale, must be greater than zero: " + fontScale);
			}
			if (fontScale > 10.0) {
				throw new IllegalArgumentException("Invalid font scale, must be no more than 10: " + fontScale);
			}
			log.info("Using a font scale of " + fontScale);
		} else {
			log.info("Using the default font scale of " + fontScale);
		}
	}
	
	private void loadGridValues(CommandLine cmd) {
		gridValues = cmd.hasOption("gridValues");
		log.info("Include grid values flag is set to " + gridValues);
	}
	
	private void loadGradient(CommandLine cmd) {
		if(cmd.hasOption("colourGradient")) {
			colourGradient = Integer.parseInt(cmd.getOptionValue("colourGradient"));
			if (colourGradient < 1 || colourGradient > HeatMapGradient.getCannedGradientCount()) {
				throw new IllegalArgumentException("Heat map colour gradient : " + colourGradient + ". Must be between 1 and " + HeatMapGradient.getCannedGradientCount() + " (inclusive).");
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
	
	private void loadGenerateJSON(CommandLine cmd) {
		generateJSON = cmd.hasOption("generateJSON");
		log.info("Generate JSON file flag is set to " + generateJSON);
	}
	
	private void loadGridLines(CommandLine cmd) {
		gridLines = cmd.hasOption("gridLines");
		log.info("Include grid lines flag is set to " + gridLines);
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

	public boolean isGenerateJSON() {
		return generateJSON;
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

	public boolean isGridLines() {
		return gridLines;
	}

	public boolean isGridValues() {
		return gridValues;
	}

	public int getDigits() {
		return digits;
	}

	public double getFontScale() {
		return fontScale;
	}
}
