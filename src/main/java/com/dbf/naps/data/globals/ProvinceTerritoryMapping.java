package com.dbf.naps.data.globals;

import java.util.HashMap;
import java.util.Map;

public class ProvinceTerritoryMapping {
	
	private static final Map<String, String> provTerrMap = new HashMap<>();

    static {
        provTerrMap.put("NEWFOUNDLAND AND LABRADOR", "NL");
        provTerrMap.put("PRINCE EDWARD ISLAND", "PE");
        provTerrMap.put("NOVA SCOTIA", "NS");
        provTerrMap.put("NEW BRUNSWICK", "NB");
        provTerrMap.put("QUEBEC", "QC");
        provTerrMap.put("ONTARIO", "ON");
        provTerrMap.put("MANITOBA", "MB");
        provTerrMap.put("SASKATCHEWAN", "SK");
        provTerrMap.put("ALBERTA", "AB");
        provTerrMap.put("BRITISH COLUMBIA", "BC");
        provTerrMap.put("YUKON", "YT");
        provTerrMap.put("NORTHWEST TERRITORIES", "NT");
        provTerrMap.put("NUNAVUT", "NU");
    }

    public static String getCode(String name) {
        return provTerrMap.get(name.toUpperCase());
    }
}
