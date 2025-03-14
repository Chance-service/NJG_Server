package com.guaji.game.momo;

public class MomoScore {
	protected String puid;
	protected String scoreType;
	protected int score;
	
	public MomoScore() {
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public String getScoreType() {
		return scoreType;
	}

	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return String.format("{\"userid\":\"%s\", \"score_type\":\"%s\", \"score\":\"%d\"}", MomoManager.getMomoUserId(puid), scoreType, score);
	}
}
