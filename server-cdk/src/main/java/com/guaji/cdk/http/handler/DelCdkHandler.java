package com.guaji.cdk.http.handler;

import java.io.IOException;
import java.util.Map;

import net.sf.json.JSONObject;

import com.guaji.cdk.Cdk;
import com.guaji.cdk.CdkServices;
import com.guaji.cdk.http.CdkHttpServer;
import com.guaji.cdk.http.param.DelCdkParam;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * cdk删除操作
 * 
 */
public class DelCdkHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		JSONObject jsonObject = new JSONObject();
		Map<String, String> params = CdkHttpServer.parseHttpParam(httpExchange);
		Cdk.checkToken(params.get("token"));
		
		DelCdkParam cdkparam = new DelCdkParam();
		boolean state = false;
		if (cdkparam.initParam(params)) {
			cdkparam.toLowerCase();
			state = CdkServices.getInstance().delCdk(cdkparam);
		}
		if(state) {
			jsonObject.put("status", "success");
		}else {
			jsonObject.put("status", "cdk not exists");
		}

		CdkHttpServer.response(httpExchange, jsonObject);
	}
}
