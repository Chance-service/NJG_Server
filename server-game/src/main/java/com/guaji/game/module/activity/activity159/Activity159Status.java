package com.guaji.game.module.activity.activity159;

import java.util.HashSet;
import java.util.Set;

import org.guaji.os.GuaJiTime;

/**
 * @author 作者 TING LIN
 * @version 创建时间：2023年10月19日 下午1:57:22 类说明
 */
public class Activity159Status {

	/**
	 * @Fields 
	 */
	private int VIPPoint;
	/**
	 * @Fields gotVIPPointTime : 一天內最後一次獲得VIP點數時間
	 */
	private long gotVIPPointTime;
	/**
	 * @Fields gotAwardCfgIds :已经领取的所有奖励配置
	 */
	
	private Set<Integer> gotAwardCfgIds;

	public Activity159Status() {
		VIPPoint = 0;
		gotAwardCfgIds = new HashSet<Integer>();
	}

	public int getVIPPoint() {
		// 当前系统时间
		long currentTime = System.currentTimeMillis();
		if (GuaJiTime.isSameDay(this.gotVIPPointTime, currentTime)) {
			return VIPPoint;
		} else {
			return 0;
		}
		
	}

	public void setVIPPoint(int vippoint) {
		VIPPoint = vippoint;
	}

	public Set<Integer> getGotAwardCfgIds() {
		return gotAwardCfgIds;
	}

	public void setGotAwardCfgIds(Set<Integer> gotAwardCfgIds) {
		this.gotAwardCfgIds = gotAwardCfgIds;
	}

	/**
	 * @param point 累積點數
	 * @return
	 */
	public boolean addVIPPoint(int point) {
		// 当前系统时间
		long currentTime = System.currentTimeMillis();
		if (GuaJiTime.isSameDay(this.gotVIPPointTime, currentTime)) {
			this.VIPPoint += point;
		} else {
			setVIPPoint(point);
		}
		setGotVIPPointTime(currentTime);
		return true;
	}

	public long getGotVIPPointTime() {
		return gotVIPPointTime;
	}

	public void setGotVIPPointTime(long gotVIPPointTime) {
		this.gotVIPPointTime = gotVIPPointTime;
	}

	/**
	 * 是否红点提示，已经充值，且奖励没有领取
	 */
	public boolean showRedPoint() {
//		Map<Object, ContinueRechargeMoneyCfg> cfgList = ConfigManager.getInstance()
//				.getConfigMap(ContinueRechargeMoneyCfg.class);
//
//		for (ContinueRechargeMoneyCfg cfg : cfgList.values()) {
//			// 
//			if (getContinueRechargeMoney() >= cfg.getnTotalMoney() && !isAlreadyGot(cfg.getId())) {
//				// 累计充值金额未满足
//				return true;
//			}
//
//		}
		return false;
	}

	/**
	 * 对应配置对应的奖励是否已经领取过
	 * 
	 * @param cfgId
	 * @return
	 */
	public boolean isAlreadyGot(int cfgId) {
		if (gotAwardCfgIds.contains((Integer) cfgId)) {
			return true;
		}
		return false;
	}

	/**
	 * 添加领取过的cfgId
	 */
	public void addGotAwardCfgId(int cfgId) {
		gotAwardCfgIds.add(cfgId);
	}
	/**
	 * 重置清除已領取獎項
	 */
	public void resetGotAwardCfgId() {
		gotAwardCfgIds.clear();
	}

}
