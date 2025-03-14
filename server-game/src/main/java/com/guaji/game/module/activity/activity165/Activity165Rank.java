package com.guaji.game.module.activity.activity165;

public class Activity165Rank implements Comparable<Activity165Rank> {

	private int playerId;
	private int score;

	private int rank;


	public Activity165Rank(){}

	public Activity165Rank(int playerId, int score) {
		super();
		this.playerId = playerId;
		this.score = score;
	}
	

	public Activity165Rank(int playerId, int score, int rank) {
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
	public int compareTo(Activity165Rank status) {
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
