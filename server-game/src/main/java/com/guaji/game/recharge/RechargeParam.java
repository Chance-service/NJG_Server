package com.guaji.game.recharge;

import org.guaji.os.MyException;

import net.sf.json.JSONObject;

public class RechargeParam {
	private String payMoney;
	private int goodsCount;
	private String goodsId;
	private String orderSerial;
	private String puid;
	private String platform;
	private long expirtTime;
	private int serverId;
	private boolean test;
	private String status;
	

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
	
	public String getstatus() {
		return status;
	}

	public static RechargeParam valueOf(String param) {
		if (param != null && param.length() > 0) {
			try {
				RechargeParam rechargeParam = new RechargeParam();
				JSONObject jsonObject = JSONObject.fromObject(param);
				rechargeParam.puid = jsonObject.getString("puid").trim().toLowerCase();
				rechargeParam.orderSerial = jsonObject.getString("orderSerial");
				rechargeParam.platform = jsonObject.getString("platform").trim().toLowerCase();
				rechargeParam.payMoney = jsonObject.getString("payMoney");
				rechargeParam.goodsId = jsonObject.getString("goodsId");
				rechargeParam.goodsCount = jsonObject.getInt("goodsCount");
				rechargeParam.serverId = jsonObject.getInt("serverId");
				String testStr = jsonObject.getString("test");
				rechargeParam.test = testStr.equals("true");
				//周卡到期时间
				if(jsonObject.containsKey("expirtTime"))
					rechargeParam.expirtTime=Long.parseLong(jsonObject.getString("expirtTime"));
				else
					rechargeParam.expirtTime=0;
				if(jsonObject.containsKey("status"))
					rechargeParam.status=jsonObject.getString("status");
				else
					rechargeParam.status="-2";

				
				return rechargeParam;
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return null;
	}
}
