package com.dbf.naps.data.loader.integrated;

import java.io.File;
import java.util.List;
import com.dbf.naps.data.loader.NAPSDataLoader;

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
		return null;
	}




}
