package com.redisserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 读取配置
 * 
 * @author Nannan.Gao
 */
public class JediseConfig {

	private final Logger logger = Logger.getLogger(JediseConfig.class.getName());

	/**
	 * 地址
	 */
	private String redisHost;

	/**
	 * 端口
	 */
	private int redisPort;

	/**
	 * redis密码
	 */
	private String redisPassword;

	/**
	 * 管理员密码
	 */
	private String adminPassword;

	private static JediseConfig instance = new JediseConfig();

	public static JediseConfig getInstance() {
		return instance;
	}

	public void initialize() {

		InputStream is = this.getClass().getResourceAsStream("/redis.properties");
		Properties properties = new Properties();
		try {
			properties.load(is);
		} catch (IOException e) {
			logger.error("解析数据库连接池配置文件出错" + e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.error(null, e);
			}
		}


		redisHost = properties.get("redisHost").toString();
		redisPort = Integer.parseInt(properties.get("redisPort").toString());
		redisPassword = properties.get("redisPassword").toString();
		adminPassword = properties.getProperty("adminPass").toString();
	}

	public String getRedisHost() {
		return redisHost;
	}

	public int getRedisPort() {
		return redisPort;
	}

	public String getRedisPassword() {
		return redisPassword;
	}

	public void setRedisPassword(String redisPassword) {
		this.redisPassword = redisPassword;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

}
