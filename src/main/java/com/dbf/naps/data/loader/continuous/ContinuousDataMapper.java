package com.dbf.naps.data.loader.continuous;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;

public interface ContinuousDataMapper {

	@Insert("INSERT into naps.continuous_data (site_id, pollutant_id, date_time, year, month, day, hour, day_of_week, data)"
			+ " values (#{siteId}, #{pollutantId}, #{datetime}, #{year}, #{month}, #{day}, #{hour}, #{dayOfWeek}, #{data})"
			+ " ON CONFLICT DO NOTHING;")
	public int insertContinuousData(Integer siteId, String pollutantId, Date datetime, Integer year, Integer month, Integer day, Integer hour, String dayOfWeek, BigDecimal data);
	
	@Insert({"<script>", 
        "INSERT into naps.continuous_data (site_id, pollutant_id, date_time, year, month, day, hour, day_of_week, data) "
        + "values ",
        "<foreach collection='continuousDataRecords' item='record' index='index' open='(' separator = '),(' close=')' >"
        + "#{record.siteId}, #{record.pollutantId}, #{record.datetime}, #{record.year}, #{record.month}, #{record.day}, #{record.hour}, #{record.dayOfWeek}, #{record.data}"
        + "</foreach>"
        + " ON CONFLICT DO NOTHING;",
        "</script>"})
	public int insertContinuousDataBulk(List<ContinuousDataRecord> continuousDataRecords);
}
