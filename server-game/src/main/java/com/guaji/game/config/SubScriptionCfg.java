package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/Subscription168.xml", struct = "map")
public class SubScriptionCfg extends ConfigBase {
	/**
	 * 配置id,對應Recharage goodsId
	 */
	@Id
	protected final int id;
	/**
	 * 活動內可領取獎勵次數
	 */
	private final int times ;
	/**
	 * 購買當下贈禮
	 */
	private final String buyReward;
	/**
	 * 每次贈禮
	 */
	private final String dayReward;
	
	public SubScriptionCfg() {
		id = 0;
		times =0;
		buyReward ="";
		dayReward ="";
	}
	
	public int getId() {
		return id;
	}
	
	public int getTimes() {
		return times;
	}

	
	public String getBuyReward() {
		return buyReward;
	}

	public String getDayReward() {
		return dayReward;
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
