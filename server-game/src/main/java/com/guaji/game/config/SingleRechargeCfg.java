package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/singleRecharge.xml", struct = "map")
public class SingleRechargeCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 单笔充值数额
	 */
	private final int singleRecharge;
	/**
	 * 单笔充值奖励
	 */
	private final String awards;
	/**
	 * 可以领取几次
	 */
	private final int maxRechargeTimes;

	public SingleRechargeCfg() {
		this.id = 0;
		this.singleRecharge = 0;
		this.awards = "";
		this.maxRechargeTimes = 0;
	}

	public int getId() {
		return id;
	}

	public int getMaxRechargeTimes() {
		return maxRechargeTimes;
	}

	public int getSingleRecharge() {
		return singleRecharge;
	}

	public String getAwards() {
		return awards;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
