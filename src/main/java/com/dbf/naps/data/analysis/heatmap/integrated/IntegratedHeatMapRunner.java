package com.dbf.naps.data.analysis.heatmap.integrated;

import java.io.File;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.analysis.heatmap.HeatMapOptions;
import com.dbf.naps.data.analysis.heatmap.HeatMapRunner;

public class IntegratedHeatMapRunner extends HeatMapRunner {
		
	public IntegratedHeatMapRunner(int threadId, HeatMapOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}

	protected String getDataset(){
		return "Integrated";
	}
}
