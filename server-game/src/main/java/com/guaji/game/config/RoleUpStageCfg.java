package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 佣兵升阶
 * 
 * @author Administrator
 *
 */
@ConfigManager.XmlResource(file = "xml/roleRank.xml", struct = "list")
public class RoleUpStageCfg extends ConfigBase {
	private static int maxStageLevel = 0;
	@Id
	protected final int id;

	/**
	 * 初始等级
	 */
	protected final int rankLevel;

	/**
	 * 升阶消耗
	 */
	protected final String cost;

	/**
	 * 最高星
	 */
	protected final int needStar;

	/**
	 * 命中
	 */
	protected final int hitStarRatio;
	/**
	 * 闪避
	 */
	protected final int dodgeStarRatio;
	/**
	 * 暴击
	 */
	protected final int critStarRatio;
	/**
	 * 韧性
	 */
	protected final int resilienceStarRatio;
	/**
	 * 物抗
	 */
	protected final int phyDefStarRatio;
	/**
	 * 磨抗
	 */
	protected final int magicDefStarRatio;
	/**
	 * hp
	 */
	protected final int hpStarRatio;
	/**
	 * 最小伤害
	 */
	protected final int damageStarRatio;
	/**
	 * mp
	 */
	protected final int mpStarRatio;
	/**
	 * 护甲
	 */
	protected final int armorStarRatio;

	/**
	 * 解锁技能
	 */
	protected final int activeSkill;
	/**
	 * 解锁光环
	 */
	protected final int ringId;

	public RoleUpStageCfg() {
		id = 0;
		rankLevel = 0;
		activeSkill = 0;
		cost = null;
		ringId = 0;
		needStar = 0;
		hitStarRatio = 0;
		dodgeStarRatio = 0;
		critStarRatio = 0;
		resilienceStarRatio = 0;
		phyDefStarRatio = 0;
		magicDefStarRatio = 0;
		hpStarRatio = 0;
		damageStarRatio = 0;
		mpStarRatio = 0;
		armorStarRatio = 0;
	}

	public int getId() {
		return id;
	}

	public int getRankLevel() {
		return rankLevel;
	}

	public String getCost() {
		return cost;
	}

	public int getNeedStar() {
		return needStar;
	}

	public int getHitStarRatio() {
		return hitStarRatio;
	}

	public int getDodgeStarRatio() {
		return dodgeStarRatio;
	}

	public int getCritStarRatio() {
		return critStarRatio;
	}

	public int getResilienceStarRatio() {
		return resilienceStarRatio;
	}

	public int getPhyDefStarRatio() {
		return phyDefStarRatio;
	}

	public int getMagicDefStarRatio() {
		return magicDefStarRatio;
	}

	public int getHpStarRatio() {
		return hpStarRatio;
	}

	public int getDamageStarRatio() {
		return damageStarRatio;
	}

	public int getMpStarRatio() {
		return mpStarRatio;
	}

	public int getArmorStarRatio() {
		return armorStarRatio;
	}

	public static int getMaxStageLevel() {
		return maxStageLevel;
	}

	public static void setMaxStageLevel(int maxStageLevel) {
		RoleUpStageCfg.maxStageLevel = maxStageLevel;
	}

	public int getActiveSkill() {
		return activeSkill;
	}

	public int getRingId() {
		return ringId;
	}

	@Override
	protected boolean assemble() {
		if (rankLevel > maxStageLevel) {
			maxStageLevel = rankLevel;
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 获取配置基础属性
	 * 
	 * @param id
	 * @param level
	 * @return
	 */
	public static RoleUpStageCfg getRoleUpStageCfg(int id, int stageLevel) {
		List<RoleUpStageCfg> list = ConfigManager.getInstance().getConfigList(RoleUpStageCfg.class);
		for (RoleUpStageCfg cfg : list) {
			if (cfg.getId() == id && cfg.rankLevel == stageLevel) {
				return cfg;
			}
		}

		return null;
	}
	

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	/**
	 * 获取当前佣兵升星配置
	 * 
	 * @param roleId
	 * @return
	 */
	public static List<RoleUpStageCfg> getCurrentRoleCfgList(int roleId) {
		List<RoleUpStageCfg> list = new ArrayList<>();
		List<RoleUpStageCfg> cfgs = ConfigManager.getInstance().getConfigList(RoleUpStageCfg.class);
		for (RoleUpStageCfg cfg : cfgs) {
			if (cfg.getId() == roleId) {
				list.add(cfg);
			}
		}
		return list;
	}
}
