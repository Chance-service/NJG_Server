package com.server.paynotice.mapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.server.paynotice.pojo.PayNoticeInfo;


/**
 * @author Administrator
 *
 */
public interface PayNoticeInfoMapper {

	/**
	 * 插入paynotice
	 * 
	 * @param 支付通知对象
	 * @return
	 */
	public int insert(PayNoticeInfo info);
	
	/**
	 * 更新充值状态
	 * @param statusCode
	 * @param id
	 * @return
	 */
	public int updateStatus(int statusCode,int id);
	
	
	/**
	 * @param statusCode 订单状态
	 * @param getProductTime 发货时间
	 * @param id 订单号id
	 * @return
	 */
	public int updateStatusAndPTime(int statusCode,Timestamp getProductTime,int id);

	
	/**
	 * @param orderNo
	 * @param sdkChannel
	 * @return
	 */
	
	public List<PayNoticeInfo> getPayNoticeInfo(String orderNo,String sdkChannel);
}
