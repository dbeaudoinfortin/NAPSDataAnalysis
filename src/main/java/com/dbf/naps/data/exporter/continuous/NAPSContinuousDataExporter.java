package com.dbf.naps.data.exporter.continuous;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import com.dbf.naps.data.db.mappers.ContinuousDataMapper;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.exporter.NAPSDataExporter;
import com.dbf.naps.data.records.DataGroup;

public class NAPSContinuousDataExporter extends NAPSDataExporter {

	public NAPSContinuousDataExporter(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSContinuousDataExporter dataExporter = new NAPSContinuousDataExporter(args);
		dataExporter.run();
	}

	@Override
	protected String getFilePrefix() {
		return "Continuous";
	}

	@Override
	protected List<DataGroup> getDataGroups(int startYear, int endYear, Collection<String> pollutants,
			Collection<Integer> sites, boolean groupByYear, boolean groupByPollutant, boolean groupBySite) {
		try(SqlSession session = getSqlSessionFactory().openSession(true)) {
			return session.getMapper(ContinuousDataMapper.class).getDataGroups(startYear, endYear, pollutants,
					sites, groupByYear, groupByPollutant,groupBySite);
		}
	}

	@Override
	protected Class<? extends DataMapper> getDataMapperClass() {
		return ContinuousDataMapper.class;
	}
}
