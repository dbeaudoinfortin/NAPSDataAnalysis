package com.dbf.naps.data.analysis.heatmap.axis;

public class StringAxis extends Axis<String> {

	public StringAxis(int count) {
		super(count);
	}
	
	public StringAxis(String... strings) {
		super(strings.length);
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
