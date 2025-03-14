package com.guaji.game.gm;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.entity.CampWarEntity;
import com.guaji.game.manager.CampWarManager;
import com.guaji.game.util.GsonUtil;
import com.sun.net.httpserver.HttpExchange;

public class GetCampWarKillerSetHandler  extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		JsonObject jsonObject = new JsonObject();
		Field multiKillRankSetField = getClassField(CampWarManager.getInstance(), "curMultiKillRankSet");
		if(multiKillRankSetField == null){
			return;
		}		
		multiKillRankSetField.setAccessible(true);
		
		try {
			@SuppressWarnings("unchecked")
			TreeSet<CampWarEntity>  multiKillRankSet = ((TreeSet<CampWarEntity>)multiKillRankSetField.get(CampWarManager.getInstance()));
			Map<Integer, Integer> hashCodeMap = new HashMap<Integer, Integer>();
 			for(CampWarEntity entity : multiKillRankSet){
 				if(hashCodeMap.containsKey(entity.hashCode())){
 					int count = hashCodeMap.get(entity.hashCode()) + 1;
 					hashCodeMap.put(entity.hashCode(), count);
 					//jsonObject.addProperty(String.valueOf(entity.hashCode()), entity.getId());
 				}else{
 					hashCodeMap.put(entity.hashCode(), 1);
 				}
 				
 				String str = GsonUtil.getJsonInstance().toJson(entity);
 				jsonObject.addProperty(String.valueOf(entity.hashCode()), str);
			}
 			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
	
	
	/**
	 * 获取域
	 * @param instance
	 * @param attrName
	 * @return
	 */
	public static Field getClassField(Object instance, String attrName) {
		Field field = null;
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
		
		if (field != null) {
			field.setAccessible(true);
		}
		return field;
	}
}
