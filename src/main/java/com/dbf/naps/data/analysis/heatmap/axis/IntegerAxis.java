package com.dbf.naps.data.analysis.heatmap.axis;

import java.util.Collection;

public class IntegerAxis extends Axis<Integer> {

	public IntegerAxis() {}
	
	public IntegerAxis(int count) {
		super(count);
	}
	
	public IntegerAxis(Collection<Integer> entries){
		super(entries);
	}

	public IntegerAxis(int min, int max) {
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
