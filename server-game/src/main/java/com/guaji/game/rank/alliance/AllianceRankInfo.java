package com.guaji.game.rank.alliance;

import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.rank.BaseRankingObj;
import com.guaji.game.rank.IRankingObj;

public class AllianceRankInfo extends BaseRankingObj {

	/**
	 * 排行榜类型
	 */
	private RankType rankType;

	/**
	 * 等级
	 */
	private int level;

	/**
	 * 知名度
	 */
	private int bossVitality;

	public AllianceRankInfo() {

	}


	public AllianceRankInfo(String str, RankType rankType) {
		this.rankType = rankType;
		String[] split = str.split(",");
		id = Integer.parseInt(split[0]);
		if (rankType == RankType.ALLIANCE_LEVEL_RANK) {
			level = Integer.parseInt(split[1]);
		} else if (rankType == RankType.ALLIANCE_VITALITY_RANK) {
			bossVitality = Integer.parseInt(split[1]);
		}

	}

	@Override
	public int compareTo(IRankingObj o) {
		AllianceRankInfo rank = (AllianceRankInfo) o;

		if (rankType == RankType.ALLIANCE_LEVEL_RANK) {

			return rank.getLevel() - this.getLevel();
		} else if (rankType == RankType.ALLIANCE_VITALITY_RANK) {

			return rank.getBossVitality() - this.getBossVitality();
		} else {
			return this.getId() - rank.getId();
		}
	}

	public RankType getRankType() {
		return rankType;
	}

	public void setRankType(RankType rankType) {
		this.rankType = rankType;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getBossVitality() {
		return bossVitality;
	}

	public void setBossVitality(int bossVitality) {
		this.bossVitality = bossVitality;
	}

	/**
	 * @return 获取排序数据
	 */
	public int getRankData() {
		if (rankType == RankType.ALLIANCE_LEVEL_RANK) {
			return this.level;
		} else if (rankType == RankType.ALLIANCE_VITALITY_RANK) {
			return this.bossVitality;
		} else {
			return 0;
		}
	}
}
