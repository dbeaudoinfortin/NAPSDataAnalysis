package com.dbf.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class ExcelSheetFactory {
	
	private static final Logger log = LoggerFactory.getLogger(ExcelSheetFactory.class);
	
	public static ExcelSheet createSheet(File excelFile, String... matchingSheetNames) throws IOException {
		if(excelFile.getName().toUpperCase().endsWith(".XLS")) {
			return createXLSSheet(excelFile, matchingSheetNames);
		}
		
		//Handle XLSX sheet
		log.info("Trying to load XLSX Excel workbook " + excelFile + " into memory.");
		
		try (FileInputStream fis = new FileInputStream(excelFile);
				org.apache.poi.ss.usermodel.Workbook workbook = new XSSFWorkbook(fis)) {
			
			org.apache.poi.ss.usermodel.Sheet sheet = null;
			if (workbook.getNumberOfSheets() == 1 || matchingSheetNames.length < 1) {
				sheet = workbook.getSheetAt(0); //Only a single sheet is expected
			} else {
				//We need to find the sheet that we want using a case-insensitive partial match approach
				List<String> matchingSheetNamesUpper = getMatchingSheetNames(matchingSheetNames);
				for(int i = 0; i < workbook.getNumberOfSheets(); i++ ) {
					if (sheetNameMatches(workbook.getSheetName(i), matchingSheetNamesUpper)) {
						sheet = workbook.getSheetAt(i);
						break;
					}
				}
			}
			
			if (null == sheet)
				throw new IllegalArgumentException("Could not locate a matching sheet inside of the workbook using sheet names: " + matchingSheetNames);
			
			return new XLSXExcelSheet(sheet);
		}
	}
	
	private static ExcelSheet createXLSSheet(File excelFile, String... matchingSheetNames) throws IOException {
		//NOTE: the XLS data files are inconsistent with most seemingly in BIFF4 format and some in BIFF8 format.
		//I suspect they were all initially generated in BIFF4 format and some were later corrected and re-uploaded in BIFF8.
		//This is surprising since data up until 2009 is in BIFF4, which is a format from 1992.
		//For example, in year 2002, all the BIFF4 have a 2010 last modified date but the BIFF8 formated S60211_DICH.XLS
		//has a 2016 date.
		log.info("Trying to load BIFF8 Excel workbook " + excelFile + " into memory.");
	
		Workbook workbook = null;
		try {
			//NOTE: It looks like this hold the entire sheet in memory
			workbook = Workbook.getWorkbook(excelFile);
			
			Sheet sheet = null;
			if (workbook.getNumberOfSheets() == 1 || matchingSheetNames.length < 1) {
				sheet = workbook.getSheet(0); //Only a single sheet is expected
			} else {
				//We need to find the sheet that we want using a case-insensitive partial match approach
				List<String> matchingSheetNamesUpper = getMatchingSheetNames(matchingSheetNames);
				for(String sheetName : workbook.getSheetNames()) {
					if (sheetNameMatches(sheetName, matchingSheetNamesUpper)) {
						sheet = workbook.getSheet(sheetName);
						break;
					}
				}
			}
			
			if (null == sheet)
				throw new IllegalArgumentException("Could not locate a matching sheet inside of the workbook using sheet names: " + matchingSheetNames);
			
			//Note: the input stream is already closed, we don't really want to call workbook.close() 
			//since that will clear the underlying data
			return new BIFF8ExcelSheet(sheet);
			
		} catch(BiffException e) {
			log.info("BIFF Exception encountered. Trying to load pre-BIFF8 Excel workbook " + excelFile + " into memory: ", e.getMessage());
			return new OldBIFFExcelSheet(excelFile);
		}
	}
	
	private static boolean sheetNameMatches(String sheetName, List<String> matchingSheetNames) {
		String sheetNameUpper = sheetName.toUpperCase();
		for (String matchingSheetName : matchingSheetNames) {
			if (sheetNameUpper.startsWith(matchingSheetName)) {
				return true;
			}
		}
		return false;
	}
	
	private static List<String> getMatchingSheetNames(String... matchingSheetNames) {
		List<String> matchingSheetNamesUpper = new ArrayList<String>(matchingSheetNames.length);
		for(String sheetName : matchingSheetNames) {
			matchingSheetNamesUpper.add(sheetName.toUpperCase());
		}
		return matchingSheetNamesUpper;
	}
}
