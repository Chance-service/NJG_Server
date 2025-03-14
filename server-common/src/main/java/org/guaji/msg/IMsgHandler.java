package org.guaji.msg;

import org.guaji.app.AppObj;

/**
 * 消息处理句柄
 */
public interface IMsgHandler {
	/**
	 * 消息处理器
	 * 
	 * @param appObj
	 * @param msg
	 * @return
	 */
	public boolean onMessage(AppObj appObj, Msg msg);
}
