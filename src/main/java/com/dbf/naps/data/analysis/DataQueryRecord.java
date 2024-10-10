package com.dbf.naps.data.analysis;

import java.math.BigDecimal;

public class DataQueryRecord {
	private Object field_1;
	private Object field_2;
	private Object field_3;
	private Object field_4;
	private Object field_5;
	private BigDecimal value;
	
	private Integer sampleCount;
	private Double stdDevPop;
	private Double stdDevSmp;
	
	public DataQueryRecord() {}

	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Object getField_1() {
		return field_1;
	}

	public Object getField_2() {
		return field_2;
	}

	public Object getField_3() {
		return field_3;
	}

	public Object getField_4() {
		return field_4;
	}

	public Object getField_5() {
		return field_5;
	}

	public void setField_1(Object field_1) {
		this.field_1 = field_1;
	}

	public void setField_2(Object field_2) {
		this.field_2 = field_2;
	}

	public void setField_3(Object field_3) {
		this.field_3 = field_3;
	}

	public void setField_4(Object field_4) {
		this.field_4 = field_4;
	}

	public void setField_5(Object field_5) {
		this.field_5 = field_5;
	}

	public Integer getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(Integer sampleCount) {
		this.sampleCount = sampleCount;
	}

	public Double getStdDevPop() {
		return stdDevPop;
	}

	public void setStdDevPop(Double stdDevPop) {
		this.stdDevPop = stdDevPop;
	}

	public Double getStdDevSmp() {
		return stdDevSmp;
	}

	public void setStdDevSmp(Double stdDevSmp) {
		this.stdDevSmp = stdDevSmp;
	}
}
