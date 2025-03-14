package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/skillLevel.xml", struct = "list")
public class SkillLevelCfg extends ConfigBase {
	
	private final int skillId;
	/**
	 * 等级
	 */
	private final int level;
	/* 战斗类型定义*/
	private final String battleType;
	/**
	 * 施放消耗mp
	 */
	protected final int costMP;
	/**
	 * 冷却回合
	 */
	protected final int cool;
	/**
	 * 逻辑脚本id
	 */
	protected final int scriptId;
	/**
	 * 伤害类型
	 */
	protected final int damageType;
	/**
	 * 参数1
	 */
	private final int arg1;
	/**
	 * 参数2
	 */
	private final int arg2;
	/**
	 * 参数3
	 */
	private final int arg3;
	/**
	 * 回合数
	 */
	private final int round;

	// 元素相关
	/** 是否附带元素伤害 */
	private final int hasElementDamage;
	/** 是否附带buff类型元素伤害 */
	private final int isBuff;
	/** 伤害的元素类型 */
	private final int elementType;
	/** 元素伤害加成比例 */
	private final int elementDamageRatio;

	protected List<Integer> battleTypeList;

	public SkillLevelCfg() {
		skillId = 0;
		level = 0;
		costMP = 0;
		cool = 0;
		scriptId = 0;
		damageType = 0;
		arg1 = 0;
		arg2 = 0;
		arg3 = 0;
		round = 1;

		hasElementDamage = 0;
		isBuff = 0;
		elementType = 0;
		elementDamageRatio = 0;
		battleType = "";
		battleTypeList = new ArrayList<Integer>();
	}

	public boolean isHasElementDamage() {
		return hasElementDamage != 0;
	}

	public boolean isBuff() {
		return isBuff != 0;
	}

	public int getElementType() {
		return elementType;
	}

	public int getElementDamageRatio() {
		return elementDamageRatio;
	}

	public int getCostMP() {
		return costMP;
	}

	public int getCool() {
		return cool;
	}

	public int getScriptId() {
		return scriptId;
	}

	public int getDamageType() {
		return damageType;
	}
	
	public String getbattleType() {
		return battleType;
	}
	
	public boolean chkbattletype(int type) {
		if (battleTypeList.size() == 0)
				return true;
		else
			return battleTypeList.contains(type);
			
	}
	
	@Override
	protected boolean assemble() {
		battleTypeList.clear();
		String[] battleTypeSt = battleType.trim().split(",");
		for (String type : battleTypeSt) {
			if (Integer.valueOf(type) != 0) {
				battleTypeList.add(Integer.valueOf(type));
			}
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getSkillId() {
		return skillId;
	}

	public int getLevel() {
		return level;
	}

	public int getArg1() {
		return arg1;
	}

	public int getArg2() {
		return arg2;
	}

	public int getArg3() {
		return arg3;
	}
	
	public static SkillLevelCfg getSkillLevelCfg(int skillId, int level, int battleType) {
		for(SkillLevelCfg skillLevelCfg : ConfigManager.getInstance().getConfigList(SkillLevelCfg.class)) {
			if(skillLevelCfg.getSkillId() == skillId && skillLevelCfg.getLevel() == level && skillLevelCfg.chkbattletype(battleType)) {
				return skillLevelCfg;
			}
		}
		return null;
	}

	public int getRound() {
		return round;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}
