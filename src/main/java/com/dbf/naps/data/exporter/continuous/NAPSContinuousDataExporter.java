package com.dbf.naps.data.exporter.continuous;

import java.io.File;

import com.dbf.naps.data.exporter.ExtractorOptions;
import com.dbf.naps.data.exporter.NAPSCSVExporter;

public class NAPSContinuousDataExporter extends NAPSCSVExporter<ExtractorOptions> {

	public NAPSContinuousDataExporter(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSContinuousDataExporter dataExporter = new NAPSContinuousDataExporter(args);
		dataExporter.run();
	}
	
	@Override
	public Class<ExtractorOptions> getOptionsClass(){
		return ExtractorOptions.class;
	}

	@Override
	protected String getDataset() {
		return "Continuous";
	}

	@Override
	protected Runnable processFile(File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		return new ContinuousExporterRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite);
	}
}
