package com.dbf.naps.data.analysis.query.integrated;

import java.io.File;
import java.util.List;

import com.dbf.naps.data.analysis.heatmap.NAPSHeatMapExporter;
import com.dbf.naps.data.analysis.query.NAPSDataQuery;
import com.dbf.naps.data.db.mappers.ContinuousDataMapper;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.db.mappers.IntegratedDataMapper;

public class NAPSIntegratedDataQuery extends NAPSDataQuery {

	public NAPSIntegratedDataQuery(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSIntegratedDataQuery dataQuery = new NAPSIntegratedDataQuery(args);
		dataQuery.run();
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
		return new IntegratedQueryRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite);
	}	
}
