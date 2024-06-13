package com.dbf.naps.data.db.mappers;

import java.math.BigDecimal;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.dbf.naps.data.records.SampleRecord;
import com.dbf.naps.data.records.SiteRecord;

public interface DataMapper {
	
	@Select("SELECT id from naps.pollutants where name = #{name}")
	public Integer getPollutantID(String name);
	
	@Insert("INSERT into naps.pollutants (name)"
			+ " values (#{name})"
			+ " ON CONFLICT DO NOTHING;")
	public int insertPollutant(String name);
	
	@Select("<script> "
			+ "SELECT id from naps.methods where dataset = #{dataset} and report_type = #{reportType} and method "
			+ "<if test=\"method != null\">= #{method}</if>"
			+ "<if test=\"method == null\">IS NULL</if>"
			+ " and units = #{units}"
			+ "</script>")
	public Integer getMethodID(String dataset, String reportType, String method, String units);
	
	@Insert("INSERT into naps.methods (dataset, report_type, method, units)"
			+ " values (#{dataset}, #{reportType}, #{method}, #{units})"
			+ " ON CONFLICT DO NOTHING;")
	public int insertMethod(String dataset, String reportType, String method, String units);
	
	@Insert("INSERT into naps.samples (naps_sample_id, canister_id, fine, cartridge, media, sample_mass, spec_mass, dichot_mass, sample_vol, sample_duration, tsp)"
			+ " values (#{napsID}, #{canisterID}, #{fine}, #{cartridge}, #{media}, #{mass}, #{specMass}, #{dichotMass}, #{volume}, #{duration}, #{tsp})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int insertSample(SampleRecord sample);
	
	@Select("SELECT id from naps.sites where NAPS_id = #{NAPSId}")
	public Integer getSiteID(Integer NAPSId);
	
	@Insert("INSERT into naps.sites (NAPS_id, city_name, prov_terr, latitude, longitude)"
			+ " values (#{NAPSId}, #{cityName}, #{provTerr}, #{latitude}, #{longitude})"
			+ " ON CONFLICT DO NOTHING;") //Don't overwrite because this is only partial data
	public int insertSitePartial(Integer NAPSId, String cityName, String provTerr, BigDecimal latitude, BigDecimal longitude);
	
	@Insert("INSERT into naps.sites (NAPS_id, station_name, city_name, prov_terr, latitude, longitude, site_type, urbanization, neighbourhood, land_use, scale, elevation)"
			+ " values (#{NAPSId}, #{stationName}, #{cityName}, #{provTerr}, #{latitude}, #{longitude},"
			+ " #{siteType}, #{urbanization}, #{neighbourhood}, #{landUse}, #{scale}, #{elevation})"
			+ " ON CONFLICT (NAPS_id) DO UPDATE"
			+ " SET"
			+ "  station_name = excluded.station_name,"
			+ "  site_type = excluded.site_type,"
			+ "  urbanization = excluded.urbanization,"
			+ "  neighbourhood = excluded.neighbourhood,"
			+ "  land_use = excluded.land_use,"
			+ "  scale = excluded.scale,"
			+ "  elevation = excluded.elevation;") //Overwrite the site since we have full data, but only update the column missing from the partial data
	public int insertSiteFull(SiteRecord site);
	
}
