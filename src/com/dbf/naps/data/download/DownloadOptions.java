package com.dbf.naps.data.download;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.BaseOptions;

public class DownloadOptions extends BaseOptions {

	private static final Logger log = LoggerFactory.getLogger(DownloadOptions.class);
	
	private final Options options = new Options();
	
	private Path downloadPath;
	private boolean overwriteFiles = false;
	private int yearStart = 1974;
	private int yearEnd   = Year.now().getValue();
	
	public DownloadOptions(String[] args) throws IllegalArgumentException {
		super(args);
		
		options.addRequiredOption("p","downloadPath", true, "Local path for downloaded files.");
		options.addOption("ys","yearStart", true, "Start year (inclusive).");
		options.addOption("ye","yearEnd", true, "End year (inclusive).");	
		options.addOption("o","overwriteFiles", false, "Replace existing files.");	
		loadFromArgs(args);
	}
	
	private void loadFromArgs(String[] args) throws IllegalArgumentException {
		CommandLine cmd = null;
		try {
			cmd = (new DefaultParser()).parse(options, args);
		}
		catch(ParseException e) {
			throw new IllegalArgumentException(e);
		}
		
		loadDownloadPath(cmd);
		loadYearStart(cmd); //Check me first!
		loadYearEnd(cmd);
		loadOverwriteFiles(cmd);
	}
	
	private void loadOverwriteFiles(CommandLine cmd) {
		overwriteFiles = cmd.hasOption("overwriteFiles");
		log.info("Overwrite existing files flag set to: " + overwriteFiles);
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
	
	private void loadDownloadPath(CommandLine cmd) {
		if(cmd.hasOption("downloadPath")) {
			downloadPath = Paths.get(cmd.getOptionValue("downloadPath"));
			if (!downloadPath.toFile().isDirectory()) {
				throw new IllegalArgumentException("Download path " + downloadPath + " is not a valid directory.");
			}
			log.info("Using download path: " + downloadPath);
		} else {
			throw new IllegalArgumentException("Download path is required.");
		}
	}

	public Path getDownloadPath() {
		return downloadPath;
	}

	public int getYearStart() {
		return yearStart;
	}

	public int getYearEnd() {
		return yearEnd;
	}

	public boolean isOverwriteFiles() {
		return overwriteFiles;
	}

	@Override
	protected Options getOptions() {
		return options;
	}
}
