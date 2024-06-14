package com.dbf.naps.data.records;

public class DataGroup {
	private Integer year;
	private String  pollutantName;
	private Integer siteID;
	
	public Integer getYear() {
		return year;
	}
	
	public String getPollutantName() {
		return pollutantName;
	}
	
	public Integer getSiteID() {
		return siteID;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public void setPollutantName(String pollutantName) {
		this.pollutantName = pollutantName;
	}
	
	public void setSiteID(Integer siteID) {
		this.siteID = siteID;
	}
}
