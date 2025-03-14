package com.guaji.cs;

import java.util.Properties;

import org.guaji.app.App;
import org.guaji.log.Log;
import org.guaji.xid.GuaJiXID;

public class CrossServerMain {

	/**
	 * 当前用户路径
	 */
	private static String userDir;

	/**
	 * 获取用户路径
	 * 
	 * @return
	 */
	public static String getUserDir() {
		return userDir;
	}

	/**
	 * 主函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new App(GuaJiXID.valueOf(0)) {
			};
			// 打印启动参数
			for (int i = 0; i < args.length; i++) {
				System.out.println(args[i]);
			}
			// 关闭回调
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					Log.logPrintln("CrossServer Kill Shutdown.");
					Log.logPrintln("shutting down complete");
					CrossServer.getInstance().breakLoop();
				}
			});
			// 打印系统信息
			Properties props = System.getProperties();
			Log.logPrintln("Os: " + props.getProperty("os.name") + ", Arch: " + props.getProperty("os.arch") + ", Version: " + props.getProperty("os.version"));
			// 用户路径
			userDir = System.getProperty("user.dir");
			
			// 创建并初始化服务
			if (CrossServer.getInstance().init(userDir + "/config/cs.cfg")) {
				// 启动服务器
				CrossServer.getInstance().run();
			} else {
				Log.errPrintln("CrossServer Init Failed.");
			}
			// 退出
			Log.logPrintln("CrossServer Exit.");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
