package com.dbf.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.OldLabelRecord;
import org.apache.poi.hssf.record.OldSheetRecord;
import org.apache.poi.hssf.record.OldStringRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.excel.Records.OldDimensionsRecord;

public class OldBIFFExcelSheet extends BaseExcelSheet {
	
	private static final Logger log = LoggerFactory.getLogger(ExcelSheetFactory.class);
	
	@SuppressWarnings("unused") //Will use eventually
	private int biffVersion;
	
    private BOFRecord bof;
    
	private String[][] rawData;
	
	public OldBIFFExcelSheet(File excelFile) throws IOException {
		loadOldBIFFFile(excelFile);
	}
	
	//Adapted from the Apache POI library's OldExcelExtractor 
	//https://github.com/apache/poi/blob/trunk/poi/src/main/java/org/apache/poi/hssf/extractor/OldExcelExtractor.java
	private void loadOldBIFFFile(File excelFile) throws FileNotFoundException, IOException {
		try (FileInputStream is = new FileInputStream(excelFile)) {
			RecordInputStream ris = new RecordInputStream(is);

			setBOF(ris); //The first record contains the BOF

			CodepageRecord codepage = null;
			while (ris.hasNextRecord()) {
				int sid = ris.getNextSid();
				ris.nextRecord();

				switch (sid) {
					case OldSheetRecord.sid:
						OldSheetRecord sheetRecord = new OldSheetRecord(ris);
						sheetRecord.setCodePage(codepage);
						log.debug("Found sheet " + sheetRecord.getSheetname());
						break;
					case OldLabelRecord.biff2_sid:
					case OldLabelRecord.biff345_sid:
						OldLabelRecord labelRecord = new OldLabelRecord(ris);
						labelRecord.setCodePage(codepage);
						rawData[labelRecord.getColumn()][labelRecord.getRow()] = labelRecord.getValue();
						break;
					case OldStringRecord.biff2_sid:
					case OldStringRecord.biff345_sid:
						OldStringRecord stringRecord = new OldStringRecord(ris);
						stringRecord.setCodePage(codepage);
						break;
					case NumberRecord.sid:
						NumberRecord nr = new NumberRecord(ris);
						rawData[nr.getColumn()][nr.getRow()] = "" + nr.getValue();
						break;
					case OldDimensionsRecord.sid:
						OldDimensionsRecord dr = new OldDimensionsRecord(ris);
						rawData = new String[dr.getLastCol()][dr.getLastRow()];
						break;
					case RKRecord.sid:
						RKRecord rr = new RKRecord(ris);
						rawData[rr.getColumn()][rr.getRow()] = "" + rr.getRKNumber();
						break;
					case CodepageRecord.sid:
						codepage = new CodepageRecord(ris);
						break;
					case FormatRecord.sid: //Doesn't support old formats, need to create OldFormatRecord class
					case 1091: //This is DATEMODE - 1900 vs 1904
					default:
						ris.readFully(IOUtils.safelyAllocate(ris.remaining(), HSSFWorkbook.getMaxRecordLength()));
				}
			}
		}
	}
	
	private void setBOF(RecordInputStream ris) {
		
		if (!ris.hasNextRecord()) {
			throw new IllegalArgumentException("File contains no records!");
		}
		ris.nextRecord();

		switch (ris.getSid()) {
		case BOFRecord.biff2_sid:
			biffVersion = 2;
			break;
		case BOFRecord.biff3_sid:
			biffVersion = 3;
			break;
		case BOFRecord.biff4_sid:
			biffVersion = 4;
			break;
		case BOFRecord.biff5_sid:
			biffVersion = 5;
			break;
		default:
			throw new IllegalArgumentException("File does not begin with a BOF, found sid of " + ris.getSid());
		}

		bof = new BOFRecord(ris);
		if (bof.getType() != BOFRecord.TYPE_WORKSHEET) {
			throw new IllegalArgumentException("File is not a worksheet.");
		}
		bof.getType();
	}

	@Override
	public int columnCount() {
		return rawData.length;
	}

	@Override
	public int rowCount() {
		if (rawData.length < 1) return 0;
		return rawData[0].length;
	}

	@Override
	public String getCellContents(int column, int row) {
		String s = rawData[column][row];
		return (null == s) ? "" : s;
	}

	@Override
	public Date getCellDate(int column, int row) {
		String rawDate = rawData[column][row];
		if(null == rawDate || "".equals(rawDate)) return null;
		
		return extractRawDate(rawDate);
	}
}
