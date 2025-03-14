package com.guaji.game.rank;

public abstract class BaseRankingObj implements IRankingObj{
	protected int id;
	/**
	 * 
	 */
	protected int rankPos;
	
	public int getId() {
		return id;
	}
	public int getRankPos() {
		return rankPos;
	}
	public void setId(int id) {
		this.id = id;
	}

	
	/* (非 Javadoc) 
	* <p>Title: setRankPos</p> 
	* <p>Description: </p> 
	* @param rankPos 
	* @see com.guaji.game.rank.IRankingObj#setRankPos(int) 
	*/
	public void setRankPos(int rankPos) {
		this.rankPos = rankPos;
	}
}
