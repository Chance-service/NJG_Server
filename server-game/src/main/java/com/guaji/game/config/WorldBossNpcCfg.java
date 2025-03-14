package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/worldBoss.xml", struct = "map")
public class WorldBossNpcCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int bossId;
	/**
	 * 權重
	 */
	private final int rate;
	/**
	 * 獎勵id
	 */
	private final int awardsId;
	
	
	public WorldBossNpcCfg(){
		this.bossId = 0;
		this.rate = 0;
		this.awardsId = 0;
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
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getBossId() {
		return bossId;
	}

	public int getRate() {
		return rate;
	}
	
	public int getawardId() {
		return awardsId;
	}	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}

