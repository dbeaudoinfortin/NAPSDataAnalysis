package com.dbf.excel;

import jxl.Sheet;

public class BIFF8ExcelSheet implements ExcelSheet {
	
	private Sheet sheet;
	
	public BIFF8ExcelSheet(Sheet sheet) {
		this.sheet = sheet;	
	}

	@Override
	public int columnCount() {
		return sheet.getColumns();
	}

	@Override
	public int rowCount() {
		return sheet.getRows();
	}

	@Override
	public String getCellContents(int column, int row) {
		//Returns empty string in case of null
		return sheet.getCell(column, row).getContents();
	}
}
