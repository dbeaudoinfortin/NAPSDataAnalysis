package com.dbf.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Workbook;
import jxl.read.biff.BiffException;


public class ExcelSheetFactory {
	
	private static final Logger log = LoggerFactory.getLogger(ExcelSheetFactory.class);
	
	public static List<ExcelSheet> getSheets(File excelFile, List<String> matchingSheetNames, List<String> excludedSheetNames) throws IOException {
		if(excelFile.getName().toUpperCase().endsWith(".XLS")) return createXLSSheet(excelFile, matchingSheetNames, excludedSheetNames);
		
		//Handle XLSX sheet
		log.info("Trying to load XLSX Excel workbook " + excelFile + " into memory.");
		
		try (FileInputStream fis = new FileInputStream(excelFile);
				org.apache.poi.ss.usermodel.Workbook workbook = new XSSFWorkbook(fis)) {
			
			if (workbook.getNumberOfSheets() == 1 || matchingSheetNames.size() < 1) {
				return Collections.singletonList(new XLSXExcelSheet(workbook.getSheetAt(0))); //Only a single sheet is expected
			} 
			
			//We need to find the sheet that we want using a case-insensitive partial match approach
			List<ExcelSheet> matchingSheets = new ArrayList<ExcelSheet>();
			for(int i = 0; i < workbook.getNumberOfSheets(); i++ ) {
				if (sheetNameMatches(workbook.getSheetName(i), matchingSheetNames, excludedSheetNames))
					matchingSheets.add(new XLSXExcelSheet(workbook.getSheetAt(i)));
			}
			
			if (matchingSheets.isEmpty()) {
				log.warn("Could not locate a matching sheet inside of the workbook using sheet names " + matchingSheetNames + ". Falling back to the first sheet.");
				matchingSheets.add(new XLSXExcelSheet(workbook.getSheetAt(0)));
			}
			return matchingSheets;
		}
	}
	
	private static List<ExcelSheet> createXLSSheet(File excelFile, List<String> matchingSheetNames, List<String> excludedSheetNames) throws IOException {
		//NOTE: the XLS data files are inconsistent with most seemingly in BIFF4 format and some in BIFF8 format.
		//I suspect they were all initially generated in BIFF4 format and some were later corrected and re-uploaded in BIFF8.
		//This is surprising since data up until 2009 is in BIFF4, which is a format from 1992.
		//For example, in year 2002, all the BIFF4 have a 2010 last modified date but the BIFF8 formated S60211_DICH.XLS
		//has a 2016 date.
		log.info("Trying to load BIFF8 Excel workbook " + excelFile + " into memory.");
		try {
			//NOTE: It looks like this holds the entire workbook in memory. Lucky the data files from NAPS are not too big
			Workbook workbook = Workbook.getWorkbook(excelFile);
			
			if (workbook.getNumberOfSheets() == 1 || matchingSheetNames.size() < 1) {
				return Collections.singletonList(new BIFF8ExcelSheet(workbook.getSheet(0))); //Only a single sheet is expected
			}
			
			//We need to find the sheet that we want using a case-insensitive partial match approach
			List<ExcelSheet> matchingSheets = new ArrayList<ExcelSheet>();
			for(String sheetName : workbook.getSheetNames()) {
				if (sheetNameMatches(sheetName, matchingSheetNames, excludedSheetNames))
					matchingSheets.add(new BIFF8ExcelSheet(workbook.getSheet(sheetName)));
			}
			if (matchingSheets.isEmpty()) {
				log.warn("Could not locate a matching sheet inside of the workbook using sheet names " + matchingSheetNames + ". Falling back to the first sheet.");
				matchingSheets.add(new BIFF8ExcelSheet(workbook.getSheet(0)));
			}

			//Note: the input stream is already closed, we don't really want to call workbook.close() 
			//since that will clear the underlying data	
			return matchingSheets;
		} catch(BiffException e) {
			//TODO: May want to support more than one sheet in the very old XLS files
			log.info("BIFF Exception encountered. Trying to load pre-BIFF8 Excel workbook " + excelFile + " into memory: ", e.getMessage());
			return Collections.singletonList(new OldBIFFExcelSheet(excelFile));
		}
	}
	
	private static boolean sheetNameMatches(String sheetName, List<String> matchingSheetNames, List<String> excludedSheetNames) {
		String sheetNameUpper = sheetName.toUpperCase();
		
		for (String excludedSheetName : excludedSheetNames) {
			if (sheetNameUpper.startsWith(excludedSheetName)) return false;
		}
		
		for (String matchingSheetName : matchingSheetNames) {
			if (sheetNameUpper.startsWith(matchingSheetName)) return true;
		}
		
		log.warn("Ignoring unexpected sheet: " + sheetName);
		return false;
	}
}
