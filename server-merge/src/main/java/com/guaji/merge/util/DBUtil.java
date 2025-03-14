package com.guaji.merge.util;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.ibatis.jdbc.ScriptRunner;

import com.guaji.merge.App;
import com.guaji.merge.config.DbConfigInfo;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.config.db.Field;
import com.guaji.merge.config.db.Table;
import com.guaji.merge.db.C3P0Impl;

public class DBUtil {

	private static Log logger = App.logger;
	private static Log exception = App.exception;

	public static boolean checkTable(String dbname, C3P0Impl impl, DbInfo dbInfo) {
		boolean isSuccess = true;
		Map<String, Object> tableNameMap = getAllTables(dbname, impl);
		logger.info("开始检查数据库【" + dbname + "】中的表是否匹配");
		for (Table table : dbInfo.getTableList()) {
			if (tableNameMap.get(table.getName()) == null) {
				logger.info("dbInfo.xml中存在表【" + table.getName() + "】,但是数据库【" + dbname + "】中不存在此表");
				isSuccess = false;
			}
		}

		Iterator<String> iterator = tableNameMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (dbInfo.getTableMap().get(key) == null) {
				logger.info("数据库【" + dbname + "】中存在表【" + key + "】,但是dbInfo.xml中没有定义");
				isSuccess = false;
			}
		}

		logger.info("检查数据库【" + dbname + "】中的表匹配【完成】☺☺☺☺☺☺☺☺☺");
		logger.info("开始检查数据库【" + dbname + "】表中的字段是否匹配");
		for (Table table : dbInfo.getTableList()) {
			Map<String, Object> fieldMap = getTableField(table.getName(), impl, dbname);
			for (Field field : table.getFieldList()) {
				if (fieldMap.get(field.getName()) == null) {
					logger.info("dbInfo.xml中表【" + table.getName() + "】存在字段" + field.getName() + ",但是数据库【" + dbname
							+ "】中不存在此字段");
					isSuccess = false;
				}
			}

			Iterator<String> fIterator = fieldMap.keySet().iterator();
			while (fIterator.hasNext()) {
				String key = fIterator.next();
				if (table.getField(key) == null) {
					logger.info("数据库【" + dbname + "】的表【" + table.getName() + "】存在字段【" + key + "】,但是dbInfo.xml中不存在此字段");
					isSuccess = false;
				}
			}
		}
		logger.info("检查数据库【" + dbname + "】中的表字段匹配【完成】☺☺☺☺☺☺☺☺☺");
		return isSuccess;
	}

	public static Map<String, Object> getAllTables(String dbName, C3P0Impl impl) {
		String table_name = "table_name";
		String tableSql = "SELECT " + table_name + " FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + dbName
				+ "'";
		List<Map<String, Object>> tabelMaps = impl.executeQuery(tableSql);
		Map<String, Object> tableNameMap = new HashMap<String, Object>();
		for (Map<String, Object> tm : tabelMaps) {
			String tableName = String.valueOf(tm.get(table_name));
			tableNameMap.put(tableName, new Object());
		}
		return tableNameMap;
	}

	public static Map<String, Object> getTableField(String tableName, C3P0Impl impl, String dbName) {
		String fieldSql = "select  column_name  from Information_schema.columns  where table_Name = '" + tableName
				+ "' and TABLE_SCHEMA='" + dbName + "';";
		List<Map<String, Object>> fieldMaps = impl.executeQuery(fieldSql);
		Map<String, Object> map = new HashMap<String, Object>();
		for (Map<String, Object> fm : fieldMaps) {
			String columnName = String.valueOf(fm.get("column_name"));
			map.put(columnName, new Object());
		}
		return map;
	}

	/**
	 * @param dbname 数据库
	 * @param dbInfo 数据库配置
	 * @return
	 */
	public static boolean checkDataBase(String dbname, DbConfigInfo dbInfo) {

		Connection conn = null;
		Statement stat = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String url = String.format("jdbc:mysql://%s:%s/test?useUnicode=true", dbInfo.getIp(), dbInfo.getPort());

			conn = DriverManager.getConnection(url, dbInfo.getUsername(), dbInfo.getPassword());// 建立连接
			conn.setAutoCommit(false);
			stat = conn.createStatement();
			// 判断数据库是否存在
			String checkdatabase = String.format("show databases like \"%s\"", dbname);
			String createdatabase = String
					.format("create  database  %s default character set utf8mb4 collate utf8mb4_general_ci", dbname);
			// String dropdatabase = String.format("drop database %s", dbname);
			stat = (Statement) conn.createStatement();
			ResultSet resultSet = stat.executeQuery(checkdatabase);
			// 存在 删除数据库重建
			if (!resultSet.next())
				stat.executeUpdate(createdatabase);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (stat != null) {
					stat.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * @param ip
	 * @param port
	 * @param userName
	 * @param pwd
	 * @param sqlFilePath
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public static boolean execSqlFileByMysql(DbConfigInfo dbInfo, String sqlFilePath) {

		String url = "jdbc:mysql://" + dbInfo.getIp() + ":" + dbInfo.getPort() + "/" + dbInfo.getDb();
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, dbInfo.getUsername(), dbInfo.getPassword());
			conn.setAutoCommit(false);
			ScriptRunner runner = new ScriptRunner(conn);
			runner.setStopOnError(true);
			runner.setSendFullScript(false);
			runner.setDelimiter(";");
			runner.setFullLineDelimiter(false);
			runner.setLogWriter(null);
			runner.runScript(new InputStreamReader(new FileInputStream(sqlFilePath), "utf-8"));
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			exception.error(e);
			return false;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}

	public static boolean execBatchSql(DbConfigInfo dbInfo, List<String> sqlArr) {

		String url = "jdbc:mysql://" + dbInfo.getIp() + ":" + dbInfo.getPort() + "/" + dbInfo.getDb()
				+ "?characterEncoding=utf8";
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, dbInfo.getUsername(), dbInfo.getPassword());
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			for (String sql : sqlArr) {
				stmt.addBatch(sql);
			}
			stmt.executeBatch();
			conn.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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
