package com.dbf.excel;

import java.util.Date;

import jxl.Cell;
import jxl.DateCell;
import jxl.Sheet;

public class BIFF8ExcelSheet extends BaseExcelSheet {
	
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

	@Override
	public Date getCellDate(int column, int row) {
		Cell cell = sheet.getCell(column, row);
		if(cell instanceof DateCell) {
			return ((DateCell) cell).getDate();
		}
		String rawDate = cell.getContents();
		if ("".equals(rawDate)) return null;
		return extractRawDate(rawDate);
	}
}
