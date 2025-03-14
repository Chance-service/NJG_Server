package com.guaji.game.gm;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.guaji.db.DBManager;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.guaji.game.manager.DailyStatisticsManager;
import com.guaji.game.util.NjginfoData;
import com.sun.net.httpserver.HttpExchange;

/**
 * 查詢統計資訊
 * 
 * @author lin12
 *
 */

public class NjgPlayerInfoHandler extends GuaJiScript {

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

			jsonObject.addProperty("status", 1);
			
			// 找出下一天
			SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd");
			Date eDate = null;
			if (startdate != null && startdate.length() > 0 && enddate == null ) {
				eDate = edf.parse(startdate);
			} else if (startdate != null && startdate.length() > 0 && enddate != null && enddate.length() > 0) {
				eDate = edf.parse(enddate);
			}
			Calendar afterDayCalendar =(Calendar) edf.getCalendar().clone();
			afterDayCalendar.add(Calendar.DAY_OF_YEAR, 1);
			String afterDay = edf.format(afterDayCalendar.getTime());
			
			Map<Integer,Map<Integer,Integer>> calmap = new HashMap<>();
			String calSql = "select playerId,goodsId from recharge where isTest = 0" ;
			List<Object> ret = DBManager.getInstance().executeQuery(calSql);
			if (ret != null && ret.size() > 0) {
				for (int i = 0 ; i < ret.size() ; i ++) {
					Object[] objArray = (Object[])ret.get(i);
					
					int playerId = (Integer)objArray[0];
					int goodsId =  (Integer)objArray[1];
					
					if (calmap.containsKey(playerId)) {
						if (calmap.get(playerId).containsKey(goodsId)) {
							calmap.get(playerId).replace(goodsId, calmap.get(playerId).get(goodsId)+1);
						} else {
							calmap.get(playerId).put(goodsId,1);
						}
					} else {
						Map<Integer,Integer> amap = new HashMap<>();
						amap.put(goodsId, 1);
						calmap.put(playerId,amap);
					}
				}
			}
			
			
			String paySql = String.format("select player.id,player.name,player.level,player.serverId,player.createTime,player.loginTime,status.passMapId,player.payMoney from player inner join status on player.id = status.playerId  where player.createTime >= '%s' and player.createTime < '%s' order by player.id;", startdate, afterDay);
			List<Object> results = DBManager.getInstance().executeQuery(paySql);
			
			if (results != null && results.size() > 0) {
				for (int i = 0 ; i < results.size() ; i ++) {
					Object[] objArray = (Object[])results.get(i);
					NjginfoData infodate = new NjginfoData();
					JsonObject platJson = new JsonObject();
					if (objArray.length > 0 && objArray[0] != null) {
						infodate.setId((Integer)objArray[0]);
					}
					
					if (objArray.length > 1 && objArray[1] != null) {
						infodate.setName(String.valueOf(objArray[1]));
					}
					
					if (objArray.length > 2 && objArray[2] != null) {
						infodate.setLevel((Integer)objArray[2]);
					}
					
					if (objArray.length > 3 && objArray[3] != null) {
						infodate.setServerId((Integer)objArray[3]);
					}
					
					if (objArray.length > 4 && objArray[4] != null) {
						infodate.setCreateTime((Date)objArray[4]);
					}
					
					if (objArray.length > 5 && objArray[5] != null) {
						infodate.setLoginTime((Date)objArray[5]);
					}
					
					if (objArray.length > 6 && objArray[6] != null) {
						infodate.setPassMapId((Integer)objArray[6]);
					}
					
					if (objArray.length > 7 && objArray[7] != null) {
						infodate.setPayMoney((Double)objArray[7]);
					}
					platJson.addProperty("id", infodate.getId());
					platJson.addProperty("name", infodate.getName());
					platJson.addProperty("level", infodate.getLevel());
					platJson.addProperty("serverId", infodate.getServerId());
					SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					platJson.addProperty("createTime", dateParser.format(infodate.getCreateTime()));
					platJson.addProperty("loginTime", dateParser.format(infodate.getLoginTime()));
					platJson.addProperty("passMapId", infodate.getPassMapId());
					platJson.addProperty("totalplay", 100000);
					platJson.addProperty("payMoney",infodate.getPayMoney());
					
					if (infodate.getPayMoney() != 0.0) {
						if (calmap.containsKey(infodate.getId())) {
							for (Map.Entry<Integer, Integer> entry :calmap.get(infodate.getId()).entrySet()) {
								platJson.addProperty(String.valueOf(entry.getKey()),entry.getValue());
							}
						}
					}
					
					jsonArray.add(platJson);
				}
				
			}		
			jsonObject.add("info", jsonArray);
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
			
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}

}
