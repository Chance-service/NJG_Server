package com.guaji.cdk.http.param;

import java.util.Map;

import org.guaji.os.MyException;

/**
 * cdk类型奖励重置
 * 
 */
public class ResetRewardParam {
	private String key;

	public void toLowerCase() {
		if (key != null) {
			key = key.toLowerCase();
		}
		
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean initParam(Map<String, String> params) {
		try {
			if (params.containsKey("key")) {
				key = params.get("key");
			}
		} catch (Exception e) {
			MyException.catchException(e);
			return false;
		}
		return true;
	}
}
