package com.dbf.naps.data.globals;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiteTypeMapping {
	private static final Map<SiteType, String> prettyMap = new HashMap<SiteType, String>(4);
	
	static {
		prettyMap.put(SiteType.PE, "General Population");
		prettyMap.put(SiteType.RB, "Regional Background");
		prettyMap.put(SiteType.T,  "Transportation");
		prettyMap.put(SiteType.PS, "Point Source");
	}
	
	 public static String getSiteType(SiteType siteType) {
	    	return prettyMap.get(siteType);
	 }
	 
	 public static List<String> getSiteTypeStrings(Collection<SiteType> siteTypes) {
	    return siteTypes.stream().sorted().map(st->prettyMap.get(st)).toList();
	 }
}
