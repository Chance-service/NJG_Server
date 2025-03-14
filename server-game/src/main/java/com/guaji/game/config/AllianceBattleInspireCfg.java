package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/allianceBattleInspire.xml", struct = "list")
public class AllianceBattleInspireCfg extends ConfigBase{
	
	private final int fightGroup;
	
	private final int times;
	
	private final int costGold;
	
	private final int hpAdd ;
	
	private final int harmAdd ;
	
	/**
	 * 声望加成
	 */
	private final int extraReward1 ;
	/**
	 * 挑战券加成
	 */
	private final int extraReward2 ;
	/**
	 * 宝石袋加成
	 */
	private final int extraReward3 ;
	
	public AllianceBattleInspireCfg() {
		this.fightGroup = 0;
		this.times = 0;
		this.costGold = 0;
		this.hpAdd = 0;
		this.harmAdd = 0;
		this.extraReward1 = 0;
		this.extraReward2 = 0;
		this.extraReward3 = 0;
	}

	@Override
	protected boolean assemble() {
		return true;
	}

	public int getFightGroup() {
		return fightGroup;
	}

	public int getTimes() {
		return times;
	}

	public int getCostGold() {
		return costGold;
	}

	public int getHpAdd() {
		return hpAdd;
	}

	public int getHarmAdd() {
		return harmAdd;
	}


	public static AllianceBattleInspireCfg getInspireCfg(int groupId, int times) {
		for(AllianceBattleInspireCfg inspireCfg : ConfigManager.getInstance().getConfigList(AllianceBattleInspireCfg.class)){
			if(inspireCfg.getFightGroup() == groupId && inspireCfg.getTimes() == times) {
				return inspireCfg;
			}
		}
		return null;
	}

	public int getExtraReward1() {
		return extraReward1;
	}

	public int getExtraReward2() { 
		return extraReward2;
	}

	public int getExtraReward3() {
		return extraReward3;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
