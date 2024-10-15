package com.dbf.naps.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

public abstract class BaseOptions {

	private static final Logger log = LoggerFactory.getLogger(BaseOptions.class);
	
	private static final Options options = new Options();
	private final CommandLineParser parser = new DefaultParser(); 
	
	private int threadCount = 1;
	
	static {
		getOptions().addOption("t","threadCount", true, "Maximum number of parallel threads.");
		getOptions().addOption("v","verbose", false, "Make logging more verbose.");	
	}

	public BaseOptions(String[] args) throws IllegalArgumentException {
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
		
		loadThreadCount(cmd);	
		loadVerbose(cmd);
	}
	
	private void loadVerbose(CommandLine cmd) {
		if(cmd.hasOption("verbose")) {
			//Need to get the logback root logger and change it to DEBUG
	        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
	        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	        rootLogger.setLevel(ch.qos.logback.classic.Level.DEBUG);
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

	public static String printOptions()
	{
		StringWriter sw = new StringWriter();
		PrintWriter  writer = new PrintWriter(sw);
		
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(writer, 120, " ", null, getOptions(), formatter.getLeftPadding(), formatter.getDescPadding(), null, false);
		writer.flush();
		return sw.toString();
	}

	public int getThreadCount() {
		return threadCount;
	}

	public static Options getOptions() {
		return options;
	}
	
	public CommandLineParser getParser() {
		return parser;
	}
}
