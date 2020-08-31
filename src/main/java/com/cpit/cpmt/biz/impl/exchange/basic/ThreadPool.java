package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
	private static ExecutorService threadPool = null;
	public synchronized static ExecutorService getThreadPool(){
		if(null == threadPool) {
			threadPool = Executors.newFixedThreadPool(50);
		}
		return threadPool;
	}
}
