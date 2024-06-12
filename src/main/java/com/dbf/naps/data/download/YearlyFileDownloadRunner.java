package com.dbf.naps.data.download;

import java.nio.file.Path;

import com.dbf.naps.data.download.options.YearlyDownloaderOptions;

public abstract class YearlyFileDownloadRunner extends FileDownloadRunner<YearlyDownloaderOptions> {
	
	private final int year;
	
	public YearlyFileDownloadRunner(int year, int threadId, YearlyDownloaderOptions config, Path downloadPath) {
		super(threadId, config, downloadPath);
		this.year = year;
	}
	
	public int getYear() {
		return year;
	}
}
