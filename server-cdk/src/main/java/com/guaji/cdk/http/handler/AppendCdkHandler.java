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
import com.guaji.cdk.data.CdkTypeReward;
import com.guaji.cdk.http.CdkHttpServer;
import com.guaji.cdk.http.param.AppendCdkParam;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * cdk追加处理
 * 
 */
public class AppendCdkHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
//		int status = CdkService.CDK_PARAM_ERROR;
//		JSONObject jsonObject = new JSONObject();
//		List<String> genCdks = new LinkedList<String>();
//
//		Map<String, String> params = CdkHttpServer.parseHttpParam(httpExchange);
//		Cdk.checkToken(params.get("token"));
//
//		AppendCdkParam cdkparam = new AppendCdkParam();
//		if (cdkparam.initParam(params)) {
//			cdkparam.toLowerCase();
//			status = CdkServices.getInstance().appendCdk(cdkparam, genCdks);
//		}
//
//		jsonObject.put("status", String.valueOf(status));
//		if (status == CdkService.CDK_STATUS_OK) {
//			JSONArray jsonArray = new JSONArray();
//			for (String cdk : genCdks) {
//				jsonArray.add(cdk);
//			}
//
//			CdkTypeReward typeReward = CdkServices.getInstance().getTypeReward(cdkparam.getGame() + "." + cdkparam.getType());
//			jsonObject.put("type", typeReward.toString());
//			jsonObject.put("cdks", jsonArray);
//		}
//
//		CdkHttpServer.response(httpExchange, jsonObject);
	}
}
