package org.guaji.app;

import org.guaji.listener.Listener;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

/**
 * 对象功能模块
 */
public class ObjModule extends Listener {
	/**
	 * 模块宿主对象
	 */
	protected AppObj appObj;

	/**
	 * 模块构造函数
	 * 
	 * @param appObj
	 */
	public ObjModule(AppObj appObj) {
		this.appObj = appObj;
	}

	/**
	 * 获取模块宿主对象
	 * 
	 * @return
	 */
	public AppObj getAppObj() {
		return this.appObj;
	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	public boolean onTick() {
		return true;
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	public boolean onMessage(Msg msg) {
		return super.invokeMessage(appObj, msg);
	}

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	public boolean onProtocol(Protocol protocol) {
		return super.invokeProtocol(appObj, protocol);
	}
}
