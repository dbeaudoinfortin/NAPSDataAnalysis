package com.dbf.naps.data.analysis.query;

import com.dbf.naps.data.exporter.NAPSDataExporter;

public abstract class NAPSDataQuery extends NAPSDataExporter<ExtendedDataQueryOptions> {

	public NAPSDataQuery(String[] args) {
		super(args);
	}

	@Override
	public Class<ExtendedDataQueryOptions> getOptionsClass(){
		return ExtendedDataQueryOptions.class;
	}

	@Override
	protected String getFileExtension() {
		return ".csv";
	}
}
