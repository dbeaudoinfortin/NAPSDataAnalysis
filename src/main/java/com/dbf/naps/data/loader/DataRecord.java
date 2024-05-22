package com.dbf.naps.data.loader;

import java.math.BigDecimal;
import java.util.Date;

public class DataRecord {

	private Integer siteId;
	private Integer pollutantId;
	private Date datetime;
	private Integer year;
	private Integer month;
	private Integer day;
	private Integer dayOfWeek;
	private BigDecimal data;

	public DataRecord() {}
	
	public DataRecord(DataRecord other) {
		this.siteId = other.siteId;
		this.pollutantId = other.pollutantId;
		this.datetime = other.datetime;
		this.year = other.year;
		this.month = other.month;
		this.day = other.day;
		this.dayOfWeek = other.dayOfWeek;
		this.data = other.data;
	}

	public Integer getSiteId() {
		return siteId;
	}
	
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}
	
	public Integer getPollutantId() {
		return pollutantId;
	}
	
	public void setPollutantId(Integer pollutantId) {
		this.pollutantId = pollutantId;
	}
	
	public Date getDatetime() {
		return datetime;
	}
	
	//Deprecated Date functions since Java 1.1, probably safe to ignore :)
	@SuppressWarnings("deprecation")
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
		this.day = datetime.getDate();
		this.dayOfWeek = datetime.getDay();
		this.month = datetime.getMonth() + 1;
		this.year = datetime.getYear() + 1900; //This is really lazy, oh well
	}
	
	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Integer getMonth() {
		return month;
	}
	
	public void setMonth(Integer month) {
		this.month = month;
	}
	
	public Integer getDay() {
		return day;
	}
	
	public void setDay(Integer day) {
		this.day = day;
	}
	
	public Integer getDayOfWeek() {
		return dayOfWeek;
	}
	
	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	public BigDecimal getData() {
		return data;
	}
	
	public void setData(BigDecimal data) {
		this.data = data;
	}	
}
