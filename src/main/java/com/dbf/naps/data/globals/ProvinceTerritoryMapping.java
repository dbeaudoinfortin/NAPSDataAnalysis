package com.dbf.naps.data.globals;

import java.util.HashMap;
import java.util.Map;

public class ProvinceTerritoryMapping {
	
	private static final Map<String, ProvTerr> provTerrMap = new HashMap<String, ProvTerr>();

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
    }

    public static String getCode(String name) {
    	ProvTerr prov = provTerrMap.get(name.toUpperCase());
    	if (null == prov) return null;
        return prov.name();
    }
    
    public static ProvTerr getProvTerr(String name) {
    	return provTerrMap.get(name.toUpperCase());
    }
}
