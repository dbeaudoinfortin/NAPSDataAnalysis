package com.dbf.naps.data.loader;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.DBOptions;

public class LoaderOptions extends DBOptions {

	private static final Logger log = LoggerFactory.getLogger(LoaderOptions.class);

	private Path dataPath;
	private boolean includeNulls = false;
	
	static {
		getOptions().addRequiredOption("p","dataPath", true, "Local path for raw data files previously downloaded.");
		getOptions().addOption("n"  ,"nulls" , false, "Include null values.");
	}

	public LoaderOptions(String[] args) throws IllegalArgumentException {
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
		
		loadDataPath(cmd);
		loadIncludeNulls(cmd);
	}
	
	private void loadIncludeNulls(CommandLine cmd) {
		if(cmd.hasOption("nulls")) {
			includeNulls = true;
			log.info("Will include null values.");
		} else {
			log.info("Will exclude null values.");
		}
	}
	
	private void loadDataPath(CommandLine cmd) {
		if(cmd.hasOption("dataPath")) {
			dataPath = Paths.get(cmd.getOptionValue("dataPath"));
			if (!dataPath.toFile().isDirectory()) {
				throw new IllegalArgumentException("Data path " + dataPath + " is not a valid directory.");
			}
			log.info("Using data path: " + dataPath);
		} else {
			throw new IllegalArgumentException("Data path is required.");
		}
	}

	public Path getDataPath() {
		return dataPath;
	}

	public boolean isIncludeNulls() {
		return includeNulls;
	}
}
