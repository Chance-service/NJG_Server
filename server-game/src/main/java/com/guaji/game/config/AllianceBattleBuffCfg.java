package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/allianceBattleBuff.xml", struct = "map")
public class AllianceBattleBuffCfg extends ConfigBase{
	@Id
	private final int id;
	
	private final int phyDefAdd ;
	
	private final int magicDefAdd ;
	
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
	
	public AllianceBattleBuffCfg() {
		this.id = 0;
		this.phyDefAdd = 0;
		this.magicDefAdd = 0;
		this.extraReward1 = 0;
		this.extraReward2 = 0;
		this.extraReward3 = 0;
	}

	@Override
	protected boolean assemble() {
		return true;
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

	public int getId() {
		return id;
	}

	public int getMagicDefAdd() {
		return magicDefAdd;
	}

	public int getPhyDefAdd() {
		return phyDefAdd;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
