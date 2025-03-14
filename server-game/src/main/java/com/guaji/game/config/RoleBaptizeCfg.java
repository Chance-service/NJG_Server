package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.protocol.Const;

/**
 * 佣兵洗属性
 * 
 * @author xulin
 * 
 */
@ConfigManager.XmlResource(file = "xml/roleBaptize.xml", struct = "map")
public class RoleBaptizeCfg extends ConfigBase {

	@Id
	protected final int level;

	protected final int strenght1;

	protected final int agility1;

	protected final int intellect1;

	protected final int stamina1;

	protected final int strenght2;

	protected final int agility2;

	protected final int intellect2;

	protected final int stamina2;

	protected final int strenght3;

	protected final int agility3;

	protected final int intellect3;

	protected final int stamina3;

	public RoleBaptizeCfg() {
		this.level = 0;
		this.strenght1 = 0;
		this.agility1 = 0;
		this.intellect1 = 0;
		this.stamina1 = 0;

		this.strenght2 = 0;
		this.agility2 = 0;
		this.intellect2 = 0;
		this.stamina2 = 0;

		this.strenght3 = 0;
		this.agility3 = 0;
		this.intellect3 = 0;
		this.stamina3 = 0;
	}

	/**
	 * 根据职业、属性类型获得满点值
	 * 
	 * @param prof
	 * @param attrType
	 * @return int
	 */
	public int getFullValue(int prof, int attrType) {
		int[] result = getFullValue(prof);
		switch (attrType) {
		case Const.attr.STRENGHT_VALUE:
			return result[0];
		case Const.attr.AGILITY_VALUE:
			return result[1];
		case Const.attr.INTELLECT_VALUE:
			return result[2];
		case Const.attr.STAMINA_VALUE:
			return result[3];
		default:
			break;
		}
		return 0;
	}

	/**
	 * 根据职业获得满点值
	 * 
	 * @param prof
	 * @return int[] 4个值 strenght agility intellect stamina
	 */
	public int[] getFullValue(int prof) {
		int[] result = new int[4];
		switch (prof) {
		case Const.prof.MERCENARY_WARRIOR_VALUE:
			result[0] = strenght1;
			result[1] = agility1;
			result[2] = intellect1;
			result[3] = stamina1;
			break;
		case Const.prof.MERCENARY_HUNTER_VALUE:
			result[0] = strenght2;
			result[1] = agility2;
			result[2] = intellect2;
			result[3] = stamina2;
			break;
		case Const.prof.MERCENARY_MAGIC_VALUE:
			result[0] = strenght3;
			result[1] = agility3;
			result[2] = intellect3;
			result[3] = stamina3;
			break;
		default:
			break;
		}
		return result;
	}

	public int getLevel() {
		return level;
	}

	public int getStrenght1() {
		return strenght1;
	}

	public int getAgility1() {
		return agility1;
	}

	public int getIntellect1() {
		return intellect1;
	}

	public int getStamina1() {
		return stamina1;
	}

	public int getStrenght2() {
		return strenght2;
	}

	public int getAgility2() {
		return agility2;
	}

	public int getIntellect2() {
		return intellect2;
	}

	public int getStamina2() {
		return stamina2;
	}

	public int getStrenght3() {
		return strenght3;
	}

	public int getAgility3() {
		return agility3;
	}

	public int getIntellect3() {
		return intellect3;
	}

	public int getStamina3() {
		return stamina3;
	}

	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
