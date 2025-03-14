package com.guaji.game.module.activity.starEvaluation;

public class StarEvaluationStatus {

	public static final int STATUS_NONE = 1;
	
	public static final int STATUS_CLICK = 2;
	
	public static final int STATUS_REWARD = 3;
	
	private int status = STATUS_NONE;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
