package com.guaji.game.yaya;

import org.guaji.os.MyException;

import net.sf.json.JSONObject;


public class YayaParam {
	private String puid;// puid
	private String transactionId;// 流水号
	private String goodsId;// 商品id
	private String goodsName;// 商品名称
	private int goodsPrice;// 商品单价
	private int goodsCount;// 商品数量
	private int totalPrice;// 商品总价
	private String currency;// 货币类型

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public int getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(int goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public int getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(int goodsCount) {
		this.goodsCount = goodsCount;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	static YayaParam valueOf(String param) {
		if (param != null && param.length() > 0) {
			try {
				YayaParam rechargeParam = new YayaParam();
				JSONObject jsonObject = JSONObject.fromObject(param);
				rechargeParam.puid = jsonObject.getString("puid").trim().toLowerCase();
				rechargeParam.transactionId = jsonObject.getString("transactionId");
				rechargeParam.goodsId = jsonObject.getString("goodsId");
				rechargeParam.goodsName = jsonObject.getString("goodsName");
				rechargeParam.goodsPrice = jsonObject.getInt("goodsPrice");
				rechargeParam.goodsCount = jsonObject.getInt("goodsCount");
				rechargeParam.totalPrice = jsonObject.getInt("totalPrice");
				rechargeParam.currency = jsonObject.getString("currency");
				return rechargeParam;
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return null;
	}
}
