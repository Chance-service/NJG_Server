package org.guaji.intercept;

import org.guaji.app.AppObj;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

public class InterceptHandler {
	/**
	 * 帧更新
	 * 
	 * @return false表示不拦截, 否则拦截更新调用不往下进行
	 */
	public boolean onTick(AppObj appObj) {
		return false;
	}
	
	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return false表示不拦截, 否则拦截消息不往下进行
	 */
	public boolean onMessage(AppObj appObj, Msg msg) {
		return false;
	}

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return false表示不拦截, 否则拦截协议不往下进行
	 */
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		return false;
	}
}
