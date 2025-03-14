package com.guaji.cs.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;

import com.google.protobuf.InvalidProtocolBufferException;
import com.guaji.cs.CrossServer;
import com.guaji.cs.battle.BattleService;
import com.guaji.cs.tick.ITickable;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException;

/**
 * 战斗数据库管理器
 */
public class DBManager implements ITickable {

	/**
	 * 数据库主机地址
	 */
	private String dbHost;

	/**
	 * 数据库用户名
	 */
	private String dbUser;

	/**
	 * 数据库登陆密码
	 */
	private String dbPwd;

	/**
	 * 数据库连接
	 */
	private Connection dbConnection;

	/**
	 * 数据库对象
	 */
	private Statement dbStatement;

	/**
	 * 数据库存储操作对象
	 */
	private List<DBOperation> dbOpList;

	/**
	 * 枕更新时间
	 */
	private long tickTime = 0L;

	/**
	 * 数据库管理器单例对象
	 */
	private static final DBManager instance = new DBManager();


	private DBManager() {
		dbOpList = new LinkedList<DBOperation>();
		CrossServer.getInstance().addTickable(this);
	}
	
	/**
	 * 获取数据库管理器单例对象
	 * 
	 * @return
	 */
	public static DBManager getInstance() {
		return instance;
	}

	/**
	 * 初始化数据库连接
	 * 
	 * @param dbHost
	 * @param dbUser
	 * @param dbPwd
	 * @return
	 */
	public boolean init(String dbHost, String dbUser, String dbPwd) {
		this.dbHost = dbHost;
		this.dbUser = dbUser;
		this.dbPwd = dbPwd;
		return doConnect();
	}

