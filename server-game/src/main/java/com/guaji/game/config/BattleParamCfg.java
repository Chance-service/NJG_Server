package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/battleParameter.xml", struct = "map")
public class BattleParamCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int level;
	/**
	 * 护甲
	 */
	protected final int armor;
	/**
	 * 物抗
	 */
	protected final int phyDef;
	/**
	 * 魔抗
	 */
	protected final int magDef;
	/**
	 * 暴击
	 */
	protected final int critical;
	/**
	 * 韧性
	 */
	protected final int resilience;
	/**
	 * 命中
	 */
	protected final int hit;
	/**
	 * 闪避
	 */
	protected final int dodge;
	/**
	 * 破甲穿透
	 */
	protected final int avoidArmor;
	/**
	 * 物防穿透
	 */
	protected final int phyDefPenetrate;
	/**
	 * 法防穿透
	 */
	protected final int magDefPenetrate;
	
	public BattleParamCfg() {
		level = 0;
		armor = 0;
		phyDef = 0;
		magDef = 0;
		critical = 0;
		resilience = 0;
		hit = 0;
		dodge = 0;
		avoidArmor = 0;
		phyDefPenetrate = 0;
		magDefPenetrate = 0;
	}

	public int getLevel() {
		return level;
	}

	public int getArmor() {
		return armor;
	}

	public int getPhyDef() {
		return phyDef;
	}

	public int getMagDef() {
		return magDef;
	}

	public int getCritical() {
		return critical;
	}

	public int getResilience() {
		return resilience;
	}

	public int getHit() {
		return hit;
	}

	public int getDodge() {
		return dodge;
	}
	
	public int getAvoidArmor() {
		return avoidArmor;
	}

	public int getPhyDefPenetrate() {
		return phyDefPenetrate;
	}

	public int getMagDefPenetrate() {
		return magDefPenetrate;
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
