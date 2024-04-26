package com.dbf.naps.data.download;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadOptions {

	private static final Logger log = LoggerFactory.getLogger(DownloadOptions.class);
	
	private static final Options options = new Options();
	
	static {
		options.addRequiredOption("p","downloadPath", true, "Local path for downloaded files.");
		options.addOption("t","threadCount", true, "Maximum number of parallel threads.");
		options.addOption("ys","yearStart", true, "Start year (inclusive).");
		options.addOption("ye","yearEnd", true, "End year (inclusive).");	
		options.addOption("o","overwriteFiles", false, "Replace existing files.");	
	}
	
	private Path downloadPath;
	private int threadCount = 1;
	private boolean overwriteFiles = false;
	
	private int yearStart = 1974;
	private int yearEnd   = Year.now().getValue();
	

	public DownloadOptions(String[] args) throws IllegalArgumentException {
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
		loadThreadCount(cmd);
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

	private void loadThreadCount(CommandLine cmd) {
		if(cmd.hasOption("threadCount"))
		{
			threadCount = Integer.parseInt(cmd.getOptionValue("threadCount"));
			if (threadCount < 1) {
				throw new IllegalArgumentException("Invalid thread count: " + threadCount);
			}
			log.info("Using thread count: " + threadCount);
		} else {
			log.info("Using default thread count: " + threadCount);
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
	
	public static String printOptions()
	{
		StringWriter sw = new StringWriter();
		PrintWriter  writer = new PrintWriter(sw);
		
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(writer, 120, " ", null, options, formatter.getLeftPadding(), formatter.getDescPadding(), null, false);
		writer.flush();
		return sw.toString();
	}

	public Path getDownloadPath() {
		return downloadPath;
	}

	public int getThreadCount() {
		return threadCount;
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
}
