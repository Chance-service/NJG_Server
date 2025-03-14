package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.util.ConfigUtil;
import com.guaji.game.util.WeightUtil;
import com.guaji.game.util.WeightUtil.WeightItem;

/**
 * 装备锻造配置，用于熔炼锻造装备
 * 
 * @author xulinqs
 * 
 */
@ConfigManager.XmlResource(file = "xml/equipForge.xml", struct = "list")
public class EquipForgeCfg extends ConfigBase {

	/**
	 * 玩家等级下限
	 */
	private final int minLevel;

	/**
	 * 玩家等级上限
	 */
	private final int maxLevel;

	/**
	 * 可选装备列表
	 */
	private final String equips;

	/**
	 * 神器概率
	 */
	private final int godlyRatio;

	private List<WeightItem<Integer>> weightItems = new LinkedList<>();

	public EquipForgeCfg() {
		minLevel = 0;
		maxLevel = 0;
		equips = null;
		godlyRatio = 0;
	}

	/**
	 * 根据玩家等级获得配置
	 * 
	 * @param playerLevel
	 */
	public static EquipForgeCfg getEquipForgeCfg(int playerLevel) {
		List<EquipForgeCfg> equipForgeCfgs = ConfigManager.getInstance().getConfigList(EquipForgeCfg.class);
		for (EquipForgeCfg equipForgeCfg : equipForgeCfgs) {
			if (equipForgeCfg.getMinLevel() <= playerLevel && equipForgeCfg.getMaxLevel() >= playerLevel) {
				return equipForgeCfg;
			}
		}
		return null;
	}

	/**
	 * 根据权重取出生成的装备Id
	 */
	public Integer randomRewardsGroupId() {
		return WeightUtil.random(weightItems);
	}

	public int getMinLevel() {
		return minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public String getEquips() {
		return equips;
	}

	@Override
	protected boolean assemble() {
		weightItems.clear();
		weightItems = WeightUtil.convertToList(equips);
		return true;
	}

	@Override
	protected boolean checkValid() {
		for(WeightItem<Integer> weightItem : weightItems) {
			if(!ConfigUtil.checkAwardGroup(weightItem.getValue())) {
				return false;
			}
		}
		return true;
	}

	public int getGodlyRatio() {
		return godlyRatio;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
