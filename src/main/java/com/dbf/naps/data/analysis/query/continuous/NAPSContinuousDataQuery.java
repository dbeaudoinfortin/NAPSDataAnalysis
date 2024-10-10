package com.dbf.naps.data.analysis.query.continuous;

import com.dbf.naps.data.analysis.query.NAPSDataQuery;

public class NAPSContinuousDataQuery extends NAPSDataQuery {

	public NAPSContinuousDataQuery(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSContinuousDataQuery dataQuery = new NAPSContinuousDataQuery(args);
		dataQuery.run();
	}
	
	@Override
	protected String getDataset() {
		return "Continuous";
	}
}
