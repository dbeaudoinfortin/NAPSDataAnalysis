package com.dbf.naps.data.loader.integrated;

import java.io.File;
import java.util.List;
import com.dbf.naps.data.loader.NAPSDataLoader;
import com.dbf.naps.data.loader.integrated.runner.CFFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.SampleMetaDataFileLoadRunner;

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
		if(fileName.endsWith("_DICH.XLS") || fileName.endsWith("_PART25.XLS")) {
			return new CFFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile);
		} else if(fileName.endsWith("_PAH.XLS") || fileName.endsWith("_HCB.XLS") || fileName.endsWith("_VOC.XLS")
			|| fileName.endsWith("_PCDD.XLS") || fileName.endsWith("_PCB.XLS")) {
			return new SampleMetaDataFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile);
		}
		throw new IllegalArgumentException("Unsupported data file: " + dataFile);
	}
}
