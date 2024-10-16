package com.dbf.naps.data.analysis.heatmap.axis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Axis<T> {
	
	private final String title;
	protected int count;
	protected final Map<T, String> entryLabels = new HashMap<T, String>();
	protected final Map<T, Integer> entryIndices = new HashMap<T, Integer>();
	protected final Map<String, Integer> labelIndices = new HashMap<String, Integer>();
	
	public Axis(String title){
		this.title = title;
	}
	
	public Axis(String title, Collection<T> entries){
		this.title = title;
		for(T entry : entries) {
			addEntry(entry, entry.toString());
		}
	}
	
	public Axis(String title, int count){
		this.title = title;
		this.count = count;
	}
	
	public int getCount() {
		return count;
	}

	public Map<T, String> getEntryLabels() {
		return entryLabels;
	}

	public Map<T, Integer> getEntryIndices() {
		return entryIndices;
	}
	
	public Map<String, Integer> getLabelIndices() {
		return labelIndices;
	}
	
	public abstract String getLabel(T entry);
	
	public abstract Integer getIndex(Object entry);
	
	public void addEntry(T entry, String label) {
		if(entryLabels.containsKey(entry)) return;
		entryLabels.put(entry, label);
		entryIndices.put(entry, count);
		labelIndices.put(label, count);
		count++;
	}

	public String getTitle() {
		return title;
	}
}
