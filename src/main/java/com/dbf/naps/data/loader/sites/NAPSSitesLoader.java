package com.dbf.naps.data.loader.sites;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.dbf.naps.data.loader.NAPSDataLoader;

public class NAPSSitesLoader extends NAPSDataLoader {

	public NAPSSitesLoader(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		NAPSSitesLoader dataLoader = new NAPSSitesLoader(args);
		dataLoader.run();
	}

	@Override
	protected List<Class<?>> getDBMappers() {
		return Collections.emptyList();
	}

	@Override
	protected Collection<Runnable> processFile(File dataFile) {
		if(!dataFile.getName().toLowerCase().equals("sites.csv")) return null;
		return Collections.singletonList(new SitesFileLoadRunner(getThreadID(), getOptions(), getSqlSessionFactory(), dataFile));
	}
}
