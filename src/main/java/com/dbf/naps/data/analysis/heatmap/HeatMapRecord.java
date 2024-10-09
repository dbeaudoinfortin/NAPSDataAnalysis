package com.dbf.naps.data.analysis.heatmap;

import java.math.BigDecimal;

public class HeatMapRecord {
	private Object x;
	private Object  y;
	private BigDecimal value;
	
	public HeatMapRecord() {}

	public Object getX() {
		return x;
	}

	public Object getY() {
		return y;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setX(Object x) {
		this.x = x;
	}

	public void setY(Object y) {
		this.y = y;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
	
}
