package com.guaji.game.gm;

import java.util.HashMap;
import java.util.Map;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.momo.MomoManager;
import com.sun.net.httpserver.HttpExchange;

/**
 * 推送平台消息;
 * <p>
 * 支持单用户推送和多用户广播;
 *
 */
public class PlatformPushMessageHandler extends GuaJiScript {
	private static final String TYPE = "type";
	private static final String PUID = "puid";
	private static final String MESSAGE = "message";

	@Override
	public void action(String params, HttpExchange httpExchange) {
		// 根据类型推送消息
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey(TYPE) && paramsMap.containsKey(MESSAGE)) {
			// get params
			int type = Integer.parseInt(paramsMap.get(TYPE));
			String message = paramsMap.get(MESSAGE);
			// push
			BroadcastType broadcastType = BroadcastType.typeOf(type);
			if (broadcastType != null && message.length() > 0) {
				broadcastType.pushMessage(paramsMap, message);
			}
			// response
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		}
	}

	/**
	 * 广播类型;
	 * 
	 * @author crazyjohn
	 *
	 */
	static enum BroadcastType {
		/** 单个玩家 */
		SINGLE_PLAYER(1) {
			@Override
			public void pushMessage(Map<String, String> params, String message) {
				if (!params.containsKey(PUID)) {
					return;
				}
				MomoManager.getInstance().pushMsg(message, params.get(PUID));
			}

		},
		/** 所有玩家 */
		ALL_PLAYER(2) {
			@Override
			public void pushMessage(Map<String, String> params, String message) {
				MomoManager.getInstance().broadcastMsg(message);
			}

		};
		private int type;

		BroadcastType(int type) {
			this.setType(type);
		}

		private static Map<Integer, BroadcastType> types = new HashMap<Integer, BroadcastType>();

		static {
			for (BroadcastType eachType : BroadcastType.values()) {
				types.put(eachType.getType(), eachType);
			}
		}

		public static BroadcastType typeOf(int type) {
			return types.get(type);
		}

		/**
		 * 推送策略接口;
		 * 
		 */
		public abstract void pushMessage(Map<String, String> params, String message);

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	}

}
