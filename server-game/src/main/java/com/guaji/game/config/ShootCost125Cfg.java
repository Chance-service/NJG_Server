package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/shootCost125.xml", struct = "map")
public class ShootCost125Cfg extends ConfigBase {
	/**
	 * 类型
	 */
	@Id
	private final int type;
	/**
	 * 免费次数刷新时间间隔
	 */
	private final int freeRefreshTime;
	/**
	 * 1次消耗钻石数量
	 */
	private final int oneTimeCost;
	/**
	 * 10次消耗钻石
	 */
	private final int tenTimeCost;

	/**
	 * 奖池类型
	 */
	private final int poolType;

	private final int maxCount;

	public ShootCost125Cfg() {
		type = 0;
		freeRefreshTime = 0;
		oneTimeCost = 0;
		tenTimeCost = 0;
		poolType = 0;
		maxCount = 0;
	}

	public int getType() {
		return type;
	}

	public int getFreeRefreshTime() {
		return freeRefreshTime;
	}

	public int getOneTimeCost() {
		return oneTimeCost;
	}

	public int getTenTimeCost() {
		return tenTimeCost;
	}

	public int getPoolType() {
		return poolType;
	}

	public int getMaxCount() {
		return maxCount;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}
