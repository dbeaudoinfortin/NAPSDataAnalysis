package com.dbf.naps.data.loader.integrated;

import java.io.File;
import java.util.List;
import com.dbf.naps.data.loader.NAPSDataLoader;
import com.dbf.naps.data.loader.integrated.runner.DICHFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.HCBFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.PAHFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.PCDDFileLoadRunner;
import com.dbf.naps.data.loader.integrated.runner.VOCFileLoadRunner;

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
		if(fileName.endsWith("_DICH.XLS")) {
			return new DICHFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile);
		} else if(fileName.endsWith("_PAH.XLS")) {
			return new PAHFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile);
		} else if(fileName.endsWith("_PCDD.XLS")) {
			return new PCDDFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile);
		} else if(fileName.endsWith("_VOC.XLS")) {
			return new VOCFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile);
		} else if(fileName.endsWith("_HCB.XLS")) {
			return new HCBFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile);
		}
		throw new IllegalArgumentException("Unsupported data file: " + dataFile);
	}
}
