package com.server.paynotice.dbservice;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.server.paynotice.mapper.QueryInfoMapper;
import com.server.paynotice.pojo.QueryInfo;
import com.server.paynotice.util.DBUtil;

public class QueryInfoService {
	/**
	 * t_cancel_user_queryinfo insert
	 * 
	 * @param userid
	 * @return
	 */
	private static Logger logger = Logger.getLogger(QueryInfoService.class);

	public static int insertQueryInfo(String userid) {
		int ret = -1;
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			QueryInfoMapper mapper = session.getMapper(QueryInfoMapper.class);
			ret = mapper.insertQueryInfo(userid);
		} catch (Exception e) {
			// TODO: handle exception
			if (session != null) {
				// 回滚
				session.rollback();
			}
			logger.error("",e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return ret;
	}

	/**
	 * 查询t_cancel_user_queryinfo
	 * 
	 * @param zhangsan
	 * @return
	 */
	public static QueryInfo queryByUserId(String userid) {
		QueryInfo info = null;
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			QueryInfoMapper mapper = session.getMapper(QueryInfoMapper.class);
			info = mapper.queryByUserId(userid);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("",e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return info;
	}
}
