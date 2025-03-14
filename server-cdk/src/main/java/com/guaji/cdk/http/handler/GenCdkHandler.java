package com.guaji.cdk.http.handler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.util.services.CdkService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.guaji.cdk.Cdk;
import com.guaji.cdk.CdkServices;
import com.guaji.cdk.data.CdkType;
import com.guaji.cdk.http.CdkHttpServer;
import com.guaji.cdk.http.param.GenCdkParam;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * cdk生成处理
 * 
 */
public class GenCdkHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		int status = CdkService.CDK_PARAM_ERROR;
		JSONObject jsonObject = new JSONObject();
		List<String> genCdks = new LinkedList<String>();

		Map<String, String> params = CdkHttpServer.parseHttpParam(httpExchange);
		Cdk.checkToken(params.get("token"));
		
		GenCdkParam cdkparam = new GenCdkParam();
		if (cdkparam.initParam(params)) {
			cdkparam.toLowerCase();
			//status = CdkServices.getInstance().genCdk(cdkparam, genCdks);
			status =CdkServices.getInstance().genCdkByType(cdkparam, genCdks,CdkType.valueOf(cdkparam.getType()));
		}
		
		

		jsonObject.put("status", String.valueOf(status));
		if (status == CdkService.CDK_STATUS_OK) {
			JSONArray jsonArray = new JSONArray();
			for (String cdk : genCdks) {
				jsonArray.add(cdk);
			}
			jsonObject.put("cdkList", jsonArray);
			jsonObject.put("reward", cdkparam.getReward());
			jsonObject.put("key", cdkparam.getId());
			jsonObject.put("type", cdkparam.getMessageStr());
			
		}

		CdkHttpServer.response(httpExchange, jsonObject);
	}
}
