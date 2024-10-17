package com.dbf.naps.data.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.dbf.naps.data.globals.Constants;

public class Utils {

	public static boolean isAllYears(int yearStart, int yearEnd) {
		return yearStart == Constants.DATASET_YEAR_START && yearEnd == Constants.DATASET_YEAR_END;
	}
	
	public static Collection<Integer> getYearList(int yearStart, int yearEnd) {
		if(isAllYears(yearStart, yearEnd))
			return null; //Include all years
		
		return IntStream.range(yearStart, yearEnd + 1).boxed().toList();
	}
	
	public static void prettyPrintStringList(Collection<String> strings, StringBuilder sb) {
		prettyPrintStringList(strings, sb, true);
	}
	
	public static void prettyPrintStringList(Collection<String> stringsCollection, StringBuilder sb, boolean sort) {
		if(stringsCollection.isEmpty()) return;
		
		//Might not be an ordered list, might be a set
		List<String> strings = new ArrayList<String>(stringsCollection);
		if(sort && strings.size() > 1) Collections.sort(strings);
		
		sb.append(strings.get(0));
		if(strings.size() == 1)
			return;
		
		if(strings.size() == 2) {
			sb.append(" and ");
			sb.append(strings.get(1));
			return;
		}
		
		for(int i = 1; i < strings.size(); i++) {
			sb.append(i == (strings.size()-1) ? ", and " : ", ");
			sb.append(strings.get(i));
		}
	}
}
