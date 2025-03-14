package org.guaji.db.mysql;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.guaji.app.App;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;
import org.guaji.util.GuaJiTickable;

public class MysqlSession extends GuaJiTickable {
	/**
	 * 数据库连接路径
	 */
	private String  dbHost;
	/**
	 * 用户名
	 */
	private String  dbUser;
	/**
	 * 密码
	 */
	private String  dbPwd;
	/**
	 * 持有的连接池
	 */
	private List<MysqlConnection> connections;
	
	/**
	 * 构造函数
	 */
	public MysqlSession() {
		connections = new LinkedList<MysqlConnection>();
		if (App.getInstance() != null) {
			App.getInstance().addTickable(this);
		}
	}

	/**
	 * 初始化连接
	 * 
	 * @return
	 */
	public boolean init(String dbHost, String dbUser, String dbPwd, int poolSize) {
		try {
			this.dbHost = dbHost;
			this.dbUser = dbUser;
			this.dbPwd = dbPwd;
			
			for (int i=0; i<poolSize; i++) {
				MysqlConnection connection = new MysqlConnection();
				if (!connection.init(dbHost, dbUser, dbPwd)) {
					return false;
				}
				connections.add(connection);
			}
			return true;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}
	
	/**
	 * 获取db地址
	 * 
	 * @return
	 */
	public String getDbHost() {
		return dbHost;
	}

	/**
	 * 获取db地址
	 * 
	 * @return
	 */
	public String getDbUser() {
		return dbUser;
	}

	/**
	 * 获取db用户密码
	 * 
	 * @return
	 */
	public String getDbPwd() {
		return dbPwd;
	}
	
	/**
	 * 判断会话是否有效
	 * 
	 * @return
	 */
	public boolean isValid() {
		for (MysqlConnection connection : connections) {
			if (connection != null && connection.isValid()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 创建statement
	 * 
	 * @return
	 */
	public synchronized Statement createStatement() {
		Statement statement = null;
		try {
			int threadIdx = (int) (OSOperator.getThreadId() % connections.size());
			MysqlConnection connection = connections.get(threadIdx);
			statement = connection.createStatement();
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return statement;
	}
	
	/**
	 * 创建statement
	 * 
	 * @return
	 */
	public synchronized Statement prepareStatement(String sql) {
		Statement statement = null;
		try {
			int threadIdx = (int) (OSOperator.getThreadId() % connections.size());
			MysqlConnection connection = connections.get(threadIdx);
			statement = connection.prepareStatement(sql);
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return statement;
	}
	
	/**
	 * 直接执行sql语句
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public int executeSql(String sql) {
		Statement statement = null;
		try {
			statement = createStatement();
			int rowCount = statement.executeUpdate(sql);
			return rowCount;
		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		}
		return 0;
	}
	
	/**
	 * 获取名字
	 */
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * 帧更新
	 */
	@Override
	public void onTick() {
		for (MysqlConnection connection : connections) {
			if (connection != null) {
				connection.keepAlive();
			}
		}
	}
}
