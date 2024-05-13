package com.dbf.naps.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseOptions {

	private static final Logger log = LoggerFactory.getLogger(BaseOptions.class);
	
	private int threadCount = 1;

	public BaseOptions(String[] args) throws IllegalArgumentException {
		getOptions().addOption("t","threadCount", true, "Maximum number of parallel threads.");	
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
		
		loadThreadCount(cmd);
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

	public String printOptions()
	{
		StringWriter sw = new StringWriter();
		PrintWriter  writer = new PrintWriter(sw);
		
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(writer, 120, " ", null, getOptions(), formatter.getLeftPadding(), formatter.getDescPadding(), null, false);
		writer.flush();
		return sw.toString();
	}

	protected abstract Options getOptions();

	public int getThreadCount() {
		return threadCount;
	}


}
