package com.server.paynotice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.server.paynotice.common.AppConst;
import com.server.paynotice.common.Settings;
import com.server.paynotice.log.MyLog4J;
import com.server.paynotice.netty.NettyServer;
import com.server.paynotice.schedule.ScheduleService;
import com.server.paynotice.service.impl.AutoSubQueueService;
import com.server.paynotice.util.DBUtil;
import com.server.paynotice.xmlparser.PayNoticeUrlManager;
import com.server.paynotice.xmlparser.RechargeConfig;

/**
 * Hello world!
 *
 */
public class App {
	private static Logger logger = Logger.getLogger(App.class);

	private static String settingsCfg;

	private static String logCfg;

	private static String mybatisCfg;

	private static String payNoticeCfg;

	// private static String rechargeCfg;//充值配置

	static {
		initPath();
		initSetiings();
		// 初始化日志配置
		MyLog4J.init(logCfg);
		PayNoticeUrlManager.init(payNoticeCfg);
	}

	public static void main(String[] args) {

		logger.info("1、加载数据库配置*********************");
		parsePort(args);
		logger.info("1、加载数据库配置*********************");
		DBUtil.init(mybatisCfg);

		// 绑定端口
		boolean bindStatus = NettyServer.bind(Settings.port);
		if (bindStatus) {
			logger.info("3、绑定socket端口*****************成功!");
		} else {
			logger.info("3、绑定socket端口*****************失败!");
			return;
		}

		// 加载xml
		ScheduleService.executeLoadXml();
		logger.info("3、加载支付相关配置*********************");
		// 加载购买产品配置项
		RechargeConfig.getRechargeConfig("android_hutuo");
		RechargeConfig.getRechargeConfig("ios_hutuo");
		RechargeConfig.getRechargeConfig("aws_hutuo");
		logger.info("4、加载商品列表配置*********************");
		// AutoSubOrderManager.getInstance().init();
		logger.info("5、充值通知服务器启动完毕，端口：" + Settings.port);

	}

	private static void parsePort(String[] args) {
		if (args.length == 0) {
			return;
		}
		try {
			for (String arg : args) {
				if (arg.contains("--port")) {
					String port = arg.substring(arg.indexOf("=") + 1, arg.length());
					Settings.port = Integer.parseInt(port);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
		}

	}

	public static void initPath() {
		int resetLength = 0;
		StringBuffer buffer = new StringBuffer();
		buffer.append(AppConst.SOURCE_DIR);
		resetLength = buffer.length();
		buffer.append(AppConst.SETTINGS);
		settingsCfg = buffer.toString();
		buffer.setLength(resetLength);
		buffer.append(AppConst.MYBATIS);
		mybatisCfg = buffer.toString();
		buffer.setLength(resetLength);
		buffer.append(AppConst.LOG);
		logCfg = buffer.toString();
		buffer.setLength(resetLength);
		buffer.append(AppConst.PAY_NOTICE);
		payNoticeCfg = buffer.toString();
	}

	public static void initSetiings() {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(settingsCfg);
			Properties prop = new Properties();
			prop.load(inputStream);
			Settings.security = prop.getProperty("security");
			Settings.debug = Integer.parseInt(prop.getProperty("debug"));
			Settings.port = Integer.parseInt(prop.getProperty("port"));
			Settings.apple_receipturl = prop.getProperty("apple_receipturl");
			Settings.google_receipturl = prop.getProperty("google_receipturl");
			Settings.aws_receipturl = prop.getProperty("aws_receipturl");
			Settings.aliAccessKeyId = prop.getProperty("aliAccessKeyId");
			Settings.aliccessKeySecret = prop.getProperty("aliccessKeySecret");
			Settings.alinameSrvAddr = prop.getProperty("alinameSrvAddr");
			Settings.topicName = prop.getProperty("topicName");
			inputStream.close();
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
