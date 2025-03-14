package com.server.paynotice.dbservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.server.paynotice.mapper.CancelUserMapper;
import com.server.paynotice.pojo.CancelUser;
import com.server.paynotice.util.DBUtil;

public class CancelUserService {

	private static Logger logger = Logger.getLogger(CancelUserService.class);
	
	
	public static List<CancelUser> queryUsersByUserid(String userid) {
		// TODO Auto-generated method stub
		List<CancelUser> list = new ArrayList<CancelUser>();
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			CancelUserMapper mapper = session.getMapper(CancelUserMapper.class);
			list = mapper.queryUsersByUserid(userid);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("",e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return list;
	}

	public static List<CancelUser> queryUsersByDays(int day) {
		// TODO Auto-generated method stub
		List<CancelUser> list = new ArrayList<CancelUser>();
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			CancelUserMapper mapper = session.getMapper(CancelUserMapper.class);
			list = mapper.queryUsersByDays(day);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("",e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return list;
	}

	public static int insertUser(String userid) {
		// TODO Auto-generated method stub
		int ret = -1;
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			CancelUserMapper mapper = session.getMapper(CancelUserMapper.class);
			ret = mapper.insertUser(userid);
		} catch (Exception e) {
			// TODO: handle exception
			if (session != null) {
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

	public static int delUser(String userid) {
		// TODO Auto-generated method stub
		int ret = -1;
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			CancelUserMapper mapper = session.getMapper(CancelUserMapper.class);
			ret = mapper.delUser(userid);
		} catch (Exception e) {
			// TODO: handle exception
			if (session != null) {
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

	public static int delUser(List<Integer> ids) {
		// TODO Auto-generated method stub
		int ret = -1;
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			CancelUserMapper mapper = session.getMapper(CancelUserMapper.class);
			ret = mapper.delUsers(ids);
		} catch (Exception e) {
			// TODO: handle exception
			if (session != null) {
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

}
