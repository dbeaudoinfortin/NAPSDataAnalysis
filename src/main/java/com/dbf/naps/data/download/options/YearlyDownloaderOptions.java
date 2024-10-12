package com.dbf.naps.data.download.options;

import java.time.Year;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class YearlyDownloaderOptions extends DownloaderOptions {

	private static final Logger log = LoggerFactory.getLogger(YearlyDownloaderOptions.class);
	
	private int yearStart = 1974;
	private int yearEnd   = Year.now().getValue();
	
	static {
		getOptions().addOption("ys","yearStart", true, "Start year (inclusive).");
		getOptions().addOption("ye","yearEnd", true, "End year (inclusive).");	
	}
	
	public YearlyDownloaderOptions(String[] args) throws IllegalArgumentException {
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
		
		loadYearStart(cmd); //Check me first!
		loadYearEnd(cmd);
	}

	private void loadYearEnd(CommandLine cmd) {
		if(cmd.hasOption("yearEnd")) {
			yearEnd = Integer.parseInt(cmd.getOptionValue("yearEnd"));
			if (yearEnd < yearStart || yearEnd > Year.now().getValue() ) {
				throw new IllegalArgumentException("Invalid end year: " + yearEnd);
			}
			log.info("Using end year: " + yearEnd);
		} else {
			log.info("Using default end year: " + yearEnd);
		}
	}
	
	private void loadYearStart(CommandLine cmd) {
		if(cmd.hasOption("yearStart")) {
			yearStart = Integer.parseInt(cmd.getOptionValue("yearStart"));
			if (yearStart < 1969 || yearStart > Year.now().getValue()) {
				throw new IllegalArgumentException("Invalid start year: " + yearStart);
			}
			log.info("Using start year: " + yearStart);
		} else {
			log.info("Using default start year: " + yearStart);
		}
	}

	public int getYearStart() {
		return yearStart;
	}

	public int getYearEnd() {
		return yearEnd;
	}
}
