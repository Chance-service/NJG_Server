package com.server.paynotice.dbservice;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.server.paynotice.mapper.SnapshotMapper;
import com.server.paynotice.pojo.Snapshot;
import com.server.paynotice.util.DBUtil;

public class SnapshotService {
	private static Logger logger = Logger.getLogger(SnapshotService.class);

	public static int insertSnapshot(int action, String userid) {
		// TODO Auto-generated method stub
		int ret = -1;
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			SnapshotMapper mapper = session.getMapper(SnapshotMapper.class);
			mapper.insertSnapshot(action, userid);
		} catch (Exception e) {
			// TODO: handle exception
			if (session != null) {
				session.rollback();
			}
			logger.error("", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return ret;
	}

	public static int insertSnapshots(List<Snapshot> snapshots) {
		// TODO Auto-generated method stub
		int ret = -1;
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			SnapshotMapper mapper = session.getMapper(SnapshotMapper.class);
			mapper.insertSnapshots(snapshots);
		} catch (Exception e) {
			// TODO: handle exception
			if (session != null) {
				session.rollback();
			}
			logger.error("", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return ret;
	}

	public static int delSnapshotByDays(int days) {
		// TODO Auto-generated method stub
		int ret = -1;
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			SnapshotMapper mapper = session.getMapper(SnapshotMapper.class);
			ret = mapper.delSnapshotByDays(days);
		} catch (Exception e) {
			// TODO: handle exception
			if (session != null) {
				session.rollback();
			}
			logger.error("", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return ret;

	}

	public static List<Snapshot> querySnapshots(Timestamp lastTime) {
		// TODO Auto-generated method stub
		SqlSession session = null;
		List<Snapshot> list = null;
		try {
			session = DBUtil.getSession();
			SnapshotMapper mapper = session.getMapper(SnapshotMapper.class);
			list = mapper.querySnapshots(lastTime);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return list == null ? new ArrayList<Snapshot>() : list;
	}

	public static List<Snapshot> queryUserSnapshots(Timestamp lastTime, String userid) {
		SqlSession session = null;
		List<Snapshot> list = null;
		try {
			session = DBUtil.getSession();
			SnapshotMapper mapper = session.getMapper(SnapshotMapper.class);
			list = mapper.queryUserSnapshots(lastTime, userid);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return list;
	}
}
