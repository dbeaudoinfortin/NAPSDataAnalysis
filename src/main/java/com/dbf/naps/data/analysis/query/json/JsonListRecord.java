package com.dbf.naps.data.analysis.query.json;

import java.util.ArrayList;
import java.util.List;

public class JsonListRecord extends JsonMultiRecord {
	
	private List<JsonSingleRecord> values;
	
	public JsonListRecord() {}

	public JsonListRecord(String name, List<JsonSingleRecord> data) {
		super(name);
		this.values = data;
	}
	
	public JsonListRecord(String name) {
		super(name);
		this.values = new ArrayList<JsonSingleRecord>();
	}

	public List<JsonSingleRecord> getValues() {
		return values;
	}

	public void setValues(List<JsonSingleRecord> values) {
		this.values = values;
	}
	
	
}
