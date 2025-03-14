package com.guaji.game.module.activity.welfareReward;

/**
 * 天降福利活动存储
 */
public class WelfareRewardStatus {

	/**
	 * 当前阶段
	 */
	private int currentStep;
	/**
	 * 可不可以领奖
	 */
	private boolean canPlay;

	public WelfareRewardStatus() {
		super();
		this.currentStep = 1;
		this.canPlay = true;
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public boolean canPlay() {
		return canPlay;
	}

	public void setCanPlay(boolean canPlay) {
		this.canPlay = canPlay;
	}

}
