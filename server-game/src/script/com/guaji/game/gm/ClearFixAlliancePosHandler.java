package com.guaji.game.gm;

import java.lang.reflect.Field;
import java.util.Set;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.util.GuaJiTickable;

import com.guaji.game.GsApp;
import com.sun.net.httpserver.HttpExchange;

/**
 * 玩家封号处理
 */
public class ClearFixAlliancePosHandler extends GuaJiScript {
	
	@SuppressWarnings("unchecked")
	@Override
	public void action(String params, final HttpExchange httpExchange) {
		// 参数解析: params=playerid:0;type:0
		try {
			Field field = getField(GsApp.getInstance(),"tickables"); 
			field.setAccessible(true);
			((Set<GuaJiTickable>)field.get(GsApp.getInstance())).clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 回复状态
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
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
