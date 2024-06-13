package com.dbf.naps.data.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;

import com.dbf.naps.data.records.IntegratedDataRecord;

public interface IntegratedDataMapper {
	
	@Insert("<script>"
        + "INSERT into naps.integrated_data (site_id, pollutant_id, method_id, sample_id, date_time, year, month, day, day_of_week, data) "
        + "values "
        + "<foreach collection='dataRecords' item='record' index='index' open='(' separator = '),(' close=')' >"
        + "#{record.siteId}, #{record.pollutantId}, #{record.methodId}, #{record.sampleId}, #{record.datetime}, #{record.year}, #{record.month}, #{record.day}, "
        + "#{record.dayOfWeek}, #{record.data}"
        + "</foreach>"
        + " ON CONFLICT DO NOTHING;"
        + "</script>")
	public int insertIntegratedDataBulk(List<IntegratedDataRecord> dataRecords);
}
