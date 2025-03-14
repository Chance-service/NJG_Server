package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;

@ConfigManager.XmlResource(file = "xml/Hero_NG.xml", struct = "map")
public class Hero_NGListCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 類型 GsConst.RoleType 1.主角色 2.英雄 4.精靈
	 */ 
	protected final int Type;
	/**
	 * 技能索引
	 */
	protected final String Skills;
	/**
	 * 被動
	 */
	protected final String Passive;
	/**
	 * 職業
	 */
	protected final int Job;
	/**
	 * 初始星等
	 */
	protected final int Star;
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
	 * 細項屬性
	 */
	protected final String State;
	/**
	 * 
	 */
	protected final String SKMP;
	/**
	 * 職業獲得魔力補正
	 */
	protected final float ClassCorrection;
	/**
	 * 可擁有皮膚
	 */
	protected final String Skin;
	/**
	 * 激活条件，是否需要材料激活（0不需要 1需要）
	 */
	protected final int CostType;
	/**
	 * 解锁消耗
	 */
	protected final String Cost;
	/**
	 *屬性
	 */
	protected final int Attr;
	/**
	 * 發言權比重
	 */
	protected final int Sayso;
	/**
	 * 战斗属性数据
	 */
	private Attribute attribute;
//	/**
//	 * 一般技能表
//	 */
//	private List<Integer> SkillList;
//	/**
//	 * 被動技能
//	 */
//	private List<Integer> PassiveList;
	/**
	 * 有連擊的技能對照表<技能ID,連擊數>
	 */
	private Map<Integer,Integer> SATKMap;
	
	/**
	 * 可擁有皮膚列表
	 */
	protected final Set<Integer>SkinList;
	/**
	 * 玩家角色資訊索引
	 */
	private static int playerCfgIdx;
	/**
	 * 免費角色資訊索引
	 */
	private static List<Integer> freeCfgIdx = new ArrayList<>();

	
	public int getId() {
		return this.id;
	}
	
	public int getType() {
		return this.Type;
	}
	
	public int getJob() {
		return this.Job;
	}
	
	public int getStar() {
		return this.Star;
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
	
	public int getCostType() {
		return this.CostType;
	}
	
	public String getCost() {
		return this.Cost;
	}
	
	public int getattr() {
		return this.Attr;
	}
	
	public int getSayso() {
		return Sayso;
	}

	public Attribute getattribute() {
		return this.attribute.clone();
	}
	
	public static int getPlayerCfgIdx() {
		return playerCfgIdx;
	}
	
	public static List<Integer> getFreeCfgIdx() {
		return freeCfgIdx;
	}
	
	public static boolean isFreeIdx(int idx) {
		return (freeCfgIdx.contains(idx));
	}
	
	public String getSKMP() {
		return SKMP;
	}

	/**
	 * 是為該角色的Skin
	 * @param skinId
	 * @return
	 */
	public boolean isRoleSkin(int skinId) {
		if (SkinList.contains(skinId)) {
			return true;
		}
		return false;
	}
	
	
	public Hero_NGListCfg() {
		id = 0;
		this.Type = 0;
		this.Skills = null;
		this.Passive = null;
		this.Job = 0;
		this.Star = 0;
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
		this.State = null;
		this.SKMP = null;
		this.ClassCorrection = 1.0f;
		this.Skin ="";
		this.CostType = 0;
		this.Cost ="";
		this.Attr = 0;
		this.Sayso = 0;
		this.SATKMap = new HashMap<>();
		this.attribute = new Attribute();
		this.SkinList = new HashSet<>();
	}
	
	@Override
	protected boolean assemble() {
	
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
		
		SkinList.clear();
		if (!Skin.isEmpty()) {
			String[] SkinStr = Skin.split(",");
			for (String S_S : SkinStr) {
				SkinList.add(Integer.valueOf(S_S.trim()));
			}
		}
		
		// record player Role index
		if (getType() == 1) {
			playerCfgIdx = getId();
		}
		
		// record free Role
		if ((getCostType() == 0)&&(!freeCfgIdx.contains(getId()))) {
			freeCfgIdx.add(getId());
		}
		
		return true;
	}
		
	/**
	 * 获取佣兵所有佣兵
	 * 
	 * @return
	 */
	public static Map<Integer, Hero_NGListCfg> getRoleInfoMap() {
		Map<Integer, Hero_NGListCfg> map = new HashMap<Integer, Hero_NGListCfg>();

		Map<Object, Hero_NGListCfg> roleMap = ConfigManager.getInstance().getConfigMap(Hero_NGListCfg.class);
		for (Hero_NGListCfg role : roleMap.values()) {
				map.put(role.getId(), role);
		}
		return map;
	}
	
}
