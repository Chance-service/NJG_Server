package com.test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.guaji.app.App;
import org.guaji.app.AppCfg;
import org.guaji.config.ConfigStorage;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;

import com.test.thread.RobotThread;

public class ServerTest {
	



	public static final Random RANDOM = new Random();
	
	/**
	 *
	 */
	public static final int BATCH_NUMS = 10;
	/**
	 *
	 */
	public static String ip="35.77.200.120";
	/**
	 *
	 */
	public static final int PORT=9596;
	/***
	 *
	 */
	private static int nums = 400;
	/**
	 * cpu
	 */
	private static int cpuNums = Runtime.getRuntime().availableProcessors();
	
	
	private static ExecutorService executorService;
	public static void main(String[] args){

	
		new App(GuaJiXID.valueOf(0)) {
		};
	
		if(args.length>1){
			nums = Integer.parseInt(args[1]);
		}
		executorService = Executors.newFixedThreadPool(cpuNums*2);
		int threadNums = cpuNums*2;
		int pNumPerTthread = nums/threadNums;
		for(int i = 0;i<threadNums;i++){
			RobotThread thread = new RobotThread(pNumPerTthread);
			executorService.submit(thread);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
}
