package com.guaji.game.rank.player;

import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.rank.BaseRankingObj;
import com.guaji.game.rank.IRankingObj;

public class PlayerInfoRank extends BaseRankingObj {

	/**
	 * 玩家等级
	 */
	private int level;

	/**
	 * 玩家战力
	 */
	private int score;

	/**
	 * 排行榜类型
	 */
	private RankType rankType;

	public PlayerInfoRank() {

	}

	public PlayerInfoRank(String str) {
		String[] split = str.split(",");
		id = Integer.parseInt(split[0]);
		level = Integer.parseInt(split[1]);
		score = Integer.parseInt(split[2]);
	}

	@Override
	public int compareTo(IRankingObj o) {

		PlayerInfoRank rank = (PlayerInfoRank) o;
		if (this.getRankType() == RankType.LEVEL_ALL_RANK || this.getRankType() == RankType.LEVEL_PROFCS_RANK
				|| this.getRankType() == RankType.LEVEL_PROFGS_RANK
				|| this.getRankType() == RankType.LEVEL_PROFJS_RANK) {
			/*
			if (this.getLevel() == rank.getLevel()) {
				return this.getId() - rank.getId();
			} else {
				return rank.getLevel() > this.getLevel() ? 1 : -1;
			}
			*/
			return rank.getLevel() - this.getLevel();
		} else {
			/*
			if (this.getScore() == rank.getScore()) {
				return this.getId() - rank.getId();
			} else {
				return rank.getScore() > this.getScore() ? 1 : -1;
			}*/
			
			return rank.getScore() - this.getScore();
		}

	}

	public int GetRankData() {
		if (this.getRankType() == RankType.LEVEL_ALL_RANK || this.getRankType() == RankType.LEVEL_PROFCS_RANK
				|| this.getRankType() == RankType.LEVEL_PROFGS_RANK
				|| this.getRankType() == RankType.LEVEL_PROFJS_RANK) {
			return this.getLevel();
		} else {
			return this.getScore();
		}
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public RankType getRankType() {
		return rankType;
	}

	public void setRankType(RankType rankType) {
		this.rankType = rankType;
	}

}
