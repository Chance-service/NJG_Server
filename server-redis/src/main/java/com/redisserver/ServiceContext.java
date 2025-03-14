package com.redisserver;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.redisserver.servlet.AccountDataCenter;

import GameServerManager.GameServerDataCenter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class ServiceContext implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(ServiceContext.class.getName());

	/**
	 * redis 客户端连接池
	 */
	public static JedisPool jedisPool;

	private static JedisPoolConfig config;
	
	private static boolean accountDataCenterState;
	private static boolean gameServerDataCenterState;

	static {
		config = new JedisPoolConfig();
		config.setMaxTotal(600);// 设置最大连接数
		config.setMaxIdle(100); // 设置最大空闲数
//		config.
		config.setMaxWaitMillis(10000);// 设置超时时间
		config.setTestOnBorrow(true);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	//入口
	@Override
	public void contextInitialized(ServletContextEvent arg0) 
	{

		JediseConfig.getInstance().initialize();

		logger.info("install redis service......");

		jedisPool = new JedisPool(config, JediseConfig.getInstance().getRedisHost(), JediseConfig.getInstance().getRedisPort(),Protocol.DEFAULT_TIMEOUT,JediseConfig.getInstance().getRedisPassword());
		if (jedisPool.getNumActive() < 0) {
			logger.info("init jedis's pool fail ......");
		}
		
		AccountDataCenter.getInstance().initData();
		GameServerDataCenter.getInstance().initData();

	}
	
	public static void setAccountDataCenterState(boolean flag)
	{
		accountDataCenterState = flag;
		return;
	}
	
	public static boolean getAccountDataCenterState()
	{
		return accountDataCenterState;
	}
	
	public static void setGameServerDataCenterState(boolean flag)
	{
		gameServerDataCenterState = flag;
		return;
	}
	
	public static boolean getGameServerDataCenterState()
	{
		return gameServerDataCenterState;
	}
	

}
