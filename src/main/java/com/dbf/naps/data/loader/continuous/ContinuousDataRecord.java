package com.dbf.naps.data.loader.continuous;

import com.dbf.naps.data.loader.DataRecord;

public class ContinuousDataRecord extends DataRecord {

	private Integer hour;

	public ContinuousDataRecord() {	}
	
	public Integer getHour() {
		return hour;
	}
	
	public void setHour(Integer hour) {
		this.hour = hour;
	}
}
