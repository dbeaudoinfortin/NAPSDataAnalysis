package com.dbf.naps.data.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.FileRunner;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.records.ExportDataRecord;
import com.dbf.naps.data.utilities.Utils;

public abstract class ExporterRunner<O extends ExtractorOptions> extends FileRunner<O> {
	
	private static final int MAX_ROWS_PER_QUERY = 1_000_000;
	
	private static final Logger log = LoggerFactory.getLogger(ExporterRunner.class);
	
	//Note: SimpleDateFormat is not thread safe, must not be static
	private final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		
	public ExporterRunner(int threadId, O config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}
	
	@Override
	public void run() {
		try {
			checkFile();
			
			log.info(getThreadId() + ":: Starting export of CSV file " + getDataFile() + ".");
			exportData();
			log.info(getThreadId() + ":: Completed export of CSV file " + getDataFile() + ".");
		 } catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR exporting to CSV file " + getDataFile() + ".", t);
			return;
		 }
	}

	public void exportData() {
		int offset = 0;
		List<? extends ExportDataRecord> records = null;
		while (true) {
			try(SqlSession session = getSqlSessionFactory().openSession(true)) {
				records = session.getMapper(getDataMapper()).getExportData(
					getSpecificYear() != null ? List.of(getSpecificYear()) : Utils.getYearList(getConfig().getYearStart(), getConfig().getYearEnd()),
					getSpecificPollutant() != null ? List.of(getSpecificPollutant()) : getConfig().getPollutants(),
					getSpecificSite() != null ? List.of(getSpecificSite()) : getConfig().getSites(), offset, MAX_ROWS_PER_QUERY);
			}
			
			if(records.isEmpty()) {
				if (offset == 0) log.info(getThreadId() + ":: No records found for " + getDataFile() + ". Skipping file.");
				return;
			}
		
			try {
				writeToCSV(records, offset == 0);
			} catch (IOException e) {
				throw new IllegalArgumentException("Failed to write to CSV file " + getDataFile(), e);
			}
			
			if(records.size() < MAX_ROWS_PER_QUERY) {
				//Assume no more data
				log.info(getThreadId() + ":: No more records found for " + getDataFile() + ".");
				return;
			}
			offset += MAX_ROWS_PER_QUERY;
		}
		
	}
	
	private void writeToCSV(List<? extends ExportDataRecord> records, boolean printHeader) throws IOException {
		log.info(getThreadId() + ":: Writing " + records.size() + " record(s) for " + getDataFile() + ".");
		CSVFormat format = CSVFormat.EXCEL
			.builder()
			.setTrim(false)
			.setHeader(records.get(0).getHeader())
			.setSkipHeaderRecord(!printHeader)
			.build();
		try(BufferedWriter writer = Files.newBufferedWriter(getDataFile().toPath(), StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
			writer.write('\ufeff'); //Manually print the UTF-8 BOM
			try(CSVPrinter printer = new CSVPrinter(writer, format)){
				for(ExportDataRecord record : records) {
					record.printToCSV(printer, ISO_DATE_FORMAT);
				}
			}
		}
	}
	
	protected abstract Class<? extends DataMapper> getDataMapper();
}
