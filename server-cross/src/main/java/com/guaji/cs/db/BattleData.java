package com.guaji.cs.db;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 战斗数据
 */
public class BattleData extends DBOperation {

	/**
	 * 主键自增长
	 */
	private int id;
	
	/**
	 * 玩家标识
	 */
	private String identify;
	
	/**
	 * 战斗发起者
	 */
	private String initiator;
	
	/**
	 * 玩家是否战斗胜利
	 */
	private int winner;
	
	/**
	 * 玩家积分变化
	 */
	private int scoreChange;
	
	/**
	 * 战斗过程
	 */
	private byte[] battle;


	public BattleData() {
		identify = "";
		initiator = "";
		winner = 0;
		scoreChange = 0;
		this.battle = new byte[1];
	}

	public BattleData(String identify, String initiator, int winner, int scoreChange, byte[] battle) {
		this.identify = identify;
		this.initiator = initiator;
		this.winner = winner;
		this.scoreChange = scoreChange;
		this.battle = battle;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}
	
	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public int getWinner() {
		return winner;
	}

	public void setWinner(int winner) {
		this.winner = winner;
	}

	public int getScoreChange() {
		return scoreChange;
	}

	public void setScoreChange(int scoreChange) {
		this.scoreChange = scoreChange;
	}

	public byte[] getBattle() {
		return battle;
	}

	public void setBattle(byte[] battle) {
		this.battle = battle;
	}

	/**
	 * 新增记录
	 */
	@Override
	public boolean insert() throws Exception {
		String sql = String.format("INSERT INTO cross_battle(identify, initiator, winner, scoreChange, battle) VALUES('%s', '%s', '%d', '%d', ?);",
															 identify, initiator, winner, scoreChange);
		PreparedStatement dbStatement = DBManager.getInstance().createPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS);
		dbStatement.setBinaryStream(1, new ByteArrayInputStream(battle), battle.length);
		int rows = dbStatement.executeUpdate();
		ResultSet resultSet = dbStatement.getGeneratedKeys();
		if(resultSet.next()) {
			this.id = resultSet.getInt(1);
		}
		dbStatement.close();
		return rows > 0;
	}
	
	/**
	 * 删除战斗记录
	 */
	@Override
	public boolean delete() throws Exception {
		String sql = "DELETE FROM cross_battle WHERE id = ?";
		PreparedStatement dbStatement = DBManager.getInstance().createPreparedStatement(sql);
		dbStatement.setInt(1, this.id);
		int rows = dbStatement.executeUpdate();
		dbStatement.close();
		return rows > 0;
	}
}
