package com.dbf.naps.data.exporter.integrated;

import java.io.File;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.db.mappers.IntegratedDataMapper;
import com.dbf.naps.data.exporter.ExtractorOptions;
import com.dbf.naps.data.exporter.ExporterRunner;

public class IntegratedExporterRunner extends ExporterRunner<ExtractorOptions> {
		
	public IntegratedExporterRunner(int threadId, ExtractorOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}

	@Override
	protected Class<? extends DataMapper> getDataMapper() {
		return IntegratedDataMapper.class;
	}
}
