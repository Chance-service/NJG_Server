package com.guaji.game.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.protocol.Const;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.PlayerUtil;

@ConfigManager.XmlResource(file = "xml/Tower_Monster.xml", struct = "map")
public class TowerMonsterCfg extends ConfigBase {
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
	 * 力量成長加權
	 */
	private final int  StrenghtRate;
	/**
	 * 智力成長加權
	 */
	private  final int  IntellectRate;
	/**
	 * 敏捷成長加權
	 */
	private final int  AgilityRate;
	/**
	 * 體力成長加權
	 */
	private final int  StaminaRate;
	
	/**
	 * 属性数据
	 */
	// Attribute attribute;
	
	public TowerMonsterCfg() {
		this.id = 0;
		this.Class = 0;
		this.Element = 0;
		this.Skills = null;
		this.Passive = null;
		this.IsMagic = 0;
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
		this.StrenghtRate = 0;
		this.IntellectRate = 0;
		this.AgilityRate = 0;
		this.StaminaRate = 0;
		
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
	
	public Attribute getAttribute(int level,float ratio) {
		Attribute attribute = new Attribute();
		int value = 0;
		double dvalue = 0.0f;
		value = Math.round((Strenght + StrenghtRate*(level -1))*ratio);
		attribute.add(Const.attr.STRENGHT, value);
		value = Math.round((Intellect + IntellectRate*(level -1))*ratio);
		attribute.add(Const.attr.INTELLECT, value);
		value = Math.round((Agility + AgilityRate*(level -1))*ratio);
		attribute.add(Const.attr.AGILITY, value);
		value = Math.round((Stamina + StaminaRate*(level -1))*ratio);
		attribute.add(Const.attr.STAMINA, value);
		value = attribute.getValue(Const.attr.STAMINA)*10 + HP;
		attribute.add(Const.attr.HP, value);
		
		//113
		dvalue = attribute.getValue(Const.attr.STRENGHT)*100+attribute.getValue(Const.attr.AGILITY)*25+Attack*100;
		dvalue = dvalue / 100;
		dvalue = Math.round(dvalue * 1.0f) / 1.0f;
		attribute.add(Const.attr.ATTACK_attr,(int)dvalue);
		
		//114
		dvalue = attribute.getValue(Const.attr.INTELLECT)*100+attribute.getValue(Const.attr.AGILITY)*25+Magic*100;
		dvalue = dvalue / 100;
		dvalue = Math.round(dvalue * 1.0f) / 1.0f;
		attribute.add(Const.attr.MAGIC_attr,(int)dvalue);
		
		//106
		dvalue = attribute.getValue(Const.attr.STRENGHT)*30+attribute.getValue(Const.attr.STAMINA)*50+PhyDef*100;
		dvalue = dvalue / 100;
		dvalue = Math.round(dvalue * 1.0f) / 1.0f;
		attribute.add(Const.attr.PHYDEF,(int)dvalue);
		//107
		dvalue = attribute.getValue(Const.attr.INTELLECT)*30+attribute.getValue(Const.attr.STAMINA)*50+MagDef*100;
		dvalue = dvalue / 100;
		dvalue = Math.round(dvalue * 1.0f) / 1.0f;
		attribute.add(Const.attr.MAGDEF,(int)dvalue);
		
		//109
		dvalue = attribute.getValue(Const.attr.STRENGHT)*20+attribute.getValue(Const.attr.INTELLECT)*20+
				attribute.getValue(Const.attr.AGILITY)*30 + Hit*100;
		dvalue = dvalue / 100;
		dvalue = Math.round(dvalue * 1.0f) / 1.0f;
		attribute.add(Const.attr.HIT,(int)dvalue);
		
		//110
		dvalue = attribute.getValue(Const.attr.STRENGHT)*15+attribute.getValue(Const.attr.INTELLECT)*15+
				attribute.getValue(Const.attr.AGILITY)*30-attribute.getValue(Const.attr.STAMINA)*5 + Dodge*100;
		dvalue = dvalue / 100;
		dvalue = Math.round(dvalue * 1.0f) / 1.0f;
		attribute.add(Const.attr.DODGE,(int)dvalue);
		
		// 108
		dvalue = attribute.getValue(Const.attr.AGILITY)*50+Critical*100;
		dvalue = dvalue / 100;
		dvalue = Math.round(dvalue * 1.0f) / 1.0f;
		attribute.add(Const.attr.CRITICAL,(int)dvalue);
		
		//111
		dvalue = attribute.getValue(Const.attr.STAMINA)*20*+CriResist*100;
		dvalue = value / 100;
		dvalue = Math.round(dvalue * 1.0f) / 1.0f;
		attribute.add(Const.attr.RESILIENCE,(int)dvalue);
		
		attribute.add(Const.attr.BUFF_RETURN_BLOOD, RecoverHP);
		attribute.add(Const.attr.BUFF_CRITICAL_DAMAGE, CriDamage);
		attribute.add(Const.attr.BUFF_PHYDEF_PENETRATE, PhyPenetrate);
		attribute.add(Const.attr.BUFF_MAGDEF_PENETRATE, MagPenetrate);		
		attribute.add(Const.attr.BUFF_AVOID_CONTROL, Immunity);
		
		return attribute;
	}
	
	public List<Integer> getSkillList(){
		return this.SkillList;
	}
	
	public int getFight(int level,float ratio) {
		boolean isMagic = (this.IsMagic == 1);
		Attribute attribute = getAttribute(level,ratio);
		float fightValue = PlayerUtil.getFightValue(attribute,isMagic);
		return (int)fightValue;
	}
	
	@Override
	protected boolean assemble() {
		SkillList.clear();
		if (!Skills.isEmpty()) {
			SkillList = GameUtil.StringToIntList(Skills);
		}
		
		PassiveList.clear();
		if (!Passive.isEmpty()) {

			PassiveList = GameUtil.StringToIntList(Passive);
			
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
		
		return true;
	}
	
}
