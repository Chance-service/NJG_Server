package com.guaji.game.module.activity.wordsexchange;

import java.util.HashMap;
import java.util.Map;

import com.guaji.game.config.WordsExchangeCfg;

public class WordsExchangeStatus {

	private Map<Integer, Integer> exchangeMap = new HashMap<Integer, Integer>();
	
	/**
	 * 获得该类型剩余兑换次数
	 * @param type
	 * @return
	 */
	public int getLeftExchangeTimes(int type) {
		int exchangedTimes = 0;
		if(exchangeMap.containsKey(type)) {
			exchangedTimes = exchangeMap.get(type);
		}
		return WordsExchangeCfg.getWordsExchangeCfg(type, 0).getDailyLimit() - exchangedTimes;
	}
	
	/**
	 * 增加已兑换次数
	 * @param type
	 */
	public void increateExchangeTimes(int type) {
		if(exchangeMap.containsKey(type)) {
			exchangeMap.put(type, exchangeMap.get(type) + 1);
		}else{
			exchangeMap.put(type,1);
		}
	}
	
}
