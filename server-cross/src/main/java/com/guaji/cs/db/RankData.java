package com.guaji.cs.db;

import java.sql.PreparedStatement;

/**
 * 玩家跨服竞技排行信息
 */
public class RankData extends DBOperation{

	/**
	 * 玩家唯一标识
	 */
	private String identify;
	
	/**
	 * 玩家连胜次数
	 */
	private int winTimes;
	
	/**
	 * 玩家现有积分
	 */
	private int score;
	
	/**
	 * 玩家排名
	 */
	private int rank;

	/**
	 * 构造函数
	 */
	public RankData() {
		this.identify = "";
		this.winTimes = 0;
		this.score = 0;
		this.rank = 0;
	}
	
	/**
	 * 带参构造函数
	 * 
	 * @param identify
	 * @param score
	 * @param isWin
	 */
	public RankData(String identify, int score, boolean isWin) {
		this.identify = identify;
		this.score = score;
		if (isWin) {
			this.winTimes ++;
		} else {
			this.winTimes = 0;
		}
	}
	
	/**
	 * 玩家唯一标识
	 */
	public String getIdentify() {
		return identify;
	}

	/**
	 * 玩家唯一标识
	 */
	public void setIdentify(String identify) {
		this.identify = identify;
	}

	/**
	 * 玩家连胜次数
	 */
	public int getWinTimes() {
		return winTimes;
	}

	/**
	 * 玩家连胜次数
	 */
	public void setWinTimes(int winTimes) {
		this.winTimes = winTimes;
	}

	/**
	 * 玩家积分
	 */
	public int getScore() {
		return score;
	}

	/**
	 * 玩家现有积分
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * 段位排名
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * 段位排名
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	/**
	 * 增加连胜次数
	 * 
	 * @return
	 */
	public int addWinTimes() {
		this.winTimes ++;
		return winTimes;
	}
	
	/**
	 * 清理连胜次数
	 * 
	 * @return
	 */
	public int clearWinTimes() {
		this.winTimes = 0;
		return winTimes;
	}
	
	/**
	 * 增加积分
	 * 
	 * @param score
	 * @return
	 */
	public int addScore(int score){
		this.score += score;
		return this.score;
	}
	
	/**
	 * 减积分
	 * 
	 * @param score
	 * @return
	 */
	public int minusScore(int score){
		this.score -= score;
		if(this.score < 0){
			this.score = 0;
		}
		return this.score;
	}
	
	@Override
	public boolean insert() throws Exception {
		String sql = String.format("INSERT INTO rank_data(identify, winTimes, score, rank) VALUES('%s', %d, %d, %d);", identify, winTimes, score, rank);
		PreparedStatement dbStatement = DBManager.getInstance().createPreparedStatement(sql);
		int rows = dbStatement.executeUpdate();
		dbStatement.close();
		return rows > 0;
	}

	@Override
	public boolean update() throws Exception {
		String sql = String.format("UPDATE rank_data SET winTimes='%d', score=%d ,rank=%d WHERE identify='%s';", winTimes, score, rank, identify);
		PreparedStatement dbStatement = DBManager.getInstance().createPreparedStatement(sql);
		int rows = dbStatement.executeUpdate();
		dbStatement.close();
		return rows > 0;
	}

	@Override
	public String toString() {
		return "identify:" + this.identify + "  rank:" + this.rank + " score:" + this.score;
	}
}
