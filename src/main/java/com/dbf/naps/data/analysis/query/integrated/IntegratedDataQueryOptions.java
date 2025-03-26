package com.dbf.naps.data.analysis.query.integrated;

import com.dbf.naps.data.analysis.query.ExtendedDataQueryOptions;

public class IntegratedDataQueryOptions extends ExtendedDataQueryOptions {

	public IntegratedDataQueryOptions(String[] args) throws IllegalArgumentException {
		super(args);
	}

	@Override
	public boolean allowAggregationFieldHour() {
		return false;
	}
	
	@Override
	public boolean supportsAQHI() {
		return false;
	}
}
