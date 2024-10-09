package com.dbf.naps.data.analysis.heatmap.continuous;

import java.io.File;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.analysis.heatmap.HeatMapOptions;
import com.dbf.naps.data.analysis.heatmap.HeatMapRunner;
import com.dbf.naps.data.db.mappers.ContinuousDataMapper;
import com.dbf.naps.data.db.mappers.DataMapper;

public class ContinuousHeatMapRunner extends HeatMapRunner {
		
	public ContinuousHeatMapRunner(int threadId, HeatMapOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}

	protected String getDataset(){
		return "Continuous";
	}

	@Override
	protected Class<? extends DataMapper> getDataMapper() {
		return ContinuousDataMapper.class;
	}
}
