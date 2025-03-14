package com.guaji.game.module.activity.recharge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.AccRechargeCfg;

/**
 * 每日累计充值返利活动
 * @author xpf
 */
public class AccRechargeStatus {
	
	/**
	 *  活动期间每日活动状态
	 */
	private Map<Integer, AccRechargeOneDayStatus> everyDayStatusMap;
	
	public AccRechargeStatus(){
		everyDayStatusMap = new HashMap<Integer, AccRechargeOneDayStatus>();
	}
	
	/**
	 * 活动期间玩家一共充值的数额
	 * @return
	 */
	public int getTotalRecharge(){
		int total = 0;
		for(Map.Entry<Integer, AccRechargeOneDayStatus> entry : everyDayStatusMap.entrySet()){
			total += entry.getValue().getAccRechargeMoney();
		}
		return total;
	}
	
	/**
	 * 获取今日累计充值钻石数量
	 * @return
	 */
	public int getTodayRechargeAmount(){
		int date = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		if (everyDayStatusMap.containsKey(date)) {
			AccRechargeOneDayStatus oneDayStatus = everyDayStatusMap.get(date);
			return oneDayStatus.getAccRechargeMoney();
		} else {
			return 0;
		}
	} 
	
	/**
	 * 获取今日累计充值活动已领取的奖励
	 * @return
	 */
	public Set<Integer> getTodayGotAwardCfgIds(){
		int date = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		if (everyDayStatusMap.containsKey(date)) {
			AccRechargeOneDayStatus oneDayStatus = everyDayStatusMap.get(date);
			return oneDayStatus.getGotAwardCfgIds();
		} else {
			everyDayStatusMap.clear();
			AccRechargeOneDayStatus oneDayStatus = new AccRechargeOneDayStatus();
			everyDayStatusMap.put(date, oneDayStatus);
			return oneDayStatus.getGotAwardCfgIds();
		}
	}
	
	/**
	 * 增加充值数额
	 */
	public void addAccRechargeAmount(int amount){
		int date = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		if (everyDayStatusMap.containsKey(date)) {
			AccRechargeOneDayStatus oneDayStatus = everyDayStatusMap.get(date);
			oneDayStatus.addAccRechargeAmount(amount);
		} else {
			everyDayStatusMap.clear();
			AccRechargeOneDayStatus oneDayStatus = new AccRechargeOneDayStatus();
			oneDayStatus.addAccRechargeAmount(amount);
			everyDayStatusMap.put(date, oneDayStatus);
		}
	}
	
	/**
	 * 对应配置对应的奖励是否已经领取过
	 * @param cfgId
	 * @return
	 */
	public boolean isAlreadyGot(int cfgId){
		int date = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		if (everyDayStatusMap.containsKey(date)) {
			return everyDayStatusMap.get(date).isAlreadyGot(cfgId);
		} else {
			return false;
		}
	}
	
	/**
	 * 添加领取过的cfgId
	 */
	public void addGotAwardCfgId(int cfgId){
		int date = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		if (everyDayStatusMap.containsKey(date)) {
			AccRechargeOneDayStatus oneDayStatus = everyDayStatusMap.get(date);
			oneDayStatus.addGotAwardCfgId(cfgId);
		} else {
			everyDayStatusMap.clear();
			AccRechargeOneDayStatus oneDayStatus = new AccRechargeOneDayStatus();
			oneDayStatus.addGotAwardCfgId(cfgId);
			everyDayStatusMap.put(date, oneDayStatus);
		}
	}

	/**
	 * 是否红点提示，是否领取过了
	 */
	public boolean showRedPoint(){
		Map<Object, AccRechargeCfg> cfgs = ConfigManager.getInstance().getConfigMap(AccRechargeCfg.class);
		if(cfgs == null || cfgs.isEmpty()){
			return false;
		}
		
		for(AccRechargeCfg cfg : cfgs.values()){
			//累计充值达到，并且没有领取
			if(getTodayRechargeAmount() >= cfg.getSum() && !isAlreadyGot(cfg.getId())){
				return true;
			}
		}
		return false;
	}
	
}
