package com.dbf.excel;

import java.util.Date;

public abstract class RawDataExcelSheet extends BaseExcelSheet {
	
	protected String[][] rawData;
	
	protected String sheetName;
	
	public RawDataExcelSheet() {
		super();
	}
	
	@Override
	public int columnCount() {
		return rawData.length;
	}

	@Override
	public int rowCount() {
		if (rawData.length < 1) return 0;
		return rawData[0].length;
	}

	@Override
	public String getCellContents(int column, int row) {
		String s = rawData[column][row];
		return (null == s) ? "" : s;
	}

	@Override
	public Date getCellDate(int column, int row) {
		String rawDate = rawData[column][row];
		if(null == rawDate || "".equals(rawDate)) return null;
		
		return extractRawDate(rawDate);
	}
	
	@Override
	public String getName() {
		return sheetName;
	}
}
