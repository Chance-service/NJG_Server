package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/haremExchange.xml", struct = "map")
public class HaremExchangeCfg extends ConfigBase {

	@Id
	protected final int id;

	/**
	 * 兑换--消耗积分
	 */
	protected final int costCredits;

	/**
	 * 兑换-消耗道具
	 */
	protected final String costItems;

	/**
	 * 兑换--获得的奖励
	 */
	protected final String exchangeItems;

	/**
	 * 兑换每天限制次数
	 */
	protected final int limitTimes;
	
	/**
	 * 兑换次数是否需要重置
	 */
	protected final int isReset;

	public HaremExchangeCfg() {
		this.id = 0;
		this.costCredits = 0;
		this.costItems = null;
		this.exchangeItems = null;
		this.limitTimes = 0;
		this.isReset=0;
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

	public String getCostItems() {
		return costItems;
	}

	public String getExchangeItems() {
		return exchangeItems;
	}

	public int getLimitTimes() {
		return limitTimes;
	}

	public int getIsReset() {
		return isReset;
	}

}
