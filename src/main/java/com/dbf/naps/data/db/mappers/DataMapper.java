package com.dbf.naps.data.db.mappers;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.dbf.naps.data.analysis.AggregateFunction;
import com.dbf.naps.data.analysis.AggregationField;
import com.dbf.naps.data.analysis.DataQueryRecord;
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
			+ " and s.naps_id in <foreach collection='sites' item='site' index='index' open='(' separator = ',' close=')'>#{site}</foreach>"
			+ "</if>"
			+ "<if test=\"pollutants != null &amp;&amp; !pollutants.isEmpty()\">"
			+ " and p.name in <foreach collection='pollutants' item='pollutant' index='index' open='(' separator = ',' close=')'>#{pollutant}</foreach>"
			+ "</if>"
			+ " group by"
			+ " <if test=\"groupByYear\">d.year</if>"
			+ " <if test=\"groupByPollutant\"><if test=\"groupByYear\">,</if>p.name</if>"
			+ " <if test=\"groupBySite\"><if test=\"groupByYear || groupByPollutant\">,</if>s.naps_id</if>"
			+ "</script>")
	public List<DataRecordGroup> getExportDataGroups(int startYear, int endYear, Collection<String> pollutants, Collection<Integer> sites, boolean groupByYear, boolean groupByPollutant, boolean groupBySite, String dataset);

	public List<? extends ExportDataRecord> getExportData(Collection<Integer> years, Collection<String> pollutants, Collection<Integer> sites, int offset , int limit);

	public List<DataQueryRecord> getQueryData(Collection<AggregationField> fields, AggregateFunction function, Collection<Integer> years, Collection<String> pollutants, Collection<Integer> sites, Collection<Integer> months, String siteName, String cityName, Collection<String> provTerr);
}
