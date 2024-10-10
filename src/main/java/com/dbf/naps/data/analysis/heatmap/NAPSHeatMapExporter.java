package com.dbf.naps.data.analysis.heatmap;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.exporter.NAPSDataExporter;

import javafx.application.Platform;

public abstract class NAPSHeatMapExporter<O extends HeatMapOptions> extends NAPSDataExporter<O> {
	
	private static final Logger log = LoggerFactory.getLogger(NAPSHeatMapExporter.class);

	public NAPSHeatMapExporter(String[] args) {
		super(args);
	}
	
	@Override
	protected void run() {
		log.info("Initializing rendering engine...");
		Platform.startup(() -> {
            log.info("Rendering engine initialized.");
        });
		
		try {
			super.run();
		} finally {
			log.info("Shutting down rendering engine...");
			Platform.exit();
	        log.info("Rendering engine shut down.");
		}
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
