package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.util.GameUtil;

@ConfigManager.XmlResource(file = "xml/roleBase.xml", struct = "map")
public class RoleCfg extends ConfigBase {
	@Id
	protected final int id;
	/**
	 * 类型(主角 1or佣兵2)
	 */
	protected final int type;
	/**
	 * 职业
	 */
	protected final int profession;

	/**
	 * 品级
	 */
	protected final int quality;
	/**
	 * 初始经验
	 */
	protected final int exp;
	/**
	 * 初始名字
	 */
	protected final String name;
	/**
	 * 解锁消耗
	 */
	protected final String cost;
	/**
	 * 激活条件，是否需要材料激活（0不需要 1需要）
	 */
	protected final int costType;
	/**
	 * 最大进阶
	 */
	protected final int maxRank;
	/**
	 * 最大培养属性比率
	 */
	protected final float trainRatio;
	/**
	 * 创建主角默认装备
	 */
	protected final String equip5;

	/**
	 * 携带技能
	 */
	protected final String skills;

	/**
	 * 狂暴开始回合数
	 */
	protected final int rageRound;
	/**
	 * 狂暴伤害层加成
	 */
	protected final float rageAddition;
	/**
	 * 基础佣兵ID
	 */
	protected final int baseId;
	/**
	 * 皮肤ID
	 */
	protected final String skins;
	/**
	 * 技能id列表
	 */
	protected List<Integer> skillIds;
	/**
	 * 皮肤id列表
	 */
	protected List<Integer> skinList;
	
	public RoleCfg() {
		id = 0;
		type = 0;
		profession = 0;
		quality = 0;
		skills = null;
		exp = 0;
		name = null;
		costType = 0;
		maxRank = 0;
		cost = null;
		trainRatio = 0;
		skillIds = new LinkedList<Integer>();
		rageRound = 0;
		rageAddition = 0;
		equip5 = null;
		baseId = 0;
		skins = "";
		skinList = new ArrayList<Integer>();
	}

	public int getId() {
		return id;
	}

	public int getProfession() {
		return profession;
	}

	public int getExp() {
		return exp;
	}

	public String getName() {
		return name;
	}

	public List<Integer> getSkillIds() {
		return skillIds;
	}

	public void setSkillIds(List<Integer> skillIds) {
		this.skillIds = skillIds;
	}

	public int getQuality() {
		return quality;
	}

	public String getCost() {
		return cost;
	}

	public int getCostType() {
		return costType;
	}

	public int getMaxRank() {
		return maxRank;
	}

	public float getTrainRatio() {
		return trainRatio;
	}

	public int getRageRound() {
		return rageRound;
	}

	public float getRageAddition() {
		return rageAddition;
	}

	public String getEquip5() {
		return equip5;
	}

	public int getType() {
		return type;
	}

	public String getSkills() {
		return skills;
	}

	/**
	 * 获取佣兵所有佣兵
	 * 
	 * @return
	 */
	public static Map<Integer, RoleCfg> getRoleInfoMap() {
		Map<Integer, RoleCfg> map = new HashMap<Integer, RoleCfg>();

		Map<Object, RoleCfg> roleMap = ConfigManager.getInstance().getConfigMap(RoleCfg.class);
		for (RoleCfg role : roleMap.values()) {
			if (GameUtil.isHero(role.getType())) {
				map.put(role.getId(), role);
			}
		}
		return map;
	}

	@Override
	protected boolean assemble() {
		skillIds.clear();
		if (skills != null && skills.length() > 0) {
			String[] items = skills.split(",");
			for (String item : items) {
				int skillId = Integer.valueOf(item.trim());
				if (skillId > 0) {
					skillIds.add(skillId);
				}
			}
		}
		skinList.clear();
		if (skins != null && skins.length() > 0) {
			String[] items = skins.split(",");
			for (String item : items) {
				int skin = Integer.valueOf(item.trim());
				if (skin > 0 && !skinList.contains(skin)) {
					skinList.add(skin);
				}
			}
		}
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

	public int getBaseId() {
		return baseId;
	}

	public List<Integer> getSkinList() {
		return skinList;
	}
}
