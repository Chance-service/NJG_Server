package com.guaji.game.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;

@ConfigManager.XmlResource(file = "xml/class_New.xml", struct = "map")
public class NewHeroClassCfg extends ConfigBase{
	/**
	 * 配置id(職業)
	 */
	@Id
	protected final int id;
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
	 *力量成長
	 */
	protected final int StrenghtRate;
	/**
	 *智力成長
	 */
	protected final int IntellectRate;
	/**
	 *敏捷成長
	 */
	protected final int AgilityRate;
	/**
	 *耐力成長
	 */
	protected final int StaminaRate;
	/**
	 * 等級點數
	 */
	protected final int LvPoint;
	/**
	 * 普功次數
	 */
	protected final int NATK;
	/**
	 * 技能連擊次數
	 */
	protected final String SATK;
	/**
	 * 攻擊力
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
	 * 職業獲得魔力補正
	 */
	protected final float ClassCorrection;
	/**
	 * 職業等級限制
	 */
	protected final int LimitLevel;
	/**
	 * Tier層級
	 */
	protected final int Tier;
	/**
	 * 職業名稱
	 */
	protected final String Name;
	/**
	 * 細項屬性
	 */
	protected final String State;
	/**
	 * 战斗属性数据
	 */
	private Attribute attribute;
	/**
	 * 一般技能表
	 */
	private List<Integer> SkillList;
	/**
	 * 被動技能
	 */
	private List<Integer> PassiveList;
	/**
	 * 有連擊的技能對照表<技能ID,連擊數>
	 */
	private Map<Integer,Integer> SATKMap;
	
	public NewHeroClassCfg() {
		this.id = 0;
		this.Skills = null;
		this.Passive = null;
		this.IsMagic = 0;
		this.StrenghtRate = 0;
		this.IntellectRate = 0;
		this.AgilityRate = 0;
		this.StaminaRate = 0;
		this.LvPoint = 0;
		this.NATK = 0;
		this.SATK = null;
		this.ATKSpeed = 0;
		this.ATKMP = 0;
		this.DEFMP = 0;
		this.ClassCorrection = 1.0f;
		this.LimitLevel = 0;
		this.Tier = 0;
		this.Name = null;
		this.State = null;
		this.SkillList =  new LinkedList<>();
		this.PassiveList =  new LinkedList<>();
		this.SATKMap = new HashMap<>();
		this.attribute = new Attribute();
		
	}
	
	public int getid() {
		return this.id;
	}
		
	public boolean IsMagic() {
		return this.IsMagic == 1;
	}
	
	public int getStrenghtRate() {
		return this.StrenghtRate;
	}
	
	public int getIntellectRate() {
		return this.IntellectRate;
	}

	public int getAgilityRate() {
		return this.AgilityRate;
	}
	
	public int getStaminaRate() {
		return this.StaminaRate;
	}

	public int getLvPoint() {
		return this.LvPoint;
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
	
	public float getClassCorrection() {
		return this.ClassCorrection;
	}
	
	public int getLimitLevel() {
		return this.LimitLevel;
	}
	
	public int getTier() {
		return this.Tier;
	}
	
	public String getName() {
		return this.Name;
	}
	
	public List<Integer> getSkillList(){
		return this.SkillList;
	}
	
	public List<Integer> getPassiveList(){
		return this.PassiveList;
	}
	
	public Attribute getattribute() {
		return this.attribute.clone();
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
			String[] Sks = Passive.split(",");
			for (String sid : Sks) {
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
		
		if (!State.isEmpty()) {
			attribute.initByString(State);
		}
		return true;
	}
	
}
