package com.guaji.cdk;

import org.apache.log4j.PropertyConfigurator;
import org.guaji.config.XmlCfg;
import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;

import com.guaji.cdk.http.CdkHttpServer;

/**
 * CDK服务器
 * 
 */
public class Cdk {
	/**
	 * 系统token校验
	 */
	private static String httpToken = "";

	public static void setToken(String token) {
		httpToken = token.trim();
	}

	public static boolean checkToken(String token) {
		if (httpToken != null && httpToken.length() > 0) {
			if (token == null || !token.equals(httpToken)) {
				throw new RuntimeException("http token check failed.");
			}
		}
		return true;
	}

	public static void main(String[] args) {
		try {
			// 退出构造装载
			ShutDownHook.install();

			// 打印系统信息
			OSOperator.printOsEnv();

			XmlCfg conf = new XmlCfg(System.getProperty("user.dir")
					+ "/conf/config.xml");
			// 加载log4j 的配置
			PropertyConfigurator.configure("conf/log4j.properties");
			
			// 初始化redis客户端
			if (!CdkServices.getInstance().initMC(conf.getString("redis.addr"), conf.getInt("redis.timeout"),
					conf.getInt("redis.port"), conf.getString("redis.pwd"))) {

				Log.errPrintln("init redis failed, addr: " + conf.getString("redis.addr") + ", port: "
						+ conf.getString("redis.port"));
				return;
			}

			CdkHttpServer cdkService = new CdkHttpServer();
			cdkService.setup(conf.getString("httpserver.addr"), conf.getInt("httpserver.port"),
					conf.getInt("httpserver.pool"));

			// 设置校验码
			if (conf.containsKey("httpserver.token")) {
				Cdk.setToken(conf.getString("httpserver.token"));
			}

			// cdkService.run();

		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
}
