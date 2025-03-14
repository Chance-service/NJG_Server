package com.guaji.game.gm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.List;
import java.util.Date;

import org.guaji.db.DBManager;
import org.guaji.net.NetStatistics;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.guaji.game.entity.DailyStatisticsEntity;
import com.guaji.game.manager.DailyStatisticsManager;
import com.sun.net.httpserver.HttpExchange;

/**
 * 查詢統計資訊
 * 
 * @author lin12
 *
 */

public class StatisticalInfoHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nowdate = sdf.format(Calendar.getInstance().getTime()); // 今天
			Calendar calendar =(Calendar) sdf.getCalendar().clone();
			String startdate = ""; // 今天
			
//			InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody(), "UTF-8");
//			BufferedReader br = new BufferedReader(reader);
//			int b;
//			StringBuilder buf = new StringBuilder(512);
//			while ((b = br.read()) != -1) {
//			    buf.append((char) b);
//			}
//			
//			br.close();
//			reader.close();
//			String body = buf.toString();
			
//			Calendar afterDayCalendar = GuaJiTime.getCalendar();
//			afterDayCalendar.setTimeInMillis(calendar.getTimeInMillis());
//			afterDayCalendar.add(Calendar.DAY_OF_YEAR, 1);
//			String nextdate = sdf.format(afterDayCalendar.getTime());
			String enddate = null;
			 
			
			if (params != null && params.length() > 0) {
				Map<String, String> bodyMap = GuaJiScriptManager.paramsToMap(params);
				if (bodyMap.containsKey("startdate")){
					startdate = bodyMap.get("startdate");
					if (bodyMap.containsKey("enddate")){
						enddate = bodyMap.get("enddate");
					}
					if (startdate.equals(enddate)) {
						enddate = null;
					} else {
						SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd");
						Date eDate = edf.parse(enddate);
						Calendar afterDayCalendar =(Calendar) edf.getCalendar().clone();
						afterDayCalendar.add(Calendar.DAY_OF_YEAR, 1);
						String nextdate = edf.format(afterDayCalendar.getTime());
						enddate = nextdate;
					}
				} else {
					// 只送今天
					startdate = nowdate;
				}
			}
			
			JsonObject jsonObject = new JsonObject();
			
			JsonArray jsonArray = new JsonArray();
			jsonObject.addProperty("status", 0);
			
			if( DailyStatisticsManager.getInstance()==null){
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
			
			if (startdate != null && startdate.length() > 0 && enddate == null ) {
				
				DailyStatisticsEntity dailyStatistics = DailyStatisticsManager.getInstance().statistics(startdate, false);
				jsonObject.addProperty("status", 1);
				JsonObject platJson = new JsonObject();
				// 找出下一天
				SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd");
				Date eDate = edf.parse(dailyStatistics.getDate());
				Calendar afterDayCalendar =(Calendar) edf.getCalendar().clone();
				afterDayCalendar.add(Calendar.DAY_OF_YEAR, 1);
				String afterDay = edf.format(afterDayCalendar.getTime());
				
				String paySql = String.format("select sum(payMoney) from player where createTime >= '%s' and createTime < '%s' ", dailyStatistics.getDate(), afterDay);
				List<Object> results = DBManager.getInstance().executeQuery(paySql);
				if (results != null && results.size() > 0) {
					if (results.get(0) != null) {
						platJson.addProperty("payMoney",(int)Math.ceil(Double.valueOf(results.get(0).toString())/100));
					} else {
						platJson.addProperty("payMoney", 0);
					}
				} else {
					platJson.addProperty("payMoney", 0);
				}
				platJson.addProperty("nowDate", dailyStatistics.getDate());
				platJson.addProperty("createAcc", dailyStatistics.getNewUsers());
				platJson.addProperty("nameDone", dailyStatistics.getNameDone());
				platJson.addProperty("ftuxDone", dailyStatistics.getFtuxDone());
				platJson.addProperty("teachDone", dailyStatistics.getTeachDone());
				//platJson.addProperty("hcgDone",dailyStatistics.getHcgDone());				
				jsonArray.add(platJson);
				jsonObject.add("info", jsonArray);
			} else if (startdate != null && startdate.length() > 0 && enddate != null && enddate.length() > 0) {
				List<DailyStatisticsEntity> DailyList = DailyStatisticsManager.getInstance().InquireHistory(startdate,enddate);
				if (DailyList.size() > 0) {
					jsonObject.addProperty("status", 1);
					for (DailyStatisticsEntity aEntity : DailyList) {
						JsonObject platJson = new JsonObject();
						// 找出下一天
						SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd");
						Date eDate = edf.parse(aEntity.getDate());
						Calendar afterDayCalendar =(Calendar) edf.getCalendar().clone();
						afterDayCalendar.add(Calendar.DAY_OF_YEAR, 1);
						String afterDay = edf.format(afterDayCalendar.getTime());
						
						String paySql = String.format("select sum(payMoney) from player where createTime >= '%s' and createTime < '%s' ", aEntity.getDate(), afterDay);
						List<Object> results = DBManager.getInstance().executeQuery(paySql);
						if (results != null && results.size() > 0) {
							if (results.get(0) != null) {
								platJson.addProperty("payMoney",(int)Math.ceil(Double.valueOf(results.get(0).toString())/100));
							} else {
								platJson.addProperty("payMoney", 0);
							}
						} else {
							platJson.addProperty("payMoney", 0);
						}
						platJson.addProperty("nowDate", aEntity.getDate());
						platJson.addProperty("createAcc", aEntity.getNewUsers());
						platJson.addProperty("nameDone", aEntity.getNameDone());
						platJson.addProperty("ftuxDone", aEntity.getFtuxDone());
						platJson.addProperty("teachDone", aEntity.getTeachDone());
						jsonArray.add(platJson);
					}
					jsonObject.add("info", jsonArray);
				}
				
			}
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
			
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}

}
