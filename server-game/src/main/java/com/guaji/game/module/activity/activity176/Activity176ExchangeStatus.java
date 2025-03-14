package com.guaji.game.module.activity.activity176;

import java.util.HashMap;

/**
 *活動兌換
 * @author LIN
 *
 */
public class Activity176ExchangeStatus 
{
	private HashMap<Integer,HashMap<Integer,Integer>> exchangeCountTable;
	
	/**
	 * 构造
	 */
	public Activity176ExchangeStatus()
	{
		this.exchangeCountTable = new HashMap<>();
	}
	
	/**
	 * 获取某个物品已经兑换的数量
	 * @param itemId
	 * @return
	 */
	public int getExchangeCount(int activityId, int itemId)
	{
		if(exchangeCountTable.containsKey(activityId))
		{
			if (exchangeCountTable.get(activityId).containsKey(itemId)) {
				return exchangeCountTable.get(activityId).get(itemId);
			}
		
		}
		return 0;
	}
	
	/**
	 * 修改某个物品已经兑换数量
	 * @param itemId
	 * @param count
	 */
	
	public void modifyExchangeCount(int activityId,int Id, int count)
	{
		if (exchangeCountTable.containsKey(activityId)){
			exchangeCountTable.get(activityId).put(Id, count);
		} else {
			HashMap<Integer,Integer> aMap = new HashMap<>();
			aMap.put(Id, count);
			exchangeCountTable.put(activityId, aMap);
		}
		
	}
	
	public HashMap<Integer,HashMap<Integer,Integer>> getExchangeInfo()
	{
		return exchangeCountTable;
	}
	
}
