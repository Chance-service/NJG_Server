package com.server.paynotice.dbservice;

import java.sql.Timestamp;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.server.paynotice.mapper.AutoSubOrderMapper;
import com.server.paynotice.mapper.PayNoticeInfoMapper;
import com.server.paynotice.pojo.AutoSubOrder;
import com.server.paynotice.pojo.PayNoticeInfo;
import com.server.paynotice.util.DBUtil;

public class PayNoticeInfoService {

	private static Logger logger = Logger.getLogger(PayNoticeInfoService.class);

	/**
	 * 插入支付通知数据
	 * 
	 * @param info
	 * @return
	 */
	public static int insert(PayNoticeInfo info) {
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			PayNoticeInfoMapper mapper = session.getMapper(PayNoticeInfoMapper.class);
			int ret = mapper.insert(info);
			return ret;
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return 0;
	}

	/**
	 * @param info        订单常规信息
	 * @param autoSubInfo 订单自动订阅部分信息
	 * @return
	 */
	public static int insertOrderByTx(PayNoticeInfo info, AutoSubOrder autoSubInfo) {

		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			PayNoticeInfoMapper mapper = session.getMapper(PayNoticeInfoMapper.class);
			AutoSubOrderMapper autoSubMapper = session.getMapper(AutoSubOrderMapper.class);
			int ret = mapper.insert(info);
			autoSubInfo.setOrderId(info.getId());
			autoSubMapper.insert(autoSubInfo);
			session.commit();
			return ret;
		} catch (Exception e) {
			session.rollback();
			logger.error("", e);
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return 0;
	}

	/**
	 * @param statusCode 订单状态
	 * @param id         自动增长列订单编号
	 * @return
	 */
	public static boolean update(int statusCode, int id) {
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			PayNoticeInfoMapper mapper = session.getMapper(PayNoticeInfoMapper.class);
			int row = mapper.updateStatus(statusCode, id);
			return row > 0;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return false;
	}

	/**
	 * @param statusCode 订单状态
	 * @param updateTime 更新支付时间
	 * @param id         订单编号
	 * @return
	 */
	public static boolean update(int statusCode, Timestamp updateTime, int id) {
		// TODO Auto-generated method stub
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			PayNoticeInfoMapper mapper = session.getMapper(PayNoticeInfoMapper.class);
			// int row = mapper.updateStatus(statusCode, id);
			int row = mapper.updateStatusAndPTime(statusCode, updateTime, id);
			return row > 0;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return false;
	}

	/**
	 * @param orderNo    订单编号
	 * @param sdkChannel 支付渠道编号
	 * @return
	 */
	public static List<PayNoticeInfo> getOrderInfoByOrderNo(String orderNo, String sdkChannel) {
		// TODO Auto-generated method stub
		SqlSession session = null;
		try {
			session = DBUtil.getSession();
			PayNoticeInfoMapper mapper = session.getMapper(PayNoticeInfoMapper.class);
			List<PayNoticeInfo> rows = mapper.getPayNoticeInfo(orderNo, sdkChannel);
			return rows;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return null;
	}
}
