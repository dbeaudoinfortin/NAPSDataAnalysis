package com.dbf.naps.data.globals;

import java.util.HashMap;
import java.util.Map;

public class MonthMapping {
	
	private static final Map<String, Integer> monthMap = new HashMap<String, Integer>();

    static {
    	 monthMap.put("JANUARY", 1);
         monthMap.put("FEBRUARY", 2);
         monthMap.put("MARCH", 3);
         monthMap.put("APRIL", 4);
         monthMap.put("MAY", 5);
         monthMap.put("JUNE", 6);
         monthMap.put("JULY", 7);
         monthMap.put("AUGUST", 8);
         monthMap.put("SEPTEMBER", 9);
         monthMap.put("OCTOBER", 10);
         monthMap.put("NOVEMBER", 11);
         monthMap.put("DECEMBER", 12);
    }

    public static Integer getMonth(String name) {
    	return monthMap.get(name.toUpperCase());
    }
}
