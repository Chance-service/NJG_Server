package com.server.paynotice.mapper;

import java.sql.Timestamp;

import com.server.paynotice.pojo.AutoSubOrder;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：Apr 11, 2019 2:23:43 PM
* 类说明
*/
public interface AutoSubOrderMapper {
	/**
	 * @param info  自动订单概要信息
	 * @return 返回插入的自动增长列编号
	 */
	public int insert(AutoSubOrder info);
	
	/**
	 * @param orderId 订单编号
	 * @param statusCode 状态标识 1 续订状态 0 已取消订阅
	 * @return
	 */
	public int updateStatus(int orderId,int statusCode);

	/**
	 * @param orderId 订单编号
	 * @param exprieDate 续订到期时间
	 * @return
	 */
	public int updateExprieDate(int orderId,Timestamp exprieDate);


}
