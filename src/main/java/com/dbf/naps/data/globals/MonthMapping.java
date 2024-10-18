package com.dbf.naps.data.globals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthMapping {
	
	private static final Map<String, Integer> monthMap = new HashMap<String, Integer>(12);
	private static final List<String> months = new ArrayList<String>(12); 

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

		months.add("January");
		months.add("February");
		months.add("March");
		months.add("April");
		months.add("May");
		months.add("June");
		months.add("July");
		months.add("August");
		months.add("September");
		months.add("October");
		months.add("November");
		months.add("December");
    }

    public static Integer getMonth(String name) {
    	return monthMap.get(name.toUpperCase());
    }
    
    public static String getMonth(int month) {
    	return months.get(month-1);
    }
    
    public static List<String> getMonthStrings(Collection<Integer> months) {
    	return months.stream().sorted().map(month->getMonth(month)).toList();
    }
}
