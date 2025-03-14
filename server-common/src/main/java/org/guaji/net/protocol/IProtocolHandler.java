package org.guaji.net.protocol;

import org.guaji.app.AppObj;

/**
 * 协议处理句柄
 */
public interface IProtocolHandler {
	/**
	 * 协议处理
	 * 
	 * @param appObj
	 * @param protocol
	 * @return
	 */
	public boolean onProtocol(AppObj appObj, Protocol protocol);
}
