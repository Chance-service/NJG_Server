package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;


@ConfigManager.XmlResource(file = "xml/princeDevilsExchange.xml", struct = "map")
public class PrinceDevilsExchangeCfg extends ConfigBase {

	@Id
	private final int id;
	/** 兑换消耗积分 */
	private final int costCredits;
	/** 兑换奖励 */
	private final String exchangeItems;
	/** 兑换次数限制 */
	private final int limitTimes;

	public PrinceDevilsExchangeCfg() {
		this.id = 0;
		this.costCredits = 0;
		this.exchangeItems = null;
		this.limitTimes = 0;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
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
