package com.dbf.naps.data;

import java.io.File;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.DBRunner;
import com.dbf.naps.data.exporter.ExporterOptions;

public abstract class FileRunner<O extends ExporterOptions> extends DBRunner<O> {
	
	private static final Logger log = LoggerFactory.getLogger(FileRunner.class);
	
	private final File dataFile;
	private final Integer specificYear;
	private final String specificPollutant;
	private final Integer specificSite;
		
	public FileRunner(int threadId, O config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory);
		this.dataFile = dataFile;
		this.specificYear = specificYear;
		this.specificPollutant = specificPollutant;
		this.specificSite = specificSite;
	}
	
	public void checkFile() {
		checkFile(dataFile);
	}
	
	public void checkFile(File dataFile) {
		if(dataFile.isDirectory()) {
			throw new IllegalArgumentException("File path is a directory: " + dataFile);
		} else if (dataFile.isFile()) {
			if(getConfig().isOverwriteFiles()) {
				log.warn(getThreadId() + ":: Deleting existing file " + dataFile + ".");
				dataFile.delete();
			} else {
				throw new IllegalArgumentException("Cannot write to path \"" + dataFile +"\". The file already exists and the overwrite flag is set to false.");
			}
		}
	}

	public File getDataFile() {
		return dataFile;
	}

	public Integer getSpecificYear() {
		return specificYear;
	}

	public String getSpecificPollutant() {
		return specificPollutant;
	}

	public Integer getSpecificSite() {
		return specificSite;
	}
}