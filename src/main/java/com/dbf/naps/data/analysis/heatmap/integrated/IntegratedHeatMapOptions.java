package com.dbf.naps.data.analysis.heatmap.integrated;

import com.dbf.naps.data.analysis.heatmap.HeatMapOptions;

public class IntegratedHeatMapOptions extends HeatMapOptions {

	public IntegratedHeatMapOptions(String[] args) throws IllegalArgumentException {
		super(args);
	}

	@Override
	public boolean allowAggregationFieldHour() {
		return true;
	}
	
	@Override
	public boolean supportsAQHI() {
		return false;
	}
}
