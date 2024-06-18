package com.dbf.naps.data.exporter.integrated;

import java.io.File;
import java.util.List;

import com.dbf.naps.data.db.mappers.DataMapper;
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
		return List.of(IntegratedDataMapper.class, DataMapper.class);
	}

	@Override
	protected String getDataset() {
		return "Integrated";
	}

	@Override
	protected Runnable processFile(File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		return new IntegratedExporterRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite);
	}
}