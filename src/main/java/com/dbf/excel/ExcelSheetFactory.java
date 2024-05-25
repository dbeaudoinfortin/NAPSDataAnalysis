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
		//NOTE: the XLS version of the files is inconsistent with most seemingly BIFF4 and some BIFF8.
		//I suspect they were all initially generated in BIFF4 format and some were later corrected and re-uploaded in BIFF8.
		//This is surprising since data up until 2009 is in BIFF4, which is a format from 1992.
		//For example, in year 2002, all the BIFF4 have a 2010 last modified date but the BIFF8 formated S60211_DICH.XLS
		//has a 2016 date.
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
		}
	}
}
