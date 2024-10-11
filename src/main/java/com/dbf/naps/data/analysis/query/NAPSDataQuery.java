package com.dbf.naps.data.analysis.query;

import java.io.File;
import com.dbf.naps.data.analysis.ExtendedDataQueryRunner;
import com.dbf.naps.data.exporter.NAPSCSVExporter;

public abstract class NAPSDataQuery<O extends ExtendedDataQueryOptions> extends NAPSCSVExporter<O> {

	public NAPSDataQuery(String[] args) {
		super(args);
	}
	
	@Override
	protected Runnable processFile(File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		return new ExtendedDataQueryRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite, getDataset());
	}	
}
