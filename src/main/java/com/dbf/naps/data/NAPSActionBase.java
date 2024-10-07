package com.dbf.naps.data;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NAPSActionBase<O extends BaseOptions> {

	private static final Logger log = LoggerFactory.getLogger(NAPSActionBase.class);
	
	private final ThreadPoolExecutor threadPool; 
	private final AtomicInteger THREAD_ID_COUNTER = new AtomicInteger(0);
	
	protected final O options;
	
	public NAPSActionBase(String[] args) {
		options = loadOptions(args);
		
		log.info("Initializing thread pool with a size of " + options.getThreadCount());
		threadPool = new ThreadPoolExecutor(options.getThreadCount(), options.getThreadCount(), 100l, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
	}
	
	private O loadOptions(String[] args) {
		try {
			//This sure isn't elegant
			return getOptionsClass().getConstructor(String[].class).newInstance((Object)args);
		} catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			log.error("Error reading command line options: ", e);
			log.info("Command line usage:\n" + O.printOptions());
			System.exit(0);
			return null;
		}
	}

	protected void waitForTaskCompletion(List<Future<?>> futures) {
		futures.forEach(f->{
			try {
				f.get();
			} catch (ExecutionException | InterruptedException e) {
				throw new RuntimeException("Failed to wait for completion of tasks.", e); 
			}
		});
	}
	
	public O getOptions() {
		return options;
	}
	
	public abstract Class<O> getOptionsClass();
	
	protected int getThreadID() {
		return THREAD_ID_COUNTER.getAndIncrement();
	}
	
	protected Future<?> submitTask(Runnable task) {
		return threadPool.submit(task);
	}
	
	protected Collection<Future<?>> submitTasks(Collection<Runnable> tasks) {
		return tasks.stream().map(this::submitTask).collect(Collectors.toList());
	}
	
	protected void end() {
		threadPool.shutdownNow();
	}
	
	protected abstract void run();
}
