package org.guaji.msg;

import org.guaji.app.AppObj;

/**
 * rpc调用响应器
 */
public abstract class RpcInvoker {
	/**
	 * 消息响应
	 * 
	 * @param targetObj
	 * @param msg
	 * @return
	 */
	public abstract boolean onMessage(AppObj targetObj, Msg msg); 
	
	/**
	 * 消息完成
	 * 
	 * @param callerObj
	 * @return
	 */
	public abstract boolean onComplete(AppObj callerObj);
}
