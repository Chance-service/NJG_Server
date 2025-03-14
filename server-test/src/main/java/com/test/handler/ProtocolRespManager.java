package com.test.handler;

import java.util.HashMap;
import java.util.Map;

import com.guaji.game.protocol.HP;

public class ProtocolRespManager {
	private static Map<Integer, IRespProtocolHandler> handler = new HashMap<Integer, IRespProtocolHandler>();
	static{
		handler.put(HP.code.LOGIN_S_VALUE, new Pros_Login());
		handler.put(HP.code.ROLE_CREATE_S_VALUE, new Pros_CreateRole());
	}
	
	public static IRespProtocolHandler getHandler(int command){
		return handler.get(command);
	}
}
