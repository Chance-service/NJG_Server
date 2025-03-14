package com.guaji.game.gm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.PlayerUtil;
import com.sun.net.httpserver.HttpExchange;

public class PlayerInfoR2Handler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		
		if (paramsMap.containsKey("playerid") || paramsMap.containsKey("puid")) {
			PlayerEntity playerEntity = null;
			Player player = null;
			if(paramsMap.containsKey("puid")) {
				String puid = paramsMap.get("puid");
				if (paramsMap.containsKey("puid")) {
					List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where puid='" + puid + "'");
					if (playerEntities.size() >= 1) {
						playerEntity = playerEntities.get(0);
					}
				}
			} else if (paramsMap.containsKey("playerid")) {
				int playerid = Integer.valueOf(paramsMap.get("playerid"));
				List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where id=" + playerid);
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
				jsonObject.addProperty("code", 1);
				jsonObject.addProperty("id", playerEntity.getId());
				jsonObject.addProperty("puid", playerEntity.getPuid());
				jsonObject.addProperty("name", playerEntity.getName());
				jsonObject.addProperty("level", playerEntity.getLevel());
				jsonObject.addProperty("gold", playerEntity.getRmbGold());
				jsonObject.addProperty("rechargeAmount", playerEntity.getRecharge());
				jsonObject.addProperty("lastLoginTime", playerEntity.getLoginTime().getTime());
				jsonObject.addProperty("registerTime", playerEntity.getCreateTime().getTime());
				if (player == null || !player.isOnline()) {
					jsonObject.addProperty("isOnline", "false");
				} else {
					jsonObject.addProperty("isOnline", "true");
				}
				//加入充值信息
				int totalRechargeGold = 0;
				String sql = "";
				if(paramsMap.containsKey("puid")) {
					String puid = paramsMap.get("puid");
					sql = "select sum(addGold) from recharge where puid='" + puid + "'";
				} else if(paramsMap.containsKey("playerid")) {
					int playerid = Integer.valueOf(paramsMap.get("playerid"));
					sql = "select sum(addGold) from recharge where playerid=" + playerid;
				}
				List<Object> results = DBManager.getInstance().executeQuery(sql);
				if (results != null && results.size() > 0 && results.get(0) != null) {
					totalRechargeGold = ((BigDecimal)results.get(0)).intValue();
				}
				jsonObject.addProperty("totalRechargeGold", totalRechargeGold);
				
				int lastRechargeTime = 0;
				if(paramsMap.containsKey("puid")) {
					String puid = paramsMap.get("puid");
					sql = "select unix_timestamp(createTime) from recharge where puid='" + puid + "' order by createTime desc limit 1";
				} else if(paramsMap.containsKey("playerid")) {
					int playerid = Integer.valueOf(paramsMap.get("playerid"));
					sql = "select unix_timestamp(createTime) from recharge where playerid=" + playerid + " order by createTime desc limit 1";
				}				
				results = DBManager.getInstance().executeQuery(sql);
				if (results != null && results.size() > 0 && results.get(0) != null) {
					lastRechargeTime = ((BigInteger)results.get(0)).intValue();
				}
				jsonObject.addProperty("lastRechargeTime", lastRechargeTime);
			} else {
				jsonObject.addProperty("code", 0);
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
