package com.dbf.naps.data.analysis.query;

import java.io.File;
import com.dbf.naps.data.analysis.ExtendedDataQueryRunner;

public abstract class NAPSExtendedDataQuery<O extends ExtendedDataQueryOptions> extends NAPSDataQuery<O> {

	public NAPSExtendedDataQuery(String[] args) {
		super(args);
	}
	
	@Override
	protected String getFileExtension() {
		return ".csv";
	}
	
	@Override
	protected Runnable processFile(File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		return new ExtendedDataQueryRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite, getDataset());
	}	
}
