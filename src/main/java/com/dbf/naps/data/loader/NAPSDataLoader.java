package com.dbf.naps.data.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.db.NAPSDBAction;
import com.dbf.utils.stacktrace.StackTraceCompactor;

public abstract class NAPSDataLoader extends NAPSDBAction<LoaderOptions> {

	private static final Logger log = LoggerFactory.getLogger(NAPSDataLoader.class);
		
	public NAPSDataLoader(String[] args) {
		super(args);
	}
	
	protected void run() {
		log.info("Welcome! 🙂");
		
		try
		{
			super.run();
			loadFiles();
		} catch (Throwable t) {
			log.error("Unexpected failure.\n" + StackTraceCompactor.getCompactStackTrace(t));
		}
		
		log.info("Goodbye! 🙂");
		end();
	}

	private void loadFiles() throws IOException {
		List<Future<?>> futures = new ArrayList<Future<?>>();
		final Path rawPath = getOptions().getDataPath();
		
		log.info("Examining all files at the path " + rawPath);

		if(!rawPath.toFile().isDirectory()) {
			log.error("The path to the raw data is not valid: " + rawPath);
			return;
		}
		
		recurseDir(rawPath, futures);
		log.info("All files have been examined. " + futures.size() + " task(s) have been created. Waiting for completion...");
		
		waitForTaskCompletion(futures);
	}
	
	private void recurseDir(final Path dirPath, Collection<Future<?>> futures) throws IOException {
		Files.list(dirPath).forEach(path -> {
			File dataFile = path.toFile();
			if (dataFile.isDirectory()) {
				try {
					recurseDir(path, futures);
				} catch (IOException e) {
					log.error("Failed to read directory: " + path, e);
					return;	//Don't prevent other files from processing
				}
			} else if (dataFile.isFile()) {
				try {
					Collection<Runnable> tasks = processFile(dataFile);
					if(null != tasks) futures.addAll(submitTasks(tasks));
				} catch (Exception e) {
					log.error("Failed to queue up task for file: " + dataFile, e);
					return; //Don't prevent other files from processing
				}
			}//ignore symbolic links
		});
	}
	
	public Class<LoaderOptions> getOptionsClass(){
		return LoaderOptions.class;
	}
	
	protected abstract Collection<Runnable> processFile(File dataFile);
}
