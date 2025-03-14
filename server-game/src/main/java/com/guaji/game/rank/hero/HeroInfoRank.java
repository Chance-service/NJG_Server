package com.guaji.game.rank.hero;

import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.rank.BaseRankingObj;
import com.guaji.game.rank.IRankingObj;

public class HeroInfoRank extends BaseRankingObj {

	/**
	 * 英雄等级
	 */
	private int level;

	/**
	 * 英雄表格編號
	 */
	private int itemId;
	
	/**
	 * 英雄星等
	 */
	private int starLevel;

	/**
	 * 玩家战力
	 */
	private int score;
	/**
	 * 英雄皮膚
	 */
	private int skinId;

	/**
	 * 排行榜类型
	 */
	private RankType rankType;

	public HeroInfoRank() {

	}

	public HeroInfoRank(String str) {
		ValueOf(str);
	}
	
	public void ValueOf(String str) {
		//playerId,itemId,fightvalue,level,starLevel,skinId
		String[] split = str.split(",");
		id = Integer.parseInt(split[0]);  // playerId
		itemId = Integer.parseInt(split[1]);
		score = Integer.parseInt(split[2]);
		level = Integer.parseInt(split[3]);
		starLevel = Integer.parseInt(split[4]);
		skinId = Integer.parseInt(split[5]);
	}

	@Override
	public int compareTo(IRankingObj o) {

		HeroInfoRank rank = (HeroInfoRank) o;
		
		if (rank.getScore() == this.getScore()) {
			return  this.getItemId() - rank.getItemId(); //小到大
		}
		return rank.getScore() - this.getScore(); //大到小
	}

	public int GetRankData() {
		return this.getScore();
	}
	
	public int getStarLevel() {
		return this.starLevel;
	}
	
	public void setStarLevel(int lv) {
		this.starLevel = lv;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public int getSkinId() {
		return skinId;
	}

	public void setSkinId(int skinId) {
		this.skinId = skinId;
	}

	public RankType getRankType() {
		return rankType;
	}

	public void setRankType(RankType rankType) {
		this.rankType = rankType;
	}

}
