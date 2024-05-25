package com.dbf.naps.data.loader.integrated;

import java.math.BigDecimal;

import com.dbf.naps.data.loader.DataRecord;

public class IntegratedDataRecord extends DataRecord {

	private Boolean fine;
	private BigDecimal mass;

	public IntegratedDataRecord() {	}

	public IntegratedDataRecord(IntegratedDataRecord other) {
		super(other);
		this.fine = other.fine;
		this.mass = other.mass;
	}

	public Boolean getFine() {
		return fine;
	}

	public void setFine(Boolean fine) {
		this.fine = fine;
	}

	public BigDecimal getMass() {
		return mass;
	}

	public void setMass(BigDecimal mass) {
		this.mass = mass;
	}	
	
	
}
