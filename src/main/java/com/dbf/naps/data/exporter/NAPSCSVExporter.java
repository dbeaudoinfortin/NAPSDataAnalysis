package com.dbf.naps.data.exporter;

public abstract class NAPSCSVExporter extends NAPSDataExporter<ExporterOptions> {

	public NAPSCSVExporter(String[] args) {
		super(args);
	}

	@Override
	public Class<ExporterOptions> getOptionsClass(){
		return ExporterOptions.class;
	}

	@Override
	protected String getFileExtension() {
		return ".csv";
	}
}
