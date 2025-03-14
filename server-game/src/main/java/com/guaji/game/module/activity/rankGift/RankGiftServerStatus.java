package com.guaji.game.module.activity.rankGift;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author qianhang
 *
 */
public class RankGiftServerStatus {
	
	public static class ArenaRankType {
		private int playerId;
		private int isNPC;
		private String name = "";
		
		public ArenaRankType() {
			
		}
		
		public ArenaRankType(int playerId, int isNPC, String name) {
			this.playerId = playerId;
			this.isNPC = isNPC;
			this.name = name;
		}
		
		public int getPlayerId() {
			return playerId;
		}
		public int getIsNPC() {
			return isNPC;
		}
		public void setPlayerId(int playerId) {
			this.playerId = playerId;
		}
		public void setIsNPC(int isNPC) {
			this.isNPC = isNPC;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	/** 经验排名的玩家ID*/
	private List<Integer> expRankGift;
	/** 竞技排名的玩家ID*/
	private List<ArenaRankType> arenaRankGift;
	
	/** 是否发过奖*/
	private boolean isGrantAwards;
	
	public RankGiftServerStatus(){
		expRankGift = new ArrayList<Integer>();
		arenaRankGift = new ArrayList<ArenaRankType>();
	}
	
	/**
	 * 是否发过奖
	 * @return
	 */
	public boolean isGrantAwards() {
		return isGrantAwards;
	}
	/**
	 * 设置是否发奖
	 * @param isGrantAwards
	 */
	public void setGrantAwards(boolean isGrantAwards) {
		this.isGrantAwards = isGrantAwards;
	}
	
	public List<Integer> getExpRankGift() {
		return expRankGift;
	}
	public List<ArenaRankType> getArenaRankGift() {
		return arenaRankGift;
	}
	
	public void addExpRank(int playerId) {
		expRankGift.add(playerId);
	}
	public void clearExpRank() {
		expRankGift.clear();
	}
	
	public void addArenaRank(int playerId, int isNPC, String name) {
		arenaRankGift.add(new ArenaRankType(playerId, isNPC, name));
	}
	public void clearArenaRank() {
		arenaRankGift.clear();
	}
}
