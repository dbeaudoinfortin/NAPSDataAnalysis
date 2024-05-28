package com.dbf.naps.data.loader.integrated;

import java.io.File;
import java.util.List;
import com.dbf.naps.data.loader.NAPSDataLoader;
import com.dbf.naps.data.loader.integrated.runner.CFFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.SampleMetaDataFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.SpeciationFileLoadRunner;

public class NAPSIntegratedDataLoader extends NAPSDataLoader {

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
	protected Runnable processFile(File dataFile) {
		
		String fileName = dataFile.getName().toUpperCase();
		if(fileName.endsWith("_DICH.XLS") ) {
			return new CFFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "DICHOT");
		} else if(fileName.endsWith("_PART25.XLS")) {
			return new CFFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "PART25");
		} else if(fileName.endsWith("_PAH.XLS")) {
			return new SampleMetaDataFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "PAH");
		} else if(fileName.endsWith("_HCB.XLS") ) {
			return new SampleMetaDataFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "HCB");
		} else if(fileName.endsWith("_VOC.XLS")) {
			return new SampleMetaDataFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "VOC");
		} else if(fileName.endsWith("_PCDD.XLS") ) {
			return new SampleMetaDataFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "PCDD");
		} else if( fileName.endsWith("_PCB.XLS")) {
			return new SampleMetaDataFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "PCB");
		} else if( fileName.endsWith("_CARB.XLS")) {
			return new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "CARB");
		} else if( fileName.endsWith("_IC.XLS")) {
			return new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "IC");
		} else if( fileName.endsWith("_ICPMS.XLS")) {
			return new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "ICPMS");
		} else if( fileName.endsWith("_NA.XLS")) {
			return new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "NA");
		}  else if( fileName.endsWith("_NH4.XLS")) {
			return new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "NH4");
		}  else if( fileName.endsWith("_SPEC.XLS")) {
			return new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "SPEC");
		} else if( fileName.endsWith("_WICPMS.XLS")) {
			return new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "WICPMS");
		} else if( fileName.endsWith("_LEV.XLS")) {
			return new SpeciationFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile, "LEV");
		} 
		throw new IllegalArgumentException("Unsupported data file: " + dataFile);
	}
}
