package com.guaji.cdk.http.handler;

import java.io.IOException;
import java.util.Map;

import org.guaji.util.services.CdkService;

import net.sf.json.JSONObject;

import com.guaji.cdk.Cdk;
import com.guaji.cdk.CdkServices;
import com.guaji.cdk.http.CdkHttpServer;
import com.guaji.cdk.http.param.DelTypeParam;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * cdk类型删除
 * 
 */
public class DelTypeHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
//		int status = GuaJiCdkService.CDK_PARAM_ERROR;
//		JSONObject jsonObject = new JSONObject();
//
//		Map<String, String> params = CdkHttpServer.parseHttpParam(httpExchange);
//		Cdk.checkToken(params.get("token"));
//		
//		DelTypeParam cdkparam = new DelTypeParam();
//		if (cdkparam.initParam(params)) {
//			cdkparam.toLowerCase();
//			CdkServices.getInstance().delCdkType(cdkparam);
//			status = GuaJiCdkService.CDK_STATUS_OK;
//		}
//
//		jsonObject.put("status", String.valueOf(status));
//		CdkHttpServer.response(httpExchange, jsonObject);
	}
}
