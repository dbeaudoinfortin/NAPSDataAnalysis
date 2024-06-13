package com.dbf.naps.data.exporter.continuous;

import java.io.File;
import java.util.List;

import com.dbf.naps.data.db.mappers.ContinuousDataMapper;
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
	protected List<Class<?>> getDBMappers() {
		return List.of(ContinuousDataMapper.class);
	}

	@Override
	protected Runnable processFile(File dataFile, Integer year, String pollutant, Integer site) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getFilePrefix() {
		return "Continuous";
	}

}
