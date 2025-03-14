package com.guaji.game.module.activity.recharge;

import java.util.HashMap;
import java.util.Map;

/**
 * 首冲翻倍记录
 * @author xulinqs
 *
 */
public class FirstRechargeStatus {

	/**
	 * 充值信息
	 */
	private Map<Integer, Integer> rechargeInfo = new HashMap<Integer, Integer>();
	
	/**
	 * 根据商品id获得充值额度
	 * @param goodsId
	 * @return
	 */
	public int getRecharge(int goodsId) {
		if(rechargeInfo.containsKey(goodsId)) {
			return rechargeInfo.get(goodsId);
		}
		return 0;
	}
	
	/**
	 * 设置商品充值
	 */
	public void setRecharge(int goodsId, int rechargeNum) {
		if(!rechargeInfo.containsKey(goodsId)) {
			rechargeInfo.put(goodsId, rechargeNum);
		}else{
			rechargeInfo.put(goodsId, rechargeInfo.get(goodsId) + rechargeNum);
		}
	}

	public void clear() {
		this.rechargeInfo.clear();
	}
	
}
