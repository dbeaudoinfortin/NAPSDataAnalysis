package com.dbf.naps.data.db.mappers;

import java.math.BigDecimal;
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
			+ " <if test=\"groupBySite || (sites != null &amp;&amp; !sites.isEmpty()) || (provTerr != null &amp;&amp; !provTerr.isEmpty()) "
			+ "|| (cityName != null &amp;&amp; !cityName.isEmpty()) || (siteName != null &amp;&amp; !siteName.isEmpty())\">inner join naps.sites s on d.site_id = s.id</if>"
			+ " where"
			+ " year &gt;= #{startYear} and year &lt;= #{endYear}"
			+ "<if test=\"valueUpperBound != null\">and d.data &lt;= #{valueUpperBound}</if>"
			+ "<if test=\"valueLowerBound != null\">and d.data &gt;= #{valueLowerBound}</if>"
			+ "<if test=\"siteName != null &amp;&amp; !siteName.isEmpty()\">and s.station_name LIKE '%#{siteName}%'</if>"
			+ "<if test=\"cityName != null &amp;&amp; !cityName.isEmpty()\">and s.city_name LIKE '%#{cityName}%'</if>"
			+ "<if test=\"provTerr != null &amp;&amp; !provTerr.isEmpty()\">and s.prov_terr in <foreach collection='provTerr' item='prov' index='index' open='(' separator = ',' close=')'>#{prov}</foreach></if>"
			+ "<if test=\"months != null &amp;&amp; !months.isEmpty()\">and d.month in<foreach collection='months' item='month' index='index' open='(' separator = ',' close=')'>#{month}</foreach></if>"
			+ "<if test=\"daysOfMonth != null &amp;&amp; !daysOfMonth.isEmpty()\">and d.day in<foreach collection='daysOfMonth' item='dayOfMonth' index='index' open='(' separator = ',' close=')'>#{dayOfMonth}</foreach></if>"
			+ "<if test=\"daysOfWeek  != null &amp;&amp; !daysOfWeek.isEmpty()\">and d.day_of_week in<foreach collection='daysOfWeek' item='dayOfWeek' index='index' open='(' separator = ',' close=')'>#{dayOfWeek}</foreach></if>"
			+ "<if test=\"sites  != null &amp;&amp; !sites.isEmpty()\">"
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
	public List<DataRecordGroup> getExportDataGroups(
			int startYear, int endYear, Collection<String> pollutants, Collection<Integer> sites,		 //Per-file filters
			boolean groupByYear, boolean groupByPollutant, boolean groupBySite,							 //Grouping
			Collection<Integer> months, Collection<Integer> daysOfMonth, Collection<Integer> daysOfWeek, //Basic filters
			String siteName, String cityName, Collection<String> provTerr,								 //Basic filters
			BigDecimal valueUpperBound, BigDecimal valueLowerBound,										 //Advanced filters
			String dataset);																			 //Continuous vs. Integrated

	public List<? extends ExportDataRecord> getExportData(Collection<Integer> years, Collection<String> pollutants, Collection<Integer> sites, int offset , int limit);

	@Select("<script>"
			+ "select distinct(m.units)"
			+ "<if test=\"dataset.equals(&quot;Continuous&quot;)\">from naps.continuous_data d</if>"
			+ "<if test=\"dataset.equals(&quot;Integrated&quot;)\">from naps.integrated_data d</if>"
			+ " inner join naps.pollutants p on d.pollutant_id = p.id"
			+ " inner join naps.sites s on d.site_id = s.id"
			+ " inner join naps.methods m on d.method_id = m.id"
			+ " where 1=1"
			+ "<if test=\"valueUpperBound != null\">and d.data &lt;= #{valueUpperBound}</if>"
			+ "<if test=\"valueLowerBound != null\">and d.data &gt;= #{valueLowerBound}</if>"
			+ "<if test=\"siteName != null &amp;&amp; !siteName.isEmpty()\">and s.station_name LIKE '%#{siteName}%'</if>"
			+ "<if test=\"cityName != null &amp;&amp; !cityName.isEmpty()\">and s.city_name LIKE '%#{cityName}%'</if>"
			+ "<if test=\"provTerr != null &amp;&amp; !provTerr.isEmpty()\">and s.prov_terr in <foreach collection='provTerr' item='prov' index='index' open='(' separator = ',' close=')'>#{prov}</foreach></if>"
			+ "<if test=\"months != null &amp;&amp; !months.isEmpty()\">and d.month in<foreach collection='months' item='month' index='index' open='(' separator = ',' close=')'>#{month}</foreach></if>"
			+ "<if test=\"daysOfMonth != null &amp;&amp; !daysOfMonth.isEmpty()\">and d.day in<foreach collection='daysOfMonth' item='dayOfMonth' index='index' open='(' separator = ',' close=')'>#{dayOfMonth}</foreach></if>"
			+ "<if test=\"daysOfWeek  != null &amp;&amp; !daysOfWeek.isEmpty()\">and d.day_of_week in<foreach collection='daysOfWeek' item='dayOfWeek' index='index' open='(' separator = ',' close=')'>#{dayOfWeek}</foreach></if>"
			+ "<if test=\"years  != null &amp;&amp; !years.isEmpty()\">and d.year in <foreach collection='years' item='year' index='index' open='(' separator = ',' close=')'>#{year}</foreach></if>"
			+ "<if test=\"sites  != null &amp;&amp; !sites.isEmpty()\">and s.naps_id in <foreach collection='sites' item='site' index='index' open='(' separator = ',' close=')'>#{site}</foreach></if>"
			+ "<if test=\"pollutants != null &amp;&amp; !pollutants.isEmpty()\">and p.name in <foreach collection='pollutants' item='pollutant' index='index' open='(' separator = ',' close=')'>#{pollutant}</foreach></if>"
			+ "</script>")
	public List<String> getDistinctUnits(
			Collection<Integer> years, Collection<String> pollutants, Collection<Integer> sites,					//Per-file filters
			Collection<Integer> months, Collection<Integer> daysOfMonth, Collection<Integer> daysOfWeek,			//Basic filters
			String siteName, String cityName, Collection<String> provTerr,											//Basic filters
			BigDecimal valueUpperBound, BigDecimal valueLowerBound,													//Advanced filters			
			String dataset);																						//Continuous vs. Integrated
	
	@Select("<script>"
			+ "select"
			+"<foreach collection='fields' item='field' index='index' open='' separator = ',' close=','>"
				+ "<if test=\"field == null\">null</if>"
				+ "<if test=\"field != null\">"
				+ "<if test=\"field.name() == 'YEAR'\">d.year</if>"
				+ "<if test=\"field.name() == 'MONTH'\">d.month</if>"
				+ "<if test=\"field.name() == 'DAY'\">d.day</if>"
				+ "<if test=\"field.name() == 'HOUR'\">d.hour</if>"
				+ "<if test=\"field.name() == 'DAY_OF_WEEK'\">d.day_of_week</if>"
				+ "<if test=\"field.name() == 'DAY_OF_YEAR'\">d.day_of_year</if>"
				+ "<if test=\"field.name() == 'WEEK_OF_YEAR'\">d.week_of_year</if>"
				+ "<if test=\"field.name() == 'NAPS_ID'\">s.naps_id</if>"
				+ "<if test=\"field.name() == 'POLLUTANT'\">p.name</if>"
				+ "<if test=\"field.name() == 'PROVINCE_TERRITORY'\">s.prov_terr</if>"
				+ "<if test=\"field.name() == 'URBANIZATION'\">s.urbanization</if>"
				+ "<if test=\"field.name() == 'SITE_TYPE'\">s.site_type</if>"
				+ "</if>"
				+ "as field_${index}"
			+"</foreach>"
			+ "<if test=\"function.name() == 'AVG'\">avg(d.data)</if>"
			+ "<if test=\"function.name() == 'MIN'\">min(d.data)</if>"
			+ "<if test=\"function.name() == 'MAX'\">max(d.data)</if>"
			+ "<if test=\"function.name() == 'COUNT'\">count(d.data)</if>"
			+ "<if test=\"function.name() == 'SUM'\">sum(d.data)</if>"
			+ "<if test=\"function.name() == 'NONE'\">d.data</if>"
			+ "as value"
			+ "<if test=\"sampleCount\">, "
				+ "<if test=\"function.name() != 'NONE'\">count(d.data)</if>"
				+ "<if test=\"function.name() == 'NONE'\">1</if>"
			+ " as sampleCount</if>"
			+ "<if test=\"stdDevPop\">, stddev_pop(d.data) as stdDevPop</if>"
			+ "<if test=\"stdDevSmp\">, stddev_samp(d.data) as stdDevSmp</if>"
			+ "<if test=\"dataset.equals(&quot;Continuous&quot;)\">from naps.continuous_data d</if>"
			+ "<if test=\"dataset.equals(&quot;Integrated&quot;)\">from naps.integrated_data d</if>"
			+ " inner join naps.pollutants p on d.pollutant_id = p.id"
			+ " inner join naps.sites s on d.site_id = s.id"
			+ " where 1=1"
			+ "<if test=\"valueUpperBound != null\">and d.data &lt;= #{valueUpperBound}</if>"
			+ "<if test=\"valueLowerBound != null\">and d.data &gt;= #{valueLowerBound}</if>"
			+ "<if test=\"siteName != null &amp;&amp; !siteName.isEmpty()\">and s.station_name LIKE '%#{siteName}%'</if>"
			+ "<if test=\"cityName != null &amp;&amp; !cityName.isEmpty()\">and s.city_name LIKE '%#{cityName}%'</if>"
			+ "<if test=\"provTerr != null &amp;&amp; !provTerr.isEmpty()\">and s.prov_terr in <foreach collection='provTerr' item='prov' index='index' open='(' separator = ',' close=')'>#{prov}</foreach></if>"
			+ "<if test=\"months != null &amp;&amp; !months.isEmpty()\">and d.month in<foreach collection='months' item='month' index='index' open='(' separator = ',' close=')'>#{month}</foreach></if>"
			+ "<if test=\"daysOfMonth != null &amp;&amp; !daysOfMonth.isEmpty()\">and d.day in<foreach collection='daysOfMonth' item='dayOfMonth' index='index' open='(' separator = ',' close=')'>#{dayOfMonth}</foreach></if>"
			+ "<if test=\"daysOfWeek  != null &amp;&amp; !daysOfWeek.isEmpty()\">and d.day_of_week in<foreach collection='daysOfWeek' item='dayOfWeek' index='index' open='(' separator = ',' close=')'>#{dayOfWeek}</foreach></if>"
			+ "<if test=\"years  != null &amp;&amp; !years.isEmpty()\">and d.year in <foreach collection='years' item='year' index='index' open='(' separator = ',' close=')'>#{year}</foreach></if>"
			+ "<if test=\"sites  != null &amp;&amp; !sites.isEmpty()\">and s.naps_id in <foreach collection='sites' item='site' index='index' open='(' separator = ',' close=')'>#{site}</foreach></if>"
			+ "<if test=\"pollutants != null &amp;&amp; !pollutants.isEmpty()\">and p.name in <foreach collection='pollutants' item='pollutant' index='index' open='(' separator = ',' close=')'>#{pollutant}</foreach></if>"
			+ "<if test=\"fields != null &amp;&amp; !fields.isEmpty()\">"
				+ "group by <foreach collection='fields' item='field' index='index' open='' separator = ',' close=''>field_${index}</foreach>"
			+ "</if>"
			+ "<if test=\"resultUpperBound != null || resultLowerBound != null || minSampleCount != null\">"
			+ "having 1=1"
			+ "<if test=\"resultUpperBound != null\">and"
				+ "<if test=\"function.name() == 'AVG'\">avg(d.data)</if>"
				+ "<if test=\"function.name() == 'MIN'\">min(d.data)</if>"
				+ "<if test=\"function.name() == 'MAX'\">max(d.data)</if>"
				+ "<if test=\"function.name() == 'COUNT'\">count(d.data)</if>"
				+ "<if test=\"function.name() == 'SUM'\">sum(d.data)</if>"
				+ "&lt;= #{resultUpperBound}"
			+ "</if>"
			+ "<if test=\"resultLowerBound != null\">and"
				+ "<if test=\"function.name() == 'AVG'\">avg(d.data)</if>"
				+ "<if test=\"function.name() == 'MIN'\">min(d.data)</if>"
				+ "<if test=\"function.name() == 'MAX'\">max(d.data)</if>"
				+ "<if test=\"function.name() == 'COUNT'\">count(d.data)</if>"
				+ "<if test=\"function.name() == 'SUM'\">sum(d.data)</if>"
				+ "&gt;= #{resultLowerBound}"
			+ "</if>"
			+ "<if test=\"minSampleCount != null\">and count(d.data) &gt;= #{minSampleCount}</if>"
			+ "</if>"
			+ "order by"
				+ "<if test=\"fields != null &amp;&amp; !fields.isEmpty()\">"
				+ "<foreach collection='fields' item='field' index='index' open='' separator = ',' close=''>field_${index}</foreach>"
				+ "</if>"
				+ "<if test=\"fields == null || fields.isEmpty()\">"
				+ "value desc"
				+ "</if>"
			+ "</script>")
	public List<DataQueryRecord> getQueryData(Collection<AggregationField> fields, AggregateFunction function, 		//Grouping
			Collection<Integer> years, Collection<String> pollutants, Collection<Integer> sites,					//Per-file filters
			Collection<Integer> months, Collection<Integer> daysOfMonth, Collection<Integer> daysOfWeek,			//Basic filters
			String siteName, String cityName, Collection<String> provTerr,											//Basic filters
			BigDecimal valueUpperBound, BigDecimal valueLowerBound,													//Advanced filters
			boolean sampleCount, boolean stdDevPop, boolean stdDevSmp,												//Additional Columns
			Double resultUpperBound, Double resultLowerBound, Integer minSampleCount,								//Having conditions						
			String dataset);																						//Continuous vs. Integrated
}
