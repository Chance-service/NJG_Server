package com.guaji.merge.db;

import java.sql.Connection;
import java.sql.SQLException;

public class MysqlAccessor {

	private DBSource dbSource;

	public MysqlAccessor(DBSource dbSource) {
		this.dbSource = dbSource;
	}

	protected <T> T execute(SimpleJdbcCallback<T> action) {
		return execute(action, true);
	}

	private <T> T execute(SimpleJdbcCallback<T> action, boolean flag) {
		Connection conn = null;
		try {
			conn = dbSource.getConnection();
			T result = action.doIt(conn);
			conn.close();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

interface SimpleJdbcCallback<T> {
	T doIt(Connection conn) throws SQLException;
}
