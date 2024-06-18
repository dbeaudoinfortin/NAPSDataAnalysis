package com.dbf.naps.data.records;

import java.io.IOException;
import java.text.SimpleDateFormat;
import org.apache.commons.csv.CSVPrinter;

public class IntegratedExportDataRecord extends ExportDataRecord {

	public enum Header {
	     NAPS_Site_ID,
	     Station_Name,
	     Pollutant,
	     Date,
	     Value,
	     Units,
	     Report_Type,
	     Method
	 }
	
	private String reportType;
	private String method;
	
	public IntegratedExportDataRecord() {}
	
	@Override
	public void printToCSV(CSVPrinter printer, SimpleDateFormat dateFormat) throws IOException {
		printer.printRecord(super.getSiteNapsId(), super.getSiteName(), super.getPollutantName(), 
				dateFormat.format(super.getDatetime()), super.getData(), super.getUnits(), reportType, method);
	}
	
	@Override
	public Class<? extends Enum<?>> getHeader() {
		return Header.class;
	}

	public String getReportType() {
		return reportType;
	}

	public String getMethod() {
		return method;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}
