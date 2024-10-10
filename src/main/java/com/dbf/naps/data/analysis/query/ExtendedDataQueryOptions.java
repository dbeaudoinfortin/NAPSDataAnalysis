package com.dbf.naps.data.analysis.query;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import com.dbf.naps.data.analysis.DataQueryOptions;

public class ExtendedDataQueryOptions extends DataQueryOptions {

	static {
		getOptions().addOption("d3","dimension3", true, "Data field for dimension 3.");
		getOptions().addOption("d4","dimension4", true, "Data field for dimension 4.");
		getOptions().addOption("d5","dimension5", true, "Data field for dimension 5.");
	}

	public ExtendedDataQueryOptions(String[] args) throws IllegalArgumentException {
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
		
		loadAggregationField(cmd, 3, false);
		loadAggregationField(cmd, 4, false);
		loadAggregationField(cmd, 5, false);
	}
}
