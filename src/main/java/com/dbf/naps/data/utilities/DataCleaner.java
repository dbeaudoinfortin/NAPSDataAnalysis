package com.dbf.naps.data.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.globals.Constants;

public class DataCleaner {
	
	private static final Logger log = LoggerFactory.getLogger(DataCleaner.class);
	
	public static BigDecimal parseLatitude(String latitudeRaw) {
		try {
			return new BigDecimal(latitudeRaw);
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("Invalid latitude " + latitudeRaw, e);
		}
	}
	
	public static BigDecimal parseLongitude(String longitudeRaw) {
		BigDecimal longitude;
		
		try {
			longitude = new BigDecimal(longitudeRaw);
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("Invalid longitude " + longitudeRaw, e);
		}
		
		if (longitude.longValue() < -188L) {
			//Some of the longitude data is bad
			String longitudeCorrected = longitudeRaw.substring(1);
			if(longitudeCorrected.startsWith("1")) {
				//Is it over 100?
				longitudeCorrected = "-" + longitudeCorrected.substring(0,3) + "." + longitudeCorrected.substring(3);
			} else {
				longitudeCorrected = "-" + longitudeCorrected.substring(0,2) + "." + longitudeCorrected.substring(2);
			}
			
			try {
				longitude = new BigDecimal(longitudeCorrected);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid longitude " + longitudeRaw, e);
			}
		}
		
		return longitude;
	}
	
	public static BigDecimal extractDecimalData(String rawValue, boolean ignoreError) {
		//These all represent missing data points
		//Data points are allowed to be null.
		//Negative data points are different. They generally represent "below the detection limit"
		if (rawValue == null || rawValue.equals("") || rawValue.equals("-") || rawValue.startsWith("-99")) return null;
		
		try {
			BigDecimal decimalVal = new BigDecimal(rawValue);
			 //Make sure the scale is less than the error of a double
			decimalVal = decimalVal.setScale(10, RoundingMode.HALF_UP);
			if (decimalVal.compareTo(Constants.bigDecimal0) < 0) {
				//Negative values are to be treated as below the detection limit and thus zero
				decimalVal = Constants.bigDecimal0;
			}
    		return decimalVal;
		} catch (NumberFormatException e){
			if(!ignoreError) throw new IllegalArgumentException("Invalid decimal data point: " + rawValue, e);
			log.warn("Invalid decimal data point: " + rawValue);
			return null;
		}
	}
	
	public static Double extractDoubleData(String rawValue, boolean ignoreError) {
		//Data points are allowed to be null
		if (rawValue == null || rawValue.equals("")) return null;
		
		//We annoyingly have time data in the form of "3hr 50min"
		//Convert to the form of 5:34:30, which will then be parsed
		rawValue = rawValue.toUpperCase();
		rawValue = rawValue.replace("HR ", ":");
		rawValue = rawValue.replace("MIN", ":");
		if(rawValue.endsWith(":")) rawValue += "00";
		
		try {
			if(rawValue.contains(":")) {
				//Parse the value as a duration string in the form of hours:minutes:seconds
				return convertDurationToHours(rawValue);
			}
			return Double.parseDouble(rawValue); //Try as a double
		} catch (NumberFormatException e){
			if(!ignoreError)
				throw new IllegalArgumentException("Invalid integer data point: " + rawValue, e);
			log.warn("Invalid integer data point: " + rawValue);
			return null;
		}
	}
	
	private static double convertDurationToHours(String duration) {
        // Split the duration string into hours, minutes, and seconds
        String[] parts = duration.split(":");
        
        if (parts.length > 3 || parts.length < 2)
        	throw new NumberFormatException("Unparsable duration string: " + duration);
        
        // Parse the parts into integers
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        //Some durations are in the form of "24:00" instead of "24:00:00"
        int seconds = parts.length == 3 ? Integer.parseInt(parts[2]) : 0;

        // Convert minutes and seconds to hours
        double minutesToHours = minutes / 60.0;
        double secondsToHours = seconds / 3600.0;

        // Sum up hours, minutes in hours, and seconds in hours
        return hours + minutesToHours + secondsToHours;
    }
	
	private static final Pattern COLUMN_ABBREVIATION_PATTERN = Pattern.compile(" \\([A-Za-z0-9]+\\)"); //" (BkFLT)" in "Benzo(k)Fluoranthene (BkFLT)"
	private static final Map<String, String> COLUMN_ABBREVIATION_CACHE = new ConcurrentHashMap<String, String>();
	public static String replaceColumnHeaderAbbreviation(String rawColumnHeader) {
		rawColumnHeader = rawColumnHeader.trim();
		return COLUMN_ABBREVIATION_CACHE.computeIfAbsent(rawColumnHeader, columnHeader -> {
			Matcher matcher = COLUMN_ABBREVIATION_PATTERN.matcher(columnHeader);

	        int lastMatchStart = -1;
	        int lastMatchEnd = -1;

	        // Find the last match
	        while (matcher.find()) {
	            lastMatchStart = matcher.start();
	            lastMatchEnd = matcher.end();
	        }

	        // If a match is found, perform the replacement
	        if (lastMatchStart != -1 && lastMatchEnd != -1) {
	           return columnHeader.substring(0, lastMatchStart) + columnHeader.substring(lastMatchEnd);
	        }
	        
	        return columnHeader;
		});
	}
	
	private static final Map<String, String> COLUMN_UNITS_CACHE = new ConcurrentHashMap<String, String>();
	public static String replaceColumnHeaderUnits(String rawColumnHeader) {
		rawColumnHeader = rawColumnHeader.trim();
		return COLUMN_UNITS_CACHE.computeIfAbsent(rawColumnHeader, columnHeader -> {	        
			return columnHeader.replace(" (ug/m3)", "").replace(" ug/m3", ""); //Sometimes we have brackets, sometimes we don't ¯\_(ツ)_/¯
		});	
	}
	
	public static String sanatizeFileName(String fileName) {
		fileName = fileName.replace("/", "_");
		fileName = fileName.replace("\\", "_");
		fileName = fileName.replace("\"", "'");
		fileName = fileName.replace("*", "#");
		fileName = fileName.replace(":", ";");
		fileName = fileName.replace("|", "-");
		fileName = fileName.replace("<", "");
		fileName = fileName.replace(">", "");
		return fileName;
	}
}
