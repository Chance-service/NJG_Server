package com.guaji.game.rank;

public interface IRankingObj extends Comparable<IRankingObj> {

	/**
	 * 获得该排名对象在排行中唯一标识;
	 * 
	 * @return
	 */
	public int getId();

	/**
	 * 设置该排名对象的名次;
	 * 
	 * @param rankPos
	 */
	public void setRankPos(int rankPos);

}
