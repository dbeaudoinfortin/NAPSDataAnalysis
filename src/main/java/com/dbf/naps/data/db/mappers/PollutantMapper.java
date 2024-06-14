package com.dbf.naps.data.db.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface PollutantMapper {
	
	@Select("SELECT id from naps.pollutants where name = #{name}")
	public Integer getPollutantID(String name);
	
	@Insert("INSERT into naps.pollutants (name)"
			+ " values (#{name})"
			+ " ON CONFLICT DO NOTHING;")
	public int insertPollutant(String name);
}
