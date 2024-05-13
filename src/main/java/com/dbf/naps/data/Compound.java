package com.dbf.naps.data;

import java.util.HashMap;
import java.util.Map;

public enum Compound {
	CO,
	NO2,
	NO,
	NOX,
	O3,
	SO2,
	PM10,
	PM25;
	
	private static Map<String, Compound> lookupMap;
	
	static {
		Compound[] allValues = Compound.values();
		lookupMap = new HashMap<String, Compound>(allValues.length);
		for(Compound compound : allValues) {
			lookupMap.put(compound.name(), compound);
		}
	}
	
	public static boolean contains(String value) {
		return lookupMap.containsKey(value);
	}
}
