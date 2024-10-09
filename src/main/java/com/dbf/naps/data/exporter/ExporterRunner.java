package com.dbf.naps.data.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.DBRunner;
import com.dbf.naps.data.db.mappers.DataMapper;
import com.dbf.naps.data.records.ExportDataRecord;

public abstract class ExporterRunner extends DBRunner<ExporterOptions> {
	
	private static final int MAX_ROWS_PER_QUERY = 1_000_000;
	
	private static final Logger log = LoggerFactory.getLogger(ExporterRunner.class);
	
	private File dataFile;
	private Integer specificYear;
	private String specificPollutant;
	private Integer specificSite;
	
	//Note: SimpleDateFormat is not thread safe, must not be static
	private final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		
	public ExporterRunner(int threadId, ExporterOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory);
		this.dataFile = dataFile;
		this.specificYear = specificYear;
		this.specificPollutant = specificPollutant;
		this.specificSite = specificSite;
	}
	
	@Override
	public void run() {
		
		try {
			log.info(getThreadId() + ":: Starting export of CSV file " + dataFile + ".");
			exportData();
			log.info(getThreadId() + ":: Completed export of CSV file " + dataFile + ".");
		 } catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR exporting to CSV file " + dataFile + ".", t);
			return;
		 }
	}

	public void exportData() {		
		if(dataFile.isDirectory()) {
			throw new IllegalArgumentException("File path is a directory: " + dataFile);
		} else if (dataFile.isFile()) {
			if(getConfig().isOverwriteFiles()) {
				log.warn(getThreadId() + ":: Deleting existing file " + dataFile + ".");
				dataFile.delete();
			} else {
				throw new IllegalArgumentException("Cannot export to path \"" + dataFile +"\". The file already exists and the overwrite flag is set to false.");
			}
		}
		
		int offset = 0;
		List<? extends ExportDataRecord> records = null;
		while (true) {
			try(SqlSession session = getSqlSessionFactory().openSession(true)) {
				records = session.getMapper(getDataMapper()).getExportData(
					specificYear != null ? List.of(specificYear) : IntStream.range(getConfig().getYearStart(), getConfig().getYearEnd() + 1).boxed().toList(),
					specificPollutant != null ? List.of(specificPollutant) : getConfig().getPollutants(),
					specificSite != null ? List.of(specificSite) : getConfig().getSites(), offset, MAX_ROWS_PER_QUERY);
			}
			
			if(records.isEmpty()) {
				if (offset == 0) log.info(getThreadId() + ":: No records found for " + dataFile + ". Skipping file.");
				return;
			}
		
			try {
				writeToCSV(records, offset == 0);
			} catch (IOException e) {
				throw new IllegalArgumentException("Failed to write to CSV file " + dataFile, e);
			}
			
			if(records.size() < MAX_ROWS_PER_QUERY) {
				//Assume no more data
				log.info(getThreadId() + ":: No more records found for " + dataFile + ".");
				return;
			}
			offset += MAX_ROWS_PER_QUERY;
		}
		
	}
	
	private void writeToCSV(List<? extends ExportDataRecord> records, boolean printHeader) throws IOException {
		log.info(getThreadId() + ":: Writing " + records.size() + " record(s) for " + dataFile + ".");
		CSVFormat format = CSVFormat.EXCEL
			.builder()
			.setTrim(false)
			.setHeader(records.get(0).getHeader())
			.setSkipHeaderRecord(!printHeader)
			.build();
		try(BufferedWriter writer = Files.newBufferedWriter(dataFile.toPath(), StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
			writer.write('\ufeff'); //Manually print the UTF-8 BOM
			try(CSVPrinter printer = new CSVPrinter(writer, format)){
				for(ExportDataRecord record : records) {
					record.printToCSV(printer, ISO_DATE_FORMAT);
				}
			}
		}
	}
	
	protected abstract Class<? extends DataMapper> getDataMapper();
	
	protected abstract String getDataset();
}
