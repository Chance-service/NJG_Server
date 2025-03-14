package com.guaji.game.gm;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
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

public class NjgLTVInfoHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		try {
			//String nowdate = GaujiTime.getTimeString(Calendar.getInstance().getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nowdate = sdf.format(Calendar.getInstance().getTime());
			Calendar calendar =(Calendar) sdf.getCalendar().clone();
			Date nDate = GuaJiTime.DATE_FORMATOR_YYYYMMDD(nowdate);
			String startdate = ""; //起始時間
			String enddate = "";
			int ltvNum = 30; // 取LTV天數
			 
			JsonObject jsonObject = new JsonObject();
			JsonArray jsonArray = new JsonArray();
			
			
			if (params != null && params.length() > 0) {
				Map<String, String> bodyMap = GuaJiScriptManager.paramsToMap(params);
				if (bodyMap.containsKey("startdate") && bodyMap.containsKey("enddate")){
					startdate = bodyMap.get("startdate");
					enddate = bodyMap.get("enddate");
					Date sDate = GuaJiTime.DATE_FORMATOR_YYYYMMDD(startdate);
					Date eDate = GuaJiTime.DATE_FORMATOR_YYYYMMDD(enddate);
					if (eDate.getTime() <= sDate.getTime()) {
						// 查詢區間錯誤
						jsonObject.addProperty("status", 2);
						
						GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
						return;
					}
					if ((sDate.getTime() >= nDate.getTime())||(eDate.getTime() > nDate.getTime())) {
						// 查詢未來時間
						jsonObject.addProperty("status", 5);
						GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
						return;
					}

				} else {
					jsonObject.addProperty("status", 3);
					GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
					return;
				}
			} else {
				jsonObject.addProperty("status", 4);
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
				
			if( DailyStatisticsManager.getInstance()==null){
				jsonObject.addProperty("status", 0);
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}

			jsonObject.addProperty("status", 1); // no error
			
			// 找出下一天
			SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd");
			Date eDate = null;
//			if (startdate != null && startdate.length() > 0 && enddate == null ) {
//				eDate = edf.parse(startdate);
//			} else if (startdate != null && startdate.length() > 0 && enddate != null && enddate.length() > 0) {
//				eDate = edf.parse(enddate);
//			}
//			Calendar afterDayCalendar =(Calendar) edf.getCalendar().clone();
//			afterDayCalendar.add(Calendar.DAY_OF_YEAR, 1);
//			String afterDay = edf.format(afterDayCalendar.getTime());
			
			String caSql = String.format("select date,newUsers from daily_statistics where createTime >= '%s' and createTime < '%s'",startdate,enddate);
			Map<String,Integer> calmap = new HashMap<>();
			List<Object> ret = DBManager.getInstance().executeQuery(caSql);
			if (ret != null && ret.size() > 0) {
				for (int i = 0 ; i < ret.size() ; i ++) {
					Object[] objArray = (Object[])ret.get(i);
					String dateString = (String)objArray[0];
					int newusers = (Integer)objArray[1];
					calmap.put(dateString,newusers);
				}
			}
			Map<String,List<String>> idmap = new HashMap<>();
			String Sql = String.format("select id,createTime from player where createTime >= '%s' and createTime < '%s'",startdate,enddate) ;
			ret = DBManager.getInstance().executeQuery(Sql);
			if (ret != null && ret.size() > 0) {
				for (int i = 0 ; i < ret.size() ; i ++) {
					Object[] objArray = (Object[])ret.get(i);
					String id = String.valueOf((Integer)objArray[0]);
				    Date adate = (Date)objArray[1];
				    SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd");
				    String dateString = cdf.format(adate);
				    if (idmap.containsKey(dateString)) {
				    	idmap.get(dateString).add(id);
				    } else {
				    	List<String> cList = new ArrayList<>();
				    	cList.add(id);
				    	idmap.put(dateString, cList);
				    }
				}
			}
			
			Map<String,List<Integer>> LTVMap = new HashMap<>();
			
			for (Map.Entry<String,List<String>> entry : idmap.entrySet()) {
				String aDateString = entry.getKey(); // 查詢時間
				List alist = entry.getValue(); // 查詢時間當天創角的ID
				if (alist.size() > 0) {
					String idinStr = String.format("playerId in (%s)",String.join(",",alist));
					StringBuilder sb = new StringBuilder();
					for (int i = 1 ; i <= ltvNum ; i++) {
						// 取時間區間 ex: 2023-12-01 ~ 2023-12-dd;
						String afterDay = GuaJiTime.DATE_FORMATOR_NEXT_NDay(aDateString,i);//qdf.format(afterDayCalendar.getTime());
						Date afterDate = GuaJiTime.DATE_FORMATOR_YYYYMMDD(afterDay);
						sb.append(i == 1 ? String.format("select (select sum(goodsCost) from recharge where %s and createTime >= '%s' and  createTime < '%s') as LTV%d",idinStr,aDateString,afterDay,i) :
								String.format(", (select sum(goodsCost) from recharge where %s and createTime >= '%s' and  createTime < '%s') as LTV%d",idinStr,aDateString,afterDay,i));
						if (afterDate.getTime() >= nDate.getTime()) {
							break;
						}
					}
					List<Object> callback = DBManager.getInstance().executeQuery(sb.toString());
					if (callback != null && callback.size() > 0) {
						Object[] objArray = (Object[])callback.get(0);
						List<Integer> nlist = new ArrayList<>();
						if (objArray != null){
							for (int j = 0 ; j < objArray.length ; j++) {
								if (objArray[j] == null) {
									nlist.add(0);
								} else {
									nlist.add((int)Math.ceil(Double.valueOf(objArray[j].toString())));
								}
							}	
						} else { // only one value
							//((BigInteger)callback.get(0)).intValue();
							Object aObj = (Object)callback.get(0);
							int aVal = 0;
							if (aObj != null) {
								aVal = (int)Math.ceil(Double.valueOf(aObj.toString()));
							}
							nlist.add(aVal);
						}
						LTVMap.put(aDateString,nlist);
					}
				}
			}
			// 排序由小到大
			SortedSet<String> keys = new TreeSet<>(calmap.keySet());
			for(String key : keys) {
				JsonObject platJson = new JsonObject();
				platJson.addProperty("createTime",key);
				int newusers = calmap.get(key);
				platJson.addProperty("newusers",newusers);
				if (LTVMap.containsKey(key)) {
					for(int i = 0 ; i < LTVMap.get(key).size(); i++) {
						if (LTVMap.get(key).get(i) != 0) {
							int num = LTVMap.get(key).get(i)/100; // Hmoney conver us
							double d = (double) (num/newusers);
							double dnum = Math.ceil(d*10.0)/10.0;
							platJson.addProperty(String.format("LTV%d",i+1),Double.valueOf(dnum).toString());
						} else {
							platJson.addProperty(String.format("LTV%d",i+1),"0.0");
						}
					}
				}
				jsonArray.add(platJson);
			}
			
			jsonObject.add("info", jsonArray);
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
			
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}

}
