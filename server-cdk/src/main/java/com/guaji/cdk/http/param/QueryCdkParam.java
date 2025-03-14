package com.guaji.cdk.http.param;

import java.util.Map;

import org.guaji.os.MyException;

/**
 * cdk 查询功能
 * 
 */
public class QueryCdkParam {
	private String cdk;

	public String getCdk() {
		return cdk;
	}

	public void setCdk(String cdk) {
		this.cdk = cdk;
	}

	public void toLowerCase() {
		if (cdk != null) {
			cdk = cdk.toLowerCase();
		}
	}

	public boolean initParam(Map<String, String> params) {
		try {
			cdk = params.get("cdk");

		} catch (Exception e) {
			MyException.catchException(e);
			return false;
		}
		return true;
	}
}
