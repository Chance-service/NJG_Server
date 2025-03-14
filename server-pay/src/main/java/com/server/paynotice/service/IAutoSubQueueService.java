package com.server.paynotice.service;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：Apr 23, 2019 12:10:58 PM 类说明
 */
public interface IAutoSubQueueService {
	/**
	 * @return true 启动成功 false 启动失败 启动消息队列服务
	 */
	public boolean startup();

	/**
	 * @param sdkChannel 支付渠道编号
	 * @param message    投递的消息
	 * @param expirTime  队列触发时间
	 * @return true 投递成功 false 投递失败
	 */
	public boolean DeliveryMessage(String sdkChannel, String message, Long expirTime);

	/**
	 * 程序关闭 销毁消息队列服务
	 */
	public void destroy();
}
