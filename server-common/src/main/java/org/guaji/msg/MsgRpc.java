package org.guaji.msg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.os.MyException;

/**
 * 消息远程过程调用
 */
public class MsgRpc {
	/**
	 * 响应者列表
	 */
	private Map<Integer, RpcInvoker> rpcInvokerMap;
	
	/**
	 * 全局实例对象
	 */
	private static MsgRpc instance = null;
	
	/**
	 * 获取rpc实例
	 * 
	 * @return
	 */
	public static MsgRpc getInstance() {
		if (instance == null) {
			instance = new MsgRpc();
		}
		return instance;
	}
	
	/**
	 * 构造
	 */
	private MsgRpc() {
		rpcInvokerMap = new ConcurrentHashMap<Integer, RpcInvoker>();
	}
	
	/**
	 * 消息远程调用
	 * 
	 * @param msg 必须有target和source
	 * @return
	 */
	public boolean call(Msg msg, RpcInvoker invoker) {
		if (invoker == null) {
			return false;
		}
		
		if (!msg.getTarget().isValid() || !msg.getSource().isValid()) {
			throw new RuntimeException("rpc message need target and source");
		}
		
		int rpcId = msg.buildRpcMsg();
		if (rpcId > 0) {
			rpcInvokerMap.put(rpcId, invoker);
			return App.getInstance().postMsg(msg);
		}
		return false;
	}
	
	/**
	 * 响应处理, 系统调用
	 * 
	 * @param msg
	 */
	public void onRequest(AppObj targetObj, Msg msg) {
		if (msg.getType() == Msg.MSG_RPC_REQ && msg.getRpcId() > 0 && rpcInvokerMap.containsKey(msg.getRpcId())) {
			RpcInvoker invoker = rpcInvokerMap.get(msg.getRpcId());
			
			try {
				invoker.onMessage(targetObj, msg);
			} catch (Exception e) {
				MyException.catchException(e);
			}
			
			Msg respMsg = Msg.valueOf(msg.getMsg(), msg.getSource(), msg.getTarget());
			respMsg.setType(Msg.MSG_RPC_RESP).setRpcId(msg.getRpcId());
			App.getInstance().postMsg(respMsg);
		}
	}
	
	/**
	 * 回调处理, 系统调用
	 * 
	 * @param msg
	 */
	public void onResponse(AppObj callerObj, Msg msg) {
		if (msg.getType() == Msg.MSG_RPC_RESP && msg.getRpcId() > 0 && rpcInvokerMap.containsKey(msg.getRpcId())) {
			RpcInvoker invoker = rpcInvokerMap.remove(msg.getRpcId());
			
			try {
				invoker.onComplete(callerObj);
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
	}
}
