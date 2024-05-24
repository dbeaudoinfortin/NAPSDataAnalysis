package com.dbf.naps.data.loader;

import java.math.BigDecimal;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface DataMapper {
	
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
