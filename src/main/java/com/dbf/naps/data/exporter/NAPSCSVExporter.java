package com.dbf.naps.data.exporter;

import java.util.List;

import com.dbf.naps.data.db.mappers.DataMapper;

public abstract class NAPSCSVExporter<O extends ExporterOptions> extends NAPSDataExporter<O> {

	public NAPSCSVExporter(String[] args) {
		super(args);
	}
	
	@Override
	protected List<Class<?>> getDBMappers() {
		return List.of(DataMapper.class);
	}

	@Override
	protected String getFileExtension() {
		return ".csv";
	}
}
