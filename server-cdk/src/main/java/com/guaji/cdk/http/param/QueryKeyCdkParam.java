package com.guaji.cdk.http.param;

import java.util.Map;

import org.guaji.os.MyException;

/**
 * cdk 查询功能
 * 
 */
public class QueryKeyCdkParam {
	private String key;

	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void toLowerCase() {
		if (key != null) {
			key = key.toLowerCase();
		}
	}

	public boolean initParam(Map<String, String> params) {
		try {
			key = params.get("key");

		} catch (Exception e) {
			MyException.catchException(e);
			return false;
		}
		return true;
	}
}
