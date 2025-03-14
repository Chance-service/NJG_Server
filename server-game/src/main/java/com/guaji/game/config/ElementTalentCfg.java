package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.protocol.Const;

@ConfigManager.XmlResource(file = "xml/elementTalent.xml", struct = "map")
public class ElementTalentCfg extends ConfigBase {
	
	/**
	 * 每中元素对应的属性;
	 * 
	 * @author qianhang
	 *
	 */
	public class TalentConfig {
		private int talentLevel;
		private int stageLevel;
		private int AttCost;
		private int Att;
		
		public TalentConfig(int talentLevel, int stageLevel, int attCost,int att) {
			this.talentLevel = talentLevel;
			this.stageLevel = stageLevel;
			AttCost = attCost;
			Att = att;
		}
		
		public int getTalentLevel() {
			return talentLevel;
		}
		public int getStageLevel() {
			return stageLevel;
		}
		public int getAttCost() {
			return AttCost;
		}
		public int getAtt() {
			return Att;
		}
		public void setTalentLevel(int talentLevel) {
			this.talentLevel = talentLevel;
		}
		public void setStageLevel(int stageLevel) {
			this.stageLevel = stageLevel;
		}
		public void setAttCost(int attCost) {
			AttCost = attCost;
		}
		public void setAtt(int att) {
			Att = att;
		}
		
	}
	
	/** 等级 */
	@Id
	private final int talentLevel;
	/** 阶段 */
	private final int stageLevel;
	
	private final int iceAttCost;
	
	private final int iceAtt;
	
	private final int iceDefCost;
	
	private final int iceDef;
	
	private final int fireAttCost;
	
	private final int fireAtt;
	
	private final int fireDefCost;
	
	private final int fireDef;
	
	private final int thunderAttCost;
	
	private final int thunderAtt;
	
	private final int thunderDefCost;
	
	private final int thunderDef;
	
	private static final int MAX_TALENT_LEVEL = 100;
	
	private static Map<Integer, Map<Const.attr, TalentConfig>> talentMap = new HashMap<Integer, Map<Const.attr,TalentConfig>>();
	
	public ElementTalentCfg() {
		talentLevel = 0;
		stageLevel = 0;
		iceAttCost = 0;
		iceAtt = 0;
		iceDefCost = 0;
		iceDef = 0;
		fireAttCost = 0;
		fireAtt = 0;
		fireDefCost = 0;
		fireDef = 0;
		thunderAttCost = 0;
		thunderAtt = 0;
		thunderDefCost = 0;
		thunderDef = 0;
	}
	public int getTalentLevel() {
		return talentLevel;
	}
	public int getStageLevel() {
		return stageLevel;
	}
	public int getIceAttCost() {
		return iceAttCost;
	}
	public int getIceAtt() {
		return iceAtt;
	}
	public int getIceDefCost() {
		return iceDefCost;
	}
	public int getIceDef() {
		return iceDef;
	}
	public int getFireAttCost() {
		return fireAttCost;
	}
	public int getFireAtt() {
		return fireAtt;
	}
	public int getFireDefCost() {
		return fireDefCost;
	}
	public int getFireDef() {
		return fireDef;
	}
	public int getThunderAttCost() {
		return thunderAttCost;
	}
	public int getThunderAtt() {
		return thunderAtt;
	}
	public int getThunderDefCost() {
		return thunderDefCost;
	}
	public int getThunderDef() {
		return thunderDef;
	}
	
	/**
	 * 配置加载完成调用, 便于上层进行数据按照应用层要求重新构建 
	 * 备注: 返回true表示格式正确, 否则格式错误不添加进入
	 */
	protected boolean assemble() {
		Map<Const.attr, TalentConfig> talentConfigMap = new HashMap<Const.attr, TalentConfig>();
		talentConfigMap.put(Const.attr.ICE_ATTACK, new TalentConfig(getTalentLevel(), 
																		 getStageLevel(),
																		 getIceAttCost(),
																		 getIceAtt()));
		
		talentConfigMap.put(Const.attr.ICE_DEFENCE, new TalentConfig(getTalentLevel(), 
																		 getStageLevel(),
																		 getIceDefCost(),
																		 getIceDef()));
																
		talentConfigMap.put(Const.attr.FIRE_ATTACK, new TalentConfig(getTalentLevel(), 
																		 getStageLevel(),
																		 getFireAttCost(),
																		 getFireAtt()));
		
		talentConfigMap.put(Const.attr.FIRE_DEFENCE, new TalentConfig(getTalentLevel(), 
																		 getStageLevel(),
																		 getFireDefCost(),
																		 getFireDef()));
																
		talentConfigMap.put(Const.attr.THUNDER_ATTACK, new TalentConfig(getTalentLevel(), 
																		 getStageLevel(),
																		 getThunderAttCost(),
																		 getThunderAtt()));
		
		talentConfigMap.put(Const.attr.THUNDER_DENFENCE, new TalentConfig(getTalentLevel(), 
																		 getStageLevel(),
																		 getThunderDefCost(),
																		 getThunderDef()));
		
		talentMap.put(getTalentLevel(), talentConfigMap);

		return true;
	}
	
	/**
	 * 检测有消息
	 * @return
	 */
	protected boolean checkValid() {
		return true;
	}
	
	/**
	 * 获得升级属性的消耗和加成;
	 * 
	 * @param attr
	 * @param curLevel
	 * @param targetLevel
	 * @return
	 */
	public static List<Integer> getCost(Const.attr attr, int curLevel, int targetLevel) {
		int cost = 0;
		int att = 0;
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i < targetLevel + 1; i++) {
			Map<Const.attr, TalentConfig> map = talentMap.get(i);
			TalentConfig talentConfig = map.get(attr);
			att += talentConfig.getAtt();
			if (i > curLevel) {
				cost += talentConfig.getAttCost();
			}
		}
		list.add(cost);
		list.add(att);
		return list;
	}
	
	/**
	 * 返还清空属性的所有真气值;
	 * 
	 * @param attr
	 * @param level
	 * @return
	 */
	public static int backCost(Const.attr attr,  int level) {
		int cost = 0;
		for (int i = 1; i <= level; i++) {
			Map<Const.attr, TalentConfig> map = talentMap.get(i);
			TalentConfig talentConfig = map.get(attr);
			cost += talentConfig.getAttCost();
		}
		return cost;
	}
	
	public static int getMaxTalentLevel() {
		return MAX_TALENT_LEVEL;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		talentMap.clear();
	}
}
