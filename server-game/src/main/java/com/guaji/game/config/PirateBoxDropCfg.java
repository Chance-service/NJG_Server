package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiRand;

/**
 * 大富翁活动开启宝箱奖励
 */
@ConfigManager.XmlResource(file = "xml/pirateboxDrop.xml", struct = "map")
public class PirateBoxDropCfg extends ConfigBase {
	/**
	 * 掉落物组ID
	 */
	@Id
	private final int id;
	/**
	 * 奖励物品
	 */
	private final String rewards;
	/**
	 * 权重
	 */
	private final String drawRates;
	/**
	 * 消耗金幣
	 */
	private final int CoinConsume;
	/**
	 * 消耗鑽石
	 */
	private final int DiamondConsume;
	/**
	 * 权重表
	 */
	private final List<Integer> drawRateList;
	/**
	 * 無海盜权重表
	 */
	private List<Integer> nofaillist;
	/**
	 * 獎品表
	 */
	private final List<String> ItemList;
	/**
	 * 全表
	 */
	private static Map<Integer,PirateBoxDropCfg> totalMap = new HashMap<>();
	
	public PirateBoxDropCfg() {
		id = 0;
		rewards = "";
		drawRates = "";
		CoinConsume = 0;
		DiamondConsume = 0;
		drawRateList = new LinkedList<>();
		ItemList = new LinkedList<>();
		nofaillist = null;
	}

	public int getId() {
		return id;
	}

	public String getRewards() {
		return rewards;
	}
	
	public int getCoinConsume() {
		return CoinConsume;
	}
	
	public int getDiamondConsume() {
		return DiamondConsume;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		totalMap.clear();
	}

	/**
	 * 随机该类型奖励
	 */
	public static PirateBoxDropCfg getLevelCfg(int level) {
		PirateBoxDropCfg cfg = null;
		if (totalMap.containsKey(level)) {
			cfg= totalMap.get(level);
		}
		return cfg;
		//return GuaJiRand.randonWeightObject(cfgs, weightList);
	}

	@Override
	protected boolean assemble() {

		// 記錄全表
	   if (totalMap.containsKey(id)) {
		   totalMap.replace(id,this);
	   }else {
		   totalMap.put(id,this); 
	   }
	   // 轉換為數字表
	   drawRateList.clear();
		if (StringUtils.isNotEmpty(drawRates)){
			String[] conuts = drawRates.split(",");
			for (String aconut : conuts) {
				drawRateList.add(Integer.valueOf(aconut.trim()));
			}
		}
		// 獎品轉換列表
		ItemList.clear();
		if (StringUtils.isNotEmpty(rewards)){
			String[] Items = rewards.split(",");
			for (String aItem : Items) {
				ItemList.add(aItem);
			}
		}
		
		if (drawRateList.size() != ItemList.size()) {
			Log.errPrintln("activity143PirateDropCfg reward size error");
			return false;
		}
		
		int idx = 0;
		nofaillist = new ArrayList<Integer>(drawRateList);
		for (String aItem : ItemList) {
			if (aItem.equals("0")) {
				nofaillist.set(idx,0);
				break;
			}
			idx++;
		}
		
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	*可傳入不用random闖關失敗
	 */
	public String RandomReward(boolean nofail) {
		
		if (nofail) {
			return GuaJiRand.randonWeightObject(ItemList, nofaillist);	
		}
			
		return GuaJiRand.randonWeightObject(ItemList, drawRateList);
	}
	
}
