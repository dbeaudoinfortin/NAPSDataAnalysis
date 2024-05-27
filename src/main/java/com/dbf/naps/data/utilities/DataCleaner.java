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
	
	public static Integer extractIntegerData(String rawValue) {
		//Data points are allowed to be null
		if (rawValue == null || rawValue.equals("")) return null;
		
		try {
			if(rawValue.contains(".")) {
				return (int) Double.parseDouble(rawValue); //Try as a double
			} else {
				return Integer.parseInt(rawValue);
			}
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("Invalid integer data point: " + rawValue, e);
		}
	}
}
