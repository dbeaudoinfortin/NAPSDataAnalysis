package com.dbf.naps.data.db.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import com.dbf.naps.data.records.SampleRecord;

public interface SampleMapper {
	@Insert("INSERT into naps.samples (naps_sample_id, canister_id, fine, cartridge, media, sample_mass, spec_mass, dichot_mass, sample_vol, sample_duration, tsp)"
			+ " values (#{napsID}, #{canisterID}, #{fine}, #{cartridge}, #{media}, #{mass}, #{specMass}, #{dichotMass}, #{volume}, #{duration}, #{tsp})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int insertSample(SampleRecord sample);
	
}
