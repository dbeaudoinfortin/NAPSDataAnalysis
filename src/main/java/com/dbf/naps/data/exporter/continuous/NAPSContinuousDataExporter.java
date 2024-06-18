package com.dbf.naps.data.exporter.continuous;

import java.io.File;
import java.util.List;

import com.dbf.naps.data.db.mappers.ContinuousDataMapper;
import com.dbf.naps.data.db.mappers.DataMapper;
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
		return List.of(ContinuousDataMapper.class, DataMapper.class);
	}
	
	@Override
	protected String getDataset() {
		return "Continuous";
	}

	@Override
	protected Runnable processFile(File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		return new ContinuousExporterRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite);
	}
}
