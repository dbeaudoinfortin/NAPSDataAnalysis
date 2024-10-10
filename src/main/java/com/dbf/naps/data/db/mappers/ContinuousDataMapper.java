package com.dbf.naps.data.db.mappers;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.dbf.naps.data.analysis.AggregateFunction;
import com.dbf.naps.data.analysis.AggregationField;
import com.dbf.naps.data.analysis.DataQueryRecord;
import com.dbf.naps.data.records.ContinuousDataRecord;
import com.dbf.naps.data.records.ExportDataRecord;

public interface ContinuousDataMapper extends DataMapper {

	@Insert("INSERT into naps.continuous_data (site_id, pollutant_id, method_id, date_time, year, month, day, hour, day_of_week, week_of_year, day_of_year, data)"
			+ " values (#{siteId}, #{pollutantId}, #{methodId}, #{datetime}, #{year}, #{month}, #{day}, #{hour}, #{dayOfWeek}, #{weekOfYear}, #{dayOfYear}, #{data})"
			+ " ON CONFLICT DO NOTHING;")
	public int insertContinuousData(Integer siteId, String pollutantId, String methodId, Date datetime, Integer year, Integer month, Integer day, Integer hour, String dayOfWeek, String weekOfYear, BigDecimal data);
	
	@Insert("<script>"
        + "INSERT into naps.continuous_data (site_id, pollutant_id, method_id, date_time, year, month, day, hour, day_of_week, week_of_year, day_of_year, data) "
        + "values "
        + "<foreach collection='dataRecords' item='record' index='index' open='(' separator = '),(' close=')' >"
        + "#{record.siteId}, #{record.pollutantId}, #{record.methodId}, #{record.datetime}, #{record.year}, #{record.month}, #{record.day}, #{record.hour},"
        + " #{record.dayOfWeek}, #{record.weekOfYear}, #{record.dayOfYear}, #{record.data}"
        + "</foreach>"
        + " ON CONFLICT DO NOTHING;"
        + "</script>")
	public int insertContinuousDataBulk(List<ContinuousDataRecord> dataRecords);
	
	@Override
	@Select("<script>"
			+ "select"
			+ " s.naps_id as siteNapsId,"
			+ " s.station_name as siteName,"
			+ " p.name as pollutantName,"
			+ " d.date_time as datetime,"
			+ " d.data as data,"
			+ " m.units as units"
			+ " from naps.continuous_data d"
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
			+ " order by s.naps_id, p.name, d.date_time"
			+ " OFFSET #{offset} LIMIT #{limit}"
			+ "</script>")
	public List<ExportDataRecord> getExportData(Collection<Integer> years, Collection<String> pollutants, Collection<Integer> sites, int offset , int limit);
	
	@Override
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
				+ "</if>"
				+ "as field_#{index}"
			+"</foreach>"
			+ "<if test=\"function.name() == 'AVG'\">avg(d.data)</if>"
			+ "<if test=\"function.name() == 'MIN'\">min(d.data)</if>"
			+ "<if test=\"function.name() == 'MAX'\">max(d.data)</if>"
			+ "<if test=\"function.name() == 'COUNT'\">count(d.data)</if>"
			+ "<if test=\"function.name() == 'SUM'\">sum(d.data)</if>"
			+ "<if test=\"function.name() == 'NONE'\">d.data</if>"
			+ "as value"
			+ " from naps.continuous_data d"
			+ " inner join naps.pollutants p on d.pollutant_id = p.id"
			+ " inner join naps.sites s on d.site_id = s.id"
			+ " where"
			+ "<if test=\"siteName != null &amp;&amp; !siteName.isEmpty()\">s.station_name LIKE '%#{siteName}%'</if>"
			+ "<if test=\"cityName != null &amp;&amp; !cityName.isEmpty()\">"
			+ "   <if test=\"siteName != null &amp;&amp; !siteName.isEmpty()\">and</if>"
			+ "   	s.city_name LIKE '%#{cityName}%'"
			+ "</if>"
			+ "<if test=\"provTerr != null &amp;&amp; !provTerr.isEmpty()\">"
			+ "   <if test=\"(siteName != null &amp;&amp; !siteName.isEmpty()) || (cityName != null &amp;&amp; !cityName.isEmpty())\">and</if>"
			+ "		s.prov_terr in <foreach collection='provs' item='prov' index='index' open='(' separator = ',' close=')'>#{prov}</foreach>"
			+ "</if>"
			+ "<if test=\"months != null &amp;&amp; !months.isEmpty()\">"
			+ "   <if test=\"(siteName != null &amp;&amp; !siteName.isEmpty()) || (cityName != null &amp;&amp; !cityName.isEmpty()) || (provTerr != null &amp;&amp; !provTerr.isEmpty())\">and</if>"
			+ "		d.month in<foreach collection='months' item='month' index='index' open='(' separator = ',' close=')'>#{month}</foreach>"
			+ "</if>"
			+ "<if test=\"years != null &amp;&amp; !years.isEmpty()\">"
			+ "   <if test=\"(siteName != null &amp;&amp; !siteName.isEmpty()) || (cityName != null &amp;&amp; !cityName.isEmpty()) "
			+ "		|| (provTerr != null &amp;&amp; !provTerr.isEmpty()) || (months != null &amp;&amp; !months.isEmpty())\">and</if>"
			+ "		d.year in <foreach collection='years' item='year' index='index' open='(' separator = ',' close=')'>#{year}</foreach>"
			+ "</if>"
			+ "<if test=\"sites != null &amp;&amp; !sites.isEmpty()\">"
			+ "   <if test=\"(siteName != null &amp;&amp; !siteName.isEmpty()) || (cityName != null &amp;&amp; !cityName.isEmpty()) "
			+ "		|| (provTerr != null &amp;&amp; !provTerr.isEmpty()) || (months != null &amp;&amp; !months.isEmpty()) "
			+ "		|| (years != null  &amp;&amp; !years.isEmpty())\">and</if>"
			+ "   s.naps_id in <foreach collection='sites' item='site' index='index' open='(' separator = ',' close=')'>#{site}</foreach>"
			+ "</if>"
			+ "<if test=\"pollutants != null &amp;&amp; !pollutants.isEmpty()\">"
			+ "   <if test=\"(siteName != null &amp;&amp; !siteName.isEmpty()) || (cityName != null &amp;&amp; !cityName.isEmpty()) "
			+ "		|| (provTerr != null &amp;&amp; !provTerr.isEmpty()) || (months != null &amp;&amp; !months.isEmpty()) "
			+ "		|| (years != null  &amp;&amp; !years.isEmpty()) || (sites != null &amp;&amp; !sites.isEmpty()) \">and</if>"
			+ "   p.name in <foreach collection='pollutants' item='pollutant' index='index' open='(' separator = ',' close=')'>#{pollutant}</foreach>"
			+ "</if>"
			+ " group by <foreach collection='fields' item='field' index='index' open='' separator = ',' close=''>field_#{index}</foreach>"
			+ " order by <foreach collection='fields' item='field' index='index' open='' separator = ',' close=''>field_#{index}</foreach>"
			+ "</script>")
	public List<DataQueryRecord> getQueryData(Collection<AggregationField> fields, AggregateFunction function, Collection<Integer> years, Collection<String> pollutants, Collection<Integer> sites, Collection<Integer> months, String siteName, String cityName, Collection<String> provTerr);
}
