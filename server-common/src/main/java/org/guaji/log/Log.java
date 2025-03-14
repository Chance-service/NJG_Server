package org.guaji.log;

import org.guaji.app.App;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志类
 */
public class Log {
	/**
	 * 控制台打印
	 */
	static boolean consolePrint = false;
	/**
	 * 配置日志对象
	 */
	static Logger logger = LoggerFactory.getLogger("GuaJi");
	/**
	 * 调试日志对象
	 */
	static Logger debugLogger = LoggerFactory.getLogger("Debug");
	/**
	 * 配置日志对象
	 */
	static Logger exceptionLogger = LoggerFactory.getLogger("Exception");
	
	/**
	 * GVG日志
	 */
	static Logger gvgLogger = LoggerFactory.getLogger("Gvg");
	
	/**
	 * 跨服PVP日志
	 */
	static Logger crossLogger = LoggerFactory.getLogger("CrossBattle");
	
	/**
	 * GVE日志
	 */
	static Logger gveLogger = LoggerFactory.getLogger("Gve");
	/**
	 * Script日誌
	 */
	static Logger scriptlogger = LoggerFactory.getLogger("Script");
	
	/**
	 * 开启控制台打印
	 * 
	 * @param enable
	 */
	public static void enableConsole(boolean enable) {
		consolePrint = enable;
	}

	/**
	 * 调试模式输出
	 * @param msg
	 */
	public synchronized static void debugPrintln(String msg) {
		if (App.getInstance().isDebug()) {
			
			msg=String.format("time:%s %s", GuaJiTime.getTimeString(),msg);
			debugLogger.info(msg);
			// 控制台输出
			if (consolePrint) {
				System.out.println(msg);
			}
		}
	}
	
	/**
	 * 调试模式输出
	 * @param format arguments
	 */
	public synchronized static void debugInfo(String format, Object... arguments) {
		if (App.getInstance().isDebug()) {
			
			//format=String.format("time:%s %s", GuaJiTime.getTimeString(),format);
			debugLogger.info(format,arguments);
		}
	}
	/**
	 * script日誌輸出
	 */
	
	public synchronized static void scriptInfo(String format, Object... arguments) {
		if (App.getInstance().isDebug()) {
			//format=String.format("time:%s %s", GuaJiTime.getTimeString(),format);
			scriptlogger.info(format,arguments);
		}
	}
	
	/**
	 * 日志打印
	 * 
	 * @param msg
	 */
	public synchronized static void logPrintln(String msg) {
		msg=String.format("time:%s %s", GuaJiTime.getTimeString(),msg);
		logger.info(msg);
		// 控制台输出
		if (consolePrint) {
			System.out.println(msg);
		}
	}

	/**
	 * 错误打印
	 * 
	 * @param msg
	 */
	public synchronized static void errPrintln(String msg) {
		msg=String.format("time:%s %s", GuaJiTime.getTimeString(),msg);
		logger.error(msg);
		// 打印错误
		System.err.println(msg);
	}

	/**
	 * 异常打印
	 * 
	 * @param excep
	 */
	public synchronized static void exceptionPrint(Exception e) {
		if (e != null) {
			// 异常信息按照错误打印
			String stackMsg = MyException.formatStackMsg(e);
			stackMsg=String.format("time:%s %s", GuaJiTime.getTimeString(),stackMsg);
			exceptionLogger.error(stackMsg);
		}
	}
	
	/**
	 * GVG日志记录
	 * 
	 * @param message
	 */
	public static void gvgLog(String message) {
		
		message=String.format("time:%s %s", GuaJiTime.getTimeString(),message);
		gvgLogger.info(message);
		// 控制台输出
		if (consolePrint) {
			System.out.println(message);
		}
	}
	
	/**
	 * 跨服战日志记录
	 * 
	 * @param message
	 */
	public static void crossLog(String message) {
		message=String.format("time:%s %s", GuaJiTime.getTimeString(),message);
		crossLogger.info(message);
		// 控制台输出
		if (consolePrint) {
			System.out.println(message);
		}
	}
	
	/**
	 * GVE日志记录
	 * 
	 * @param message
	 */
	public static void gveLog(String message) {
		message=String.format("time:%s %s", GuaJiTime.getTimeString(),message);
		gveLogger.info(message);
		// 控制台输出
		if (consolePrint) {
			System.out.println(message);
		}
	}
}
