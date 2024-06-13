package com.dbf.naps.data.exporter.integrated;

import java.io.File;
import java.util.List;

import com.dbf.naps.data.db.mappers.IntegratedDataMapper;
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
	protected List<Class<?>> getDBMappers() {
		return List.of(IntegratedDataMapper.class);
	}

	@Override
	protected Runnable processFile(File dataFile, Integer year, String pollutant, Integer site) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getFilePrefix() {
		return "Integrated";
	}

}
