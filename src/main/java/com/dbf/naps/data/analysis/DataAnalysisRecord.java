package com.dbf.naps.data.analysis;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVPrinter;

import com.dbf.heatmaps.data.DataRecord;
import com.dbf.naps.data.analysis.query.json.JsonSingleRecord;

public class DataAnalysisRecord implements DataRecord {
	private Object field_0;
	private Object field_1;
	private Object field_2;
	private Object field_3;
	private Object field_4;
	private BigDecimal value;
	
	private Integer sampleCount;
	private Double stdDevPop;
	private Double stdDevSmp;
	
	public DataAnalysisRecord() {}
	
	public void printToCSV(CSVPrinter printer, int fieldCount) throws IOException {
		printToCSV(printer, fieldCount, false, false, false);
	}
	
	public void printToCSV(CSVPrinter printer, int fieldCount, boolean hasSampleCount, boolean hasStdDevPop, boolean hasStdDevSmp) throws IOException {
		List<Object> values = new ArrayList<Object>(9);
		if(fieldCount > 0) values.add(field_0);
		if(fieldCount > 1) values.add(field_1);
		if(fieldCount > 2) values.add(field_2);
		if(fieldCount > 3) values.add(field_3);
		if(fieldCount > 4) values.add(field_4);
		values.add(value);
		if(hasSampleCount) values.add(sampleCount);
		if(hasStdDevPop) values.add(stdDevPop);
		if(hasStdDevSmp) values.add(stdDevSmp);
		printer.printRecord(values.toArray());
	}
	
	public Object getField(int fieldIndex) {
		if(fieldIndex == 0) return field_0;
		if(fieldIndex == 1) return field_1;
		if(fieldIndex == 2) return field_2;
		if(fieldIndex == 3) return field_3;
		if(fieldIndex == 4) return field_4;
		return null;
	}
	
	public JsonSingleRecord toJsonSingleRecord() {
		return new JsonSingleRecord(value, sampleCount, stdDevPop, stdDevSmp);
	}
	
	public JsonSingleRecord toJsonSingleRecord(String name) {
		return new JsonSingleRecord(name, value, sampleCount, stdDevPop, stdDevSmp);
	}
	
	@Override
	public Object getX() {
		return field_0;
	}

	@Override
	public Object getY() {
		return field_1;
	}
	
	@Override
	public Double getValue() {
		return value.doubleValue();
	}
	
	public BigDecimal getPreciseValue(int significantDigits) {
		if (value.equals(BigDecimal.ZERO)) return BigDecimal.ZERO;
        return value.round(new MathContext(significantDigits, RoundingMode.HALF_UP)).stripTrailingZeros();
	}
	
	public void setValue(BigDecimal value) {
		this.value = value;
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

	public Object getField_0() {
		return field_0;
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

	public void setField_0(Object field_0) {
		this.field_0 = field_0;
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
}
