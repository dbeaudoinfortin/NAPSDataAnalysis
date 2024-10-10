package com.dbf.naps.data.analysis.heatmap.continuous;

import java.io.File;
import java.util.List;

import com.dbf.naps.data.analysis.heatmap.NAPSHeatMapExporter;
import com.dbf.naps.data.db.mappers.ContinuousDataMapper;
import com.dbf.naps.data.db.mappers.DataMapper;

public class NAPSContinuousDataHeatMap extends NAPSHeatMapExporter<ContinuousHeatMapOptions> {

	public NAPSContinuousDataHeatMap(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSContinuousDataHeatMap dataExporter = new NAPSContinuousDataHeatMap(args);
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
		return new ContinuousHeatMapRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite);
	}	
	
	@Override
	public Class<ContinuousHeatMapOptions> getOptionsClass(){
		return ContinuousHeatMapOptions.class;
	}
}
