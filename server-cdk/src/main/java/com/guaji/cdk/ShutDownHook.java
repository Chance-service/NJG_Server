package com.guaji.cdk;

import org.guaji.log.Log;

/**
 * 关闭退出钩子
 * 
 */
public class ShutDownHook {
	/**
	 * 安装钩子
	 */
	public static void install() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Log.logPrintln("CdkServer Kill Shutdown.");
				CdkServices.getInstance().stop();
			}
		});
	}
}
