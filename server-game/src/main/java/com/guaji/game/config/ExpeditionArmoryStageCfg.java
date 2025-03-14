package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/expeditionArmoryStage.xml", struct = "map")
public class ExpeditionArmoryStageCfg extends ConfigBase{
	/**
	 * 远征活动阶段
	 */
	@Id
	private final int stage;
	/**
	 * 完成改阶段所需的总贡献
	 */
	private final int needExp;
	/**
	 * 下个阶段
	 */
	private final int nextStage;
	/**
	 * 完成该阶段奖励的物品
	 */
	private final String finishAwards; 
	/**
	 * 领取该阶段奖励物品所需个人贡献
	 */
	private final int awardsNeedExp;
	
	public ExpeditionArmoryStageCfg() {
		this.stage = 0;
		this.finishAwards = "";
		this.needExp = 0;
		this.nextStage = 0;
		this.awardsNeedExp = 0;
	}

	public int getStage() {
		return stage;
	}

	public int getNeedExp() {
		return needExp;
	}
	
	public int getNextStage() {
		return nextStage;
	}

	public String getFinishAwards() {
		return finishAwards;
	}

	public int getAwardsNeedExp() {
		return awardsNeedExp;
	}
	
	public boolean hasNext(){
		return nextStage > 0;
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
