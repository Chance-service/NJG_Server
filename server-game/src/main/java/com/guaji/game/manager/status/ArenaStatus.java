package com.guaji.game.manager.status;

import org.guaji.os.GuaJiTime;

import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.util.GsConst;

public class ArenaStatus {
	/**
	 * 上次发奖时间
	 */
	private int lastGrantAwardTime;
	
	public ArenaStatus(){
		int todayRewardTime = (int)(GuaJiTime.getAM0Date().getTime()/1000) + SysBasicCfg.getInstance().getArenaRewardTime();
		lastGrantAwardTime = todayRewardTime - GsConst.ONE_DAY_SEC;
	} 

	public int getLastGrantAwardTime() {
		return lastGrantAwardTime;
	}

	public void setLastGrantAwardTime(int lastGrantAwardTime) {
		this.lastGrantAwardTime = lastGrantAwardTime;
	}
	
	/**
	 * 今日竞技场奖励是否已经发放
	 * @return true 今天已经发过竞技场奖励
	 */
	public boolean isGrantTodayAward(){
		int todayRewardTime = (int)(GuaJiTime.getAM0Date().getTime()/1000) + SysBasicCfg.getInstance().getArenaRewardTime();
		return (lastGrantAwardTime >= todayRewardTime);
	}
	
	/**
	 * 是否可以发放今日竞技场排名奖励
	 * @return
	 */
	public boolean canGrantTodayAward(){
		int todayRewardTime = (int)(GuaJiTime.getAM0Date().getTime()/1000) + SysBasicCfg.getInstance().getArenaRewardTime();
		int curTime = GuaJiTime.getSeconds();
		return (!isGrantTodayAward()) && (curTime >= todayRewardTime);
	}

}
