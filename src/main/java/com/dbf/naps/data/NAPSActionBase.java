package com.dbf.naps.data;

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

public abstract class NAPSActionBase {

	private static final Logger log = LoggerFactory.getLogger(NAPSActionBase.class);
	
	private ThreadPoolExecutor  THREAD_POOL = null; 
	private final AtomicInteger THREAD_ID_COUNTER = new AtomicInteger(0);
		
	protected void initBase(BaseOptions config) {
		initThreadPool(config.getThreadCount());
	}
	
	private void initThreadPool(int threadCount) { 
		log.info("Initializing thread pool with a size of " + threadCount);
		THREAD_POOL = new ThreadPoolExecutor(threadCount, threadCount, 100l, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
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
	
	protected int getThreadID() {
		return THREAD_ID_COUNTER.getAndIncrement();
	}
	
	protected Future<?> submitTask(Runnable task) {
		return THREAD_POOL.submit(task);
	}
	
	protected List<Future<?>> submitTasks(List<Runnable> tasks) {
		return tasks.stream().map(this::submitTask).collect(Collectors.toList());
	}
	
	protected abstract void run(String[] args);
}
