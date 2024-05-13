package com.dbf.naps.data.loader.continuous;

import java.math.BigDecimal;
import java.util.Date;

public class ContinuousDataRecord {

	private Integer siteId;
	private Integer pollutantId;
	private Date datetime;
	private Integer year;
	private Integer month;
	private Integer day;
	private Integer hour;
	private Integer dayOfWeek;
	private BigDecimal data;

	public ContinuousDataRecord() {	}
	
	public ContinuousDataRecord(Integer siteId, Integer pollutantId, Date datetime, Integer year, Integer month,
			Integer day, Integer hour, Integer dayOfWeek, BigDecimal data) {
		super();
		this.siteId = siteId;
		this.pollutantId = pollutantId;
		this.datetime = datetime;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.dayOfWeek = dayOfWeek;
		this.data = data;
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
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
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
	public Integer getHour() {
		return hour;
	}
	public void setHour(Integer hour) {
		this.hour = hour;
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
