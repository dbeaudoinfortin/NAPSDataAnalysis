package com.dbf.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class XLSXExcelSheet extends RawDataExcelSheet {

	public XLSXExcelSheet(Sheet sheet) {
		loadXLSXFFile(sheet);
	}

	private void loadXLSXFFile(Sheet sheet) {
		 int rows = sheet.getPhysicalNumberOfRows();

		// Determine the maximum number of columns we will need.
		// Note that the number of columns varies per row
		int cols = 0;
		for (int r = 0; r < rows; r++) {
			Row row = sheet.getRow(r);
			if (row == null) continue;
			cols = Math.max(cols, row.getPhysicalNumberOfCells());
		}

		rawData = new String[cols][rows];

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
}
