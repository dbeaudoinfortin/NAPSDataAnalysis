package com.dbf.naps.data.loader.integrated;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.dbf.naps.data.loader.NAPSDataLoader;
import com.dbf.naps.data.loader.integrated.runner.CFFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.SampleMetaDataFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.SpeciationFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.XLSXFileLoadRunner;

public class NAPSIntegratedDataLoader extends NAPSDataLoader {

	private static final Pattern XLSX_PAH_PATTERN = Pattern.compile("S[0-9]+_PAH_[0-9]{4}\\.XLSX"); //Match S90121_PAH_2010.XLSX
	private static final Pattern XLSX_PM25_PATTERN = Pattern.compile("S[0-9]+_PM25_[0-9]{4}\\.XLSX"); //Match S40103_PM25_2010.XLSX
	
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
		if(fileName.endsWith("_DICH.XLS") ) {
			return Collections.singletonList(new CFFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "DICHOT"));
		} else if(fileName.endsWith("_PART25.XLS")) {
			return Collections.singletonList(new CFFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "PM2.5"));
		} else if(fileName.endsWith("_PAH.XLS")) {
			return Collections.singletonList(new SampleMetaDataFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "PAH"));
		} else if(fileName.endsWith("_HCB.XLS") ) {
			return Collections.singletonList(new SampleMetaDataFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "HCB"));
		} else if(fileName.endsWith("_VOC.XLS")) {
			return Collections.singletonList(new SampleMetaDataFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "VOC"));
		} else if(fileName.endsWith("_PCDD.XLS") ) {
			return Collections.singletonList(new SampleMetaDataFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "PCDD"));
		} else if( fileName.endsWith("_PCB.XLS")) {
			return Collections.singletonList(new SampleMetaDataFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "PCB"));
		} else if( fileName.endsWith("_CARB.XLS")) {
			return Collections.singletonList(new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "CARB"));
		} else if( fileName.endsWith("_IC.XLS")) {
			return Collections.singletonList(new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "IC"));
		} else if( fileName.endsWith("_ICPMS.XLS")) {
			return Collections.singletonList(new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "ICPMS"));
		} else if( fileName.endsWith("_NA.XLS")) {
			return Collections.singletonList(new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "NA"));
		}  else if( fileName.endsWith("_NH4.XLS")) {
			return Collections.singletonList(new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "NH4"));
		}  else if( fileName.endsWith("_SPEC.XLS")) {
			return Collections.singletonList(new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "SPEC"));
		} else if( fileName.endsWith("_WICPMS.XLS")) {
			return Collections.singletonList(new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "WICPMS"));
		} else if( fileName.endsWith("_LEV.XLS")) {
			return Collections.singletonList(new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "LEV"));
		} else if(XLSX_PAH_PATTERN.matcher(fileName).matches()) {
			return Collections.singletonList(new XLSXFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "PAH"));
		} else if(XLSX_PM25_PATTERN.matcher(fileName).matches()) {
			return Arrays.asList(
					new XLSXFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "PM2.5")
					);
		}
		
		throw new IllegalArgumentException("Unsupported data file: " + dataFile);
	}
}
