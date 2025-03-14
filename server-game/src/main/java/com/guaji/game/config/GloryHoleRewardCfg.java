package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/GloryHoleReward.xml", struct = "list")
public class GloryHoleRewardCfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	
	/**
	 * 领奖所需累计積分
	 */
	private final int score;	
	/**
	 * 奖励
	 */
	private final String reward;

	public GloryHoleRewardCfg() {
		this.id = 0;
		this.score = 0;
		this.reward = "";
	}

	public int getId() {
		return id;
	}

	public int getScore() {
		return score;
	}

	public String getReward() {
		return reward;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	public static String getRewardByCfg(int score) {
		List<GloryHoleRewardCfg> listCfg = ConfigManager.getInstance().getConfigList(GloryHoleRewardCfg.class);
		for (GloryHoleRewardCfg gcfg :listCfg) {
			if (score >=  gcfg.getScore()) {
				return gcfg.getReward();
			}
		}
		return "";
	}
}
