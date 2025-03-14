package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 竞技场购买挑战次数相关配置
 */
@ConfigManager.XmlResource(file = "xml/arenaBuyTimes.xml", struct = "list")
public class ArenaBuyTimesCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 规则适用的最大次数
	 */
	protected final int maxTimes;
	/**
	 * 价格
	 */
	protected final int price;
	/**
	 * 增加挑战次数
	 */
	protected final int addChallengeTimes;

	public ArenaBuyTimesCfg() {
		this.id = 0;
		this.maxTimes = 0;
		this.price = 0;
		this.addChallengeTimes = 0;
	}

	public int getId() {
		return id;
	}

	public int getMaxTimes() {
		return maxTimes;
	}

	public int getPrice() {
		return price;
	}

	public int getAddChallengeTimes() {
		return addChallengeTimes;
	}

	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
