package com.server.paynotice.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：Apr 11, 2019 2:27:33 PM 自动订阅订单信息
 */
public class AutoSubOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2210710196692411259L;

	/**
	 * 对应 t_payinfo 中的id
	 */
	private int orderId;
	/**
	 * 支付平台账号信息 亚马逊检测自动订阅订单需要该参数
	 */
	private String platAccNum;
	/**
	 * 支付平台订单信息
	 */
	private String orderNum;
	/**
	 * 自动订阅到期时间
	 */
	private Date expirDate;

	/**
	 * 支付平台 标识
	 */
	private String sdkChannel;
	/**
	 * 状态 1 续订 0 已取消订阅
	 */
	private int status;
	/**
	 * 订单检测验证票据
	 */
	private String validateReceipt;

	/**
	 * 订阅次数 方便统计
	 */
	private String subOrderNumList;

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getPlatAccNum() {
		return platAccNum;
	}

	public void setPlatAccNum(String platAccNum) {
		this.platAccNum = platAccNum;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Date getExpirDate() {
		return expirDate;
	}

	public void setExpirDate(Date expirDate) {
		this.expirDate = expirDate;
	}

	public String getSdkChannel() {
		return sdkChannel;
	}

	public void setSdkChannel(String sdkChannel) {
		this.sdkChannel = sdkChannel;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getValidateReceipt() {
		return validateReceipt;
	}

	public void setValidateReceipt(String validateReceipt) {
		this.validateReceipt = validateReceipt;
	}

	public String getSubOrderNumList() {
		return subOrderNumList;
	}

	public void setSubOrderNumList(String subOrderNumList) {
		this.subOrderNumList = subOrderNumList;
	}

	
}
