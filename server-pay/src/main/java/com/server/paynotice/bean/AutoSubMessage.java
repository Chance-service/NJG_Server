package com.server.paynotice.bean;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：Apr 23, 2019 12:02:54 PM 类说明
 * 
 *	订阅类消息投递类
 */
public class AutoSubMessage {
	private int orderId;
	private String sdkChannel;
	
	
	public AutoSubMessage(int orderId, String sdkChannel) {
		super();
		this.orderId = orderId;
		this.sdkChannel = sdkChannel;
	}
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getSdkChannel() {
		return sdkChannel;
	}
	public void setSdkChannel(String sdkChannel) {
		this.sdkChannel = sdkChannel;
	}
	
}
