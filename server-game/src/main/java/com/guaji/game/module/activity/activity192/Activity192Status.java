package com.guaji.game.module.activity.activity192;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigManager;

import com.guaji.game.config.RechargeBounceCfg;
import com.guaji.game.util.GsConst;

/**
 * 免費召喚抽加強版
 */
public class Activity192Status {
	/**
	 * 紀錄已領獎勵對應ID
	 */ 
	private Set<Integer> takeId;
	/**
	 * 紀錄已達成單筆儲值 次數,對應表格type.2
	 */ 
	private Map<Integer,Integer> singleCount;
	/**
	 * 紀錄單筆儲值 ,已領取次數
	 */ 
	private Map<Integer,Integer> takeSingle;
	/**
	 * 紀錄timeIndex<type,timeIndex>
	 */ 
	private Map<Integer,Integer> timeIndex;
	/**
	 * 	累儲 對應表格type.1
	 */
	private int deposit ;
	/**
	 * 	累消費 對應表格type.3
	 */
	private int consume ;


	public Activity192Status() {
		this.deposit = 0;
		this.consume = 0;
		this.takeId = new HashSet<>();
		this.singleCount = new HashMap<>();
		this.takeSingle = new HashMap<>();
		this.timeIndex = new HashMap<>();
	}
	
	public void addDeposit(int num) {
		this.deposit = this.deposit + num;
	}
	
	public void addConsume(int num) {
		this.consume = this.consume + num;
	}

	public int getDeposit() {
		return deposit;
	}

	public void setDeposit(int deposit) {
		this.deposit = deposit;
	}

	public int getConsume() {
		return consume;
	}

	public void setConsume(int consume) {
		this.consume = consume;
	}

	public Set<Integer> getTakeId() {
		return takeId;
	}

	public void setTakeId(Set<Integer> takeId) {
		this.takeId = takeId;
	}
	
	public void addSingleCount(int cfgId) {
		if (singleCount.containsKey(cfgId)) {
			singleCount.put(cfgId, singleCount.get(cfgId) + 1);
		} else {
			singleCount.put(cfgId,1);
		}
	}
	
	public int getSingleCount(int cfgId) {
		if (singleCount.containsKey(cfgId)) {
			return singleCount.get(cfgId);
		} else {
			return 0;
		}
	}
	
	public void addTakeSingle(int cfgId) {
		if (takeSingle.containsKey(cfgId)) {
			takeSingle.put(cfgId, takeSingle.get(cfgId) + 1);
		} else {
			takeSingle.put(cfgId,1);
		}
	}
	
	public int getTakeSingle(int cfgId) {
		if (takeSingle.containsKey(cfgId)) {
			return takeSingle.get(cfgId);
		} else {
			return 0;
		}
	}
	
	public int getTimeIndex(int type) {
		if (timeIndex.containsKey(type)) {
			return timeIndex.get(type);
		} else {
			return -1;
		}
	}
	
	public void setTimeIndex(int type,int index) {
		timeIndex.put(type,index);
	}
	
	private void clearTypeData(int type) {
		if (type == GsConst.RechargeBounceType.Deposit){
			setDeposit(0);
			clearTakeId(type);
		}
		
		if (type == GsConst.RechargeBounceType.Single) {
			singleCount.clear();
			takeSingle.clear();
		}
		
		if (type == GsConst.RechargeBounceType.consums) {
			setConsume(0);
			clearTakeId(type);
		}
	}
	
	public boolean checkTimeIndex(int platformId) {
		boolean needsave = false;
		for (int type = 1; type <=GsConst.RechargeBounceType.typeCount ; type++ ) {
			int timeIndex = getTimeIndex(type);
			int nowIndex = RechargeBounceCfg.getValidTimeIndex(platformId,type);
			if (timeIndex != nowIndex) {
				setTimeIndex(type,nowIndex);
				if (timeIndex != -1) {
					if (nowIndex > 0 ) {  // 0 close type keep record
						clearTypeData(type);
					}
				}
				needsave = true;
			}
		}	
		return needsave;
	}
	
	/**
	 * 清除對應type相關領取標籤
	 * @param type
	 */
	public void clearTakeId(int type) {
		if (this.takeId.size() <= 0) {
			return;
		}
		for (RechargeBounceCfg bounceCfg : ConfigManager.getInstance().getConfigMap(RechargeBounceCfg.class).values()) {
			if (bounceCfg.getType() == type) {
				if (this.takeId.contains(bounceCfg.getId())) {
					this.takeId.remove(bounceCfg.getId());
				}
			}
		}
	}
	
}
