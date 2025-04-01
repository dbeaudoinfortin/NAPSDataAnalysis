package com.dbf.naps.data.analysis.query.json;

import java.math.BigDecimal;

public class JsonSingleRecord extends JsonRecord {
	
	private BigDecimal value;
	private Integer sampleCount;
	private Double stdDevPop;
	private Double stdDevSmp;
	
	public JsonSingleRecord() {}
	
	public JsonSingleRecord(BigDecimal value, Integer sampleCount, Double stdDevPop, Double stdDevSmp) {
		super();
		this.value = value;
		this.sampleCount = sampleCount;
		this.stdDevPop = stdDevPop;
		this.stdDevSmp = stdDevSmp;
	}
	
	public JsonSingleRecord(String name, BigDecimal value, Integer sampleCount, Double stdDevPop, Double stdDevSmp) {
		super(name);
		this.value = value;
		this.sampleCount = sampleCount;
		this.stdDevPop = stdDevPop;
		this.stdDevSmp = stdDevSmp;
	}
	
	public BigDecimal getValue() {
		return value;
	}

	public Integer getSampleCount() {
		return sampleCount;
	}

	public Double getStdDevPop() {
		return stdDevPop;
	}

	public Double getStdDevSmp() {
		return stdDevSmp;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public void setSampleCount(Integer sampleCount) {
		this.sampleCount = sampleCount;
	}

	public void setStdDevPop(Double stdDevPop) {
		this.stdDevPop = stdDevPop;
	}

	public void setStdDevSmp(Double stdDevSmp) {
		this.stdDevSmp = stdDevSmp;
	}
	
}
