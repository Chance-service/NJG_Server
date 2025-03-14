package com.server.paynotice.bean;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

public class GuaJiRechargeParam {
	private static final Logger logger = Logger.getLogger(GuaJiRechargeParam.class);

	private String payMoney;
	private int goodsCount;
	private String goodsId;
	private String orderSerial;
	private String puid;
	private String platform;
	private long expirtTime;
	private int serverId;
	private boolean test;

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public int getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(int goodsCount) {
		this.goodsCount = goodsCount;
	}

	public String getOrderSerial() {
		return orderSerial;
	}

	public void setOrderSerial(String orderSerial) {
		this.orderSerial = orderSerial;
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public long getExpirtTime() {
		return expirtTime;
	}

	public void setExpirtTime(long expirtTime) {
		this.expirtTime = expirtTime;
	}

	public static GuaJiRechargeParam valueOf(String param) {
		if (param != null && param.length() > 0) {
			try {
				GuaJiRechargeParam rechargeParam = new GuaJiRechargeParam();
				JSONObject jsonObject = JSONObject.fromObject(param);
				rechargeParam.puid = jsonObject.getString("puid").trim().toLowerCase();
				rechargeParam.orderSerial = jsonObject.getString("orderSerial");
				rechargeParam.platform = jsonObject.getString("platform").trim().toLowerCase();
				rechargeParam.payMoney = jsonObject.getString("payMoney");
				rechargeParam.goodsId = jsonObject.getString("goodsId");
				rechargeParam.goodsCount = jsonObject.getInt("goodsCount");
				rechargeParam.serverId = jsonObject.getInt("serverId");
				String isTestStr = jsonObject.getString("isTest");
				rechargeParam.test = isTestStr.equals("true");
				if (jsonObject.containsKey("expirtTime"))
					rechargeParam.expirtTime = jsonObject.getLong("expirtTime");
				
				return rechargeParam;
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return null;
	}

}
