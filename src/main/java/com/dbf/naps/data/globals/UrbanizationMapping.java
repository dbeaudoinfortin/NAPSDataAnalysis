package com.dbf.naps.data.globals;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrbanizationMapping {
	private static final Map<Urbanization, String> prettyMap = new HashMap<Urbanization, String>(4);
	
	static {
		prettyMap.put(Urbanization.LU, "Large Urban");
		prettyMap.put(Urbanization.MU, "Medium Urban");
		prettyMap.put(Urbanization.SU, "Small Urban");
		prettyMap.put(Urbanization.NU, "Rural");
	}
	
	 public static String getUrbanization(Urbanization urbanization) {
	    return prettyMap.get(urbanization);
	 }
	 
	 public static List<String> getUrbanizationStrings(Collection<Urbanization> urbs) {
	    return urbs.stream().sorted().map(u->prettyMap.get(u)).toList();
	 }   
}
