package com.guaji.game.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.protocol.Const;
import com.guaji.game.util.PlayerUtil;

@ConfigManager.XmlResource(file = "xml/monster_New.xml", struct = "map")
public class NewMonsterCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 職業
	 */
	protected final int Class;
	/**
	 * 元素
	 */
	protected final int Element ;
	/**
	 * 技能索引
	 */
	protected final String Skills;
	/**
	 * 被動
	 */
	protected final String Passive;
	/**
	 * 是否魔法攻擊力為主
	 */
	protected final int IsMagic;
	/**
	 * 等級
	 */
	protected final int Level;
	/**
	 *力量
	 */
	protected final int Strenght;
	/**
	 *智力
	 */
	protected final int Intellect;
	/**
	 *敏捷
	 */
	protected final int Agility;
	/**
	 *耐力
	 */
	protected final int Stamina;
	/**
	 * HP
	 */
	protected final int HP;
	/**
	 * 攻擊力
	 */
	protected final int Attack;
	/**
	 * 魔法攻擊力
	 */
	protected final int Magic;
	/**
	 *物理穿透
	 */
	protected final int PhyPenetrate;
	/**
	 *魔法穿透
	 */
	protected final int MagPenetrate;
	/**
	 *物理防禦
	 */
	protected final int PhyDef;
	/**
	 *魔法防禦
	 */
	protected final int MagDef;
	/**
	 *回血
	 */
	protected final int RecoverHP;
	/**
	 *暴擊傷害 (基礎傷害加上得乘百分率)
	 */
	protected final int CriDamage;
	/**
	 *抗暴值
	 */	
	protected final int CriResist;
	/**
	 *暴擊出現率
	 */
	protected final int Critical;
	/**
	 *命中值
	 */
	protected final int Hit;
	/**
	 *閃避值
	 */
	protected final int Dodge;
	/**
	 *免疫
	 */
	protected final int Immunity;
	/**
	 *普攻次數
	 */
	protected final int NATK;
	/**
	 *對應技能攻擊次數
	 */
	protected final String SATK;
	/**
	 *攻擊速度
	 */
	protected final int ATKSpeed;
	/**
	 *攻擊獲得魔力
	 */
	protected final int ATKMP;
	/**
	 *受擊獲得魔力
	 */
	protected final int DEFMP;
	/**
	 * 
	 */
	protected final String SKMP;
	/**
	 *攻擊獲得魔力職業補正
	 */
	protected final float ClassCorrection;
	/**
	 * 主動技列表
	 */
	private List<Integer> SkillList;
	/**
	 * 被動技列表
	 */
	private List<Integer> PassiveList;
	/**
	 * 有連擊的技能對照表<技能ID,連擊數>
	 */
	private Map<Integer,Integer> SATKMap;
	
	/**
	 * 属性数据
	 */
	Attribute attribute;
	
	public NewMonsterCfg() {
		this.id = 0;
		this.Class = 0;
		this.Element = 0;
		this.Skills = null;
		this.Passive = null;
		this.IsMagic = 0;
		this.Level =0;
		this.Strenght = 0;
		this.Intellect = 0;
		this.Agility = 0;
		this.Stamina = 0;
		this.HP = 0;
		this.Attack = 0;
		this.Magic = 0;
		this.PhyPenetrate = 0;
		this.MagPenetrate = 0;
		this.PhyDef = 0;
		this.MagDef = 0;
		this.RecoverHP = 0;
		this.CriDamage = 0;
		this.CriResist = 0;
		this.Critical = 0;
		this.Hit = 0;
		this.Dodge = 0;
		this.Immunity = 0;
		this.NATK = 0;
		this.SATK = null;
		this.ATKSpeed = 0;
		this.ATKMP = 0;
		this.DEFMP = 0;
		this.SKMP = null;
		this.ClassCorrection = 1.0f;
		this.SkillList =  new LinkedList<>();
		this.PassiveList = new LinkedList<>();
		this.SATKMap = new HashMap<>();
		
		this.attribute = new Attribute();
	}
	
	public int getid() {
		return this.id;
	}
	
	public int getProfession() {
		return Class;
	}
	
	public int getElement() {
		return Element;
	}
	
	public List<Integer> getPassiveList() {
		return this.PassiveList;
	}
	
	public int getIsMagic() {
		return this.IsMagic;
	}
	
	public int getHP() {
		return this.HP;
	}
	
	public int getAttack() {
		return this.Attack;
	}
	
	public int getMagic() {
		return this.Magic;
	}
	
	public int getATKSpeed() {
		return this.ATKSpeed;
	}
	
	public int getNATK() {
		return this.NATK;
	}
	
	public int getATKMP() {
		return this.ATKMP;
	}

	public int getDEFMP() {
		return this.DEFMP;
	}
	
	public int getSATK(int skillId) {
		if (SATKMap.containsKey(skillId)) {
			return SATKMap.get(skillId);
		}
		return 1;
	}
	

	public String getSKMP() {
		return SKMP;
	}

	public float getClassCorrection() {
		return this.ClassCorrection;
	}
	
	public Attribute getAttribute() {
		return attribute;
	}
	
	public List<Integer> getSkillList(){
		return this.SkillList;
	}
	
	public int getFight() {
//		int ATTACK = this.IsMagic == 0 ? attribute.getValue(Const.attr.ATTACK_attr):attribute.getValue(Const.attr.MAGIC_attr);
//		float fightValue = (ATTACK*30
//				+ attribute.getValue(Const.attr.PHYDEF)*10
//				+ attribute.getValue(Const.attr.MAGDEF)*10
//				+ attribute.getValue(Const.attr.HP)*5
//				+ attribute.getValue(Const.attr.RESILIENCE) *10
//				+ attribute.getValue(Const.attr.BUFF_PHYDEF_PENETRATE)*10
//				+ attribute.getValue(Const.attr.CRITICAL) *75
//				+ attribute.getValue(Const.attr.BUFF_CRITICAL_DAMAGE) *1000
//				+ attribute.getValue(Const.attr.HIT) *50
//				+ attribute.getValue(Const.attr.DODGE) *10);
//
//		return (int) fightValue;
		boolean isMagic = (this.IsMagic == 1);
		float fightValue = PlayerUtil.getFightValue(attribute,isMagic);
		return (int)fightValue;
	}
	
	@Override
	protected boolean assemble() {
		SkillList.clear();
		if (!Skills.isEmpty()) {
			String[] Sks = Skills.split(",");
			for (String sid : Sks) {
				SkillList.add(Integer.valueOf(sid.trim()));
			}
		}
		
		PassiveList.clear();
		if (!Passive.isEmpty()) {
			String[] Pas = Passive.split(",");
			for (String sid : Pas) {
				PassiveList.add(Integer.valueOf(sid.trim()));
			}
		}
		
		SATKMap.clear();
		if (!SATK.isEmpty()) {
			String[] SATKStr = SATK.split(",");
			for (String S_S :SATKStr) {
				String[] S_C = S_S.split("_");
				int skillId = Integer.valueOf(S_C[0].trim());
				int count = Integer.valueOf(S_C[1].trim());
				SATKMap.put(skillId,count);
			}
		}
		
		attribute.clear();
		attribute.add(Const.attr.STRENGHT, Strenght);
		attribute.add(Const.attr.INTELLECT, Intellect);
		attribute.add(Const.attr.AGILITY, Agility);
		attribute.add(Const.attr.STAMINA, Stamina);
		attribute.add(Const.attr.HP, HP);
		attribute.add(Const.attr.ATTACK_attr, Attack);
		attribute.add(Const.attr.MAGIC_attr, Magic);
		attribute.add(Const.attr.BUFF_PHYDEF_PENETRATE, PhyPenetrate);
		attribute.add(Const.attr.BUFF_MAGDEF_PENETRATE, MagPenetrate);
		attribute.add(Const.attr.PHYDEF, PhyDef);
		attribute.add(Const.attr.MAGDEF, MagDef);
		attribute.add(Const.attr.BUFF_AVOID_CONTROL, Immunity);
		attribute.add(Const.attr.BUFF_RETURN_BLOOD, RecoverHP);
		attribute.add(Const.attr.BUFF_CRITICAL_DAMAGE, CriDamage);
		attribute.add(Const.attr.RESILIENCE, CriResist);
		attribute.add(Const.attr.CRITICAL, Critical);
		attribute.add(Const.attr.HIT, Hit);
		attribute.add(Const.attr.DODGE, Dodge);
		
		//運算第二屬性
		//PlayerUtil.updateSecondaryAttribute(null, attribute, this.Class, null);

		return true;
	}

	public int getLevel() {
		return Level;
	}
	
}
