package com.guaji.merge.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.config.db.MergeHandler;

public class C3P0Impl extends MysqlAccessor {

	private QueryRunner queryRunner;
	private MergeHandler handler;

	public C3P0Impl(DBSource dbSource) {
		super(dbSource);
		queryRunner = new QueryRunner();
	}

	public List<Map<String, Object>> executeQuery(final String sql) {
		return execute(new SimpleJdbcCallback<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> doIt(Connection conn) throws SQLException {
				return queryRunner.query(conn, sql, new MapListHandler());
			}
		});
	}

	public List<String> executeQuery(final String sql, final String tablename, final DbInfo db, final int serverId,
			final boolean mergeOver) {
		handler = new MergeHandler(tablename, db, serverId, mergeOver);
		return execute(new SimpleJdbcCallback<List<String>>() {
			@Override
			public List<String> doIt(Connection conn) throws SQLException {
				return queryRunner.query(conn, sql, handler);
			}
		});
	}

	public long insert(String sql, Object... args) {
		return executeUpdate(sql, args);
	}

	public long executeUpdate(final String sql, final Object... args) {
		return execute(new SimpleJdbcCallback<Long>() {
			@Override
			public Long doIt(Connection conn) throws SQLException {
				long id = queryRunner.update(conn, sql, args);
				return id;
			}
		});
	}

	/**
	 * @param sqlCommond
	 * @return
	 */
	public boolean executeBatchSql(String sqlCommond, Object[][] params) {
		try {
			this.queryRunner.batch(sqlCommond, params);
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				this.queryRunner.getDataSource().getConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		}
	}

	public Object getCurrentMinPrimaryKeyValue() {
		return handler.getCurrentMinPrimaryKeyValue();
	}

}
