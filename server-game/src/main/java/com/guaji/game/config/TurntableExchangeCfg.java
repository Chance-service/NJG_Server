package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 大转盘活动兑换配表
 */
@ConfigManager.XmlResource(file = "xml/turntableExchange.xml", struct = "map")
public class TurntableExchangeCfg extends ConfigBase {

	@Id
	protected final int id;

	/**
	 * 兑换--消耗积分
	 */
	protected final int costCredits;

	/**
	 * 兑换--获得的奖励
	 */
	protected final String exchangeItems;

	/**
	 * 兑换每天限制次数
	 */
	protected final int limitTimes;

	public TurntableExchangeCfg() {
		this.id = 0;
		this.costCredits = 0;
		this.exchangeItems = null;
		this.limitTimes = 0;
	}

	@Override
	protected boolean assemble() {
		return super.assemble();
	}

	public int getId() {
		return id;
	}

	public int getCostCredits() {
		return costCredits;
	}

	public String getExchangeItems() {
		return exchangeItems;
	}

	public int getLimitTimes() {
		return limitTimes;
	}

}
