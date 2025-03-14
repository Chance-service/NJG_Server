package com.guaji.game;

import org.guaji.log.Log;
import org.guaji.os.MyException;

/**
 * gameserver入口
 */
public class GsMain {
	public static void main(String[] args) {		
		try {
			// 打印启动参数
			for (int i = 0; i < args.length; i++) {
				Log.logPrintln(args[i]);
			}
			
			// 创建应用
			GsApp app = new GsApp();
			if (app.init("conf")) {
				app.run();
			}
			// 退出
			Log.logPrintln("gameserver exit");
			System.exit(0);

		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
}
