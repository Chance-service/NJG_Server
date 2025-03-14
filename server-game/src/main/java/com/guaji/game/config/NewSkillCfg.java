package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.AttrInfoObj;
import com.guaji.game.util.GsConst;


@ConfigManager.XmlResource(file = "xml/skill_New.xml", struct = "map")
public class NewSkillCfg extends ConfigBase {
	@Id
	protected final int id;
	/**
	 * 數值
	 */
	protected final String values;
	/**
	 * 需要角色
	 */
	protected final int role_need;
	/**
	 * 元素影響招式
	 */
	protected final int element;
	/**
	 * 等级
	 */
	protected final int level;
	/**
	 * 施放消耗
	 */
	protected final int cost;
	/**
	 * 冷却回合
	 */
	protected final int cd;
	/**
	 * 技能種類
	 */
	protected final int SkillType;
	/**
	 * 作用人數
	 */
	protected final int Nums;
	/**
	 * 觸發條件
	 * 1：大於自身HP
	 * 2：小於自身HP
	 * 3：大於自身MP
	 * 4：小於自身MP 
	 */
	protected final int TriggerCon;
	/**
	 * 條件百分比
	 */
	protected final double TriggerPer;
//	/**
//	 * 属性数据
//	 */
	private List<AttrInfoObj> attrInfoList;
	/**
	 * 一般數值陣列
	 */
	private List<Double> ValueList;
	
	public NewSkillCfg() {
		id = 0;
		values = "";
		role_need =0;
		element = 0;
		level = 0;
		cost = 0;
		cd = 0;
		SkillType = 0;
		Nums = 0;
		TriggerCon = 0;
		TriggerPer = 0.0;
		this.attrInfoList = new LinkedList<>();
		this.ValueList =  new LinkedList<>();
	}

	public int getId() {
		return id;
	}
	
	public int getRole_need() {
		return role_need;
	}

	public int getLevel() {
		return level;
	}
	
	public int getElement() {
		return element;
	}

	public int getCost() {
		return cost;
	}

	public int getCd() {
		return cd;
	}
	
	public int getSkillType() {
		return SkillType;
	}
	
	public int getNums() {
		return Nums;
	}
	/**
	 * 觸發條件
	 * 1：大於自身HP
	 * 2：小於自身HP
	 * 3：大於自身MP
	 * 4：小於自身MP 
	 */
	public int getTriggerCon() {
		return TriggerCon;
	}
	
	public double getTriggerPer() {
		return TriggerPer;
	}
		
	public List<Double> getValues(){
		return ValueList;
	}
	
	@Override
	protected boolean assemble() {
		
		ValueList.clear();
		if (!values.isEmpty()) {
			if (SkillType == GsConst.SkillType.PASSIVE) {//被動永久屬性
				
				attrInfoList = AttrInfoObj.valueOfs(values);
	
			} else {
				String[] Sks = values.split(",");
				for (String sid : Sks) {
					ValueList.add(Double.valueOf(sid.trim()));
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

	public List<AttrInfoObj> getAttrInfoList() {
		if (getSkillType() == GsConst.SkillType.PASSIVE) {
		  return attrInfoList;
		}
		return null;
	}	
}
