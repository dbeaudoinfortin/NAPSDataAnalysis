package com.dbf.naps.data.db.mappers;

import java.util.Collection;
import java.util.List;
import com.dbf.naps.data.records.DataGroup;

public interface DataMapper {

	public List<DataGroup> getDataGroups(int startYear, int endYear, Collection<String> pollutants,  Collection<Integer> sites, boolean groupByYear, boolean groupByPollutant, boolean groupBySite);
}
