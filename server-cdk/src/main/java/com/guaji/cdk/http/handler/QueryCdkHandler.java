package com.guaji.cdk.http.handler;

import java.io.IOException;
import java.util.Map;

import org.guaji.util.services.CdkService;

import net.sf.json.JSONObject;

import com.guaji.cdk.Cdk;
import com.guaji.cdk.CdkServices;
import com.guaji.cdk.data.CdkInfo;
import com.guaji.cdk.http.CdkHttpServer;
import com.guaji.cdk.http.param.QueryCdkParam;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * cdk查询处理
 * 
 */
public class QueryCdkHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		int status = CdkService.CDK_PARAM_ERROR;
		JSONObject jsonObject = new JSONObject();

		CdkInfo cdkInfo = null;
		Map<String, String> params = CdkHttpServer.parseHttpParam(httpExchange);
		Cdk.checkToken(params.get("token"));
		
		QueryCdkParam cdkparam = new QueryCdkParam();
		if (cdkparam.initParam(params)) {
			cdkparam.toLowerCase();
			cdkInfo = CdkServices.getInstance().queryCdkInfo(cdkparam);
			if (cdkInfo == null) {
				status = CdkService.CDK_STATUS_NONEXIST;
			}else {
				jsonObject.put("cdk", cdkInfo.toString());
				if(!cdkInfo.isBeused()) {
					status = CdkService.CDK_STATUS_OK;
				}else {
					status = CdkService.CDK_STATUS_USED;
				}
			}
		}
		jsonObject.put("status", String.valueOf(status));
		CdkHttpServer.response(httpExchange, jsonObject);
	}
}
