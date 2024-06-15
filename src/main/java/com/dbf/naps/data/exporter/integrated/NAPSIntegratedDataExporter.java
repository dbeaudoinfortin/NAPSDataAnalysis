package com.dbf.naps.data.exporter.integrated;

import com.dbf.naps.data.exporter.NAPSDataExporter;

public class NAPSIntegratedDataExporter extends NAPSDataExporter {
	
	public NAPSIntegratedDataExporter(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSIntegratedDataExporter dataExporter = new NAPSIntegratedDataExporter(args);
		dataExporter.run();
	}

	@Override
	protected String getDataset() {
		return "Integrated";
	}
}
