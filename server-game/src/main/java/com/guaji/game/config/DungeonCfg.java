package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.DropItems;

@ConfigManager.XmlResource(file = "xml/dungeon.xml", struct = "map")
public class DungeonCfg extends ConfigBase {
	@Id
	private final int id;
	// 星级
	private final int star;
	// 種類
	private final int type;
	// 戰鬥力
	private final int BP;
	// 怪物id列表
	private final String monsterIds;
	private List<Integer> monsterIdList;
	// 掉落
	private final String dropItems;
	private DropItems items;

	// 副本限制等级
	private final int limitLevel;

	/**
	 *  星期幾開啟 0.日 1.一 2. 二.....
	 */
	private final String openday;
	
	private static Map<Integer,List<Integer>> WeekdayMapCfg;
	
	private static Map<Integer,List<DungeonCfg>> typeMapCfg;
	
	private static Set<Integer> allType;
	
	private static  Map<Integer,Integer> typeMaxStar;
	
	public DungeonCfg() {
		this.id = 0;
		this.monsterIds = "";
		this.star = 0;
		this.type = 0;
		this.BP = 0;
		this.dropItems = "";
		this.limitLevel = 0;
		this.openday = "";
		this.items = new DropItems();
		monsterIdList = new ArrayList<Integer>();
		WeekdayMapCfg = new HashMap<>();
		typeMapCfg = new HashMap<>();
		typeMaxStar = new HashMap<>();
		allType = new HashSet<>();
	}

	public int getId() {
		return id;
	}

	public int getStar() {
		return star;
	}
	
	public int getType() {
		return type;
	}
	
	public int getBP() {
		return BP;
	}

	public String getMonsterIds() {
		return monsterIds;
	}

	public int getLimitLevel() {
		return limitLevel;
	}
	
	public void setMonsterIdList(List<Integer> monsterIdList) {
		this.monsterIdList = monsterIdList;
	}

	@Override
	protected boolean assemble() {
		if (monsterIds.equals("")) {
			return false;
		}

		for (String monsterIdStr : monsterIds.split(",")) {
			monsterIdList.add(Integer.valueOf(monsterIdStr));
		}

		if (!items.initByString(dropItems)) {
			items = new DropItems();
		}		
		int day = 0 ;
		if (!openday.isEmpty()){
			String [] ss = openday.split(",");
			for (String dayStr : ss ) {
				day = Integer.valueOf(dayStr.trim());
				//星期幾開啟 0.日 1.一 2. 二.....
				if ((day >= 0) && (day <= 6)) {
					if (WeekdayMapCfg.containsKey(day)) {
						WeekdayMapCfg.get(day).add(this.getId());
					} else {
						List<Integer> list = new ArrayList<>();
						list.add(this.getId());
						WeekdayMapCfg.put(day, list);
					}
				}
			}
		}
		
		// get max star
		if (typeMaxStar.containsKey(type)) {
			if (typeMaxStar.get(type) < star) {
				typeMaxStar.replace(type, star);
			}
		} else {
			typeMaxStar.put(type, star);
		}
		
		if (!allType.contains(type)) {
			allType.add(type);
		}
		
		if (typeMapCfg.containsKey(type)) {
			typeMapCfg.get(type).add(this);
		} else {
			List<DungeonCfg> list = new ArrayList<DungeonCfg>();
			list.add(this);
			typeMapCfg.put(type, list);
		}
		
		return true;
	}

	public List<Integer> getMonsterIdList() {
		return monsterIdList;
	}

	public DropItems getItems() {
		return items;
	}
	
	public String getDropItems() {
		return dropItems;
	}

	/**
	 * 根据挑战剩余次数和参战人数，获取奖励
	 */
	public DropItems getDropItemInfo() {
		DropItems dropItems = new DropItems();
		dropItems = this.items;
		return dropItems;
	}
	
	/**
	 * 返回今天星期幾的地圖
	 * 
	 * @param star
	 * @return
	 */
	public static List<Integer> getWeekdayOfCfgId(int days) {
		return WeekdayMapCfg.get(days);
	}
	
	/**
	 * 返回該種類的地圖訊息
	 * 
	 * @param star
	 * @return
	 */
	public static List<DungeonCfg> getTypeMapCfg(int type) {
		return typeMapCfg.get(type);
	}
	
	/**
	 * 取該種類對應星等地圖資訊
	 */
	
	public static DungeonCfg getMagCfgByStar(int type, int star){
		for (DungeonCfg cfg :getTypeMapCfg(type)) {
			if (cfg.getStar() == star) {
				return cfg;
			}
		}
		return null;
	}
	/**
	 * 取得所有種類
	 * @return
	 */
	public static Set<Integer> getAllType(){
		return allType;
	}
	
	/**
	 * 取得該類型最大星等
	 */
	
	public static int getMaxStarByType(int type) {
		if (typeMaxStar.containsKey(type)) {
			return typeMaxStar.get(type);
		}
		return 0;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
