package com.dbf.naps.data.analysis.query.integrated;

import com.dbf.naps.data.analysis.query.NAPSExtendedDataQuery;

public class NAPSIntegratedDataQuery extends NAPSExtendedDataQuery<IntegratedDataQueryOptions> {

	public NAPSIntegratedDataQuery(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSIntegratedDataQuery dataQuery = new NAPSIntegratedDataQuery(args);
		dataQuery.run();
	}
	
	@Override
	public Class<IntegratedDataQueryOptions> getOptionsClass(){
		return IntegratedDataQueryOptions.class;
	}
	
	@Override
	protected String getDataset() {
		return "Integrated";
	}
}
