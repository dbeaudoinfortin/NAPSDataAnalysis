package com.dbf.naps.data.exporter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.DBOptions;

public class ExporterOptions extends DBOptions {

	private static final Logger log = LoggerFactory.getLogger(ExporterOptions.class);

	private Path dataPath;
	private int yearStart = 1974;
	private int yearEnd   = Year.now().getValue();
	
	private final Set<String>  pollutants = new HashSet<String>();
	private final Set<Integer> sites = new HashSet<Integer>();
	
	private boolean filePerYear = false;
	private boolean filePerPollutant = false;
	private boolean filePerSite = false;
	private boolean overwriteFiles = false;
	
	static {
		getOptions().addRequiredOption("p","dataPath", true, "Local path to save the data.");
		getOptions().addOption("ys","yearStart", true, "Start year (inclusive).");
		getOptions().addOption("ye","yearEnd", true, "End year (inclusive).");
		getOptions().addOption("pn","pollutants", true, "Comma-seperated list of pollutant names.");	
		getOptions().addOption("sid","sites", true, "Comma-seperated list of site IDs.");
		getOptions().addOption("fy","filePerYear", false, "Create a seperate file for each year.");
		getOptions().addOption("fp","filePerPollutant", false, "Create a seperate file for each pollutant.");
		getOptions().addOption("fs","filePerSite", false, "Create a seperate file for each site.");
		getOptions().addOption("o","overwriteFiles", false, "Replace existing files.");	
	}

	public ExporterOptions(String[] args) throws IllegalArgumentException {
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
		
		loadYearStart(cmd); //Check me first!
		loadYearEnd(cmd);
		loadDataPath(cmd);
		
		loadPollutants(cmd);
		loadSites(cmd);
		loadOverwriteFiles(cmd);
		
		filePerYear = cmd.hasOption("filePerYear");
		log.info("Will" + (filePerYear ? "" : " not") +  " create a file per year.");
		
		filePerPollutant = cmd.hasOption("filePerPollutant");
		log.info("Will" + (filePerPollutant ? "" : " not") +  " create a file per pollutant.");
		
		filePerSite = cmd.hasOption("filePerSite");
		log.info("Will" + (filePerSite ? "" : " not") +  " create a file per site.");
	}
	
	private void loadOverwriteFiles(CommandLine cmd) {
		overwriteFiles = cmd.hasOption("overwriteFiles");
		log.info("Overwrite existing files flag set to: " + overwriteFiles);
	}
	
	private void loadSites(CommandLine cmd) {
		if(cmd.hasOption("sites")) {
			for(String site : cmd.getOptionValue("sites").split(",")) {
				site = site.trim();
				if (site.isEmpty()) continue;
				
				try {
					sites.add(Integer.parseInt(site));
				} catch(Exception e) {
					throw new IllegalArgumentException("Specified site ID \"" + site + "\" is not an integer.");
				}
			}
			if(sites.isEmpty()) 
				throw new IllegalArgumentException("Must specify at least one site.");
			
			log.info("Using only the following site IDs: " + sites);
		} else {
			log.info("Using all site ID.");
		}
	}
	
	private void loadPollutants(CommandLine cmd) {
		if(cmd.hasOption("pollutants")) {
			for(String pollutant : cmd.getOptionValue("pollutants").split(",")) {
				pollutant = pollutant.trim();
				if (pollutant.isEmpty()) continue;
				pollutants.add(pollutant);
			}
			if(pollutants.isEmpty()) 
				throw new IllegalArgumentException("Must specify at least one pollutant.");
			
			log.info("Using only the following pollutants: " + pollutants);
		} else {
			log.info("Using all pollutants.");
		}
	}
	
	private void loadDataPath(CommandLine cmd) {
		if(cmd.hasOption("dataPath")) {
			dataPath = Paths.get(cmd.getOptionValue("dataPath"));
			File dataPathFile = dataPath.toFile();
			if (dataPathFile.exists() && !dataPathFile.isDirectory()) {
				throw new IllegalArgumentException("Data path " + dataPath + " is not a valid directory.");
			}
			log.info("Using data path: " + dataPath);
		} else {
			throw new IllegalArgumentException("Data path is required.");
		}
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
	
	public Path getDataPath() {
		return dataPath;
	}

	public int getYearStart() {
		return yearStart;
	}

	public int getYearEnd() {
		return yearEnd;
	}

	public Set<String> getPollutants() {
		return pollutants;
	}

	public Set<Integer> getSites() {
		return sites;
	}

	public boolean isFilePerYear() {
		return filePerYear;
	}

	public boolean isFilePerPollutant() {
		return filePerPollutant;
	}

	public boolean isFilePerSite() {
		return filePerSite;
	}
	
	public boolean isOverwriteFiles() {
		return overwriteFiles;
	}
}
