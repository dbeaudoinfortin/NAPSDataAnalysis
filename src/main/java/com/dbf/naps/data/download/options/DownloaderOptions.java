package com.dbf.naps.data.download.options;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.BaseOptions;

public class DownloaderOptions extends BaseOptions {

	private static final Logger log = LoggerFactory.getLogger(DownloaderOptions.class);
	
	private Path downloadPath;
	private boolean overwriteFiles = false;
	
	static {
		getOptions().addRequiredOption("p","downloadPath", true, "Local path for downloaded files.");
		getOptions().addOption("o","overwriteFiles", false, "Replace existing files.");	
	}
	
	public DownloaderOptions(String[] args) throws IllegalArgumentException {
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
		
		loadDownloadPath(cmd);
		loadOverwriteFiles(cmd);
	}
	
	private void loadOverwriteFiles(CommandLine cmd) {
		overwriteFiles = cmd.hasOption("overwriteFiles");
		log.info("Overwrite existing files flag set to: " + overwriteFiles);
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

	public boolean isOverwriteFiles() {
		return overwriteFiles;
	}
}
