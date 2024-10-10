package com.dbf.naps.data.exporter.integrated;

import java.io.File;

import com.dbf.naps.data.exporter.ExporterOptions;
import com.dbf.naps.data.exporter.NAPSCSVExporter;

public class NAPSIntegratedDataExporter extends NAPSCSVExporter<ExporterOptions> {
	
	public NAPSIntegratedDataExporter(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSIntegratedDataExporter dataExporter = new NAPSIntegratedDataExporter(args);
		dataExporter.run();
	}
	
	@Override
	public Class<ExporterOptions> getOptionsClass(){
		return ExporterOptions.class;
	}

	@Override
	protected String getDataset() {
		return "Integrated";
	}

	@Override
	protected Runnable processFile(File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		return new IntegratedExporterRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, specificYear, specificPollutant, specificSite);
	}
}