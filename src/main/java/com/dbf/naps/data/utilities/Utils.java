package com.dbf.naps.data.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	
    public static String convertToJsObjectNotation(String variableName, Map<String, Map<Integer, Set<Integer>>> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("const ");
        sb.append(variableName); //Ex. integratedDataMap
        sb.append("={");
        for (Map.Entry<String, Map<Integer, Set<Integer>>> entry : map.entrySet()) {
            sb.append("\"");
            sb.append(entry.getKey()); //Pollutant
            sb.append("\":{");
            for (Map.Entry<Integer, Set<Integer>> yearEntry : entry.getValue().entrySet()) {
                sb.append(yearEntry.getKey()); //year
                sb.append(":");
                stringifyCollection(sb, yearEntry.getValue()); //site ids, without spaces
                sb.append(",");
            }
            sb.setLength(sb.length() - 1); //Remove the last comma
            sb.append("},");
        }
        sb.setLength(sb.length() - 1); //Remove the last comma
        sb.append("}");
        return sb.toString();
    }
    
    public static void stringifyCollection(StringBuilder sb, Collection<?> items) {
    	sb.append("[");
    	for (Object item :items) {
    		sb.append(item.toString());
    		sb.append(",");
    	}
    	sb.setLength(sb.length() - 1); //Remove the last comma
    	sb.append("]");
    }
}
