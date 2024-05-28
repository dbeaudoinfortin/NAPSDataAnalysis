package com.dbf.naps.data.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		//Data points are allowed to be null
		if (rawValue == null || rawValue.equals("")) return null;
		
		try {
			BigDecimal decimalVal = new BigDecimal(rawValue);
			 //Make sure the scale is less than the error of a double
			decimalVal = decimalVal.setScale(10, RoundingMode.HALF_UP);
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
			if(!ignoreError) throw new IllegalArgumentException("Invalid integer data point: " + rawValue, e);
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
}
