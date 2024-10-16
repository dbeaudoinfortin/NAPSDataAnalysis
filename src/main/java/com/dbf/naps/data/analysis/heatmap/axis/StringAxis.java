package com.dbf.naps.data.analysis.heatmap.axis;

import java.util.Collection;

public class StringAxis extends Axis<String> {

	public StringAxis(String title, int count) {
		super(title, count);
	}
	
	public StringAxis(String title, Collection<String> entries){
		super(title, entries);
	}
	
	public StringAxis(String title, String... strings) {
		super(title, strings.length);
		for(int i = 0; i < strings.length; i++ ) {
			entryLabels.put(strings[i], strings[i]);
			entryIndices.put(strings[i], i);
			labelIndices.put(strings[i], i);
		}
	}
	
	@Override
	public String getLabel(String entry) {
		return entry;
	}

	@Override
	public Integer getIndex(Object entry) {
		return this.entryIndices.get((String) entry);
	}

}
