package com.dbf.naps.data.analysis.query.continuous;

import com.dbf.naps.data.analysis.query.NAPSDataQuery;

public class NAPSContinuousDataQuery extends NAPSDataQuery<ContinuousDataQueryOptions> {

	public NAPSContinuousDataQuery(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSContinuousDataQuery dataQuery = new NAPSContinuousDataQuery(args);
		dataQuery.run();
	}
	
	@Override
	public Class<ContinuousDataQueryOptions> getOptionsClass(){
		return ContinuousDataQueryOptions.class;
	}
	
	@Override
	protected String getDataset() {
		return "Continuous";
	}
}
