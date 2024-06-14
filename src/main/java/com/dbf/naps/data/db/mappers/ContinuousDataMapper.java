package com.dbf.naps.data.db.mappers;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.dbf.naps.data.records.ContinuousDataRecord;
import com.dbf.naps.data.records.DataGroup;

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
	
	@Override
	@Select("<script>"
			+ "select"
			+ " <if test=\"groupByYear\">d.year as year</if>"
			+ " <if test=\"groupByPollutant\"><if test=\"groupByYear\">,</if>p.name as pollutantName</if>"
			+ " <if test=\"groupBySite\"><if test=\"groupByYear || groupByPollutant\">,</if>s.naps_id as siteID</if>"
			+ " from naps.continuous_data d"
			+ " <if test=\"groupByPollutant || (pollutants != null &amp;&amp; !pollutants.isEmpty())\">inner join naps.pollutants p on d.pollutant_id = p.id</if>"
			+ " <if test=\"groupBySite || (sites != null &amp;&amp; !sites.isEmpty())\">inner join naps.sites s on d.site_id = s.id</if>"
			+ " where"
			+ " year &gt;= #{startYear} and year &lt;= #{endYear}"
			+ "<if test=\"sites != null &amp;&amp; !sites.isEmpty()\">"
			+ " and s.naps_id in <foreach collection='sites' item='site' index='index' open='(' separator = ',' close=')'>{site}</foreach>"
			+ "</if>"
			+ "<if test=\"pollutants != null &amp;&amp; !pollutants.isEmpty()\">"
			+ " and p.name in <foreach collection='pollutants' item='pollutant' index='index' open='(' separator = ',' close=')'>pollutant</foreach>"
			+ "</if>"
			+ " group by"
			+ " <if test=\"groupByYear\">d.year</if>"
			+ " <if test=\"groupByPollutant\"><if test=\"groupByYear\">,</if>p.name</if>"
			+ " <if test=\"groupBySite\"><if test=\"groupByYear || groupByPollutant\">,</if>s.naps_id</if>"
			+ "</script>")
	public List<DataGroup> getDataGroups(int startYear, int endYear, Collection<String> pollutants,  Collection<Integer> sites, boolean groupByYear, boolean groupByPollutant, boolean groupBySite);
}
