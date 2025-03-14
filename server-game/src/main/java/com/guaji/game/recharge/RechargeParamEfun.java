package com.guaji.game.recharge;

import org.guaji.os.MyException;

import net.sf.json.JSONObject;

public class RechargeParamEfun {
	private String pOrderId;
	private int opcode;
	private int eFunPaymentUserId;
	private String userId;
	private String currency;
	private String amount;
	private String gameCode;
	private String serverCode;
	private int stone;
	private String stoneType;
	private String md5Str;
	private String time;
	private String productId;
	private int activityExtra;
	private int orderStateMonth;
	private String platform;
	private int goodsCount =1 ;
	private int freePoint=0;
	
	
	
	public int getGoodsCount() {
		return goodsCount;
	}




	public void setGoodsCount(int goodsCount) {
		this.goodsCount = goodsCount;
	}




	public String getpOrderId() {
		return pOrderId;
	}




	public void setpOrderId(String pOrderId) {
		this.pOrderId = pOrderId;
	}




	public int getOpcode() {
		return opcode;
	}




	public void setOpcode(int opcode) {
		this.opcode = opcode;
	}




	public int geteFunPaymentUserId() {
		return eFunPaymentUserId;
	}




	public void seteFunPaymentUserId(int eFunPaymentUserId) {
		this.eFunPaymentUserId = eFunPaymentUserId;
	}




	public String getUserId() {
		return userId;
	}




	public void setUserId(String userId) {
		this.userId = userId;
	}




	public String getCurrency() {
		return currency;
	}




	public void setCurrency(String currency) {
		this.currency = currency;
	}




	public String getAmount() {
		return amount;
	}




	public void setAmount(String amount) {
		this.amount = amount;
	}




	public String getGameCode() {
		return gameCode;
	}




	public void setGameCode(String gameCode) {
		this.gameCode = gameCode;
	}




	public String getServerCode() {
		return serverCode;
	}




	public void setServerCode(String serverCode) {
		this.serverCode = serverCode;
	}




	public int getStone() {
		return stone;
	}




	public void setStone(int stone) {
		this.stone = stone;
	}




	public String getStoneType() {
		return stoneType;
	}




	public void setStoneType(String stoneType) {
		this.stoneType = stoneType;
	}




	public String getMd5Str() {
		return md5Str;
	}




	public void setMd5Str(String md5Str) {
		this.md5Str = md5Str;
	}




	public String getTime() {
		return time;
	}




	public void setTime(String time) {
		this.time = time;
	}




	public String getProductId() {
		return productId;
	}




	public void setProductId(String productId) {
		this.productId = productId;
	}




	public int getActivityExtra() {
		return activityExtra;
	}




	public void setActivityExtra(int activityExtra) {
		this.activityExtra = activityExtra;
	}




	public int getOrderStateMonth() {
		return orderStateMonth;
	}




	public void setOrderStateMonth(int orderStateMonth) {
		this.orderStateMonth = orderStateMonth;
	}




	public String getPlatform() {
		return platform;
	}




	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public int getFreePoint() {
		return freePoint;
	}

	public void setFreePoint(int freePoint) {
		this.freePoint = freePoint;
	}




	static RechargeParamEfun valueOf(String param) {
		if (param != null && param.length() > 0) {
			try {
				RechargeParamEfun rechargeParam = new RechargeParamEfun();
				JSONObject jsonObject = JSONObject.fromObject(param);
				rechargeParam.userId = jsonObject.getString("userId").trim();
				rechargeParam.pOrderId = jsonObject.getString("pOrderId");
				rechargeParam.platform = jsonObject.getString("platform").trim().toLowerCase();
				rechargeParam.amount = jsonObject.getString("amount");
				rechargeParam.productId = jsonObject.getString("productId");
				rechargeParam.opcode = jsonObject.getInt("opcode");
				rechargeParam.eFunPaymentUserId = jsonObject.getInt("eFunPaymentUserId");
				rechargeParam.currency = jsonObject.getString("currency");
				rechargeParam.gameCode = jsonObject.getString("gameCode");
				rechargeParam.serverCode = jsonObject.getString("serverCode");
				rechargeParam.stone = jsonObject.getInt("stone");
				rechargeParam.stoneType = jsonObject.getString("stoneType");
				rechargeParam.md5Str = jsonObject.getString("md5Str");
				rechargeParam.time = jsonObject.getString("time");
				rechargeParam.activityExtra = jsonObject.getInt("activityExtra");
				rechargeParam.orderStateMonth = jsonObject.getInt("orderStateMonth");
				if (jsonObject.containsKey("freePoint")) {
					rechargeParam.freePoint = Integer.parseInt(jsonObject.getString("freePoint"));
				}
				return rechargeParam;
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return null;
	}

	
	
}
