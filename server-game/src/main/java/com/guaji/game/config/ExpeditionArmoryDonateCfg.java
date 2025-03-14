package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/expeditionArmoryDonate.xml", struct = "map")
public class ExpeditionArmoryDonateCfg  extends ConfigBase{
	@Id
	private final int id;
	private final int donateItemId;
	private final int addExp;
	private final String award;
	
	public ExpeditionArmoryDonateCfg(){
		this.id = 0;
		this.donateItemId = 0;
		this.addExp = 0;
		this.award = "";
	}

	public int getId() {
		return id;
	}

	public int getDonateItemId() {
		return donateItemId;
	}

	public int getAddExp() {
		return addExp;
	}

	public String getAward() {
		return award;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
