package com.guaji.game.module.activity.fragmentExchange;

/**
 * 万能碎片限时兑换数据
 * 
 * @author Melvin.Mao
 * @date Jun 9, 2017 10:53:30 AM
 */
public class FragmentExchangeStatus {

	/**
	 * 活动期数
	 */
	private int stageId;
	/**
	 * 是否激活
	 */
	private boolean isActive;

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public FragmentExchangeStatus() {

	}

}
