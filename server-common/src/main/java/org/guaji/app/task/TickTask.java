package org.guaji.app.task;

import java.util.LinkedList;
import java.util.List;

import org.guaji.app.App;
import org.guaji.cache.Cache;
import org.guaji.cache.CacheObj;
import org.guaji.os.MyException;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;

/**
 * 更新类型任务
 */
public class TickTask extends GuaJiTask {
	/**
	 * 任务缓存
	 */
	private static Cache taskCache = null;
	/**
	 * 对象id
	 */
	GuaJiXID xid;
	/**
	 * 对象id列表
	 */
	List<GuaJiXID> xidList;

	/**
	 * 构造函数
	 * 
	 */
	protected TickTask() {
	}
	
	/**
	 * 构造函数
	 * 
	 * @param xid
	 */
	protected TickTask(GuaJiXID xid) {
		setParam(xid);
	}

	/**
	 * 构造函数
	 * 
	 * @param xid
	 */
	protected TickTask(List<GuaJiXID> xidList) {
		setParam(xidList);
	}

	/**
	 * 设置任务参数
	 * 
	 * @param xid
	 * @param msg
	 */
	public void setParam(GuaJiXID xid) {
		this.xid = xid;
	}

	/**
	 * 设置任务参数
	 * 
	 * @param xidList
	 * @param msg
	 */
	public void setParam(List<GuaJiXID> xidList) {
		if (this.xidList == null) {
			this.xidList = new LinkedList<GuaJiXID>();
		}
		this.xidList.clear();
		this.xidList.addAll(xidList);
	}
	
	/**
	 * 缓存对象清理
	 */
	@Override
	protected void clear() {
		xid = null;
		if (xidList != null) {
			xidList.clear();
		}
		// 释放本对象
		release(this);
	}

	/**
	 * 对象克隆
	 */
	@Override
	protected CacheObj clone() {
		return new TickTask();
	}
	
	/**
	 * 执行tick任务
	 */
	@Override
	protected int run() {
		if (xidList != null && xidList.size() > 0) {
			for (GuaJiXID xid : xidList) {
				try {
					App.getInstance().dispatchTick(xid);
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		} else {
			try {
				App.getInstance().dispatchTick(xid);
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return 0;
	}

	public static TickTask valueOf() {
		TickTask task = new TickTask();
		task.setParam(new LinkedList<GuaJiXID>());
		return task;
	}
	/**
	 * 构建对象
	 * 
	 * @param xidList
	 * @return
	 */
	public static TickTask valueOf(GuaJiXID xid) {
		TickTask task = new TickTask(xid);
		return task;
	}

	/**
	 * 构建对象
	 * 
	 * @param xidList
	 * @return
	 */
	public static TickTask valueOf(List<GuaJiXID> xidList) {
//		TickTask task = new TickTask(xidList);
//		return task;
		TickTask task = null;
		if (taskCache != null) {
			task = taskCache.create();
		} else {
			task = new TickTask();
		}
		task.setParam(xidList);
		return task;
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
	public static void release(TickTask task) {
		if (taskCache != null) {
			taskCache.release(task);
		}
	}
}
