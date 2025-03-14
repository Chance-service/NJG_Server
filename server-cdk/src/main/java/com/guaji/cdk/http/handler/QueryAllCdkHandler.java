package com.guaji.cdk.http.handler;

import java.io.IOException;
import java.util.List;

import net.sf.json.JSONObject;

import com.guaji.cdk.CdkServices;
import com.guaji.cdk.http.CdkHttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * 查询所有已经生成的cdk数据
 * 
 */
public class QueryAllCdkHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		JSONObject jsonObject = new JSONObject();
		List<String> str = CdkServices.getInstance().queryAllCdkInfo(); 
		jsonObject.put("allCdk", str);
		CdkHttpServer.response(httpExchange, jsonObject);
	}
}
