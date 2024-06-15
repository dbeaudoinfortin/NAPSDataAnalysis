package com.dbf.naps.data.exporter.continuous;

import com.dbf.naps.data.exporter.NAPSDataExporter;

public class NAPSContinuousDataExporter extends NAPSDataExporter {

	public NAPSContinuousDataExporter(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSContinuousDataExporter dataExporter = new NAPSContinuousDataExporter(args);
		dataExporter.run();
	}

	@Override
	protected String getDataset() {
		return "Continuous";
	}
}
