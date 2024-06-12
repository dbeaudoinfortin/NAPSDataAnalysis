package com.dbf.naps.data.loader.integrated;

import com.dbf.naps.data.loader.records.DataRecord;

public class IntegratedDataRecord extends DataRecord {

	private Integer sampleId;

	public IntegratedDataRecord() {	}

	public Integer getSampleId() {
		return sampleId;
	}

	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}	
}
