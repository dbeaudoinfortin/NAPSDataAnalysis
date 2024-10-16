package com.dbf.naps.data.utilities;

import java.util.Collection;
import java.util.stream.IntStream;

import com.dbf.naps.data.globals.Constants;

public class Utils {

	public static Collection<Integer> getYearList(int yearStart, int yearEnd) {
		if(yearStart == Constants.DATASET_YEAR_START && yearEnd == Constants.DATASET_YEAR_END)
			return null; //Include all years
		
		return IntStream.range(yearStart, yearEnd + 1).boxed().toList();
	}
}
