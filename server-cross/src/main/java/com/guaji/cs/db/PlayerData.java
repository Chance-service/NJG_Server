package com.guaji.cs.db;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;

import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;

/**
 * 玩家数据集合
 */
public class PlayerData extends DBOperation {
	/**
	 * 玩家唯一标识
	 */
	private String identify;

	/**
	 * 服务器名字
	 */
	private String serverName;

	/**
	 * 玩家战斗数据
	 */
	private PlayerSnapshotInfo snapshotInfo;

	/**
	 * 构造玩家数据
	 */
	public PlayerData() {
		identify = "";
		serverName = "";
	}

	/**
	 * 是否有效
	 * 
	 * @return
	 */
	public boolean isValid() {
		return identify != null && identify.length() > 0 && snapshotInfo != null;
	}

	/**
	 * 获取唯一标识
	 * 
	 * @return
	 */
	public String getIdentify() {
		return identify;
	}

	/**
	 * 设置玩家唯一标识
	 * 
	 * @param identify
	 */
	public void setIdentify(String identify) {
		this.identify = identify;
	}

	/**
	 * 获取服务器名字
	 * 
	 * @return
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * 设置服务器名字
	 * 
	 * @param playerName
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * 获取玩家快照数据
	 * 
	 * @return
	 */
	public PlayerSnapshotInfo getSnapshot() {
		return snapshotInfo;
	}

	/**
	 * 设置玩家快照数据
	 * 
	 * @param snapshotInfo
	 */
	public void setSnapshot(PlayerSnapshotInfo snapshotInfo) {
		this.snapshotInfo = snapshotInfo;
	}
	
	/**
	 * 新增记录
	 */
	@Override
	public boolean insert() throws Exception {
		String sql = String.format("INSERT INTO player_data(identify, serverName, snapshotInfo) VALUES('%s', '%s',  ?);", identify, serverName);
		PreparedStatement dbStatement = DBManager.getInstance().createPreparedStatement(sql);
		dbStatement.setBinaryStream(1, new ByteArrayInputStream(snapshotInfo.toByteArray()), snapshotInfo.toByteArray().length);
		int rows = dbStatement.executeUpdate();
		return rows > 0;
	}

	/**
	 * 更新玩家数据
	 */
	@Override
	public boolean update() throws Exception {
		String sql = String.format("UPDATE player_data SET snapshotInfo=? WHERE identify='%s';", identify);
		PreparedStatement dbStatement = DBManager.getInstance().createPreparedStatement(sql);
		dbStatement.setBinaryStream(1, new ByteArrayInputStream(snapshotInfo.toByteArray()), snapshotInfo.toByteArray().length);
		int rows = dbStatement.executeUpdate();
		return rows > 0;
	}

}
