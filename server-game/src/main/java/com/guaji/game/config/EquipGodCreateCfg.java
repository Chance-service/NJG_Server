package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/equipGodCreate.xml", struct = "map")
public class EquipGodCreateCfg extends ConfigBase{

	@Id
	private final int id ;
	
	private final int equipId ;
	
	private final int godAttr ;
	
	private final int costreputation ;
	
	private final int costSmelt ;
	
	private final int minLevel;
	
	private final int maxLevel;
	
	public EquipGodCreateCfg() {
		id = 0;
		equipId = 0;
		costSmelt = 0;
		godAttr = 0;
		costreputation = 0;
		minLevel = 0;
		maxLevel = 0;
	}

	@Override
	protected boolean assemble() {
		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		if(ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipId) == null) {
			return false;
		}
		return super.checkValid();
	}

	public int getEquipId() {
		return equipId;
	}

	public int getId() {
		return id;
	}

	public int getGodAttr() {
		return godAttr;
	}
	
	public boolean isGodAttrDouble() {
		return godAttr == 2;
	}

	public int getCostSmelt() {
		return costSmelt;
	}

	public int getCostreputation() {
		return costreputation;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
