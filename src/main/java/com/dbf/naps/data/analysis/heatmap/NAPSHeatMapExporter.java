package com.dbf.naps.data.analysis.heatmap;

import com.dbf.naps.data.exporter.NAPSDataExporter;

public abstract class NAPSHeatMapExporter extends NAPSDataExporter<HeatMapOptions> {

	public NAPSHeatMapExporter(String[] args) {
		super(args);
	}

	@Override
	public Class<HeatMapOptions> getOptionsClass(){
		return HeatMapOptions.class;
	}

	@Override
	protected String getFileExtension() {
		return "png";
	}
}
