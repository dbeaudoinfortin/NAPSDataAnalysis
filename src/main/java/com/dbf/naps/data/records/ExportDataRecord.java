package com.dbf.naps.data.records;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.csv.CSVPrinter;

public class ExportDataRecord {

	public enum Header {
	     NAPS_Site_ID,
	     Station_Name,
	     Pollutant,
	     Date,
	     Value,
	     Units
	 }
	
	private Integer siteNapsId;
	private String  siteName;
	private String pollutantName;
	private Date datetime;
	private BigDecimal data;
	private String units;
	
	public ExportDataRecord() {}
	
	public void printToCSV(CSVPrinter printer, SimpleDateFormat dateFormat) throws IOException {
		printer.printRecord(siteNapsId, siteName, pollutantName, dateFormat.format(datetime), data, units);
	}
	
	public Class<? extends Enum<?>> getHeader() {
		return Header.class;
	}

	public Integer getSiteNapsId() {
		return siteNapsId;
	}

	public String getSiteName() {
		return siteName;
	}

	public String getPollutantName() {
		return pollutantName;
	}

	public Date getDatetime() {
		return datetime;
	}


	public BigDecimal getData() {
		return data;
	}

	public String getUnits() {
		return units;
	}

	public void setSiteNapsId(Integer siteNapsId) {
		this.siteNapsId = siteNapsId;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public void setPollutantName(String pollutantName) {
		this.pollutantName = pollutantName;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public void setData(BigDecimal data) {
		this.data = data;
	}

	public void setUnits(String units) {
		this.units = units;
	}
}
