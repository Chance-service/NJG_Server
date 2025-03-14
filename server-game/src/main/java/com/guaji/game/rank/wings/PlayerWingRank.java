package com.guaji.game.rank.wings;

import com.guaji.game.rank.BaseRankingObj;
import com.guaji.game.rank.IRankingObj;

public class PlayerWingRank extends BaseRankingObj {
	
	private int playerLevel;
	private long useTime;

	public PlayerWingRank() {
		
	}

	public PlayerWingRank(String str) {
		String[] split = str.split(",");
		id = Integer.parseInt(split[0]);
		playerLevel = Integer.parseInt(split[1]);
		useTime = Long.parseLong(split[2]);
	}

	public int getPlayerLevel() {
		return playerLevel;
	}

	public long getUseTime() {
		return useTime;
	}

	public void setPlayerLevel(int playerLevel) {
		this.playerLevel = playerLevel;
	}

	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}

	@Override
	public int compareTo(IRankingObj o) {
		PlayerWingRank rank = (PlayerWingRank) o;
		if (this.getUseTime() == rank.getUseTime()) {
			return this.getId() - rank.getId();
		} else {
			return this.getUseTime() > rank.getUseTime() ? 1 : -1;
		}
	}

}
