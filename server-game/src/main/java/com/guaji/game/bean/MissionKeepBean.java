package com.guaji.game.bean;

import java.util.HashMap;

/**
 * 任务持久数据bean
 */
public class MissionKeepBean {
	
	public int type;
	
	public HashMap<String, Integer> dataMap;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public HashMap<String, Integer> getDataMap() {
		return dataMap;
	}

	public void setDataMap(HashMap<String, Integer> dataMap) {
		this.dataMap = dataMap;
	}

	public MissionKeepBean() {
	}
	
	public MissionKeepBean(int type, HashMap<String, Integer> dataMap) {
		this.type = type;
		this.dataMap = dataMap;
	}
}
