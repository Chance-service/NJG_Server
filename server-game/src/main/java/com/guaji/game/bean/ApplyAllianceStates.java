package com.guaji.game.bean;

/**
 * 玩家邮件数据
 */
public class ApplyAllianceStates {
	
	/**
	 * 公会Id
	 */
	private int allianceId;

	/**
	 * 公会状态
	 */
	private int state;
	
	/**
	 * 拒绝加入的CD时间
	 */
	private long refusedJoinTime;

	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getRefusedJoinTime() {
		return refusedJoinTime;
	}

	public void setRefusedJoinTime(long refusedJoinTime) {
		this.refusedJoinTime = refusedJoinTime;
	}
	
}
