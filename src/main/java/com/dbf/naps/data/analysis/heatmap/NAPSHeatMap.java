package com.dbf.naps.data.analysis.heatmap;

import java.util.List;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.exporter.NAPSDataExporter;

public abstract class NAPSHeatMap<O extends HeatMapOptions> extends NAPSDataExporter<O> {
	
	public NAPSHeatMap(String[] args) {
		super(args);
	}
	
	@Override
	protected void run() {
		super.run();
	}

	@Override
	protected String getFileExtension() {
		return ".png";
	}
	
	@Override
	protected List<Class<?>> getDBMappers() {
		return List.of(DataMapper.class);
	}
}
