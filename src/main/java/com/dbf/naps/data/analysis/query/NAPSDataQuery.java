package com.dbf.naps.data.analysis.query;

import java.io.File;
import com.dbf.naps.data.analysis.ExtendedDataQueryRunner;
import com.dbf.naps.data.exporter.NAPSCSVExporter;

public abstract class NAPSDataQuery extends NAPSCSVExporter<ExtendedDataQueryOptions> {

	public NAPSDataQuery(String[] args) {
		super(args);
	}

	@Override
	public Class<ExtendedDataQueryOptions> getOptionsClass(){
		return ExtendedDataQueryOptions.class;
	}
	
	@Override
	protected Runnable processFile(File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		return new ExtendedDataQueryRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite, getDataset());
	}	
}
