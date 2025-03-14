package org.guaji.msg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.guaji.app.App;
import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

/**
 * 消息对象, 做对象事件驱动
 */
public class Msg {
	/**
	 * 普通消息类型
	 */
	public static int MSG_NORMAL = 0;
	/**
	 * rpc请求消息
	 */
	public static int MSG_RPC_REQ = 1;
	/**
	 * rpc响应消息
	 */
	public static int MSG_RPC_RESP = 2;
	
	/**
	 * rpc共享id
	 */
	private static AtomicInteger msgRpcId = new AtomicInteger();
	
	/**
	 * 消息类型
	 */
	private int type;
	/**
	 * 消息数据信息
	 */
	private int msg;
	/**
	 * 消息发送时间
	 */
	private long time;
	/**
	 * 消息目标ID
	 */
	private GuaJiXID target;
	/**
	 * 消息来源ID
	 */
	private GuaJiXID source;
	/**
	 * 消息参数列表
	 */
	private List<Object> params;
	/**
	 * 用户数据
	 */
	private Object userData;
	/**
	 * 消息调用堆栈
	 */
	private String stackTrace;
	/**
	 * 全局请求id
	 */
	private int rpcId;
	
	/**
	 * 消息构造函数, 外部不可见
	 * 
	 * @param msg
	 */
	protected Msg(int msg) {
		this.msg = msg;
		this.setTime(GuaJiTime.getMillisecond());

		if (App.getInstance().isDebug()) {
			stackTrace = MyException.formatStackTrace(Thread.currentThread().getStackTrace(), 3);
		}
	}

	/**
	 * 获取消息类型
	 * 
	 * @return
	 */
	public int getMsg() {
		return msg;
	}

	/**
	 * 设置消息类型
	 * 
	 * @param msg
	 */
	public void setMsg(int msg) {
		this.msg = msg;
	}

	/**
	 * 设置消息目标对象ID
	 * 
	 * @param xid
	 */
	public Msg setTarget(GuaJiXID xid) {
		if (target != null) {
			target.set(xid);
		} else {
			target = xid.clone();
		}
		return this;
	}

	/**
	 * 获取消息目标对象ID
	 * 
	 * @return
	 */
	public GuaJiXID getTarget() {
		return target;
	}

	/**
	 * 设置消息源对象ID
	 * 
	 * @param xid
	 */
	public Msg setSource(GuaJiXID xid) {
		if (source != null) {
			source.set(xid);
		} else {
			source = xid.clone();
		}
		return this;
	}

	/**
	 * 获取消息源对象ID
	 * 
	 * @return
	 */
	public GuaJiXID getSource() {
		return source;
	}

	/**
	 * 获取消息产生时间
	 * 
	 * @return
	 */
	public long getTime() {
		return time;
	}

	/**
	 * 设置消息产生时间
	 * 
	 * @param time
	 */
	public Msg setTime(long time) {
		this.time = time;
		return this;
	}

	/**
	 * 消息是否有效
	 * 
	 * @return
	 */
	public boolean isValid() {
		return msg > 0 && target.isValid();
	}

	/**
	 * 添加参数
	 * 
	 * @param params
	 */
	public Msg pushParam(Object... params) {
		if (this.params == null) {
			this.params = new ArrayList<Object>(params.length);
		}
		this.params.addAll(Arrays.asList(params));
		return this;
	}

	/**
	 * 获取指定索引参数
	 * 
	 * @param idx
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getParam(int idx) {
		return (T) params.get(idx);
	}

	/**
	 * 获取参数列表
	 * 
	 * @return
	 */
	public List<Object> getParams() {
		return params;
	}
	
	/**
	 * 打印调用堆栈
	 */
	public void printStackTrace() {
		if (stackTrace != null) {
			Log.logPrintln(stackTrace);
		}
	}

	/**
	 * 获取用户数据
	 * 
	 * @return
	 */
	public Object getUserData() {
		return userData;
	}

	/**
	 * 设置用户数据
	 * 
	 * @param userData
	 */
	public Msg setUserData(Object userData) {
		this.userData = userData;
		return this;
	}

	/**
	 * 获取保留标记
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置保留标记
	 * 
	 * @param type
	 */
	public Msg setType(int type) {
		this.type = type;
		return this;
	}
	
	/**
	 * 获取请求id
	 * 
	 * @return
	 */
	public int getRpcId() {
		return rpcId;
	}

	/**
	 * 设置请求id
	 * 
	 * @param rpcId
	 */
	public Msg setRpcId(int rpcId) {
		this.rpcId = rpcId;
		return this;
	}
	
	/**
	 * 构建rpc消息, 返回rpcid
	 * @return
	 */
	public int buildRpcMsg() {
		type = Msg.MSG_RPC_REQ;
		rpcId = msgRpcId.incrementAndGet();
		return rpcId;
	}
	
	/**
	 * 创建对象
	 * 
	 * @param msg
	 * @return
	 */
	public static Msg valueOf(int msg) {
		Msg msgg = new Msg(msg);
		return msgg;
	}

	/**
	 * 创建消息对象
	 * 
	 * @param msg
	 * @return
	 */
	public static Msg valueOf(int msg, GuaJiXID target) {
		Msg msgg = new Msg(msg);
		msgg.setTarget(target);
		return msgg;
	}

	/**
	 * 创建消息对象
	 * 
	 * @param msg
	 * @return
	 */
	public static Msg valueOf(int msg, GuaJiXID target, GuaJiXID source) {
		Msg msgg = new Msg(msg);
		msgg.setTarget(target);
		msgg.setSource(source);
		return msgg;
	}
}
