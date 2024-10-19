package com.dbf.naps.data.globals;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ProvTerr {
	NL,
	PE,
	NS,
	NB,
	QC,
	ON,
	MB,
	SK,
	AB,
	BC,
	YT,
	NT,
	NU;
	
	public static final String ALL_VALUES = Arrays.stream(values()).map(f->f.name()).collect(Collectors.joining(", "));
}
