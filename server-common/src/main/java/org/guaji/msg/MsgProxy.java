package org.guaji.msg;

import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.app.task.MsgTask;

public abstract class MsgProxy {
	/**
	 * 代理投递消息
	 * 
	 * @param targetObj
	 */
	public boolean post(AppObj targetObj) {
		return App.getInstance().postMsgTask(MsgTask.valueOf(targetObj.getXid(), this));
	}
	
	/**
	 * 被调用的回调函数
	 * 
	 * @param targetObj
	 * @return
	 */
	public abstract int onInvoke(AppObj targetObj);
}
