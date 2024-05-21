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
			+ " ON CONFLICT DO NOTHING;")
	public int insertSite(Integer NAPSId, String cityName, String provTerr, BigDecimal latitude, BigDecimal longitude);
	
}
