package org.guaji.os;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.guaji.app.App;
import org.guaji.log.Log;

/**
 * 系统异常封装
 * 
 */
public class MyException extends Exception {
	/**
	 * 序列ID
	 */
	private static final long serialVersionUID = -521355582712781650L;

	/**
	 * 默认构造
	 */
	public MyException() {
		super(MyException.class.getName());
	}

	/**
	 * 构造函数
	 * 
	 * @param e
	 */
	public MyException(Throwable e) {
		super(MyException.class.getName(), e);
	}

	/**
	 * 构造函数
	 * 
	 * @param msg
	 * @param e
	 */
	public MyException(String msg, Throwable e) {
		super(msg, e);
	}

	/**
	 * 构造函数
	 * 
	 * @param msg
	 */
	public MyException(String msg) {
		super(MyException.class.getName() + ":" + msg);
	}
	
	/**
	 * 异常捕获
	 * @param e
	 */
	public synchronized static void catchException(Exception e) {
		if (e != null) {
			Log.exceptionPrint(e);
			
			if (App.getInstance() != null) {
				App.getInstance().reportException(e);
			}
		}
	}
	
	/**
	 * 格式化异常堆栈结构
	 * @param e
	 * @return
	 */
	public static String formatStackMsg(Exception e) {	
		if (e != null) {
			StringWriter sw = new StringWriter();  
		    PrintWriter pw = new PrintWriter(sw);  
		    e.printStackTrace(pw);  
		    return "\r\n" + sw.toString() + "\r\n"; 
			
		}
		return "";
	}
	
	/**
	 * 格式化异常堆栈结构
	 * @param e
	 * @return
	 */
	public static String formatStackTrace(StackTraceElement[] stackArray, int skipCount) {
		if (stackArray != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = skipCount; i < stackArray.length; i++) {
				StackTraceElement element = stackArray[i];
				sb.append(element.toString() + "\n");
			}
			return sb.toString();
		}
		return "";
	}
}
