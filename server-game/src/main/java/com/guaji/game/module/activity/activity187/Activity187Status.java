package com.guaji.game.module.activity.activity187;

import java.util.HashMap;
import java.util.Map;

/**
 * 階段禮包
 */
public class Activity187Status {
	 /*
     * 	禮包購買計次<goodsId,count>
     */
    private Map<Integer,Integer> GiftCounter;
    /**
     *	 紀錄禮包第幾次開啟(用於清除購買次數)
     */
    private Map<Integer,Integer> GiftUseTime;
    
	public Activity187Status() {
		this.GiftCounter = new HashMap<>();
		this.GiftUseTime = new HashMap<>(); 
	}
	
	public int getGiftCount(int goodsId) {
		if (this.GiftCounter.containsKey(goodsId)) {
			return this.GiftCounter.get(goodsId);
		}
		return 0;
	}
	
	public void addGiftCount(int goodsId) {
		if (this.GiftCounter.containsKey(goodsId)) {
			this.GiftCounter.put(goodsId, this.GiftCounter.get(goodsId)+1);
		} else {
			this.GiftCounter.put(goodsId,1);
		}
	}
	
	public Boolean removeGiftCount(int goodsId){
		if (this.GiftCounter.containsKey(goodsId)) {
			this.GiftCounter.remove(goodsId);
			return true;
		}
		return false;
	}
	
	public int getGiftUseTime(int goodsId) {
		if (this.GiftUseTime.containsKey(goodsId)) {
			return this.GiftUseTime.get(goodsId);
		}
		return -1;
	}
	
	public void setGiftUseTime(int goodsId,int value) {
		this.GiftUseTime.put(goodsId,value);
	}

}
