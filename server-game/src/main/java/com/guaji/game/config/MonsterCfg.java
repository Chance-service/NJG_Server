package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.protocol.Const;
import com.guaji.game.util.PlayerUtil;

@ConfigManager.XmlResource(file = "xml/monster.xml", struct = "map")
public class MonsterCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 怪物名字
	 */
	protected final String name;
	/**
	 * 金币掉落
	 */
	protected final int dropCoin;
	/**
	 * 经验掉落
	 */
	protected final int dropExp;
	/**
	 * 职业
	 */
	protected final int profession;
	/**
	 * 等级
	 */
	protected final int level;
	/**
	 * 技能列表
	 */
	protected final String skills;
	/**
	 * 力量
	 */
	protected final int strenght;
	/**
	 * 敏捷
	 */
	protected final int agility;
	/**
	 * 智力
	 */
	protected final int intellect;
	/**
	 * 耐力
	 */
	protected final int stamina;
	/**
	 * 生命
	 */
	protected final int hp;
	/**
	 * 魔法
	 */
	protected final int mp;
	/**
	 * 最小伤害
	 */
	protected final int minDmg;
	/**
	 * 最大伤害
	 */
	protected final int maxDmg;
	/**
	 * 护甲
	 */
	protected final int armor;
	/**
	 * 物抗
	 */
	protected final int phyDef;
	/**
	 * 法抗
	 */
	protected final int magDef;
	/**
	 * 暴击
	 */
	protected final int critical;
	/**
	 * 命中
	 */
	protected final int hit;
	/**
	 * 闪避
	 */
	protected final int dodge;
	/**
	 * 韧性
	 */
	protected final int resilience;
	/**
	 * 回魔
	 */
	protected final int recoverMp;
	/**
	 * 怪物类型
	 */
	protected final int monsterType;
	/**
	 * 逃跑回合数
	 */
	protected final int runRound;
	/**
	 * 狂暴开始回合数
	 */
	protected final int rageRound;
	/**
	 * 狂暴伤害层加成
	 */
	protected final float rageAddition;
	/**
	 * 技能列表
	 */
	List<Integer> skillIds;
	/**
	 * 冰攻击
	 */
	private final int iceAtt;
	/**
	 * 火攻击
	 */
	private final int fireAtt;
	/**
	 * 雷攻击
	 */
	private final int thunderAtt;
	/**
	 * 冰防御
	 */
	private final int iceDef;
	/**
	 * 火防御
	 */
	private final int fireDef;
	/**
	 * 雷防御
	 */
	private final int thunderDef;
	/**
	 * 是否狂怒
	 */
	private final int isFrenzy;
	/**
	 * 属性数据
	 */
	Attribute attribute;

	public MonsterCfg() {
		id = 0;
		name = "";
		dropCoin = 0;
		dropExp = 0;
		profession = 0;
		level = 0;
		skills = null;
		strenght = 0;
		agility = 0;
		intellect = 0;
		stamina = 0;
		hp = 0;
		mp = 0;
		minDmg = 0;
		maxDmg = 0;
		armor = 0;
		phyDef = 0;
		magDef = 0;
		critical = 0;
		hit = 0;
		dodge = 0;
		resilience = 0;
		recoverMp = 0;
		monsterType = 0;
		runRound = 0;
		rageRound = 0;
		rageAddition = 0;
		
		iceAtt = 0;
		fireAtt = 0;
		thunderAtt = 0;
		iceDef = 0;
		fireDef = 0;
		thunderDef = 0;
		isFrenzy = 0;
		
		skillIds = new LinkedList<Integer>();
		attribute = new Attribute();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getDropCoin() {
		return dropCoin;
	}
	
	public int getDropExp() {
		return dropExp;
	}
	
	public int getProfession() {
		return profession;
	}

	public int getLevel() {
		return level;
	}

	public String getSkills() {
		return skills;
	}

	public int getStrenght() {
		return strenght;
	}

	public int getAgility() {
		return agility;
	}

	public int getIntellect() {
		return intellect;
	}

	public int getStamina() {
		return stamina;
	}

	public int getHp() {
		return hp;
	}

	public int getMp() {
		return mp;
	}

	public int getMinDmg() {
		return minDmg;
	}

	public int getMaxDmg() {
		return maxDmg;
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

	public int getHit() {
		return hit;
	}

	public int getDodge() {
		return dodge;
	}

	public int getResilience() {
		return resilience;
	}

	public int getRecoverMp() {
		return recoverMp;
	}

	public int getMonsterType() {
		return monsterType;
	}

	public int getRunRound() {
		return runRound;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public List<Integer> getSkillIds() {
		return skillIds;
	}

	public int getFight() {
		float fightValue = (attribute.getValue(Const.attr.MINDMG) + attribute.getValue(Const.attr.MAXDMG)) * 1.3f + attribute.getValue(Const.attr.DODGE) * 5.0f + attribute.getValue(Const.attr.HP) * 0.1f + attribute.getValue(Const.attr.MP) * 0.1f + attribute.getValue(Const.attr.RESILIENCE) * 1.0f + attribute.getValue(Const.attr.CRITICAL) * 1.0f + attribute.getValue(Const.attr.PHYDEF) * 1.0f + attribute.getValue(Const.attr.MAGDEF) * 1.0f + attribute.getValue(Const.attr.ARMOR) * 1.0f + attribute.getValue(Const.attr.HIT) * 1.0f;

		return (int) fightValue;
	}

	public int getRageRound() {
		return rageRound;
	}

	public float getRageAddition() {
		return rageAddition;
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

		attribute.clear();
		attribute.add(Const.attr.STRENGHT, strenght);
		attribute.add(Const.attr.AGILITY, agility);
		attribute.add(Const.attr.INTELLECT, intellect);
		attribute.add(Const.attr.STAMINA, stamina);
		attribute.add(Const.attr.HP, hp);
		attribute.add(Const.attr.MP, mp);
		attribute.add(Const.attr.MINDMG, minDmg);
		attribute.add(Const.attr.MAXDMG, maxDmg);
		attribute.add(Const.attr.ARMOR, armor);
		attribute.add(Const.attr.PHYDEF, phyDef);
		attribute.add(Const.attr.MAGDEF, magDef);
		attribute.add(Const.attr.CRITICAL, hit);
		attribute.add(Const.attr.DODGE, dodge);
		attribute.add(Const.attr.RESILIENCE, resilience);
		attribute.add(Const.attr.RECOVERMP, recoverMp);
		attribute.add(Const.attr.ICE_ATTACK, iceAtt);
		attribute.add(Const.attr.FIRE_ATTACK, fireAtt);
		attribute.add(Const.attr.THUNDER_ATTACK, thunderAtt);
		attribute.add(Const.attr.ICE_DEFENCE, iceDef);
		attribute.add(Const.attr.FIRE_DEFENCE, fireDef);
		attribute.add(Const.attr.THUNDER_DENFENCE, thunderDef);
		
		//PlayerUtil.updateSecondaryAttribute(null, attribute, profession, null);
		
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

	public int getIceAtt() {
		return iceAtt;
	}

	public int getFireAtt() {
		return fireAtt;
	}

	public int getThunderAtt() {
		return thunderAtt;
	}

	public int getIceDef() {
		return iceDef;
	}

	public int getFireDef() {
		return fireDef;
	}

	public int getThunderDef() {
		return thunderDef;
	}

	public int isFrenzy() {
		return isFrenzy;
	}
}
