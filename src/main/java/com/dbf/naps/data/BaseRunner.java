package com.dbf.naps.data;

public abstract class BaseRunner<O extends BaseOptions> implements Runnable {
	
	private final int threadId;
	private final O config;
	
	public BaseRunner(int threadId, O config) {
		this.threadId = threadId;
		this.config = config;
	}
	
	public int getThreadId() {
		return threadId;
	}

	public O getConfig() {
		return config;
	}
}
