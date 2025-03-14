package com.guaji.game;

import java.util.Date;

import org.guaji.app.AppCfg;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

@ConfigManager.KVResource(file = "conf/cfg/gs.cfg")
public class GsConfig extends AppCfg {
	/**
	 * 游戏名
	 */
	protected final String gameId;
	/**
	 * 平台名
	 */
	protected final String platform;
	/**
	 * 服务器id
	 */
	protected final int serverId;
	/**
	 * 灰度状态
	 */
	protected final int grayState;
	/**
	 * 开服时间
	 */
	protected final String serviceDate;
	/**
	 * 开服时间
	 */
	private Date serverOpenDate;
	/**
	 * 当服最大注册数
	 */
	protected final int registerMaxSize;
	/**
	 * cdk地址
	 */
	protected final String cdkHost;
	/**
	 * cdk超时
	 */
	protected final int cdkTimeout;
	
	/**
	 * 平台地址
	 */
	protected final String platformHost;
	/**
	 * 平台超时
	 */
	protected final int platformTimeout;
	/**
	 * 数据上报地址
	 */
	protected final String reportHost;
	/**
	 * 数据上报超时
	 */
	protected final int reportTimeout;
	/**
	 * 语言
	 */
	protected final String language;
	/**
	 * 腾讯CMem服务地址
	 */
	protected final String cmcHost;
	/**
	 * ip代理地址
	 */
	protected final String ipProxyAddr;
	/**
	 * ip服务超时
	 */
	protected final int ipProxyTimeout;
	/**
	 * Redis服务器地址
	 */
	protected final String redisHost;
	/**
	 * Redis服务器端口号
	 */
	protected final int redisPort;
	
	protected final String redisPassword;

	/** statsd上报功能前缀 */
	protected final String statsdPrefix;
	/** statsd上报功ip */
	protected final String statsdHost;
	/** statsd上报端口 */
	protected final int statsdPort;
	/** 快照策略 */
	protected final int snapShotStrategy;

	/** 同步月卡周卡地址 */
	protected final String syncSubHost;
	/** 同步月卡周卡地址超时时间 */
	protected final int syncTimeout;
	/**
	 * 公告伺服器位置
	 */
	protected final String bulletinHost;
	/*
	 * 公告伺服器超時時間
	 */
	protected final int bulletinTimeout;
	/*
	 * 公告對應遊戲ID
	 */
	protected final int bulletinGameID;
	/**
	 * 伺服器鎖定創角功能
	 */
	protected final boolean createLock;

	/**
	 * 全局静态对象
	 */
	private static GsConfig instance = null;

	/**
	 * 获取全局静态对象
	 * 
	 * @return
	 */
	public static GsConfig getInstance() {
		return instance;
	}

	public GsConfig() {
		instance = this;

		gameId = "";
		platform = "";
		serverId = 0;
		registerMaxSize = 0;
		grayState = 0;
		cdkHost = "";
		cdkTimeout = 1000;
		platformHost = "";
		platformTimeout = 1000;
		reportHost = "";
		reportTimeout = 1000;
		serviceDate = "20140814";
		language = "";
		cmcHost = "";
		ipProxyAddr = "";
		ipProxyTimeout = 1000;

		redisHost = "";
		redisPort = 0;
		redisPassword = "";

		statsdPrefix = "";
		statsdHost = "";
		statsdPort = 0;
		snapShotStrategy = 1;

		syncSubHost = "";
		syncTimeout = 1000;
		bulletinHost = "";
		bulletinTimeout = 1000;
		bulletinGameID = 0;
		createLock = false;
		
	}

	public int getSnapShotStrategy() {
		return snapShotStrategy;
	}

	public String getGameId() {
		return gameId;
	}

	public String getPlatform() {
		return platform;
	}

	public int getServerId() {
		return serverId;
	}

	public int getRegisterMaxSize() {
		return registerMaxSize;
	}

	public int getGrayState() {
		return grayState;
	}

	public String getServiceDate() {
		return serviceDate;
	}

	public String getCdkHost() {
		return cdkHost;
	}

	public int getCdkTimeout() {
		return cdkTimeout;
	}
	
	public String getPlatformHost() {
		return platformHost;
	}

	public int getPlatformTimeout() {
		return platformTimeout;
	}

	public String getReportHost() {
		return reportHost;
	}

	public int getReportTimeout() {
		return reportTimeout;
	}

	public String getLanguage() {
		return language;
	}

	public String getCmcHost() {
		return cmcHost;
	}

	public String getIpProxyAddr() {
		return ipProxyAddr;
	}

	public int getIpProxyTimeout() {
		return ipProxyTimeout;
	}

	public String getRedisHost() {
		return redisHost;
	}

	public int getRedisPort() {
		return redisPort;
	}

	/**
	 * 初始化开服时间
	 * @return
	 */
	protected boolean initServerOpenDate() {
		if (this.serviceDate != null) {
			this.serverOpenDate = GuaJiTime.DATE_FORMATOR_DAYNUM(serviceDate);
		}
		return true;
	}

	public Date getServerOpenDate() {
		return serverOpenDate;
	}

	public void setServerOpenDate(Date serverOpenDate) {
		this.serverOpenDate = serverOpenDate;
	}

	public String getStatsdPrefix() {
		return statsdPrefix;
	}

	public String getStatsdHost() {
		return statsdHost;
	}

	public int getStatsdPort() {
		return statsdPort;
	}

	public String getRedisPassword() {
		return redisPassword;
	}

	public String getSyncSubHost() {
		return syncSubHost;
	}

	public int getSyncSubTimeout() {
		return syncTimeout;
	}

	public String getBulletinHost() {
		return bulletinHost;
	}

	public int getBulletinTimeout() {
		return bulletinTimeout;
	}

	public int getBulletinGameID() {
		return bulletinGameID;
	}

	public boolean isCreateLock() {
		return createLock;
	}
}
