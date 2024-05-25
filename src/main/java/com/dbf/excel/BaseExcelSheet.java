package com.dbf.excel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.ss.usermodel.DateUtil;

public abstract class BaseExcelSheet  implements ExcelSheet {
	
	//Note: SimpleDateFormat is not thread safe, must not be static
	private final SimpleDateFormat TYPICAL_DATE_FORMAT = new SimpleDateFormat("MM-dd-yy");
	private final SimpleDateFormat BAD_DATE_FORMAT = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
		
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
        		//Someone messed up the dates on a couple rows
        		return BAD_DATE_FORMAT.parse(rawDate);
        	} catch (ParseException  e2) {
        		throw new IllegalArgumentException("Could not parse date " + rawDate + ". Expecting format " + TYPICAL_DATE_FORMAT, e);
        	}
        }
	}

}
