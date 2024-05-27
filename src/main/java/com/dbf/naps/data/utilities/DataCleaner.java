package com.dbf.naps.data.utilities;

import java.math.BigDecimal;

public class DataCleaner {
	
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
	
	public static BigDecimal extractDataPoint(String rawValue) {
		//Data points are allowed to be null
		if (rawValue == null || rawValue.equals("")) return null;
		
		//We need to convert via a double
		try {
    		return new BigDecimal(rawValue);
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("Invalid data point: " + rawValue, e);
		}
		
	}
}
