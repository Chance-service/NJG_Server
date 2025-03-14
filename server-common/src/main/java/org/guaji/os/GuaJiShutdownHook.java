package org.guaji.os;

import org.guaji.app.App;
import org.guaji.log.Log;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.util.GuaJiCallback;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * 进程关闭回调钩子
 */
public class GuaJiShutdownHook implements Runnable {
	/**
	 * 是否已shutdown
	 */
	volatile boolean isShutdown = false;
	/**
	 * 回调
	 */
	GuaJiCallback callback;
	/**
	 * hook实例
	 */
	static GuaJiShutdownHook instance;

	/**
	 * 是否已shutdown
	 * 
	 * @return
	 */
	public boolean isShutdown() {
		return isShutdown;
	}

	/**
	 * 处理shutdown事项, 主要用来数据落地存储
	 * 
	 * @param notify
	 * @return
	 */
	public boolean processShutdown(boolean notify) {
		if (isShutdown) {
			return false;
		}

		Log.logPrintln("start shutting down");

		// 回调唤起
		try {
			if (callback != null) {
				callback.invoke(notify);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		
		// 通知app关闭
		App.getInstance().onShutdown();

		Log.logPrintln("shutting down complete");

		isShutdown = true;
		return true;
	}

	/**
	 * 设置回调
	 * 
	 * @param callback
	 */
	public void setCallback(GuaJiCallback callback) {
		this.callback = callback;
	}

	/**
	 * 获取实例
	 * 
	 * @return
	 */
	public static GuaJiShutdownHook getInstance() {
		if (instance == null) {
			instance = new GuaJiShutdownHook();
		}
		return instance;
	}

	/**
	 * 装载
	 */
	public void install() {
		// kill命令回调函数注册
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				processShutdown(true);
			}
		});

		// kill -12信号处理注册回调
		try {
			Signal.handle(new Signal("USR2"), new kill_SignalHandler("USR2"));
		} catch (Exception e) {
		}

		// kill -17信号处理注册回调
		try {
			Signal.handle(new Signal("CHLD"), new script_SignalHandler("CHLD"));
		} catch (Exception e) {
		}
		
		if (App.getInstance().isDebug() && OSOperator.isWindowsOS()) {
			Thread thread = new Thread(this);
			thread.setName("WinConsole");
			thread.start();
		}
	}

	@Override
	public void run() {
		try {
			System.in.read();
		} catch (Exception e) {
			MyException.catchException(e);
		}
		processShutdown(true);
	}
	
	private class kill_SignalHandler implements SignalHandler {
		private String signalName;
		
		public kill_SignalHandler(String name) {
			this.signalName = name;
		}
		
		public void handle(Signal signal) {
			Log.logPrintln("signal handler: " + this.getClass().getSimpleName() + ", name: " + signalName);
			processShutdown(false);
			System.exit(0);
		}
	}
	
	private class script_SignalHandler implements SignalHandler {
		private String signalName;
		
		public script_SignalHandler(String name) {
			this.signalName = name;
		}
		
		public void handle(Signal signal) {
			Log.logPrintln("signal handler: " + this.getClass().getSimpleName() + ", name: " + signalName);
			GuaJiScriptManager.getInstance().restart();
		}
	}
}
