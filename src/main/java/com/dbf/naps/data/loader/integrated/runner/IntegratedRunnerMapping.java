package com.dbf.naps.data.loader.integrated.runner;

import java.util.regex.Pattern;

public class IntegratedRunnerMapping {

	private Class<? extends IntegratedLoaderRunner> runnerClass;
	private String fileType;
	private String units = "ug/m³";
	private String fileNameMatch;
	private Pattern fileNamePattern;
	
	public IntegratedRunnerMapping(Class<? extends IntegratedLoaderRunner> runnerClass, String fileType, String fileNameMatch) {
		this.runnerClass = runnerClass;
		this.fileType = fileType;
		this.fileNameMatch = fileNameMatch.toUpperCase();
	}
	
	public IntegratedRunnerMapping(Class<? extends IntegratedLoaderRunner> runnerClass, String fileType, String fileNameMatch, String units) {
		this(runnerClass, fileType, fileNameMatch);
		this.setUnits(units);
	}
	
	public IntegratedRunnerMapping(Class<? extends IntegratedLoaderRunner> runnerClass, String fileType, Pattern fileNamePattern) {
		this.runnerClass = runnerClass;
		this.fileType = fileType;
		this.fileNamePattern = fileNamePattern;
	}
	
	public IntegratedRunnerMapping(Class<? extends IntegratedLoaderRunner> runnerClass, String fileType, Pattern fileNamePattern, String units) {
		this(runnerClass, fileType, fileNamePattern);
		this.setUnits(units);
	}
	
	public Class<? extends IntegratedLoaderRunner> getRunnerClass() {
		return runnerClass;
	}
	
	public void setRunnerClass(Class<IntegratedLoaderRunner> runnerClass) {
		this.runnerClass = runnerClass;
	}
	
	public String getFileType() {
		return fileType;
	}
	
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public String getFileNameMatch() {
		return fileNameMatch;
	}
	
	public void setFileNameMatch(String fileNameMatch) {
		this.fileNameMatch = fileNameMatch;
	}
	
	public Pattern getFileNamePattern() {
		return fileNamePattern;
	}
	
	public void setFileNamePattern(Pattern fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}
}
