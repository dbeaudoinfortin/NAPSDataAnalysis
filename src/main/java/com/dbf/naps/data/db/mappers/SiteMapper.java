package com.dbf.naps.data.db.mappers;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.dbf.naps.data.records.SiteRecord;

public interface SiteMapper {
	
	@Select("SELECT id from naps.sites where NAPS_id = #{NAPSId}")
	public Integer getSiteID(Integer NAPSId);
	
	@Select("SELECT naps_id from naps.sites")
	public Set<Integer> getAllSiteNapsIDs();
	
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
