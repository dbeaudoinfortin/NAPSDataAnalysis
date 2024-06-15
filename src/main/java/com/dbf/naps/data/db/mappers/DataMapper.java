package com.dbf.naps.data.db.mappers;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.dbf.naps.data.records.DataRecordGroup;
import com.dbf.naps.data.records.ExportDataRecord;

public interface DataMapper {

	@Select("<script>"
			+ "select"
			+ " <if test=\"groupByYear\">d.year as year</if>"
			+ " <if test=\"groupByPollutant\"><if test=\"groupByYear\">,</if>p.name as pollutantName</if>"
			+ " <if test=\"groupBySite\"><if test=\"groupByYear || groupByPollutant\">,</if>s.naps_id as siteID</if>"
			+ " <if test=\"dataset.equals(&quot;Continuous&quot;)\">from naps.continuous_data d</if>"
			+ " <if test=\"dataset.equals(&quot;Integrated&quot;)\">from naps.integrated_data d</if>"
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
	public List<DataRecordGroup> getDataGroups(int startYear, int endYear, Collection<String> pollutants, Collection<Integer> sites, boolean groupByYear, boolean groupByPollutant, boolean groupBySite, String dataset);
	
	@Select("<script>"
		+ "select"
		+ " s.naps_id as siteNapsId,"
		+ " s.station_name as siteName,"
		+ " m.report_type as reportType,"
		+ " m.method as method,"
		+ " p.name as pollutantName,"
		+ " d.date_time as datetime,"
		+ " d.data as data,"
		+ " m.units as units"
		+ " <if test=\"dataset.equals(&quot;Continuous&quot;)\">from naps.continuous_data d</if>"
		+ " <if test=\"dataset.equals(&quot;Integrated&quot;)\">from naps.integrated_data d</if>"
		+ " inner join naps.pollutants p on d.pollutant_id = p.id"
		+ " inner join naps.sites s on d.site_id = s.id"
		+ " inner join naps.methods m on d.method_id = m.id"
		+ " where"
		+ " <if test=\"years != null &amp;&amp; !years.isEmpty()\">year in <foreach collection='years' item='year' index='index' open='(' separator = ',' close=')'>#{year}</foreach></if>"
		+ " <if test=\"sites != null &amp;&amp; !sites.isEmpty()\">"
		+ "   <if test=\"years != null  &amp;&amp; !years.isEmpty()\">and</if>"
		+ "   s.naps_id in <foreach collection='sites' item='site' index='index' open='(' separator = ',' close=')'>#{site}</foreach>"
		+ " </if>"
		+ " <if test=\"pollutants != null &amp;&amp; !pollutants.isEmpty()\">"
		+ "   <if test=\"(years != null  &amp;&amp; !years.isEmpty()) || (sites != null &amp;&amp; !sites.isEmpty()) \">and</if>"
		+ "   p.name in <foreach collection='pollutants' item='pollutant' index='index' open='(' separator = ',' close=')'>#{pollutant}</foreach>"
		+ " </if>"
		+ " order by s.naps_id, m.dataset, m.method, p.name, d.date_time"
		+ "</script>")
	public List<ExportDataRecord> getData(Collection<Integer> years, Collection<String> pollutants, Collection<Integer> sites, String dataset);
}
