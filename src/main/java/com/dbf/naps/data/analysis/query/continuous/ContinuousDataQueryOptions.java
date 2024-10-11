package com.dbf.naps.data.analysis.query.continuous;

import com.dbf.naps.data.analysis.query.ExtendedDataQueryOptions;

public class ContinuousDataQueryOptions extends ExtendedDataQueryOptions {

	public ContinuousDataQueryOptions(String[] args) throws IllegalArgumentException {
		super(args);
	}

	@Override
	public boolean allowAggregationFieldHour() {
		return true;
	}
}
