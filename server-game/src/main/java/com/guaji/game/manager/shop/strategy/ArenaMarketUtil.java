package com.guaji.game.manager.shop.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;

import com.guaji.game.config.HonorShopCfg;
import com.guaji.game.item.ItemInfo;

public class ArenaMarketUtil {
	private static Map<Integer,List<HonorShopCfg>> cfgMaps = new HashMap<Integer,List<HonorShopCfg>>();

	/**
	 * 获取荣誉商店的产品
	 * @param type
	 * @return
	 */
	public static List<HonorShopCfg> getCfgsByType(int type){
		List<HonorShopCfg> list = cfgMaps.get(type);
		if(list==null){
			//初始化数据
			List<HonorShopCfg> orginalList = ConfigManager.getInstance().getConfigList(HonorShopCfg.class);
			for(HonorShopCfg cfg:orginalList){
				ItemInfo item = cfg.getItem();
				int orgType =item.getType()/10000;
				List<HonorShopCfg> tempList = cfgMaps.get(orgType);
				if(tempList==null){
					tempList = new ArrayList<HonorShopCfg>();
					cfgMaps.put(orgType, tempList);
				}
				tempList.add(cfg);
			}
			list = cfgMaps.get(type);
		}
		return list;
	}
}
