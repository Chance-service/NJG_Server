package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.entity.PlayerEntity;

public class FightValueRankManager extends AppObj {

	public class FightValueRankData implements Comparable<FightValueRankData> {
		private int playerId;
		private int fightValue;

		public FightValueRankData(int playerId, int fightValue) {
			this.playerId = playerId;
			this.fightValue = fightValue;
		}

		public int getPlayerId() {
			return playerId;
		}

		public void setPlayerId(int playerId) {
			this.playerId = playerId;
		}

		public int getFightValue() {
			return fightValue;
		}

		public void setFightValue(int fightValue) {
			this.fightValue = fightValue;
		}

		@Override
		public int compareTo(FightValueRankData o) {
			if (this.playerId != o.getPlayerId()) {
				if (this.fightValue != o.getFightValue()) {
					return o.getFightValue() - this.fightValue;
				}
				return o.getPlayerId() - this.playerId;
			}
			return 0;
		}
	}

	private List<FightValueRankData> fightValueRankDataList;

	private ConcurrentHashMap<Integer, FightValueRankData> playerfightValueRankDataMap;

	public FightValueRankManager(GuaJiXID xid) {
		super(xid);
		fightValueRankDataList = Collections.synchronizedList(new ArrayList<FightValueRankData>());
		playerfightValueRankDataMap = new ConcurrentHashMap<Integer, FightValueRankData>();

		if (instance == null) {
			instance = this;
		}
	}

	/**
	 * 全局对象, 便于访问
	 */
	private static FightValueRankManager instance = null;

	/**
	 * 获取全局实例对象
	 */
	public static FightValueRankManager getInstance() {
		return instance;
	}

	/**
	 * 初始化
	 */
	public void init() {
		List<PlayerEntity> playerEntities = DBManager.getInstance()
				.query("from PlayerEntity where fightValue > 0 and invalid = 0 order by fightValue desc");
		for (int i = 0; i < playerEntities.size(); i++) {
			PlayerEntity playerEntity = playerEntities.get(i);
			FightValueRankData rankData = new FightValueRankData(playerEntity.getId(), playerEntity.getFightValue());
			fightValueRankDataList.add(rankData);
			playerfightValueRankDataMap.put(rankData.getPlayerId(), rankData);
		}
	}

	/**
	 * 更新战力排行列表
	 * 
	 * @param playerId
	 * @param fightValue
	 */
	public void updateFightValueRankData(int playerId, int fightValue) {
		if (playerfightValueRankDataMap.containsKey(playerId)) {
			playerfightValueRankDataMap.get(playerId).setFightValue(fightValue);
		} else {
			FightValueRankData rankData = new FightValueRankData(playerId, fightValue);
			fightValueRankDataList.add(rankData);
			playerfightValueRankDataMap.put(rankData.getPlayerId(), rankData);
		}
		Collections.sort(fightValueRankDataList);
	}

	/**
	 * 获取指定战力区间的战力排行数据
	 * 
	 * @return
	 */
	public List<Integer> getRankDataByFightValueInterval(int minFightValue, int maxFightValue) {
		List<Integer> tmpList = new ArrayList<Integer>();
		for (FightValueRankData rankData : fightValueRankDataList) {
			if (rankData.getFightValue() < minFightValue) {
				break;
			}

			if (maxFightValue >= rankData.getFightValue()) {
				tmpList.add(rankData.getPlayerId());
			}
		}
		return tmpList;
	}

	/**
	 * 获取指定玩家在服务器中的战力排行
	 * 
	 * @return
	 */
	public int getPlayerServerFightValueRank(int playerId) {
		for (int i = 0; i < fightValueRankDataList.size(); i++) {
			FightValueRankData rankData = fightValueRankDataList.get(i);
			if (rankData.getPlayerId() == playerId) {
				return i + 1;
			}
		}
		return -1;
	}

	/**
	 * 获取指定玩家在服务器中的战斗力
	 * 
	 * @return
	 */
	public int getPlayerServerFightValue(int playerId) {

		if (!playerfightValueRankDataMap.containsKey(playerId))
			return 0;

		FightValueRankData rankData = playerfightValueRankDataMap.get(playerId);

		return rankData.fightValue;

	}
}
