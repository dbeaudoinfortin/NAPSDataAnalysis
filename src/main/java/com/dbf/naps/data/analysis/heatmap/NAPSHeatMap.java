package com.dbf.naps.data.analysis.heatmap;

import com.dbf.naps.data.analysis.query.NAPSDataQuery;

public abstract class NAPSHeatMap<O extends HeatMapOptions> extends NAPSDataQuery<O> {
	
	public NAPSHeatMap(String[] args) {
		super(args);
	}
	
	@Override
	protected void run() {
		super.run();
	}

	@Override
	protected String getFileExtension() {
		return ".png";
	}
}
