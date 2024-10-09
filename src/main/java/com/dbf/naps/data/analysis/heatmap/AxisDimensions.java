package com.dbf.naps.data.analysis.heatmap;

import java.util.Map;

public class AxisDimensions<T> {
	
	public final T min;
	public final T max;
	public final int count;
	public final Map<T, Integer> distinctEntries;
	public final Class<T> type;
	private final boolean distinct;
	
	public AxisDimensions(T min, T max, int count, Class<T> type) {
		this(min, max, count, null, type);
	}
	
	public AxisDimensions(Map<T, Integer> distinctEntries, Class<T> type) {
		this(null, null, distinctEntries, type);
	}
	
	public AxisDimensions(T min, T max, Map<T, Integer> distinctEntries, Class<T> type) {
		this(min, max, distinctEntries.size(), distinctEntries, type);
	}
	
	private AxisDimensions(T min, T max, int count, Map<T, Integer> distinctEntries, Class<T> type) {
		this.min = min;
		this.max = max;
		this.count = count;
		this.distinctEntries = distinctEntries;
		this.type = type;
		this.distinct = (distinctEntries != null);
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

	public int getCount() {
		return count;
	}

	public Map<T, Integer> getDistinctEntries() {
		return distinctEntries;
	}

	public Class<T> getType() {
		return type;
	}

	public boolean isDistinct() {
		return distinct;
	}
}
