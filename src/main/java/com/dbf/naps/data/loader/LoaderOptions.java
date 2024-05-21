package com.dbf.naps.data.loader;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.BaseOptions;

public class LoaderOptions extends BaseOptions {

	private static final Logger log = LoggerFactory.getLogger(LoaderOptions.class);

	private Path dataPath;
	private String dbHost = "localhost";
	private int    dbPort = 5432;
	private String dbName = "naps";
	private String dbUser = "postgres";
	private String dbPass = "password";
	private boolean includeNulls = false;
	
	static {
		getOptions().addRequiredOption("p","dataPath", true, "Local path for raw data files previously downloaded.");
		getOptions().addOption("dbh","dbHost", true, "Hostname for the PostgreSQL database. Default: localhost");
		getOptions().addOption("dbt","dbPort", true,  "Port for the PostgreSQL database. Default: 5432");
		getOptions().addOption("dbn","dbName", true,  "Database name for the PostgreSQL database. Default: naps");
		getOptions().addOption("dbu","dbUser", true,  "Database user name for the PostgreSQL database. Default: postgres");
		getOptions().addOption("dbp","dbPass", true,  "Database password for the PostgreSQL database. Default: password");
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
		loadDBHost(cmd);
		loadDBPort(cmd);
		loadDBName(cmd);
		loadDBUser(cmd);
		loadDBPass(cmd);
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
	
	private void loadDBPort(CommandLine cmd) {
		if(cmd.hasOption("dbPort")) {
			dbPort = Integer.parseInt(cmd.getOptionValue("dbPort"));
			if (dbPort < 0 || dbPort > 65535) {
				throw new IllegalArgumentException("DB port number: " + dbPort);
			}
			log.info("Using DB port number: " + dbPort);
		} else {
			log.info("Using default DB port number: " + dbPort);
		}
	}
	
	private void loadDBHost(CommandLine cmd) {
		if(cmd.hasOption("dbHost")) {
			dbHost = cmd.getOptionValue("dbHost");
			log.info("Using DB hostname: " + dbHost);
		} else {
			log.info("Using default DB hostname: " + dbHost);
		}
	}
	
	private void loadDBUser(CommandLine cmd) {
		if(cmd.hasOption("dbUser")) {
			dbUser = cmd.getOptionValue("dbUser");
			log.info("Using DB user name: " + dbUser);
		} else {
			log.info("Using default DB user name: " + dbUser);
		}
	}
	
	private void loadDBPass(CommandLine cmd) {
		if(cmd.hasOption("dbPass")) {
			dbPass = cmd.getOptionValue("dbPass");
			log.info("Using DB password: " + dbPass);
		} else {
			log.info("Using default DB password: " + dbPass);
		}
	}
	
	private void loadDBName(CommandLine cmd) {
		if(cmd.hasOption("dbName")) {
			dbName = cmd.getOptionValue("dbName");
			log.info("Using DB name: " + dbName);
		} else {
			log.info("Using default DB name: " + dbName);
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

	public String getDbHost() {
		return dbHost;
	}

	public int getDbPort() {
		return dbPort;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbUser() {
		return dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public boolean isIncludeNulls() {
		return includeNulls;
	}
}
