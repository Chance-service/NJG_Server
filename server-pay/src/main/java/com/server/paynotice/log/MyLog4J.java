package com.server.paynotice.log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

/**
 * Log4j加载器
 * 
 * 使用规则:
 * <ul>
 * <li><b>debug</b> 用来调试.
 * <li><b>info</b> 输出信息描述
 * <li><b>warn</b> 警告
 * <li><b>error</b> 异常
 * <li><b>fatal</b> 严重错误
 * </ul>
 *
 * @author 张林
 * 
 */
public class MyLog4J {

	/** 是否已经成功初始化 */
	private static boolean configured = false;

	/**
	 * 通过路径加载log4j
	 * 
	 * @param filename
	 */
	public static void init(String filename) {
		if (configured) {
			return;
		}
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filename);
			init(inputStream);
			configured = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过流加载log4J
	 * 
	 * @param propsFile
	 */
	public static void init(InputStream propsFile) {
		if (configured) {
			return;
		}
		boolean isDebug = false;
		Properties props = new Properties();
		try {
			props.load(propsFile);
			if (isDebug) {// false 关闭debug 模式.true开启debug模式
				props.setProperty("log4j.rootLogger", "DEBUG, stdout, infoFile, errorFile");
			} else {
				props.setProperty("log4j.rootLogger", "INFO, infoFile, errorFile");
			}
			PropertyConfigurator.configure(props);
			configured = true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (propsFile != null) {
				try {
					propsFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				propsFile = null;
			}
		}
	}

}
