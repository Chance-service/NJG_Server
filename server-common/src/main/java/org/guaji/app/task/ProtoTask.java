package org.guaji.app.task;

import org.guaji.app.App;
import org.guaji.cache.Cache;
import org.guaji.cache.CacheObj;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;

/**
 * 协议类任务
 */
public class ProtoTask extends GuaJiTask {
	/**
	 * 任务缓存
	 */
	private static Cache taskCache = null;
	/**
	 * 对象id
	 */
	GuaJiXID xid;
	/**
	 * 协议对象
	 */
	Protocol protocol;

	/**
	 * 构造函数
	 * 
	 */
	protected ProtoTask() {
	}
	
	/**
	 * 构造函数
	 * 
	 * @param xid
	 * @param sid
	 * @param protocol
	 */
	protected ProtoTask(GuaJiXID xid, Protocol protocol) {
		setParam(xid, protocol);
	}

	/**
	 * 设置任务参数
	 * 
	 * @param xid
	 * @param sid
	 * @param protocol
	 */
	public void setParam(GuaJiXID xid, Protocol protocol) {
		this.xid = xid;
		this.protocol = protocol;
	}

	/**
	 * 缓存对象清理
	 */
	@Override
	protected void clear() {
		// 清理对象
		xid = null;
		protocol = null;
		
		// 释放本对象
		release(this);
	}

	/**
	 * 缓存对象克隆
	 */
	@Override
	protected CacheObj clone() {
		return new ProtoTask();
	}
	
	/**
	 * 执行协议任务
	 */
	@Override
	protected int run() {
		try {
			if (xid != null && xid.isValid() && protocol != null) {
				boolean dispatchOK = App.getInstance().dispatchProto(xid, protocol);
				if (!dispatchOK) {
					Log.errPrintln("dispatch protocol failed, protocolId: " + protocol.getType());
				}
				// 释放协议
				Protocol.release(protocol);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return 0;
	}

	/**
	 * 设置对象缓存
	 * @param cache
	 */
	public static void setCache(Cache cache) {
		taskCache = cache;
	}
	
	/**
	 * 释放对象
	 * @param task
	 */
	public static void release(ProtoTask task) {
		if (taskCache != null) {
			taskCache.release(task);
		}
	}
	
	/**
	 * 创建协议任务的统一出口
	 * 
	 * @return
	 */
	public static ProtoTask valueOf() {
		ProtoTask task = null;
		if (taskCache != null) {
			task = taskCache.create();
		} else {
			task = new ProtoTask();
		}
		return task;
	}
	
	/**
	 * 创建协议任务的统一出口
	 * 
	 * @param xid
	 * @param sid
	 * @param protocol
	 * @return
	 */
	public static ProtoTask valueOf(GuaJiXID xid, Protocol protocol) {
		ProtoTask task = null;
		if (taskCache != null) {
			task = taskCache.create();
		} else {
			task = new ProtoTask();
		}
		task.setParam(xid, protocol);
		return task;
	}
}
