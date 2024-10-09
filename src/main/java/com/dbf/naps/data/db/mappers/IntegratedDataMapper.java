package com.dbf.naps.data.db.mappers;

import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.dbf.naps.data.analysis.AggregateFunction;
import com.dbf.naps.data.analysis.AggregationField;
import com.dbf.naps.data.analysis.heatmap.HeatMapRecord;
import com.dbf.naps.data.records.IntegratedDataRecord;
import com.dbf.naps.data.records.IntegratedExportDataRecord;

public interface IntegratedDataMapper extends DataMapper {
	
	@Insert("<script>"
        + "INSERT into naps.integrated_data (site_id, pollutant_id, method_id, sample_id, date_time, year, month, day, day_of_week, week_of_year, data) "
        + "values "
        + "<foreach collection='dataRecords' item='record' index='index' open='(' separator = '),(' close=')' >"
        + "#{record.siteId}, #{record.pollutantId}, #{record.methodId}, #{record.sampleId}, #{record.datetime}, #{record.year}, #{record.month}, #{record.day}, "
        + "#{record.dayOfWeek}, #{record.weekOfYear}, #{record.data}"
        + "</foreach>"
        + " ON CONFLICT DO NOTHING;"
        + "</script>")
	public int insertIntegratedDataBulk(List<IntegratedDataRecord> dataRecords);
	
	@Override
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
			+ " from naps.integrated_data d"
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
			+ " order by s.naps_id, m.report_Type, m.method, p.name, d.date_time, d.data"
			+ " OFFSET #{offset} LIMIT #{limit}"
			+ "</script>")
	public List<IntegratedExportDataRecord> getExportData(Collection<Integer> years, Collection<String> pollutants, Collection<Integer> sites, int offset , int limit);

	@Override
	@Select("<script>"
			+ "select"
			+ "<if test=\"x.name() == 'YEAR'\">d.year</if>"
			+ "<if test=\"x.name() == 'MONTH'\">d.month</if>"
			+ "<if test=\"x.name() == 'DAY'\">d.day</if>"
			+ "<if test=\"x.name() == 'DAY_OF_WEEK'\">d.day_of_week</if>"
			+ "<if test=\"x.name() == 'WEEK_OF_YEAR'\">d.week_of_year</if>"
			+ "<if test=\"x.name() == 'NAPS_ID'\">s.naps_id</if>"
			+ "<if test=\"x.name() == 'POLLUTANT'\">p.name</if>"
			+ "<if test=\"x.name() == 'PROVINCE_TERRITORY'\">s.prov_terr</if>"
			+ "<if test=\"x.name() == 'URBANIZATION'\">s.urbanization</if>"
			+ "as x, "
			+ "<if test=\"y.name() == 'YEAR'\">d.year</if>"
			+ "<if test=\"y.name() == 'MONTH'\">d.month</if>"
			+ "<if test=\"y.name() == 'DAY'\">d.day</if>"
			+ "<if test=\"y.name() == 'DAY_OF_WEEK'\">d.day_of_week</if>"
			+ "<if test=\"y.name() == 'WEEK_OF_YEAR'\">d.week_of_year</if>"
			+ "<if test=\"y.name() == 'NAPS_ID'\">s.naps_id</if>"
			+ "<if test=\"y.name() == 'POLLUTANT'\">p.name</if>"
			+ "<if test=\"y.name() == 'PROVINCE_TERRITORY'\">s.prov_terr</if>"
			+ "<if test=\"y.name() == 'URBANIZATION'\">s.urbanization</if>"
			+ "as y, "
			+ "<if test=\"function.name() == 'AVG'\">avg(d.data)</if>"
			+ "<if test=\"function.name() == 'MIN'\">min(d.data)</if>"
			+ "<if test=\"function.name() == 'MAX'\">max(d.data)</if>"
			+ "<if test=\"function.name() == 'COUNT'\">count(d.data)</if>"
			+ "<if test=\"function.name() == 'SUM'\">sum(d.data)</if>"
			+ "as value"
			+ " from naps.integrated_data d"
			+ " inner join naps.pollutants p on d.pollutant_id = p.id"
			+ " inner join naps.sites s on d.site_id = s.id"
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
			+ " group by x, y"
			+ " order by y, x"
			+ "</script>")
	public List<HeatMapRecord> getHeatMapData(AggregationField x, AggregationField y, AggregateFunction function, Collection<Integer> years, Collection<String> pollutants, Collection<Integer> sites);
}
