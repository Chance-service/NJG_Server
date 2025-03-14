package com.server.paynotice.pojo;

import java.sql.Timestamp;

public class PayNoticeInfo {
	/** 主键id */
	private int id;
	/** 服务器Id */
	private int sid;
	/** 订单类型 */
	private int orderType;
	/** 充值金额 */
	private double amount;
	/** 人民币汇率 */
	private double rmbRate;
	/** 用户id */
	private String uid;
	/** 平台用户id */
	private String puid;
	/** 游戏版本号 */
	private String version;
	/** 货币标识 */
	private String currency;
	/** 游戏名称 */
	private String gameName;
	/** 商品名称 */
	private String productName;
	/** 操作系统 */
	private String os;
	/** 渠道标识 */
	private String sdkChannel;
	/** 订单号 */
	private String orderNo;
	/** 支付通知url */
	private String payNoticeUrl;
	/** 支付状态码 */
	private int statusCode;

	/** 支付时间 */
	private Timestamp payTime;

	/** 游戏到账时间 */
	private Timestamp getProductTime;

	/** 平台支付时间 */
	private Timestamp pltformPayTime;
	

	
	
	/**
	 * @return 主键id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param 主键id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return 服务器id
	 */
	public int getSid() {
		return sid;
	}

	/**
	 * @param 服务器id
	 */
	public void setSid(int sid) {
		this.sid = sid;
	}

	/**
	 * @return 支付金额
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param 支付金额
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @return 人民币汇率
	 */
	public double getRmbRate() {
		return rmbRate;
	}

	/**
	 * @param 人民币汇率
	 */
	public void setRmbRate(double rmbRate) {
		this.rmbRate = rmbRate;
	}

	/**
	 * @return 用户id
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param 用户id
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return 游戏版本
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param 游戏版本
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return 货币标识
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param 货币标识
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return 游戏名称
	 */
	public String getGameName() {
		return gameName;
	}

	/**
	 * @param 游戏名称
	 */
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	/**
	 * @return 商品名称
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param 商品名称
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return 操作系统
	 */
	public String getOs() {
		return os;
	}

	/**
	 * @param 操作系统
	 */
	public void setOs(String os) {
		this.os = os;
	}

	/**
	 * @return 渠道标识
	 */
	public String getSdkChannel() {
		return sdkChannel;
	}

	/**
	 * @param 渠道标识
	 */
	public void setSdkChannel(String sdkChannel) {
		this.sdkChannel = sdkChannel;
	}

	/**
	 * @return 订单号
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * @param 订单号
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * @return 支付通知url
	 */
	public String getPayNoticeUrl() {
		return payNoticeUrl;
	}

	/**
	 * @param 支付通知url
	 */
	public void setPayNoticeUrl(String payNoticeUrl) {
		this.payNoticeUrl = payNoticeUrl;
	}

	/**
	 * @return 支付状态码
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @param 支付状态码
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return 支付通知时间
	 */
	public Timestamp getPayTime() {
		return payTime;
	}

	/**
	 * @param payTime 支付通知时间
	 */
	public void setPayTime(Timestamp payTime) {
		this.payTime = payTime;
	}

	public Timestamp getGetProductTime() {
		return getProductTime;
	}

	public void setGetProductTime(Timestamp getProductTime) {
		this.getProductTime = getProductTime;
	}

	public Timestamp getPltformPayTime() {
		return pltformPayTime;
	}

	public void setPltformPayTime(Timestamp pltformPayTime) {
		this.pltformPayTime = pltformPayTime;
	}


	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}
	


	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

}
