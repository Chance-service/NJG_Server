package org.guaji.script;

import com.sun.net.httpserver.HttpExchange;

/**
 * 脚本接口
 */
public abstract class GuaJiScript {
	/**
	 * 日志记录
	 * 
	 * @param user
	 * @param params
	 */
	public void logger(String user, String params) {
		
	}
	
	/**
	 * 响应行为
	 * 
	 * @param params
	 * @param httpExchange
	 * @return
	 */
	public abstract void action(String params, HttpExchange httpExchange);
}
