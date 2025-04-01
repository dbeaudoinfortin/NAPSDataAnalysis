package com.dbf.naps.data.analysis.query.json;

public abstract class JsonRecord {
	
	private String name;
	
	public JsonRecord(String name) {
		super();
		this.name = name;
	}

	public JsonRecord() {}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
