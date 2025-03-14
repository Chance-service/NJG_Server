package com.guaji.game.module.activity.shoot;

import java.util.HashMap;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ShootCostCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;

/**
 * 气枪打靶活动info
 */
public class ShootActivityInfo {
	/**
	 * K-类型，V-今日免费射击时间
	 */
	private Map<Integer, Integer> shootTypeTimesMap = new HashMap<Integer, Integer>();

	/**
	 * K-类型，V-活动期间总次数
	 */
	private Map<Integer, Integer> shootTypeTotalTimesMap = new HashMap<Integer, Integer>();

	/**
	 * 获取对应类型免费射击时间
	 * 
	 * @param type
	 * @return
	 */
	public Integer getTodayFindTimes(int type) {

		if (shootTypeTimesMap.containsKey(type)) {
			return shootTypeTimesMap.get(type);
		} else {
			shootTypeTimesMap.put(type, 0);
			return 0;
		}
	}

	/**
	 * 获取活动期间对应类型总计已进行次数
	 * 
	 * @param findTye
	 * @return
	 */
	public int getTotalFindTimes(int shotType) {
		if (shootTypeTotalTimesMap.containsKey(shotType)) {
			return shootTypeTotalTimesMap.get(shotType);
		} else {
			shootTypeTotalTimesMap.put(shotType, 0);
			return 0;
		}
	}

	/**
	 * 增加活动类型总次数
	 * 
	 * @param findType
	 * @param times
	 */
	public void addTotalTimes(int shootType, int count) {
		if (shootTypeTotalTimesMap.containsKey(shootType)) {
			int alreadyTimes = shootTypeTotalTimesMap.get(shootType) + count;
			shootTypeTotalTimesMap.put(shootType, alreadyTimes);
		} else {
			shootTypeTotalTimesMap.put(shootType, count);
		}
	}
	
	/**
	 * 检测免费次数是否刷新
	 */
	public boolean checkRefreshTimeCome(Player player)
	{
		Map<Integer,Integer> shootTypeTimes = getShootTypeTimesMap();
		Map<Object, ShootCostCfg> typeCfgMap = ConfigManager.getInstance().getConfigMap(ShootCostCfg.class);
		for(Map.Entry<Integer,Integer> item: shootTypeTimes.entrySet()){
			int freeTime = GuaJiTime.getSeconds() - item.getValue();
			ShootCostCfg typeCfg = typeCfgMap.get(item.getKey());
			int refreshTime = typeCfg.getFreeRefreshTime() * 60 * 60;
			// 距离免费次数时间计算
			if (freeTime >= refreshTime) {
				// 未到时间
				return true;
			}
		}
		return false;
	}

	/**
	 * 刷新免费次数时间
	 */
	public void refreshTime(int type) {
		shootTypeTimesMap.put(type, GuaJiTime.getSeconds());
	}

	public Map<Integer, Integer> getShootTypeTimesMap() {
		return shootTypeTimesMap;
	}

	public void setShootTypeTimesMap(Map<Integer, Integer> shootTypeTimesMap) {
		this.shootTypeTimesMap = shootTypeTimesMap;
	}

	/**
	 * K-类型，V-活动期间总次数
	 */
	public Map<Integer, Integer> getShootTypeTotalTimesMap() {
		return shootTypeTotalTimesMap;
	}

	/**
	 * K-类型，V-今日免费射击时间
	 */
	public void setShootTypeTotalTimesMap(Map<Integer, Integer> shootTypeTotalTimesMap) {
		this.shootTypeTotalTimesMap = shootTypeTotalTimesMap;
	}
	
	/**
	 * 清理次数
	 */
	public void clearShootInfo(){
		shootTypeTotalTimesMap.clear();
		shootTypeTimesMap.clear();
	}

}
