package com.dbf.naps.data.loader.integrated;

import com.dbf.naps.data.loader.DataRecord;

public class IntegratedDataRecord extends DataRecord {

	private Boolean fine;

	public IntegratedDataRecord() {	}

	public IntegratedDataRecord(IntegratedDataRecord other) {
		super(other);
		this.fine = other.fine;
	}

	public Boolean getFine() {
		return fine;
	}

	public void setFine(Boolean fine) {
		this.fine = fine;
	}	
	
	
}
