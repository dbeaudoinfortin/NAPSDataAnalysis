package com.dbf.naps.data.loader.integrated;

import java.util.List;

import org.apache.ibatis.annotations.Insert;

public interface IntegratedDataMapper {
	
	@Insert({"<script>", 
        "INSERT into naps.integrated_data (site_id, pollutant_id, date_time, year, month, day, fine, day_of_week, sample_mass, sample_vol, sample_duration, tsp, data) "
        + "values ",
        "<foreach collection='dataRecords' item='record' index='index' open='(' separator = '),(' close=')' >"
        + "#{record.siteId}, #{record.pollutantId}, #{record.datetime}, #{record.year}, #{record.month}, #{record.day}, "
        + "#{record.fine, typeHandler=org.apache.ibatis.type.BooleanTypeHandler}, #{record.dayOfWeek}, "
        + "#{record.mass}, #{record.volume}, #{record.tsp}, #{record.duration}, #{record.data}"
        + "</foreach>"
        + " ON CONFLICT DO NOTHING;",
        "</script>"})
	public int insertIntegratedDataBulk(List<IntegratedDataRecord> dataRecords);
}
