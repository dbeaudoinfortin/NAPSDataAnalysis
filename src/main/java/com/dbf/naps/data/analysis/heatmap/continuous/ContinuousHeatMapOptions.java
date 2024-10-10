package com.dbf.naps.data.analysis.heatmap.continuous;

import com.dbf.naps.data.analysis.heatmap.HeatMapOptions;

public class ContinuousHeatMapOptions extends HeatMapOptions {

	public ContinuousHeatMapOptions(String[] args) throws IllegalArgumentException {
		super(args);
	}

	@Override
	public boolean allowAggregationFieldHour() {
		return true;
	}
}
