package com.dbf.naps.data.analysis.heatmap.continuous;

import java.io.File;
import com.dbf.naps.data.analysis.heatmap.NAPSHeatMap;

public class NAPSContinuousDataHeatMap extends NAPSHeatMap<ContinuousHeatMapOptions> {

	public NAPSContinuousDataHeatMap(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSContinuousDataHeatMap dataExporter = new NAPSContinuousDataHeatMap(args);
		dataExporter.run();
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
