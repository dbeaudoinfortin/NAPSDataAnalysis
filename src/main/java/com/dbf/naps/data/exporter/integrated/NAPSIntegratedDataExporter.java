package com.dbf.naps.data.exporter.integrated;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.db.mappers.IntegratedDataMapper;
import com.dbf.naps.data.exporter.NAPSDataExporter;
import com.dbf.naps.data.records.DataGroup;

public class NAPSIntegratedDataExporter extends NAPSDataExporter {
	
	public NAPSIntegratedDataExporter(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSIntegratedDataExporter dataExporter = new NAPSIntegratedDataExporter(args);
		dataExporter.run();
	}

	@Override
	protected String getFilePrefix() {
		return "Integrated";
	}

	@Override
	protected List<DataGroup> getDataGroups(int startYear, int endYear, Collection<String> pollutants,
			Collection<Integer> sites, boolean groupByYear, boolean groupByPollutant, boolean groupBySite) {
		try(SqlSession session = getSqlSessionFactory().openSession(true)) {
			return session.getMapper(IntegratedDataMapper.class).getDataGroups(startYear, endYear, pollutants,
					sites, groupByYear, groupByPollutant,groupBySite);
		}
	}
	
	@Override
	protected Class<? extends DataMapper> getDataMapperClass() {
		return IntegratedDataMapper.class;
	}
}
