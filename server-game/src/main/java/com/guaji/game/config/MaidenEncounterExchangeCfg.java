package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/maidenEncounterExchange.xml", struct = "map")
public class MaidenEncounterExchangeCfg extends ConfigBase {

	@Id
	protected final int id;

	/**
	 * 兑换-消耗道具
	 */
	protected final String costItems;

	/**
	 * 兑换--获得的奖励
	 */
	protected final String exchangeItems;

	/**
	 * 兑换--每天限制次数
	 */
	protected final int limitTimes;


	public MaidenEncounterExchangeCfg() {
		this.id = 0;
		this.costItems = null;
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

	public String getCostItems() {
		return costItems;
	}

	public String getExchangeItems() {
		return exchangeItems;
	}

	public int getLimitTimes() {
		return limitTimes;
	}

}
