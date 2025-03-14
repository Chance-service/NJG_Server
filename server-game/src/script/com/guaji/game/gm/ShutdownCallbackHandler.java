package com.guaji.game.gm;

import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;

import com.sun.net.httpserver.HttpExchange;

/**
 * 配置重新加载
 */
public class ShutdownCallbackHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		try {
			Log.logPrintln("guaji script shutdown callback invoke");
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
}
