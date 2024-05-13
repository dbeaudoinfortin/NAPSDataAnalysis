package com.dbf.naps.data;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NAPSActionBase {

	private static final Logger log = LoggerFactory.getLogger(NAPSActionBase.class);
	
	protected static ThreadPoolExecutor  THREAD_POOL = null; 
	protected static final AtomicInteger THREAD_ID_COUNTER = new AtomicInteger(0);
		
	protected static void initThreadPool(int threadCount) { 
		log.info("Initializing thread pool with a size of " + threadCount);
		THREAD_POOL = new ThreadPoolExecutor(threadCount, threadCount, 100l, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
	}
	
	protected static void waitForTaskCompletion(List<Future<?>> futures) {
		futures.forEach(f->{
			try {
				f.get();
			} catch (ExecutionException | InterruptedException e) {
				throw new RuntimeException("Failed to wait for completion of tasks.", e); 
			}
		});
	}

}
