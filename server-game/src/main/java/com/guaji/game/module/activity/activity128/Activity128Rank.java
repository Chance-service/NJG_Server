package com.guaji.game.module.activity.activity128;

public class Activity128Rank implements Comparable<Activity128Rank> {

	private int playerId;
	private int score;

	private int rank;


	public Activity128Rank(){}

	public Activity128Rank(int playerId, int score) {
		super();
		this.playerId = playerId;
		this.score = score;
	}
	

	public Activity128Rank(int playerId, int score, int rank) {
		super();
		this.playerId = playerId;
		this.score = score;
		this.rank = rank;
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

	
	public int getRank() {
		return rank;
	}


	public void setRank(int rank) {
		this.rank = rank;
	}


	@Override
	public int compareTo(Activity128Rank status) {
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
