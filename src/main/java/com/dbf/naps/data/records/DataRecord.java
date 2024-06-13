package com.dbf.naps.data.records;

import java.math.BigDecimal;
import java.util.Date;

public class DataRecord {

	private Integer siteId;
	private Integer pollutantId;
	private Integer methodId;
	private Date datetime;
	private Integer year;
	private Integer month;
	private Integer day;
	private Integer dayOfWeek;
	private BigDecimal data;

	public DataRecord() {}

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

	public Integer getMethodId() {
		return methodId;
	}

	public void setMethodId(Integer methodId) {
		this.methodId = methodId;
	}	
}
