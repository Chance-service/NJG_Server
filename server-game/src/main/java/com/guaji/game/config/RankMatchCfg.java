package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 竞技场排名配置规则
 * 
 * @author xpf
 */
@ConfigManager.XmlResource(file = "xml/rankMatch.xml", struct = "list")
public class RankMatchCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 最小比率
	 */
	protected final float minRate;
	/**
	 * 最大比率
	 */
	protected final float maxRate;

	public RankMatchCfg() {
		this.id = 0;
		this.minRate = 0.0f;
		this.maxRate = 0.0f;
	}

	public int getId() {
		return id;
	}

	public float getMinRate() {
		return minRate;
	}

	public float getMaxRate() {
		return maxRate;
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
