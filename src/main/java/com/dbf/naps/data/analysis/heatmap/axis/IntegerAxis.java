package com.dbf.naps.data.analysis.heatmap.axis;

import java.util.Collection;

public class IntegerAxis extends Axis<Integer> {

	public IntegerAxis(String title) {
		super(title);
	}
	
	public IntegerAxis(String title, int count) {
		super(title, count);
	}
	
	public IntegerAxis(String title, Collection<Integer> entries){
		super(title, entries);
	}

	public IntegerAxis(String title, int min, int max) {
		super(title);
		int count = 0;
		for(int i = min; i <= max; i++ ) {
			final String label = "" + i;
			entryLabels.put(i, label);
			labelIndices.put(label, count);
			entryIndices.put(i, count++);	
		}
		this.count = count;
	}

	@Override
	public String getLabel(Integer entry) {
		return this.entryLabels.get(entry);
	}

	@Override
	public Integer getIndex(Object entry) {
		return this.entryIndices.get((Integer) entry);
	}
}
