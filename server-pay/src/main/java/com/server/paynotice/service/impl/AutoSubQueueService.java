package com.server.paynotice.service.impl;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.aliyun.openservices.ons.api.*;
import com.server.paynotice.common.Settings;
import com.server.paynotice.service.IAutoSubQueueService;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：Apr 23, 2019 12:26:21 PM 类说明
 */
public class AutoSubQueueService implements IAutoSubQueueService {

	private static Logger logger = Logger.getLogger(AutoSubQueueService.class);

	private static AutoSubQueueService singleton;
	private static Properties properties;
	private static Producer producer;

	public synchronized static AutoSubQueueService getInstance() {
		if (singleton == null) {
			singleton = new AutoSubQueueService();
		}
		return singleton;
	}

	public boolean startup() {
		properties = new Properties();
		properties.put(PropertyKeyConst.AccessKey, Settings.aliAccessKeyId);
		properties.put(PropertyKeyConst.SecretKey, Settings.aliccessKeySecret);
		properties.put(PropertyKeyConst.NAMESRV_ADDR, Settings.alinameSrvAddr);
		producer = ONSFactory.createProducer(properties);
		if (producer == null) {
			logger.error(String.format("createProducer error |Topic:{} msgBody:%s", Settings.topicName));
			return false;
		}
		producer.start();
		return true;
	}

	/**
	 * @param message   队列消息包
	 * @param expirTime 到期时间
	 * @return
	 */
	public boolean DeliveryMessage(String sdkChannel, String message, Long expirTime) {

		// 判断商品类型：设置相应的投递时间
		try {

			Message msg = new Message(Settings.topicName, sdkChannel, message.getBytes());
			msg.setStartDeliverTime(expirTime);
			SendResult sendResult = producer.send(msg);
			if (sendResult == null) {
				logger.error(String.format("Send mq message error msgBody:{%s}", message));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("Send mq message error msgBody:{%s}", message));
		}
		return true;
	}

	public void destroy() {
		properties.clear();
	}

}
