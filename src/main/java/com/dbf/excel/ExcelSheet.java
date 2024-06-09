package com.dbf.excel;

import java.util.Date;

public interface ExcelSheet {
	
	public int columnCount();
	
	public int rowCount();
	
	public String getName();
	
	public String getCellContents(int column, int row);
	
	public Date getCellDate(int column, int row);
}
