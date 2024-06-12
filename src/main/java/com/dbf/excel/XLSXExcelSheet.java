package com.dbf.excel;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class XLSXExcelSheet extends RawDataExcelSheet {

	public XLSXExcelSheet(Sheet sheet) {
		super();
		loadXLSXFFile(sheet);
	}

	private void loadXLSXFFile(Sheet sheet) {
		this.sheetName = sheet.getSheetName();
		 int rows = sheet.getLastRowNum();
		 
		 if (rows > 9000) {
			 throw new IllegalArgumentException("Sheet is too big. Row count of " + rows + " exceeds the maximum of 9000.");
		 }

		// Determine the maximum number of columns we will need.
		// Note that the number of columns varies per row
		int cols = 0;
		for (int r = 0; r < rows; r++) {
			Row row = sheet.getRow(r);
			if (row == null) continue;
			cols = Math.max(cols, row.getLastCellNum());
		}
		
		if (cols > 500) {
			 throw new IllegalArgumentException("Sheet is too big. Column count of " + cols + " exceeds the maximum of 150.");
		 }

		rawData = new String[cols][rows+1];

		// Copy the raw cell contents into the rawData array
		for (int r = 0; r < rows; r++) {
			Row row = sheet.getRow(r);
			if (row == null) continue;
			
			for (int c = 0; c < cols; c++) {
				Cell cell = row.getCell(c);
				rawData[c][r] = getCellValueAsString(cell);
			}
		}
	}
	
	private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                	//The reason we convert to a string of this specific format is because only some dates are properly
                	//stored as a Date formated cell. Others are just stored as a cell with the string value of
                	//the date, for example "2010-05-08". We can't infer that the cells are really supposed to be
                	//dates without knowing the context that surrounds the cell (eg. column header).
                	//Therefore, we convert everything to a common formatted string.
                	return NEWER_DATE_FORMAT.format(cell.getDateCellValue());
                } else {
                    return Double.toString(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "Unsupported Cell Type";
        }
    }
	
	@Override
	public Date getCellDate(int column, int row) {
		String rawDate = rawData[column][row];
		if(null == rawDate || rawDate.isEmpty()) return null;
		
		return extractRawDate(rawDate);
	}
}
