package com.dbf.naps.data.db.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface MethodMapper {

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

}
