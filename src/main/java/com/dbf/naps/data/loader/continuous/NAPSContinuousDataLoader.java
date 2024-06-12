package com.dbf.naps.data.loader.continuous;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.dbf.naps.data.loader.NAPSDataLoader;

public class NAPSContinuousDataLoader extends NAPSDataLoader {

	public NAPSContinuousDataLoader(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSContinuousDataLoader dataLoader = new NAPSContinuousDataLoader(args);
		dataLoader.run();
	}

	@Override
	protected List<Class<?>> getDBMappers() {
		return List.of(ContinuousDataMapper.class);
	}

	@Override
	protected Collection<Runnable> processFile(File dataFile) {
		if(!dataFile.getName().toLowerCase().endsWith(".csv")) return null;
		return Collections.singletonList(new ContinuousLoaderRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile));
	}
}
