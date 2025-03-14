package com.server.paynotice.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReqeustExecuter {
	private static ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
	
	public static void submitTask(Runnable run)
	{
		executors.submit(run);
	}
}
