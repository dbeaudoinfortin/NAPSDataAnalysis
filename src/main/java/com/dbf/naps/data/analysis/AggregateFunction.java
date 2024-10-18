package com.dbf.naps.data.analysis;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum AggregateFunction {
	AVG,
	MIN,
	MAX,
	COUNT,
	SUM,
	NONE;
	
	public static final String ALL_VALUES = Arrays.stream(AggregateFunction.values()).map(f->f.name()).collect(Collectors.joining(", "));
}
