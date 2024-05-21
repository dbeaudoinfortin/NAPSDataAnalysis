package com.dbf.naps.data.download.continuous;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.dbf.naps.data.download.NAPSDataDownloader;
import com.dbf.naps.data.globals.Compound;
import com.dbf.naps.data.globals.Constants;

public class NAPSContinuousDataDownloader extends NAPSDataDownloader {

	public NAPSContinuousDataDownloader(String[] args) {
		super(args);
	}

	public static void main(String[] args) {
		NAPSContinuousDataDownloader dataDownloader = new NAPSContinuousDataDownloader(args);
		dataDownloader.run();
	}
	
	@Override
	protected Path getDownloadPath() {
		return getOptions().getDownloadPath().resolve(Constants.FILE_PATH_CONTINUOUS);
	}

	@Override
	protected List<Runnable> processYear(int year, Path downloadPath) {
		List<Runnable> tasks = new ArrayList<Runnable>(10);
		for (Compound compound : Compound.values()) {
			tasks.add(new ContinuousFileDownloadRunner(year, compound, getThreadID(), getOptions(), downloadPath));
		}
		return tasks;
	}
}
