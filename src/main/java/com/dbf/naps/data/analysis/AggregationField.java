package com.dbf.naps.data.analysis;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum AggregationField {
	YEAR,
	MONTH,
	DAY,
	HOUR,
	DAY_OF_WEEK,
	DAY_OF_YEAR,
	WEEK_OF_YEAR,
	NAPS_ID,
	POLLUTANT,
	PROVINCE_TERRITORY,
	URBANIZATION,
	SITE_TYPE;
	
	public static final String ALL_VALUES = Arrays.stream(AggregationField.values()).map(f->f.name()).collect(Collectors.joining(", "));
	
	public String getPrettyName() {
		switch (this) {
		case DAY:
			return "Day of the Month";
		case DAY_OF_YEAR:
			return "Day of the Year";
		case HOUR:
			return "Hour";
		case WEEK_OF_YEAR:
			return "Week of the Year";
		case DAY_OF_WEEK:
			return "Day of the Week";
		case MONTH:
			return "Month";
		case YEAR:
			return "Year";
		case NAPS_ID:
			return "NAPS Site ID";
		case POLLUTANT:
			return "Pollutant";
		case PROVINCE_TERRITORY:
			return "Province/Territory";
		case URBANIZATION:
			return "Urbanization";
		case SITE_TYPE:
			return "Site Type";
		default:
			return "";
		}
	}
}
