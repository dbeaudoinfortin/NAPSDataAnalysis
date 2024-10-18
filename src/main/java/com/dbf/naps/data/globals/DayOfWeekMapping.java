package com.dbf.naps.data.globals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DayOfWeekMapping {
	
	private static final List<String> daysOfWeek = new ArrayList<String>(7); 

    static {
    	daysOfWeek.add("Sunday");
    	daysOfWeek.add("Monday");
    	daysOfWeek.add("Tuesday");
    	daysOfWeek.add("Wednesday");
    	daysOfWeek.add("Thursday");
    	daysOfWeek.add("Friday");
    	daysOfWeek.add("Saturday");
    }

    public static String getDayOfWeek(int day) {
    	return daysOfWeek.get(day-1);
    }
    
    public static List<String> getDayOfWeekStrings(Collection<Integer> days) {
    	return days.stream().sorted().map(day->getDayOfWeek(day)).toList();
    }
}
