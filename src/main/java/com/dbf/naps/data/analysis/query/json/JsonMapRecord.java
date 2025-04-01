package com.dbf.naps.data.analysis.query.json;

import java.util.HashMap;
import java.util.Map;

public class JsonMapRecord<T> extends JsonMultiRecord {
	
	private Map<T, JsonRecord> values;
	
	public JsonMapRecord() {}

	public JsonMapRecord(String name, Map<T, JsonRecord> data) {
		super(name);
		this.values = data;
	}
	
	public JsonMapRecord(String name) {
		super(name);
		this.values = new HashMap<T, JsonRecord>();
	}

	public Map<T, JsonRecord> getValues() {
		return values;
	}

	public void setValues(Map<T, JsonRecord> values) {
		this.values = values;
	}
	
	
}
