package org.guaji.app.task;

import java.util.LinkedList;
import java.util.List;

import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.cache.CacheObj;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.msg.MsgProxy;
import org.guaji.msg.MsgRpc;
import org.guaji.obj.ObjBase;
import org.guaji.os.MyException;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;

/**
 * 消息类型任务
 */
public class MsgTask extends GuaJiTask {
	/**
	 * 对象id
	 */
	GuaJiXID xid;
	/**
	 * 消息对象
	 */
	Msg msg;
	/**
	 * 消息代理对象
	 */
	MsgProxy msgProxy;
	/**
	 * 对象id列表
	 */
	List<GuaJiXID> xidList;

	/**
	 * 构造函数
	 * 
	 */
	protected MsgTask() {
	}
	
	/**
	 * 构造函数
	 * 
	 * @param xid
	 * @param msgProxy
	 */
	protected MsgTask(GuaJiXID xid, MsgProxy msgProxy) {
		this.xid = xid;
		this.msgProxy = msgProxy;
	}

	/**
	 * 构造函数
	 * 
	 * @param xid
	 * @param msg
	 */
	protected MsgTask(GuaJiXID xid, Msg msg) {
		setParam(xid, msg);
	}
	
	/**
	 * 构造函数
	 * 
	 * @param xidList
	 * @param msg
	 */
	protected MsgTask(List<GuaJiXID> xidList, Msg msg) {
		setParam(xidList, msg);
	}

	/**
	 * 获取对象id
	 * 
	 * @return
	 */
	public GuaJiXID getXid() {
		return xid;
	}

	/**
	 * 获取对象id列表
	 * 
	 * @return
	 */
	public List<GuaJiXID> getXidList() {
		return xidList;
	}
	
	/**
	 * 设置任务参数
	 * 
	 * @param xid
	 * @param msg
	 */
	public void setParam(GuaJiXID xid, Msg msg) {
		this.xid = xid;
		this.msg = msg;
	}

	/**
	 * 设置任务参数
	 * 
	 * @param xidList
	 * @param msg
	 */
	public void setParam(List<GuaJiXID> xidList, Msg msg) {
		this.msg = msg;
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
		msg = null;
		if (xidList != null) {
			xidList.clear();
		}
	}

	/**
	 * 缓存对象克隆
	 */
	@Override
	protected CacheObj clone() {
		return new MsgTask();
	}
	
	/**
	 * 执行消息任务
	 */
	@Override
	protected int run() {
		try {
			boolean dispatchOK = false;
			// proxy请求
			if (msgProxy != null) {
				return onProxyMessage();
			}
			
			// 通用消息处理
			if (msg != null) {
				// rpc请求
				if (msg.getType() == Msg.MSG_RPC_REQ || msg.getType() == Msg.MSG_RPC_RESP) {
					return onRpcMessage();
				}
				
				if (xidList != null && xidList.size() > 0) {
					for (GuaJiXID xid : xidList) {
						dispatchOK = App.getInstance().dispatchMsg(xid, msg);
					}
				} else if (xid.isValid()) {
					dispatchOK = App.getInstance().dispatchMsg(xid, msg);
				}
			} else {
				Log.errPrintln("dispatch message null");
			}

			if (!dispatchOK) {
				Log.errPrintln("dispatch message failed, msgId: " + msg.getMsg());
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return 0;
	}

	/**
	 * rpc消息处理
	 * 
	 * @return
	 */
	private int onRpcMessage() {
		// rpc请求
		if (msg.getType() == Msg.MSG_RPC_REQ) {
			ObjBase<GuaJiXID, AppObj> objBase = App.getInstance().lockObject(msg.getTarget());
			if (objBase != null) {
				try {
					if (msg.getType() == Msg.MSG_RPC_REQ) {
						MsgRpc.getInstance().onRequest(objBase.getImpl(), msg);
					} else if (msg.getType() == Msg.MSG_RPC_RESP) {
						MsgRpc.getInstance().onResponse(objBase.getImpl(), msg);
					}
				} catch (Exception e) {
					MyException.catchException(e);
				} finally {
					objBase.unlockObj();
				}
			}
		}
		return 0;
	}
	
	/**
	 * proxy消息处理
	 * 
	 * @return
	 */
	private int onProxyMessage() {
		ObjBase<GuaJiXID, AppObj> objBase = App.getInstance().lockObject(xid);
		if (objBase != null) {
			try {
				msgProxy.onInvoke(objBase.getImpl());
			} catch (Exception e) {
				MyException.catchException(e);
			} finally {
				objBase.unlockObj();
			}
		}
		return 0;
	}
	
	/**
	 * 创建消息任务的统一出口
	 * 
	 * @param msg
	 * @return
	 */
	public static MsgTask valueOf(Msg msg) {
		MsgTask task = new MsgTask(msg.getTarget(), msg);
		return task;
	}
	
	/**
	 * 创建消息任务的统一出口
	 * 
	 * @param xid
	 * @param msg
	 * @return
	 */
	public static MsgTask valueOf(GuaJiXID xid, Msg msg) {
		MsgTask task = new MsgTask(xid, msg);
		return task;
	}

	/**
	 * 创建消息任务的统一出口
	 * 
	 * @param xidList
	 * @param msg
	 * @return
	 */
	public static MsgTask valueOf(List<GuaJiXID> xidList, Msg msg) {
		MsgTask task = new MsgTask(xidList, msg);
		return task;
	}
	
	/**
	 * 创建消息任务的统一出口
	 * 
	 * @param xid
	 * @param msg
	 * @return
	 */
	public static MsgTask valueOf(GuaJiXID xid, MsgProxy msgProxy) {
		MsgTask task = new MsgTask(xid, msgProxy);
		return task;
	}
}
