package com.dbf.naps.data.globals;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvinceTerritoryMapping {
	
	private static final Map<String, ProvTerr> provTerrMap = new HashMap<String, ProvTerr>(13);
	private static final Map<ProvTerr, String> prettyMap = new HashMap<ProvTerr, String>(13);

    static {
        provTerrMap.put("NEWFOUNDLAND AND LABRADOR", ProvTerr.NL);
        provTerrMap.put("PRINCE EDWARD ISLAND", ProvTerr.PE);
        provTerrMap.put("NOVA SCOTIA", ProvTerr.NS);
        provTerrMap.put("NEW BRUNSWICK", ProvTerr.NB);
        provTerrMap.put("QUEBEC", ProvTerr.QC);
        provTerrMap.put("ONTARIO", ProvTerr.ON);
        provTerrMap.put("MANITOBA", ProvTerr.MB);
        provTerrMap.put("SASKATCHEWAN", ProvTerr.SK);
        provTerrMap.put("ALBERTA", ProvTerr.AB);
        provTerrMap.put("BRITISH COLUMBIA", ProvTerr.BC);
        provTerrMap.put("YUKON", ProvTerr.YT);
        provTerrMap.put("NORTHWEST TERRITORIES", ProvTerr.NT);
        provTerrMap.put("NUNAVUT", ProvTerr.NU);
        
        prettyMap.put(ProvTerr.NL, "Newfoundland and Labrador");
        prettyMap.put(ProvTerr.PE, "Prince Edward Island");
        prettyMap.put(ProvTerr.NS, "Nova Scotia");
        prettyMap.put(ProvTerr.NB, "New Brunswick");
        prettyMap.put(ProvTerr.QC, "Quebec");
        prettyMap.put(ProvTerr.ON, "Ontario");
        prettyMap.put(ProvTerr.MB, "Manitoba");
        prettyMap.put(ProvTerr.SK, "Saskatchewan");
        prettyMap.put(ProvTerr.AB, "Alberta");
        prettyMap.put(ProvTerr.BC, "British Columbia");
        prettyMap.put(ProvTerr.YT, "Yukon");
        prettyMap.put(ProvTerr.NT, "Northwest Territories");
        prettyMap.put(ProvTerr.NU, "Nunavut");
    }

    public static String getCode(String name) {
    	ProvTerr prov = provTerrMap.get(name.toUpperCase());
    	if (null == prov) return null;
        return prov.name();
    }
    
    public static ProvTerr getProvTerr(String name) {
    	return provTerrMap.get(name.toUpperCase());
    }
    
    public static String getProvTerr(ProvTerr prov) {
    	return prettyMap.get(prov);
    }
    
    public static List<String> getProvTerrStrings(Collection<ProvTerr> provs) {
    	return provs.stream().map(prov->getProvTerr(prov)).sorted().toList();
    }   
}
