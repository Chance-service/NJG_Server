package com.guaji.cdk.http.handler;

import java.io.IOException;
import java.util.Map;


import net.sf.json.JSONObject;

import com.guaji.cdk.Cdk;
import com.guaji.cdk.CdkServices;
import com.guaji.cdk.http.CdkHttpServer;
import com.guaji.cdk.http.param.ResetRewardParam;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * cdk类型信息重置处理
 * 
 */
public class ResetRewardHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		JSONObject jsonObject = new JSONObject();

		Map<String, String> params = CdkHttpServer.parseHttpParam(httpExchange);
		Cdk.checkToken(params.get("token"));
		
		ResetRewardParam cdkparam = new ResetRewardParam();
		if (cdkparam.initParam(params)) {
			cdkparam.toLowerCase();
			CdkServices.getInstance().resetCdk(cdkparam);
		}
		jsonObject.put("status", "success");
		CdkHttpServer.response(httpExchange, jsonObject);
	}
}
