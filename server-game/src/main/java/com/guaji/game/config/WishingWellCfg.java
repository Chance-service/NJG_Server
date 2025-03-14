package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

@ConfigManager.XmlResource(file = "xml/WishingWell147.xml", struct = "map")
public class WishingWellCfg extends ConfigBase {
	
	public static final int Sun = 1;
	public static final int Moon = 2;
	public static final int Star = 3;

	/**
	 * 配置id;
	 */
	@Id
	private final int id;
	/**
	 * 輪盤類型;
	 */
	private final int type;
	/**
	 * 獎勵;
	 */
	private final String item ;
	/**
	 * 權重;
	 */
	private final int weight;
	/**
	 * 第幾獎項;
	 */
	private final int area;
	/**
	 * 各類型的輪子分類
	 */
	private static Map<Integer,HashMap<Integer,List<WishingWellCfg>>> WellsMap = new HashMap<Integer,HashMap<Integer,List<WishingWellCfg>>>();

	public WishingWellCfg() {
		// 初始化值
		this.id = 0;
		this.type = 0;
		this.item = "";
		this.weight = 0;
		this.area = 0;
	}


	public static WishingWellCfg getWingCfgByKey(int key) {
		WishingWellCfg wellCfg = ConfigManager.getInstance().getConfigByKey(WishingWellCfg.class, key);
		return wellCfg;
	}
	
	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public String getItem() {
		return item;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public int getArea() {
		return area;
	}

	@Override
	protected boolean assemble() {
		if (WellsMap.containsKey(getType())) {
			if (WellsMap.get(getType()).containsKey(getArea())) {
				WellsMap.get(getType()).get(getArea()).add(this);
			} else {
				List<WishingWellCfg> aList = new ArrayList<WishingWellCfg>();
				aList.add(this);
				WellsMap.get(getType()).put(getArea(), aList);
			}
		} else {
			List<WishingWellCfg> aList = new ArrayList<WishingWellCfg>();
			aList.add(this);
			HashMap<Integer, List<WishingWellCfg>> aMap = new HashMap<Integer,List<WishingWellCfg>>();
			aMap.put(getArea(),aList);
			WellsMap.put(getType(), aMap);
		}
		
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		
	}
	
	public static Map<Integer,List<WishingWellCfg>> getTypeWell(int type){
		if (WellsMap.containsKey(type)) {
			return WellsMap.get(type);
		}
		return null;
	}
	
//	public static int getMaxArea(int type) {
//		if (MaxArea.containsKey(type)) {
//			return MaxArea.get(type);
//		}
//		return 0;
//	}
	
	public static HashSet<Integer> getWellCfgIdx(int type) {
		HashSet<Integer> aSet = new HashSet<>();
		Map<Integer,List<WishingWellCfg>> WellMap = getTypeWell(type);
		//int area = 0;
		int idx = 0;
		List<Integer> weightList = new ArrayList<>();
		List<Integer> idxList = new ArrayList<>();
		for (Map.Entry<Integer,List<WishingWellCfg>> entry: WellMap.entrySet()) {
			//area = entry.getKey();
			idx = 0;
			List<WishingWellCfg> aListCfg = entry.getValue();
			if (aListCfg.size() <= 0) {
				continue;
			} else if (aListCfg.size() > 1) { // 兩個以上需要random
				weightList.clear();
				idxList.clear();
				for (WishingWellCfg acfg : aListCfg) {
					weightList.add(acfg.getWeight());
					idxList.add(acfg.getId());
				}
				idx = GuaJiRand.randonWeightObject(idxList,weightList);
				aSet.add(idx);
			}  else if (aListCfg.size() == 1) {
				idx = aListCfg.get(0).getId();
				aSet.add(idx);
			}
		}
		return aSet;
	}
	
	/*
	 * 取得該輪盤有多少獎項
	 * 
	 */
	public static int getWellMaxReward (int type) {
		Map<Integer,List<WishingWellCfg>> areaMap = getTypeWell(type);
		if (areaMap != null) {
			return areaMap.keySet().size();
		} else {
			return -1;
		}
	}
	
}