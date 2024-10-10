package com.dbf.naps.data.exporter.continuous;

import java.io.File;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.db.mappers.ContinuousDataMapper;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.exporter.ExporterOptions;
import com.dbf.naps.data.exporter.ExporterRunner;

public class ContinuousExporterRunner extends ExporterRunner<ExporterOptions> {
	
	public ContinuousExporterRunner(int threadId, ExporterOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}

	@Override
	protected Class<? extends DataMapper> getDataMapper() {
		return ContinuousDataMapper.class;
	}
}
