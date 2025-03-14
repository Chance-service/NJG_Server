package com.server.paynotice.mapper;

import com.server.paynotice.pojo.QueryInfo;

public interface QueryInfoMapper {
	/**
	 * 根据userid查询数据
	 * @param userid
	 * @return
	 */
	public QueryInfo queryByUserId(String userid);
	/***
	 * 插入数据
	 * @param userid
	 * @return
	 */
	public int insertQueryInfo(String userid);
}
