package com.dbf.naps.data.loader.integrated;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.db.mappers.IntegratedDataMapper;
import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.NAPSDataLoader;
import com.dbf.naps.data.loader.integrated.runner.XLS_SimpleLoaderRunner;
import com.dbf.naps.data.loader.integrated.runner.IntegratedRunnerMapping;
import com.dbf.naps.data.loader.integrated.runner.SampleMetaDataLoaderRunner;
import com.dbf.naps.data.loader.integrated.runner.XLS_NewerLoaderRunner;
import com.dbf.naps.data.loader.integrated.runner.XLSX_LoaderRunner;

public class NAPSIntegratedDataLoader extends NAPSDataLoader {

	private static final List<IntegratedRunnerMapping> mappings = new ArrayList<IntegratedRunnerMapping>();
	private static final List<Pattern> excludedPatterns = new ArrayList<Pattern>();
	static {
		mappings.add(new IntegratedRunnerMapping(XLS_SimpleLoaderRunner.class, "DICHOT", "_DICH.XLS"));
		mappings.add(new IntegratedRunnerMapping(XLS_SimpleLoaderRunner.class, "PM2.5", "_PART25.XLS"));
		
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "PAH", "_PAH.XLS", "ng/m³"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "HCB", "_HCB.XLS", "ng/m³"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "VOC", "_VOC.XLS"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "VOC", "_VOCS.XLS")); //One file is mis-named :) 
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "PCDD", "_PCDD.XLSX", "pg/m³"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "PCDD", "_PCDD.XLS", "pg/m³"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "PCB", "_PCB.XLS", "pg/m³"));
		
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "CARB", "_CARB.XLS"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "IC", "_IC.XLS"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "ICPMS", "_ICPMS.XLS", "ng/m³"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "NA", "_NA.XLS"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "NH4", "_NH4.XLS"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "SPEC", "_SPEC.XLS"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "WICPMS", "_WICPMS.XLS", "ng/m³"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataLoaderRunner.class, "LEV", "_LEV.XLS", "ng/m³"));
		
		//These are post-2010 files that still use the XLS format. They have a slightly different structure from the rest.
		mappings.add(new IntegratedRunnerMapping(XLS_NewerLoaderRunner.class, "VOC", Pattern.compile("S[0-9]+(_24HR)?_VOC_[0-9]{4}(_EN)?\\.XLS"))); //Match S54401_VOC_2016_EN.XLS, S62601_24hr_VOC_2014.XLS
		mappings.add(new IntegratedRunnerMapping(XLS_NewerLoaderRunner.class, "VOC_4HR", Pattern.compile("S[0-9]+_4HR_VOC_[0-9]{4}(_EN)?\\.XLS"))); //S62601_4hr_VOC_2014.XLS
		mappings.add(new IntegratedRunnerMapping(XLS_NewerLoaderRunner.class, "CARB", Pattern.compile("S[0-9]+_CARBONYLS_[0-9]{4}(_EN)?\\.XLS"))); //Match S54401_CARBONYLS_2016_EN.XLS
		
		mappings.add(new IntegratedRunnerMapping(XLSX_LoaderRunner.class, "PAH", Pattern.compile("S[0-9]+_PAH_[0-9]{4}(_EN)?\\.XLSX"), "ng/m³")); //Match S90121_PAH_2010.XLSX
		mappings.add(new IntegratedRunnerMapping(XLSX_LoaderRunner.class, "PM2.5", Pattern.compile("S[0-9]+_PM25_[0-9]{4}(_EN)?\\.XLSX"))); //Match S40103_PM25_2010.XLSX
		mappings.add(new IntegratedRunnerMapping(XLSX_LoaderRunner.class, "PM2.5-10", Pattern.compile("S[0-9]+_PM25\\-10_[0-9]{4}(_EN)?\\.XLSX"))); //Match S30113_PM25-10_2010.XLSX

		mappings.add(new IntegratedRunnerMapping(XLSX_LoaderRunner.class, "CARB", Pattern.compile("S[0-9]+_CARBONYLS_[0-9]{4}(_EN)?\\.XLSX"))); //Match S070119_CARBONYLS_2018_EN.XLSX
		mappings.add(new IntegratedRunnerMapping(XLSX_LoaderRunner.class, "VOC", Pattern.compile("S[0-9]+_VOC_[0-9]{4}(_EN)?\\.XLSX"))); //Match S070119_VOC_2018_EN.XLSX

		//These are summary files that don't contain full data
		excludedPatterns.add(Pattern.compile("S[0-9]+_PM25_[0-9]{4}(_EN)?\\.XLS"));
		excludedPatterns.add(Pattern.compile("S[0-9]+_PM25_[0-9]{4}(_EN)?\\.XLS"));
	}
	
	public NAPSIntegratedDataLoader(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSIntegratedDataLoader dataLoader = new NAPSIntegratedDataLoader(args);
		dataLoader.run();
	}

	@Override
	protected List<Class<?>> getDBMappers() {
		return List.of(IntegratedDataMapper.class);
	}

	@Override
	protected Collection<Runnable> processFile(File dataFile) {
		
		String fileName = dataFile.getName().toUpperCase();
		
		//Ignore change logs
		if(fileName.startsWith("CHANGE") )
			return Collections.emptyList();
		
		//Ignore the CSV versions of the files, we will process the Excel sheets
		if(fileName.endsWith(".CSV") )
			return Collections.emptyList();
				
		//Ignore the French copies of the data
		if(fileName.endsWith("_FR.XLS") || fileName.endsWith("_FR.XLSX"))
			return Collections.emptyList();

		//These are files that we explicitly know we don't want to read
		for(Pattern excludedPattern : excludedPatterns) {
			if(excludedPattern.matcher(fileName).matches()) 
				return Collections.emptyList();
		}
		
		try {
			for(IntegratedRunnerMapping mapping : mappings) {
				if((null != mapping.getFileNameMatch() && fileName.endsWith(mapping.getFileNameMatch()))
					|| (null != mapping.getFileNamePattern() && mapping.getFileNamePattern().matcher(fileName).matches())) {
					return Collections.singletonList((Runnable) mapping.getRunnerClass().getConstructor(int.class, LoaderOptions.class, SqlSessionFactory.class, File.class, String.class, String.class)
							.newInstance(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, mapping.getFileType(), mapping.getUnits()));
				}
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Failed to created a runner for the data file: " + dataFile, e);
		}
		throw new IllegalArgumentException("Unsupported data file: " + dataFile);
	}
}
