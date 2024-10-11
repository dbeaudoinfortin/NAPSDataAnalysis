package com.dbf.naps.data.analysis.heatmap.integrated;

import java.io.File;
import com.dbf.naps.data.analysis.heatmap.NAPSHeatMap;

public class NAPSIntegratedDataHeatMap extends NAPSHeatMap<IntegratedHeatMapOptions> {

	public NAPSIntegratedDataHeatMap(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSIntegratedDataHeatMap dataExporter = new NAPSIntegratedDataHeatMap(args);
		dataExporter.run();
	}
	
	@Override
	protected String getDataset() {
		return "Integrated";
	}

	@Override
	protected Runnable processFile(File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		return new IntegratedHeatMapRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite);
	}
	
	@Override
	public Class<IntegratedHeatMapOptions> getOptionsClass(){
		return IntegratedHeatMapOptions.class;
	}
}
