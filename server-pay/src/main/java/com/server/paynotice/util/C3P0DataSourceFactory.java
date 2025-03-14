package com.server.paynotice.util;

import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0DataSourceFactory extends PooledDataSourceFactory {
	public C3P0DataSourceFactory() {
		// TODO Auto-generated constructor stub
		this.dataSource = new ComboPooledDataSource();
	}
}
