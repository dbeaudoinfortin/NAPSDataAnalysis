package com.dbf.naps.data.analysis.query;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum QueryOutputTypes {
	CSV,
	JSON,
	JSON_SLIM;
	
	public static final String ALL_VALUES = Arrays.stream(values()).map(f->f.name()).collect(Collectors.joining(", "));
}