	/**
	 * 进行连接
	 * 
	 * @return
	 */
	private boolean doConnect() {
		try {
			// 加载驱动程序
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			dbConnection = DriverManager.getConnection(dbHost, dbUser, dbPwd);
			dbStatement = dbConnection.prepareStatement("");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dbConnection != null && dbStatement != null;
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 */
	public Connection getConnection() {
		return dbConnection;
	}

	/**
	 * 获取
	 * 
	 * @return
	 */
	public Statement getStatement() {
		return dbStatement;
	}

	/**
	 * 创建
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement createPreparedStatement(String sql) throws SQLException {
		if (dbConnection != null) {
			return dbConnection.prepareStatement(sql);
		}
		return null;
	}
	
	/**
	 * 创建
	 * 
	 * @param sql
	 * @param keys
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement createPreparedStatement(String sql, int keys) throws SQLException {
		if (dbConnection != null) {
			return dbConnection.prepareStatement(sql, keys);
		}
		return null;
	}

	/**
	 * 加载跨服战数据
	 */
	public void loadCrossData() {
		// 加载玩家数据
		Collection<PlayerData> players = this.loadPlayers();
		// 加载战报数据
		Collection<BattleData> battles = this.loadBattleData();
		// 加载排行数据
		Collection<RankData> ranks = this.loadRankData();
		// 设置到对应的缓存容器中
		BattleService.getInstance().setPayerMap(players);
		BattleService.getInstance().setBattleMap(battles);
		BattleService.getInstance().setRankList(ranks);
	}

	/**
	 * 清空所有数据
	 */
	public void clearCrossData() {
		try {
			dbStatement.addBatch("TRUNCATE player_data");
			dbStatement.addBatch("TRUNCATE rank_data");
			dbStatement.addBatch("TRUNCATE cross_battle");
			dbStatement.executeBatch();
			Log.logPrintln("Clear cross battle data");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从数据库加载跨服竞技玩家数据
	 * 
	 * @return
	 */
	private Collection<PlayerData> loadPlayers() {
		LinkedList<PlayerData> playerList = new LinkedList<PlayerData>();
		String sql = String.format("SELECT * FROM player_data;");
		ResultSet resultSet = null;
		try {
			resultSet = dbStatement.executeQuery(sql);
			while (resultSet.next()) {
				PlayerData playerData = new PlayerData();
				playerData.setIdentify(resultSet.getString("identify"));
				playerData.setServerName(resultSet.getString("serverName"));
				try {
					PlayerSnapshotInfo snapshotInfo = PlayerSnapshotInfo.parseFrom(resultSet.getBytes("snapshotInfo"));
					playerData.setSnapshot(snapshotInfo);
				} catch (InvalidProtocolBufferException e) {
					e.printStackTrace();
				}
				if (playerData.isValid()) {
					playerList.add(playerData);
					Log.logPrintln("Load Player OK, Identify: " + playerData.getIdentify());
				} else {
					Log.errPrintln("Load Player Failed, Identify: " + playerData.getIdentify());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return playerList;
	}
	
	/**
	 * 加载排行数据
	 * 
	 * @return
	 */
	private Collection<RankData> loadRankData() {
		LinkedList<RankData> rankList = new LinkedList<RankData>();
		String sql = String.format("SELECT * FROM rank_data ORDER BY rank ASC;");
		ResultSet resultSet = null;
		try {
			resultSet = dbStatement.executeQuery(sql);
			while (resultSet.next()) {
				RankData battleData = new RankData();
				battleData.setIdentify(resultSet.getString("identify"));
				battleData.setWinTimes(resultSet.getInt("winTimes"));
				battleData.setScore(resultSet.getInt("score"));
				battleData.setRank(resultSet.getInt("rank"));
				rankList.add(battleData);
			}
			Log.logPrintln("rank_data is ok");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return rankList;
	}

	/**
	 * 加载战斗过程数据
	 * 
	 * @return
	 */
	private Collection<BattleData> loadBattleData() {
		LinkedList<BattleData> battleList = new LinkedList<BattleData>();
		String sql = String.format("SELECT * FROM cross_battle;");
		ResultSet resultSet = null;
		try {
			resultSet = dbStatement.executeQuery(sql);
			while (resultSet.next()) {
				BattleData battleData = new BattleData();
				battleData.setId(resultSet.getInt("id"));
				battleData.setIdentify(resultSet.getString("identify"));
				battleData.setInitiator(resultSet.getString("initiator"));
				battleData.setWinner(resultSet.getInt("winner"));
				battleData.setScoreChange(resultSet.getInt("scoreChange"));
				battleData.setBattle(resultSet.getBytes("battle"));
				battleList.add(battleData);
			}
			Log.logPrintln("cross_battle is ok");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return battleList;
	}

	/**
	 * 帧更新
	 */
	@Override
	public void onTick() {
		if (GuaJiTime.getMillisecond() - tickTime > 60000) {
			tickTime = GuaJiTime.getMillisecond();
			try {
				DBManager.getInstance().getStatement().executeQuery("select 1");
			} catch (SQLException e) {
				Log.errPrintln("mysql connection keep alive error");
				MyException.catchException(e);
			}
		}
		// 刷新db存储
		if (dbOpList.size() > 0) {
			synchronized (dbOpList) {
				flushDbOperation(false);
			}
		}
	}

	/**
	 * 是否有缓存的DB存储操作
	 * 
	 * @param dbOp
	 */
	public boolean hasCachedDbOperation() {
		return dbOpList.size() > 0;
	}

	/**
	 * 添加DB存储操作
	 * 
	 * @param dbOp
	 * @param opType
	 */
	public void addDbOperation(DBOperation dbOp, int opType) {
		if (dbOp != null && dbOp.setOpType(opType)) {
			synchronized (dbOpList) {
				dbOpList.add(dbOp);
			}
		}
	}

	/**
	 * DB存储更新操作
	 * 
	 * @param flushAll
	 */
	public void flushDbOperation(boolean flushAll) {
		Iterator<DBOperation> it = dbOpList.iterator();
		while (it.hasNext()) {
			DBOperation dbOp = (DBOperation) it.next();
			boolean connGoaway = false;
			try {
				dbOp.execute();
			} catch (CommunicationsException | MySQLNonTransientConnectionException e2) {
				connGoaway = true;
				doConnect();
				Log.errPrintln("Mysql Connection Terminate, Try Reconnect...");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!flushAll) {
				if (!connGoaway) {
					it.remove();
				}
				break;
			}
		}
		if (flushAll) {
			dbOpList.clear();
		}
	}
}
