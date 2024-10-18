package com.dbf.naps.data.exporter;

public abstract class NAPSCSVExporter<O extends ExtractorOptions> extends NAPSDataExtractor<O> {

	public NAPSCSVExporter(String[] args) {
		super(args);
	}

	@Override
	protected String getFileExtension() {
		return ".csv";
	}
}
