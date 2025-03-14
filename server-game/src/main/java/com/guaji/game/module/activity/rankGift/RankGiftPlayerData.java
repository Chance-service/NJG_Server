package com.guaji.game.module.activity.rankGift;

import com.guaji.game.entity.PlayerEntity;

/**
 * 用于保存用户经验，等级数据
 * @author qianhang
 *
 */
public class RankGiftPlayerData implements Comparable<RankGiftPlayerData> {
	private int playerId;
	private String playerName;
	private int playerLevel;
	private long playerExp;
	
	public RankGiftPlayerData(PlayerEntity playerEntity) {
		this.playerId = playerEntity.getId();
		this.playerName = playerEntity.getName();
		this.playerLevel = playerEntity.getLevel();
		this.playerExp = playerEntity.getExp();
	}
	
	public RankGiftPlayerData(int playerId, String playerName, int playerLevel, long playerExp) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.playerLevel = playerLevel;
		this.playerExp = playerExp;
	}
	
	public int getPlayerId() {
		return playerId;
	}
	public String getPlayerName() {
		return playerName;
	}
	public int getPlayerLevel() {
		return playerLevel;
	}
	public long getPlayerExp() {
		return playerExp;
	}
	
	public void setPlayerLevel(int level) {
		this.playerLevel = level;
	}
	public void setPlayerExp(long exp) {
		this.playerExp = exp;
	}
	public void setPlayerName(String name) {
		this.playerName = name;
	}
	
	@Override
	public int compareTo(RankGiftPlayerData o) {
		if (o.getPlayerLevel() == this.getPlayerLevel()) {
			if(o.getPlayerExp() == this.getPlayerExp()) {
				return this.getPlayerId() - o.getPlayerId(); 
			}
			return o.getPlayerExp() > this.getPlayerExp() ? 1 : -1;
		} 
		return o.getPlayerLevel() - this.getPlayerLevel();
	}

}
