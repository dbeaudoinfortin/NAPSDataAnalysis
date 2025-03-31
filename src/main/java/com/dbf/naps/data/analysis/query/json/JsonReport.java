package com.dbf.naps.data.analysis.query.json;

public class JsonReport {
	
	private String title;
	private String units;
	private JsonRecord data;
	
	public JsonReport() {}

	public JsonReport(String title, String units, JsonRecord data) {
		super();
		this.title = title;
		this.setUnits(units);
		this.data = data;
	}
	
	public String getTitle() {
		return title;
	}

	public JsonRecord getData() {
		return data;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setData(JsonRecord data) {
		this.data = data;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}
}
