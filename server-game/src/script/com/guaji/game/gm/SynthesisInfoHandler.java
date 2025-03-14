package com.guaji.game.gm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.guaji.net.NetStatistics;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.guaji.game.GsApp;
import com.guaji.game.entity.DailyStatisticsEntity;
import com.guaji.game.manager.DailyStatisticsManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.StringBuilder;

/**
 * 查询单服统计信息
 * 
 * @author hawk
 */
public class SynthesisInfoHandler extends GuaJiScript {
	
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(Calendar.getInstance().getTime());
			
			InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody(), "UTF-8");
			BufferedReader br = new BufferedReader(reader);
			int b;
			StringBuilder buf = new StringBuilder(512);
			while ((b = br.read()) != -1) {
			    buf.append((char) b);
			}
			
			br.close();
			reader.close();
			String body = buf.toString();
			
			//body params=date:2023-10-12
			if (body != null && body.length() > 0) {
				Map<String, String> bodyMap = GuaJiScriptManager.postBodyToMap(body);
				date = bodyMap.get("date");
			}

			// 参数解析: params=date:2014-05-05
//			if (params != null && params.length() > 0) {
//				Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
//				date = paramsMap.get("date");
//			}

			JsonObject jsonObject = new JsonObject();
			JsonObject platJson = new JsonObject();
			JsonArray jsonArray = new JsonArray();
			jsonObject.addProperty("status", 0);
			if (date != null && date.length() > 0) {
				if( DailyStatisticsManager.getInstance()==null){
					return;
				}
				DailyStatisticsEntity dailyStatistics = DailyStatisticsManager.getInstance().statistics(date, false);
				jsonObject.addProperty("status", 1);
				jsonObject.addProperty("date", dailyStatistics.date);
				
				platJson.addProperty("newUser", dailyStatistics.newUsers);
				platJson.addProperty("newDevice", dailyStatistics.newDevice);
				//platJson.addProperty("newlyDevice", newlyDevice);
				platJson.addProperty("totalUser", dailyStatistics.totalUsers);
				platJson.addProperty("totalDevice", dailyStatistics.totalDevice);
				platJson.addProperty("online",NetStatistics.getInstance().getCurSession());
				platJson.addProperty("session", NetStatistics.getInstance().getPeakSession());
				platJson.addProperty("recharge", dailyStatistics.totalPayMoney);
				
				jsonArray.add(platJson);
				jsonObject.add("info", jsonArray);
				
				//jsonObject.addProperty("online", GsApp.getInstance().getActiveSessions().size());
//				jsonObject.addProperty("online",NetStatistics.getInstance().getCurSession());
//				jsonObject.addProperty("totalUsers", dailyStatistics.totalUsers);
//				jsonObject.addProperty("totalDevice", dailyStatistics.totalDevice);
//				jsonObject.addProperty("totalPayUsers", dailyStatistics.totalPayUsers);
//				jsonObject.addProperty("totalPayDevice", dailyStatistics.totalPayDevice);
//				jsonObject.addProperty("totalPayMoney", dailyStatistics.totalPayMoney);
//				jsonObject.addProperty("newUsers", dailyStatistics.newUsers);
//				jsonObject.addProperty("newDevice", dailyStatistics.newDevice);
//				jsonObject.addProperty("dailyActiveUsers", dailyStatistics.dailyActiveUsers);
//				jsonObject.addProperty("userRetentionRate", dailyStatistics.userRetentionRate);
//				jsonObject.addProperty("deviceRetentionRate", dailyStatistics.deviceRetentionRate);
//				jsonObject.addProperty("payUsers", dailyStatistics.payUsers);
//				jsonObject.addProperty("payDevice", dailyStatistics.payDevice);
//				jsonObject.addProperty("payMoney", dailyStatistics.payMoney);
//				jsonObject.addProperty("session", NetStatistics.getInstance().getPeakSession());
			}
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
}
