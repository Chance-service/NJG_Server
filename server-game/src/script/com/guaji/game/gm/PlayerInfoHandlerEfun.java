package com.guaji.game.gm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.module.activity.monthcard.MonthCardStatus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

public class PlayerInfoHandlerEfun extends GuaJiScript {
	
	@SuppressWarnings("unchecked")
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		// 参数解析: params=playername:xulin
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("puid")) {
			Player player = null;
			PlayerEntity playerEntity = null;
			if (paramsMap.containsKey("puid")) {
				String puid = paramsMap.get("puid");
				List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where puid='" + puid + "'");
				if (playerEntities.size() >= 1) {
					playerEntity = playerEntities.get(0);
				}
			}
	
			if (playerEntity != null) {
				player = PlayerUtil.queryPlayer(playerEntity.getId());
				if (player != null) {
					playerEntity = player.getPlayerData().getPlayerEntity();
				}
			}
	
			JsonObject jsonObject = new JsonObject(); 
			if (playerEntity != null) {
				jsonObject.addProperty("status", 1);
				jsonObject.addProperty("id", playerEntity.getId());
				jsonObject.addProperty("puid", playerEntity.getPuid());
				jsonObject.addProperty("name", playerEntity.getName());
				jsonObject.addProperty("level", playerEntity.getLevel());
				jsonObject.addProperty("exp", playerEntity.getExp());
				jsonObject.addProperty("golden", playerEntity.getRmbGold());
				jsonObject.addProperty("vip", playerEntity.getVipLevel());
				jsonObject.addProperty("silver", playerEntity.getCoin());
				//加入月卡信息
				PlayerData playerData = null;
				if (player != null) {
					playerData = player.getPlayerData();
				} else {
					playerData = new PlayerData(null);
					playerData.setPlayerEntity(playerEntity);
					playerData.loadActivity();
				}
				ActivityEntity<MonthCardStatus> activityEntity = (ActivityEntity<MonthCardStatus>) playerData.getActivityEntity(Const.ActivityId.MONTH_CARD_VALUE, 0);
				MonthCardStatus monthCardStatus = activityEntity.getActivityStatus(MonthCardStatus.class);
				int leftdate = 0 ;
				String endtime = null ;
				if(monthCardStatus == null){
					endtime = "1970-01-01" ;
				}else{
					Date begintime = monthCardStatus.getStartDate() ;
					leftdate = monthCardStatus.getLeftDays() ;
					if(begintime != null && leftdate != 0){
						endtime = getSpecifiedDayAfter(begintime,leftdate) ;
					} else {
						endtime = "1970-01-01" ;
					}
				}
				jsonObject.addProperty("onCard", endtime);
				if (player == null || !player.isOnline()) {
					jsonObject.addProperty("isOnline", "false");
				} else {
					jsonObject.addProperty("isOnline", "true");
				}
			} else {
				jsonObject.addProperty("status", 0);
			}
	
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
	
	 /**
	  * 获得指定日期之后的指定天数
	  * @param specifiedDay
	  * @return
	  */
	 public static String getSpecifiedDayAfter(Date date,int leftdate) {  
	        Calendar c = Calendar.getInstance();  
	        c.setTime(date);  
	        int day = c.get(Calendar.DATE);  
	        c.set(Calendar.DATE, day + leftdate);  
	        String dayAfter = new SimpleDateFormat("yyyy-MM-dd")  
	                .format(c.getTime());  
	        return dayAfter;  
	    } 
}
