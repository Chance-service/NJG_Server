package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/skill.xml", struct = "map")
public class SkillCfg extends ConfigBase {
	@Id
	protected final int id;
	/**
	 * 职业限制
	 */
	protected final int profLimit;
	/**
	 * 等级限制
	 */
	protected final int levelLimit;
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
	 * 学习类型 1 表示升级自动学 0 表示需要技能专精学
	 */
	private final int learnType;
	
	public SkillCfg() {
		id = 0;
		profLimit = 0;
		levelLimit = 0;
		costMP = 0;
		cool = 0;
		scriptId = 0;
		damageType = 0;
		learnType = 0;
	}

	public int getId() {
		return id;
	}

	public int getProfLimit() {
		return profLimit;
	}

	public int getLevelLimit() {
		return levelLimit;
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
	
	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getLearnType() {
		return learnType;
	}
	
	public boolean isAutoLearn() {
		return learnType == 1;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
