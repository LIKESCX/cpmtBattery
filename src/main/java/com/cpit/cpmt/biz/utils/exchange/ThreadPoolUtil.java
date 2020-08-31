package com.cpit.cpmt.biz.utils.exchange;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtil {
private static ExecutorService excthreadPool = null;
	
	public synchronized static ExecutorService getThreadPool(){
		if(excthreadPool==null){
			excthreadPool = Executors.newFixedThreadPool(50);
		}
		return 	excthreadPool;
	}
}
