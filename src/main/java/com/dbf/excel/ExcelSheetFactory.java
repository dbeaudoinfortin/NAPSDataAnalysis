package com.dbf.excel;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelSheetFactory {
	
	private static final Logger log = LoggerFactory.getLogger(ExcelSheetFactory.class);
	
	public static ExcelSheet createSheet(File excelFile) throws IOException {
		log.info("Trying to load BIFF8 Excel workbook " + excelFile + " into memory.");
	
		Workbook workbook = null;
		try {
			//NOTE: It looks like this hold the entire sheet in memory
			workbook = Workbook.getWorkbook(excelFile);
			Sheet sheet = workbook.getSheet(0); //Only a single sheet is expected
			
			//Note: the input stream is already closed, we don't really want to call workbook.close() 
			//since that will clear the underlying data
			return new BIFF8ExcelSheet(sheet);
			
		} catch(BiffException e) {
			log.info("BIFF Exception encountered. Trying to load pre-BIFF8 Excel workbook " + excelFile + " into memory.");
			return new OldBIFFExcelSheet(excelFile);
		} finally {
			
			if(null != workbook) workbook.close();
		}
	}

}
