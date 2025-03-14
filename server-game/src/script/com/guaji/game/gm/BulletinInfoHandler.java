package com.guaji.game.gm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.lang.Integer;

import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.net.NetStatistics;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.guaji.game.GsApp;
import com.guaji.game.entity.BulletinEntity;
import com.guaji.game.entity.DailyStatisticsEntity;
import com.guaji.game.manager.BulletinManager;
import com.guaji.game.manager.DailyStatisticsManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.StringBuilder;
import java.net.URLDecoder;

/**
 * 後臺編輯公告處理
 * 
 * @author hawk
 */
public class BulletinInfoHandler extends GuaJiScript {
	
	@Override
	public void action(String params, HttpExchange httpExchange) {
		JSONObject jsonObject = new JSONObject();
		try {
			
			InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody(), "UTF-8");
			BufferedReader br = new BufferedReader(reader);
			int b;
			StringBuilder buf = new StringBuilder(8192);
			while ((b = br.read()) != -1) {
			    buf.append((char) b);
			}
			
			br.close();
			reader.close();
			String body = buf.toString();
			String sendData = ""; 
			//body params=date:2023-10-12
			if (body != null && body.length() > 0) {
				Log.scriptInfo(body);
				//Map<String, String> bodyMap = GuaJiScriptManager.postBodyToMap(body);
				sendData = body;
				//Log.scriptInfo(sendData);
			}
			JSONObject sendDataObj = JSONObject.fromObject(sendData);
			
			JSONArray jsonArray = sendDataObj.getJSONArray("senddata");
			
			JSONArray statusArray = new JSONArray();
			JSONArray idArray = new JSONArray();
			
			for (int i = 0 ; i < jsonArray.size();i++) {
				int id = jsonArray.getJSONObject(i).getInt("id");
				idArray.add(id);
				if (!jsonArray.getJSONObject(i).has("action")) {
					statusArray.add(4);// 沒有指示動作
					continue;
				}
				String action = jsonArray.getJSONObject(i).getString("action");
				
				BulletinEntity bulletinEntity = BulletinManager.getInstance().getBulletinEntity(id);
				if (action.equals("new")) {
					if (bulletinEntity == null) {
						bulletinEntity = new BulletinEntity();
						bulletinEntity.setId(id);
						bulletinEntity.setPlatformId(Integer.valueOf(jsonArray.getJSONObject(i).getString("platformId")));
						bulletinEntity.setSort(jsonArray.getJSONObject(i).getInt("sort"));
						bulletinEntity.setVisible(jsonArray.getJSONObject(i).getInt("show"));
						bulletinEntity.setType(jsonArray.getJSONObject(i).getInt("type"));
						bulletinEntity.setBeginTime(new Date(jsonArray.getJSONObject(i).getLong("beginTime")));
						bulletinEntity.setEndTime(new Date(jsonArray.getJSONObject(i).getLong("endTime")));
						bulletinEntity.setTxturl(jsonArray.getJSONObject(i).getString("txturl"));
						bulletinEntity.setTitle(jsonArray.getJSONObject(i).getString("title"));
						bulletinEntity.setFixTime(new Date(jsonArray.getJSONObject(i).getLong("updateTime")));
						DBManager.getInstance().create(bulletinEntity);
						BulletinManager.getInstance().loadBulletinEntity().add(bulletinEntity);
					} else {
						statusArray.add(1); // 新增失敗
						continue;
					}
				} else if (action.equals("modify")) {
		
					if (bulletinEntity != null) {
						bulletinEntity.setPlatformId(Integer.valueOf(jsonArray.getJSONObject(i).getString("platformId")));
						bulletinEntity.setSort(jsonArray.getJSONObject(i).getInt("sort"));
						bulletinEntity.setVisible(jsonArray.getJSONObject(i).getInt("show"));
						bulletinEntity.setType(jsonArray.getJSONObject(i).getInt("type"));
						bulletinEntity.setBeginTime(new Date(jsonArray.getJSONObject(i).getLong("beginTime")));
						bulletinEntity.setEndTime(new Date(jsonArray.getJSONObject(i).getLong("endTime")));
						bulletinEntity.setTxturl(jsonArray.getJSONObject(i).getString("txturl"));
						bulletinEntity.setTitle(jsonArray.getJSONObject(i).getString("title"));
						bulletinEntity.setFixTime(new Date(jsonArray.getJSONObject(i).getLong("updateTime")));
						bulletinEntity.notifyUpdate(true);
					} else {
						statusArray.add(2); // 修改失敗
						continue;
					}
				} else if (action.equals("delete")) {
					
					if (bulletinEntity != null) {
						BulletinManager.getInstance().deleteBulletin(bulletinEntity);
					} else {
						statusArray.add(3); // 刪除失敗
						continue;
					}
				} else {
					statusArray.add(5); // 指令錯誤
					continue;
				}
				
				statusArray.add(0);
			}
			
			jsonObject.put("id", idArray);
			jsonObject.put("status", statusArray);
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		} catch (Exception e) {
			jsonObject.put("status", -1); // 程式碼錯誤
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
			MyException.catchException(e);
		}
	}
}
