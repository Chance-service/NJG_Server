package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 开启宝箱掉落
 * 
 * @author Melvin.Mao
 * @date 2017年9月15日 下午4:39:33
 */
@ConfigManager.XmlResource(file = "xml/turntableBoxReward.xml", struct = "list")
public class TurntableBoxRewardCfg extends ConfigBase {

	/**
	 * 宝箱ID
	 */
	@Id
	private final int id;
	/**
	 * 宝箱开启条件
	 */
	private final int condition;
	/**
	 * 奖励物品
	 */
	private final String rewards;

	public int getId() {
		return id;
	}

	public int getCondition() {
		return condition;
	}

	public String getRewards() {
		return rewards;
	}

	public TurntableBoxRewardCfg() {
		id = 0;
		condition = 0;
		rewards = "";
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

}
