package com.guaji.merge.db;

import java.sql.Connection;

import com.guaji.merge.config.DbConfigInfo;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库连接池
 * 
 * @author Administrator
 * 
 */
public class DBSource {

	private ComboPooledDataSource ds;

	public DBSource(DbConfigInfo dbInfo) {
		try {
			ds = new ComboPooledDataSource();
			ds.setDriverClass("com.mysql.cj.jdbc.Driver");
			ds.setJdbcUrl("jdbc:mysql://" + dbInfo.getIp() + ":"
					+ dbInfo.getPort() + "/" + dbInfo.getDb()+"?useUnicode=true&autoReconnect=true&failOverReadOnly=false");
			ds.setUser(dbInfo.getUsername());
			ds.setPassword(dbInfo.getPassword());
			ds.setMaxPoolSize(10);
			ds.setMinPoolSize(2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ComboPooledDataSource getDs() {
		return ds;
	}

	public Connection getConnection() {
		Connection con = null;
		try {
			con = ds.getConnection();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return con;
	}
}
