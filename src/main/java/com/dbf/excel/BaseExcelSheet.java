package com.dbf.excel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.ss.usermodel.DateUtil;

public abstract class BaseExcelSheet implements ExcelSheet {
	
	//Note: SimpleDateFormat is not thread safe, must not be static
	protected final SimpleDateFormat TYPICAL_DATE_FORMAT = new SimpleDateFormat("MM-dd-yy", Locale.ENGLISH);
	protected final SimpleDateFormat NEWER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH); //2010 and beyond
	protected final SimpleDateFormat BAD_DATE_FORMAT = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
	
	public BaseExcelSheet() {
		TYPICAL_DATE_FORMAT.setLenient(false);
		NEWER_DATE_FORMAT.setLenient(false);
		BAD_DATE_FORMAT.setLenient(false);
	}
	
	//Work-around for all the strange date formats across several excel versions
	protected Date extractRawDate(String rawDate) {
		try {
			if(!rawDate.contains("-")) {
				//This is a date likely in Excel's special 1900 format
				//TODO: The date could also be in 1904 format. We need to read the DATEMODE record to certain.
				//This likely isn't an issue because all XLS NAPS files in BIFF4 file format use the 1900 date format
				return DateUtil.getJavaDate(Double.parseDouble(rawDate));
			} else {
				return TYPICAL_DATE_FORMAT.parse(rawDate);
			}
        } catch (ParseException | NumberFormatException e) {
        	try {
        		//Files after 2010 use the ISO standard
        		return NEWER_DATE_FORMAT.parse(rawDate);
        	} catch (ParseException | NumberFormatException e2) {
        		try {
            		//Someone messed up the dates on a couple rows
            		return BAD_DATE_FORMAT.parse(rawDate);
            	} catch (ParseException  e3) {
            		throw new IllegalArgumentException("Could not parse date " + rawDate + ". Expecting format " + TYPICAL_DATE_FORMAT + " or " + NEWER_DATE_FORMAT);
            	}
        	}
        }
	}
}
