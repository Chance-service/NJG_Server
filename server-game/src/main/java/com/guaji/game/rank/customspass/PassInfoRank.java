package com.guaji.game.rank.customspass;

import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.rank.BaseRankingObj;
import com.guaji.game.rank.IRankingObj;

public class PassInfoRank extends BaseRankingObj {

	/**
	 * 通关关卡数据
	 */
	private int mapId;
	
	
	/**
	 * 排行榜类型
	 */
	private RankType rankType;

	
	public PassInfoRank() {
		super();
	}


	public PassInfoRank(String str,RankType rankType) {
	
		this.rankType=rankType;
		String[] split = str.split(",");
		id = Integer.parseInt(split[0]);
		mapId = Integer.parseInt(split[1]);

	}
	

	@Override
	public int compareTo(IRankingObj o) {
		PassInfoRank rank = (PassInfoRank) o;
		if(rank.getRankType()==RankType.CUSTOMPASS_TRAINING_RANK) {
			return rank.getMapId()%100000-this.getMapId()%100000;
		}else {
			return rank.getMapId()-this.getMapId();
		}
		
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}


	public RankType getRankType() {
		return rankType;
	}


	public void setRankType(RankType rankType) {
		this.rankType = rankType;
	}

	
}
