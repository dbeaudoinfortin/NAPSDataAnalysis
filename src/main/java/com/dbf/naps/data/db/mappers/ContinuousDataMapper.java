package com.dbf.naps.data.db.mappers;

import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.dbf.naps.data.records.ContinuousDataRecord;
import com.dbf.naps.data.records.ExportDataRecord;

public interface ContinuousDataMapper extends DataMapper {

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
			+ " where 1=1"
			+ " <if test=\"years != null &amp;&amp; !years.isEmpty()\">and d.year in <foreach collection='years' item='year' index='index' open='(' separator = ',' close=')'>#{year}</foreach></if>"
			+ " <if test=\"sites != null &amp;&amp; !sites.isEmpty()\">and s.naps_id in <foreach collection='sites' item='site' index='index' open='(' separator = ',' close=')'>#{site}</foreach></if>"
			+ " <if test=\"pollutants != null &amp;&amp; !pollutants.isEmpty()\">and p.name in <foreach collection='pollutants' item='pollutant' index='index' open='(' separator = ',' close=')'>#{pollutant}</foreach></if>"
			+ " order by s.naps_id, p.name, d.date_time"
			+ " OFFSET #{offset} LIMIT #{limit}"
			+ "</script>")
	public List<ExportDataRecord> getExportData(Collection<Integer> years, Collection<String> pollutants, Collection<Integer> sites, int offset , int limit);
}
