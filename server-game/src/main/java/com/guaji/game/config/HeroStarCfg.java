package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/Hero_Star.xml", struct = "map")
public class HeroStarCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	protected final int ID;
	
	/**
	 *  英雄ID
	 */
	protected final int Hero;
	/**
	 *  星等
	 */
	protected final int Star;
	/**
	 *  消耗道具
	 */
	protected final String Cost;
	/**
	 *  技能
	 */
	protected final String Skills;
	/**
	 *  等級
	 */
	protected final int Level;
	/**
	 *  技能列表
	 */
	protected List<Integer> SkillList;
	/**
	 *  消耗道具型態
	 */
	protected List<ItemInfo> CostList;
	/**
	 *  英雄ID,星數,星等配置表
	 */
	protected static Map<Integer,Map<Integer,HeroStarCfg>> HeroStarMap ;
	
	/**
	 *  英雄ID,星數,星等配置表
	 */
	protected static Map<Integer,Integer> HeroMaxStar ;
	
	/**
	 *  升星獎勵
	 */
	protected final String Awards;
	
	/**
	 *  升星成長
	 */
	protected final float Ratio;

	@Override
	protected boolean assemble() {
		// 技能轉換為列表
		SkillList.clear();
		if (!Skills.isEmpty()) {
			String[] ss = Skills.split(",");
			for(String s : ss) {
				SkillList.add(Integer.valueOf(s.trim()));
			}
		}
		// 消耗道具格式轉換
		if (!Cost.isEmpty()) {
			CostList = ItemInfo.valueListOf(Cost);
		}
		// 取最高的等級
		if (HeroMaxStar.containsKey(Hero)) {
			if (HeroMaxStar.get(Hero) < Star) {
				HeroMaxStar.replace(Hero, Star);
			}
		} else {
			HeroMaxStar.put(Hero,Star);
		}
		
		if (HeroStarMap.containsKey(Hero)) {
			HeroStarMap.get(Hero).put(Star,this);
		} else {
			Map<Integer,HeroStarCfg> aMap = new HashMap<Integer,HeroStarCfg>();
			aMap.put(Star,this);
			HeroStarMap.put(Hero,aMap);
		}
		
		return super.assemble();
	}

	public HeroStarCfg() {
		ID=0;
		Hero =0;
		Star=0;
		Cost="";
		Skills="";
		Level =0;
		Awards = "";
		Ratio = 0.0f;
		HeroStarMap = new HashMap<Integer,Map<Integer,HeroStarCfg>>();
		HeroMaxStar = new HashMap<Integer,Integer>();
		CostList = new ArrayList<ItemInfo>();
		SkillList = new ArrayList<Integer>();
	}
	
	public static HeroStarCfg getHeroStarCfg(int heroId , int star) {
		if (HeroStarMap.containsKey(heroId)) {
			if (HeroStarMap.get(heroId).containsKey(star)) {
				return HeroStarMap.get(heroId).get(star);
			}
		}
		return null;
	}
	
	public static int getHeroMaxStar(int id) {
		if (HeroMaxStar.containsKey(id)) {
			return HeroMaxStar.get(id);
		}
		return 0;
	}

	public int getId() {
		return ID;
	}

	public int getHero() {
		return Hero;
	}
	
	public int getStar() {
		return Star;
	}
	
	public String getCost() {
		return Cost;
	}
	
	public String getAwards(){
		return Awards;
	}
	
	public List<ItemInfo> getCostList() {
		return CostList;
	}
	
	public String getSkills() {
		return Skills;
	}
	
	public List<Integer> getSkillList(){
		return SkillList;
	}
		
	public int getLevel() {
		return Level;
	}
	
	public float getRatio() {
		return Ratio;
	}
}
