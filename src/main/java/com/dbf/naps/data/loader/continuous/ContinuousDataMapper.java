package com.dbf.naps.data.loader.continuous;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface ContinuousDataMapper {
	
	@Select("SELECT id from naps.pollutants where name = #{name}")
	public Integer getPollutantID(String name);
	
	@Insert("INSERT into naps.pollutants (name)"
			+ " values (#{name})"
			+ " ON CONFLICT DO NOTHING;")
	public int insertPollutant(String name);
	
	@Select("SELECT id from naps.sites where NAPS_id = #{NAPSId}")
	public Integer getSiteID(Integer NAPSId);
	
	@Insert("INSERT into naps.sites (NAPS_id, city_name, prov_terr, latitude, longitude)"
			+ " values (#{NAPSId}, #{cityName}, #{provTerr}, #{latitude}, #{longitude})"
			+ " ON CONFLICT DO NOTHING;")
	public int insertSite(Integer NAPSId, String cityName, String provTerr, BigDecimal latitude, BigDecimal longitude);
	
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
