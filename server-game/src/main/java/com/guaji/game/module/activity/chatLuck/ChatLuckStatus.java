package com.guaji.game.module.activity.chatLuck;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.os.GuaJiTime;

public class ChatLuckStatus {

	private Map<Long, Set<String>> hasTriggerMap = new ConcurrentHashMap<>();
	
	public void addTriggeredKey(String key) {
		long time = GuaJiTime.getAM0Date().getTime();
		Set<String> keys = hasTriggerMap.get(time);
		if(keys == null) {
			keys = new HashSet<>();
			hasTriggerMap.put(time, keys);
		}
		keys.add(key);
	}
	
	public boolean isTriggered(String key) {
		long time = GuaJiTime.getAM0Date().getTime();
		Set<String> keys = hasTriggerMap.get(time);
		if(keys == null || !keys.contains(key)) {
			return false;
		}
		return true;
	}
	
}
