package com.dbf.excel;

public interface ExcelSheet {
	
	public int columnCount();
	public int rowCount();
	
	public String getCellContents(int column, int row);
}
