package com.guaji.cdk.http.param;

import java.util.Map;

import org.guaji.os.MyException;

/**
 * 删除指定cdk批次
 * 
 */
public class DelCdkParam {
	private String key;


	public void toLowerCase() {
		key = key.toLowerCase();
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
