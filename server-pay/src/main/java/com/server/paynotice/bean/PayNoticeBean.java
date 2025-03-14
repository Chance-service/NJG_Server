package com.server.paynotice.bean;

public class PayNoticeBean {
	
	/**服务器ID*/
	private int serverId;
	
	/**sdk渠道标识*/
	private String sdkChannel;
	
	/**支付通知url*/
	private String baseUrl;
	
	/**
	 * 服务器id
	 * @return
	 */
	public int getServerId() {
		return serverId;
	}
	/**
	 * 服务器id
	 * @param serverId
	 */
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	/**
	 * sdk渠道名
	 * @return
	 */
	public String getSdkChannel() {
		return sdkChannel;
	}
	/**
	 * sdk渠道名
	 * @param sdkChannel
	 */
	public void setSdkChannel(String sdkChannel) {
		this.sdkChannel = sdkChannel;
	}
	/**
	 * 游戏服接收支付通知地址
	 * @return
	 */
	public String getBaseUrl() {
		return baseUrl;
	}
	/**
	 * 游戏服接收通知地址
	 * @param baseUrl
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
