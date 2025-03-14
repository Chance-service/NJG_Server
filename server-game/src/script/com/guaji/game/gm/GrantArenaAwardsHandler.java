package com.guaji.game.gm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.entity.ArenaEntity;
import com.guaji.game.manager.ArenaManager;
import com.sun.net.httpserver.HttpExchange;

public class GrantArenaAwardsHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		JsonObject jsonObject = new JsonObject();
		try {
			Field rankMapfield = getField(ArenaManager.getInstance(), "rankArenaEntityMap"); 
			if(rankMapfield == null){
				return;
			}
			rankMapfield.setAccessible(true);
			
			@SuppressWarnings("unchecked")
			Map<Integer, ArenaEntity> tmpRankMap = (Map<Integer, ArenaEntity>)rankMapfield.get(ArenaManager.getInstance());
			Map<Integer, ArenaEntity> arenaRankMap = new HashMap<Integer, ArenaEntity>();
			arenaRankMap.putAll(tmpRankMap);
			Method grantAwardsMethod = getMethod(ArenaManager.getInstance(), "grantArenaDayRankAward");
			if (grantAwardsMethod != null) {
				grantAwardsMethod.setAccessible(true);
				grantAwardsMethod.invoke(ArenaManager.getInstance(), arenaRankMap);
				jsonObject.addProperty("grant", 1);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		jsonObject.addProperty("status", 1);
		GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
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
