package com.dbf.naps.data.analysis.integrated;

import java.io.File;
import java.util.List;

import com.dbf.naps.data.analysis.heatmap.NAPSHeatMapExporter;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.db.mappers.IntegratedDataMapper;

public class NAPSIntegratedDataHeatMap extends NAPSHeatMapExporter {

	public NAPSIntegratedDataHeatMap(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSIntegratedDataHeatMap dataExporter = new NAPSIntegratedDataHeatMap(args);
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
		return new IntegratedHeatMapRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite);
	}
}
