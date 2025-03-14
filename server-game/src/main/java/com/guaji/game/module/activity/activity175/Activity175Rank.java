package com.guaji.game.module.activity.activity175;

public class Activity175Rank implements Comparable<Activity175Rank> {

	private int playerId;
	private int score;

	public Activity175Rank(){}

	public Activity175Rank(int playerId, int score) {
		super();
		this.playerId = playerId;
		this.score = score;
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public int compareTo(Activity175Rank status) {
		// 根据积分从大到小排序
		if (this.score > status.getScore()) {
			return -1;
		} else if (this.score < status.getScore()) {
			return 1;
		} else {
			return 0;
		}
	}
}
