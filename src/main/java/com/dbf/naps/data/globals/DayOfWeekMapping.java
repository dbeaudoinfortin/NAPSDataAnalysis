package com.dbf.naps.data.globals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayOfWeekMapping {
	
	private static final Map<String, Integer> daysMap = new HashMap<String, Integer>(7);
	private static final List<String> daysOfWeek = new ArrayList<String>(7); 

    static {
    	
    	daysMap.put("sunday", 1);
    	daysMap.put("monday", 2);
    	daysMap.put("tuesday", 3);
    	daysMap.put("wednesday", 4);
    	daysMap.put("thursday", 5);
    	daysMap.put("friday", 6);
    	daysMap.put("saturday", 7);
    	
    	daysOfWeek.add("Sunday");
    	daysOfWeek.add("Monday");
    	daysOfWeek.add("Tuesday");
    	daysOfWeek.add("Wednesday");
    	daysOfWeek.add("Thursday");
    	daysOfWeek.add("Friday");
    	daysOfWeek.add("Saturday");
    }

    public static Integer getDayOfWeek(String name) {
    	return daysMap.get(name.toLowerCase());
    }
    
    public static String getDayOfWeek(int day) {
    	return daysOfWeek.get(day-1);
    }
    
    public static List<String> getDayOfWeekStrings(Collection<Integer> days) {
    	return days.stream().sorted().map(day->getDayOfWeek(day)).toList();
    }
}
