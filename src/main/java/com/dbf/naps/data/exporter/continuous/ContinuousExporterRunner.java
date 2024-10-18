package com.dbf.naps.data.exporter.continuous;

import java.io.File;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.db.mappers.ContinuousDataMapper;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.exporter.ExtractorOptions;
import com.dbf.naps.data.exporter.ExporterRunner;

public class ContinuousExporterRunner extends ExporterRunner<ExtractorOptions> {
	
	public ContinuousExporterRunner(int threadId, ExtractorOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}

	@Override
	protected Class<? extends DataMapper> getDataMapper() {
		return ContinuousDataMapper.class;
	}
}
