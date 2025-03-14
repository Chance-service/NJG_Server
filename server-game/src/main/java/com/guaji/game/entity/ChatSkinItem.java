package com.guaji.game.entity;

/**
 * 聊天皮肤购买单项
 */
public class ChatSkinItem {

	//皮肤ID
	private int skinId;
	//结束时间
	private long endTime;
	//最近购买的活动期次
	private int stageId;
	
	public int getSkinId() {
		return skinId;
	}
	public void setSkinId(int skinId) {
		this.skinId = skinId;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public int getStageId() {
		return stageId;
	}
	public void setStageId(int stageId) {
		this.stageId = stageId;
	}
}
