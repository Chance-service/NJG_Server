package com.guaji.game.module.activity.commendationTribe;

import org.guaji.config.ConfigManager;

import com.guaji.game.config.CommentdationTribeCfg;

/**
 * 部族的嘉奖
 * @author Administrator
 */
public class CommendationTribeStatus {
	
	/**
	 * 当前阶段
	 */
	private int curStage = 1;
	/**
	 * 运气值
	 */
	private int luckyValue = 0;
	/**
	 * 当前消耗的钻石
	 */
	private int curCostGold = 0;
	/**
	 * 当前使用的次数
	 */
	private int curUseTimes = 0;

	public CommendationTribeStatus() {
		this.curStage = 1;
		this.luckyValue = 0;
	}

	public int getCurStage() {
		return curStage;
	}

	public void setCurStage(int curStage) {
		this.curStage = curStage;
	}

	public int getLuckyValue() {
		return luckyValue;
	}

	public void setLuckyValue(int luckyValue) {
		this.luckyValue = luckyValue;
	}
	
	/**
	 * 增加幸运值
	 * @param value
	 */
	public void addLuckyValue(int value) {
		this.luckyValue += value;
	}

	public int getCurCostGold() {
		return curCostGold;
	}

	public void setCurCostGold(int curCostGold) {
		this.curCostGold = curCostGold;
	}

	public void addGoldCost(int value) {
		this.curCostGold += value;
	}

	public int getCurUseTimes() {
		return curUseTimes;
	}

	public void setCurUseTimes(int curUseTimes) {
		this.curUseTimes = curUseTimes;
	}

	public int getLeftCount() {
		CommentdationTribeCfg cfg = ConfigManager.getInstance().getConfigByKey(CommentdationTribeCfg.class, this.curStage);
		if(cfg != null) {
			return this.curCostGold / cfg.getGoldExcCount() - this.curUseTimes;
		}
		return 0;
	}

	/**
	 * 到下一个阶段
	 */
	public void pass() {
		this.curStage ++ ;
		this.luckyValue = 0;
	}
	
}
