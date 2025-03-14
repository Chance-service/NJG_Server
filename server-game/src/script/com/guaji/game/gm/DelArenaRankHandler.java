package com.guaji.game.gm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.entity.ArenaEntity;
import com.guaji.game.manager.ArenaManager;
import com.sun.net.httpserver.HttpExchange;

public class DelArenaRankHandler extends GuaJiScript {

	@SuppressWarnings("unchecked")
	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		JsonObject jsonObject = new JsonObject();
		Field playerIdMapfield = getField(ArenaManager.getInstance(), "playerIdArenaEntityMap");
		Field rankMapfield = getField(ArenaManager.getInstance(), "rankArenaEntityMap"); 
		if(playerIdMapfield == null || rankMapfield == null){
			return;
		}
		
		playerIdMapfield.setAccessible(true);
		rankMapfield.setAccessible(true);
		if (paramsMap.containsKey("playerid")) {
			int playerId = Integer.valueOf(paramsMap.get("playerid"));
			try {
				Method grantAwardsMethod = getMethod(ArenaManager.getInstance(),"getArenaEntityByPlayerId");
				if (grantAwardsMethod != null) {
					grantAwardsMethod.setAccessible(true);
					Object o = grantAwardsMethod.invoke(ArenaManager.getInstance(), playerId);
					if(o != null){
						ArenaEntity areEntity = (ArenaEntity) o;
						areEntity.delete();
						((Map<Integer, ArenaEntity>)playerIdMapfield.get(ArenaManager.getInstance())).remove(areEntity.getPlayerId());
						((Map<Integer, ArenaEntity>)rankMapfield.get(ArenaManager.getInstance())).remove(areEntity.getRank());
						jsonObject.addProperty("delete", 1);
					}
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
			jsonObject.addProperty("status", 1);
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		}
		
		if (paramsMap.containsKey("rank")) {
			int rank = Integer.valueOf(paramsMap.get("rank"));
			try {
				Method grantAwardsMethod = getMethod(ArenaManager.getInstance(),"getArenaEntityByRank");
				
				if (grantAwardsMethod != null) {
					grantAwardsMethod.setAccessible(true);
					Object o = grantAwardsMethod.invoke(ArenaManager.getInstance(), rank);
					if(o != null){
						ArenaEntity areEntity = (ArenaEntity) o;
						areEntity.delete();
						((Map<Integer, ArenaEntity>)playerIdMapfield.get(ArenaManager.getInstance())).remove(areEntity.getPlayerId());
						((Map<Integer, ArenaEntity>)rankMapfield.get(ArenaManager.getInstance())).remove(areEntity.getRank());
						jsonObject.addProperty("delete", 1);
					}
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
			jsonObject.addProperty("status", 1);
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		}
	}
	
	private Method getMethod(Object obj, String name) {
		for(Method method : obj.getClass().getDeclaredMethods()){
			if(method.getName().indexOf(name) >= 0) {
				return method;
			}
		};
		return null;
	}
	
	private Field getField(Object instance, String attrName) {
		Field field = null;
		if (field == null) {
			try {
				try {
					Class<?> instanceClass = instance.getClass();
					do {
						try {
							field = instanceClass.getField(attrName);
						} catch (Exception e) {
							instanceClass = instanceClass.getSuperclass();
						}
					} while (field == null);
				} catch (Exception e) {
					Class<?> instanceClass = instance.getClass();
					do {
						try {
							field = instanceClass.getDeclaredField(attrName);
						} catch (Exception ex) {
							instanceClass = instanceClass.getSuperclass();
						}
					} while (field == null);
				}
			} catch (Exception e) {
			}
		}
		return field;
	}
}
