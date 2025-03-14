package com.guaji.game.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.config.ConfigBase;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.protocol.Const;

/**
 * 账号升级、转生基础属性数据源
 * 注：子类必须重写mapKey()函数
 */
public class LevelAttributes extends ConfigBase {

	/**
	 * 职业等级字符串映射
	 */
	private static Map<String, LevelAttributes> attributesMap = new ConcurrentHashMap<String, LevelAttributes>();
	
	/**
	 * 职业
	 */
	protected final int profession;
	
	/**
	 * 等级
	 */
	protected final int level;
	
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
	 * 属性数据
	 */
	Attribute attribute;

	public LevelAttributes() {
		profession = 0;
		level = 0;
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

		attribute = new Attribute();
	}

	public int getProfession() {
		return profession;
	}

	public int getLevel() {
		return level;
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

	public Attribute getAttribute() {
		return attribute;
	}

	/**
	 * 获取基础属性数据
	 * 
	 * @param profession 职业
	 * @param rebirthStage 转生等阶
	 * @param level 当前等级
	 * @return
	 */
	public static final LevelAttributes getProfLevelAttr(int profession, final int rebirthStage, int level) {
		String key = profession + "-" + rebirthStage + "-" + level;
		return attributesMap.get(key);
	}

	@Override
	protected boolean assemble() {
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
		attribute.add(Const.attr.HIT, hit);
		attribute.add(Const.attr.CRITICAL, critical);
		attribute.add(Const.attr.DODGE, dodge);
		attribute.add(Const.attr.RESILIENCE, resilience);
		attribute.add(Const.attr.RECOVERMP, recoverMp);
		
		attributesMap.put(this.mapKey(), this);
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
		attributesMap.clear();
	}
	
	/**
	 * 转生静态key
	 * 格式：职业-转生等阶-配置等级
	 */
	public String mapKey() {
		return null;
	}
 }
