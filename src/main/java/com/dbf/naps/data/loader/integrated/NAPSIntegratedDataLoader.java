package com.dbf.naps.data.loader.integrated;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dbf.naps.data.loader.LoaderOptions;
import com.dbf.naps.data.loader.NAPSDataLoader;
import com.dbf.naps.data.loader.integrated.runner.CFFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.CarbonylsFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.IntegratedRunnerMapping;
import com.dbf.naps.data.loader.integrated.runner.SampleMetaDataFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.SpeciationFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.VOCFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.XLSXFileLoadRunner;

public class NAPSIntegratedDataLoader extends NAPSDataLoader {

	private static final List<IntegratedRunnerMapping> mappings = new ArrayList<IntegratedRunnerMapping>();
	static {
		mappings.add(new IntegratedRunnerMapping(CFFileLoadRunner.class, "DICHOT", "_DICH.XLS"));
		mappings.add(new IntegratedRunnerMapping(CFFileLoadRunner.class, "PM2.5", "_PART25.XLS"));
		
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataFileLoadRunner.class, "PAH", "_PAH.XLS"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataFileLoadRunner.class, "HCB", "_HCB.XLS"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataFileLoadRunner.class, "VOC", "_VOC.XLS"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataFileLoadRunner.class, "PCDD", "_PCDD.XLS"));
		mappings.add(new IntegratedRunnerMapping(SampleMetaDataFileLoadRunner.class, "PCB", "_PCB.XLS"));
		
		mappings.add(new IntegratedRunnerMapping(SpeciationFileLoadRunner.class, "CARB", "_CARB.XLS"));
		mappings.add(new IntegratedRunnerMapping(SpeciationFileLoadRunner.class, "IC", "_IC.XLS"));
		mappings.add(new IntegratedRunnerMapping(SpeciationFileLoadRunner.class, "ICPMS", "_ICPMS.XLS"));
		mappings.add(new IntegratedRunnerMapping(SpeciationFileLoadRunner.class, "NA", "_NA.XLS"));
		mappings.add(new IntegratedRunnerMapping(SpeciationFileLoadRunner.class, "NH4", "_NH4.XLS"));
		mappings.add(new IntegratedRunnerMapping(SpeciationFileLoadRunner.class, "SPEC", "_SPEC.XLS"));
		mappings.add(new IntegratedRunnerMapping(SpeciationFileLoadRunner.class, "WICPMS", "_WICPMS.XLS"));
		mappings.add(new IntegratedRunnerMapping(SpeciationFileLoadRunner.class, "LEV", "_LEV.XLS"));
		
		mappings.add(new IntegratedRunnerMapping(XLSXFileLoadRunner.class, "PAH", Pattern.compile("S[0-9]+_PAH_[0-9]{4}(_EN)?\\.XLSX"))); //Match S90121_PAH_2010.XLSX
		mappings.add(new IntegratedRunnerMapping(XLSXFileLoadRunner.class, "PM2.5", Pattern.compile("S[0-9]+_PM25_[0-9]{4}(_EN)?\\.XLSX"))); //Match S40103_PM25_2010.XLSX
		mappings.add(new IntegratedRunnerMapping(XLSXFileLoadRunner.class, "PM2.5-10", Pattern.compile("S[0-9]+_PM25_[0-9]{4}\\-10(_EN)?\\.XLSX"))); //Match S30113_PM25-10_2010.XLSX
		mappings.add(new IntegratedRunnerMapping(XLSXFileLoadRunner.class, "CARB", Pattern.compile("S[0-9]+_CARBONYLS_[0-9]{4}(_EN)?\\.XLSX"))); //Match S070119_CARBONYLS_2018_EN.XLSX
		mappings.add(new IntegratedRunnerMapping(XLSXFileLoadRunner.class, "VOC", Pattern.compile("S[0-9]+_VOC_[0-9]{4}(_EN)?\\.XLSX"))); //Match S070119_VOC_2018_EN.XLSX
		
		mappings.add(new IntegratedRunnerMapping(VOCFileLoadRunner.class, "VOC", Pattern.compile("S[0-9]+_VOC_[0-9]{4}(_EN)?\\.XLS"))); //Match S54401_VOC_2016_EN.XLS
		mappings.add(new IntegratedRunnerMapping(CarbonylsFileLoadRunner.class, "CARB", Pattern.compile("S[0-9]+_CARBONYLS_[0-9]{4}(_EN)?\\.XLS"))); //Match S54401_CARBONYLS_2016_EN.XLS
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
		
		//Ignore the CSV versions of the files, we will process the Excel sheets
		if(fileName.endsWith(".CSV") )
			return Collections.emptyList();
				
		//Ignore the French copies of the data
		if(fileName.endsWith("_FR.XLS") || fileName.endsWith("_FR.XLSX"))
			return Collections.emptyList();

		try {
			for(IntegratedRunnerMapping mapping : mappings) {
				if((null != mapping.getFileNameMatch() && fileName.endsWith(mapping.getFileNameMatch()))
					|| (null != mapping.getFileNamePattern() && mapping.getFileNamePattern().matcher(fileName).matches())) {
					return Collections.singletonList((Runnable) mapping.getRunnerClass().getConstructor(int.class, LoaderOptions.class, SqlSessionFactory.class, File.class, String.class)
							.newInstance(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, mapping.getFileType()));
				}
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Failed to created a runner for the data file: " + dataFile, e);
		}
		throw new IllegalArgumentException("Unsupported data file: " + dataFile);
	}
}
