package com.guaji.game.module.activity.exchange;

import java.util.HashMap;

/**
 * 限时兑换 for 77
 * @author callan
 *
 */
public class ExchangeStatus 
{
	HashMap<String,Integer> exchangeCountTable;
	
	/**
	 * 构造
	 */
	public ExchangeStatus()
	{
		exchangeCountTable = new HashMap<String,Integer>();
	}
	
	/**
	 * 获取某个物品已经兑换的数量
	 * @param itemId
	 * @return
	 */
	public int getExchangeCount(String itemId)
	{
		if(exchangeCountTable.containsKey(itemId))
		{
			return exchangeCountTable.get(itemId);
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * 修改某个物品已经兑换数量
	 * @param itemId
	 * @param count
	 */
	
	public void modifyExchangeCount(String itemId, int count)
	{
		exchangeCountTable.put(itemId, count);
	}
	
	public HashMap<String,Integer> getExchangeInfo()
	{
		return exchangeCountTable;
	}
	
}
