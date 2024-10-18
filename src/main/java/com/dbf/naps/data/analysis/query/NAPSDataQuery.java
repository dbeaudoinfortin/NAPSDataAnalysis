package com.dbf.naps.data.analysis.query;

import com.dbf.naps.data.analysis.DataQueryOptions;
import com.dbf.naps.data.exporter.NAPSDataExtractor;
import com.dbf.naps.data.utilities.Utils;

public abstract class NAPSDataQuery<O extends DataQueryOptions> extends NAPSDataExtractor<O> {

	public NAPSDataQuery(String[] args) {
		super(args);
	}
	
	@Override
	protected void appendFilename(StringBuilder fileName, Integer year, String pollutant, Integer site) {
		super.appendFilename(fileName, year, pollutant, site);
		if(getOptions().getFields() != null && !getOptions().getFields().isEmpty()) {
			fileName.append("_By ");
			Utils.prettyPrintStringList(getOptions().getFields().stream().map(f->f.getPrettyName()).toList(), fileName, false);
		}
	}
}
