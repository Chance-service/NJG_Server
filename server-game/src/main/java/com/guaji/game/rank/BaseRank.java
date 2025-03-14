package com.guaji.game.rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.guaji.game.protocol.Const.RankType;

/**
 * 排行功能基类;
 * 
 * @author qianhang
 *
 */
public abstract class BaseRank implements IRank {
	protected List<IRankingObj> ranks = new ArrayList<IRankingObj>();
	protected boolean isOpen = false;
	protected int maxRankNum;
	protected RankType rankType;

	@Override
	public List<IRankingObj> getTopLimitRank(int limitNum) {
		List<IRankingObj> list = new ArrayList<IRankingObj>();
		int num = limitNum > ranks.size() ? ranks.size() : limitNum;
		for (int i = 0; i < num; i++) {
			IRankingObj iRankingObj = ranks.get(i);
			iRankingObj.setRankPos(i + 1);
			list.add(iRankingObj);
		}
		return list;
	}

	@Override
	public void updateRank(IRankingObj rankObj) {
		if (getRankingObjById(rankObj.getId()) != null) {
			ranks.remove(ranks.indexOf(getRankingObjById(rankObj.getId())));
		}
		ranks.add(rankObj);
		Collections.sort(ranks);
		if (ranks.size() > maxRankNum) {
			ranks.subList(maxRankNum, ranks.size()).clear();
		}
	}

	@Override
	public boolean isRankOpen() {
		return isOpen;
	}

	@Override
	public void closeRank() {
		this.isOpen = false;
		afterCloseRank();
	}

	@Override
	public void openRank() {
		this.isOpen = true;
		afterOpenRank();
	}

	@Override
	public IRankingObj getRankingObjById(int id) {
		for (IRankingObj iRankingObj : ranks) {
			if (iRankingObj.getId() == id) {
				iRankingObj.setRankPos(ranks.indexOf(iRankingObj) + 1);
				return iRankingObj;
			}
		}
		return null;
	}

	@Override
	public void setMaxRankNum(int maxRankNum) {
		this.maxRankNum = maxRankNum;
	}

	protected abstract void afterCloseRank();

	protected abstract void afterOpenRank();

	@Override
	public void setRankType(RankType rankType) {
		this.rankType = rankType;
	}

	public RankType getRankType() {
		return rankType;
	}

	public void buildRankObjs(List<?> list) {
		// TODO Auto-generated method stub
		
	}
	

}
