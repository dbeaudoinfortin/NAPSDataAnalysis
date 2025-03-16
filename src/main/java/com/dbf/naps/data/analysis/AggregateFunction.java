package com.dbf.naps.data.analysis;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum AggregateFunction {
	AVG,
	MIN,
	MAX,
	COUNT,
	SUM,
	P50,
	P95,
	P98,
	P99,
	NONE;
	
	public static final String ALL_VALUES = Arrays.stream(values()).map(f->f.name()).collect(Collectors.joining(", "));
}
