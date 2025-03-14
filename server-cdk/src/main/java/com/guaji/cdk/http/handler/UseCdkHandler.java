package com.guaji.cdk.http.handler;

import java.io.IOException;
import java.util.Map;

import org.guaji.util.services.CdkService;

import net.sf.json.JSONObject;

import com.guaji.cdk.Cdk;
import com.guaji.cdk.CdkServices;
import com.guaji.cdk.http.CdkHttpServer;
import com.guaji.cdk.http.param.UseCdkParam;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * cdk使用处理
 * 
 */
public class UseCdkHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		int status = CdkService.CDK_STATUS_NONEXIST;
		JSONObject jsonObject = new JSONObject();

		System.out.println("UseCdkHandler...httpExchange="+httpExchange);
		
		Map<String, String> params = CdkHttpServer.parseHttpParam(httpExchange);
		Cdk.checkToken(params.get("token"));
		
		UseCdkParam cdkparam = new UseCdkParam();
		if (cdkparam.initParam(params)) {
			cdkparam.toLowerCase();
			status = CdkServices.getInstance().useCdk(cdkparam);
		}

		jsonObject.put("status", String.valueOf(status));
		if (status == CdkService.CDK_STATUS_OK) {
			jsonObject.put("reward", cdkparam.getReward());
		}

		CdkHttpServer.response(httpExchange, jsonObject);
	}
}
