package com.dbf.naps.data.db.mappers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import com.dbf.naps.data.records.ContinuousDataRecord;

public interface ContinuousDataMapper extends DataMapper {

	@Insert("INSERT into naps.continuous_data (site_id, pollutant_id, method_id, date_time, year, month, day, hour, day_of_week, data)"
			+ " values (#{siteId}, #{pollutantId}, #{methodId}, #{datetime}, #{year}, #{month}, #{day}, #{hour}, #{dayOfWeek}, #{data})"
			+ " ON CONFLICT DO NOTHING;")
	public int insertContinuousData(Integer siteId, String pollutantId, String methodId, Date datetime, Integer year, Integer month, Integer day, Integer hour, String dayOfWeek, BigDecimal data);
	
	@Insert("<script>"
        + "INSERT into naps.continuous_data (site_id, pollutant_id, method_id, date_time, year, month, day, hour, day_of_week, data) "
        + "values "
        + "<foreach collection='dataRecords' item='record' index='index' open='(' separator = '),(' close=')' >"
        + "#{record.siteId}, #{record.pollutantId}, #{record.methodId}, #{record.datetime}, #{record.year}, #{record.month}, #{record.day}, #{record.hour}, #{record.dayOfWeek}, #{record.data}"
        + "</foreach>"
        + " ON CONFLICT DO NOTHING;"
        + "</script>")
	public int insertContinuousDataBulk(List<ContinuousDataRecord> dataRecords);
}
