package com.dbf.naps.data.records;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public abstract class DataRecord {

	private Integer siteId;
	private Integer pollutantId;
	private Integer methodId;
	private Date datetime;
	private Integer year;
	private Integer month;
	private Integer day;
	private Integer dayOfWeek;
	private Integer dayOfYear;
	private Integer weekOfYear;
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
	
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
		
		//All dates in excel are in the GMT timezone
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.setTime(datetime);
	    this.day = calendar.get(Calendar.DAY_OF_MONTH);
	    this.dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
	    this.dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
	    this.month = calendar.get(Calendar.MONTH) + 1;
	    this.year = calendar.get(Calendar.YEAR);
	    this.weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
	    
	    //Make an adjustment for the last week of December
	    if(this.weekOfYear == 1 && this.month == 12)
	    	this.weekOfYear = 53;
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

	public Integer getWeekOfYear() {
		return weekOfYear;
	}

	public void setWeekOfYear(Integer weekOfYear) {
		this.weekOfYear = weekOfYear;
	}

	public Integer getDayOfYear() {
		return dayOfYear;
	}

	public void setDayOfYear(Integer dayOfYear) {
		this.dayOfYear = dayOfYear;
	}	
}
