package com.dbf.naps.data.exporter.continuous;

import java.io.File;

import com.dbf.naps.data.exporter.ExporterOptions;
import com.dbf.naps.data.exporter.NAPSCSVExporter;

public class NAPSContinuousDataExporter extends NAPSCSVExporter<ExporterOptions> {

	public NAPSContinuousDataExporter(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSContinuousDataExporter dataExporter = new NAPSContinuousDataExporter(args);
		dataExporter.run();
	}
	
	@Override
	public Class<ExporterOptions> getOptionsClass(){
		return ExporterOptions.class;
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
