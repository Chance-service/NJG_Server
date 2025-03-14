package org.guaji.app;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.listener.Listener;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;

import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;

/**
 * 应用程序对象
 */
public class AppObj extends Listener {
	/**
	 * 应用对象唯一标识
	 */
	protected GuaJiXID objXid;
	/**
	 * 会话对象
	 */
	protected GuaJiSession session;
	/**
	 * 应用对象支持的模块对象
	 */
	protected Map<Integer, ObjModule> objModules;
	
	/**
	 * 应用对象构造
	 * 
	 * @param xid
	 */
	public AppObj(GuaJiXID xid) {
		setXid(xid);
		objModules = new LinkedHashMap<Integer, ObjModule>();
	}

	/**
	 * 获取对象Id
	 * 
	 * @return
	 */
	public GuaJiXID getXid() {
		return objXid;
	}

	/**
	 * 设置对象Id
	 * 
	 * @param xid
	 */
	public void setXid(GuaJiXID xid) {
		this.objXid = xid;
	}

	/**
	 * 获取对象所对应的会话
	 * 
	 * @return
	 */
	public GuaJiSession getSession() {
		return session;
	}

	/**
	 * 设置对象所对应的会话
	 * 
	 * @param session
	 */
	public void setSession(GuaJiSession session) {
		if(this.session != null){
			Log.logPrintln(String.format("player setSession setAppObject nul %s",this.session.getIpAddr()));
			this.session.setAppObject(null);
		}
		this.session = session;
	}

	/**
	 * 判断对象是否在线
	 * @return
	 */
	public boolean isOnline() {
		return this.session != null && this.session.isActive();
	}
	
	/**
	 * 发送协议
	 * 
	 * @param protocol
	 * @return
	 */
	public boolean sendProtocol(Protocol protocol) {
		if (session != null && session.isActive()) {
			if (App.getInstance().getAppCfg().isDebug()) {
				// 将协议内容格式化成json
				Message protoBuilder = protocol.getBuilder().build();
				String protoJson = JsonFormat.printToString(protoBuilder);
				Log.logPrintln(String.format("send protocol: %d, size: %d, target: %s, protocol: %s", protocol.getType(), protocol.getSize(), getXid().toString(), protoJson));
			}
			return session.sendProtocol(protocol);
		}
		return false;
	}

	/**
	 * 获取指定Id的功能模块
	 * 
	 * @param moduleId
	 * @return
	 */
	public ObjModule getModule(int moduleId) {
		return objModules.get(moduleId);
	}

	/**
	 * 获取对象所有模块表
	 * 
	 * @return
	 */
	public Map<Integer, ObjModule> getObjModules() {
		return objModules;
	}
	
	/**
	 * 注册功能模块
	 * 
	 * @param moduleId
	 * @param objModule
	 */
	public void registerModule(int moduleId, ObjModule objModule) {
		objModules.put(moduleId, objModule);
	}

	/**
	 * 移除功能模块
	 * 
	 * @param moduleId
	 */
	public boolean removeModule(int moduleId) {
		if (objModules.containsKey(moduleId)) {
			objModules.remove(moduleId);
			return true;
		}
		return false;
	}
	
	/**
	 * 更新, 子类在处理自身逻辑后需要调用父类接口
	 * 
	 * @return
	 */
	public boolean onTick() {
		for (Entry<Integer, ObjModule> entry : objModules.entrySet()) {
			try {
				entry.getValue().onTick();
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return true;
	}

	/**
	 * 消息响应, 子类在处理自身逻辑后需要调用父类接口
	 * 
	 * @param msg
	 * @return true: 表示消息被处理; false: 消息未被处理, 错误显示
	 */
	public boolean onMessage(Msg msg) {		
		boolean processed = super.invokeMessage(this, msg);
		for (Entry<Integer, ObjModule> entry : objModules.entrySet()) {
			try {
				if (entry.getValue().isListenMsg(msg.getMsg())) {
					entry.getValue().onMessage(msg);
					processed |= true;
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return processed;
	}

	/**
	 * 协议响应, 子类在处理自身逻辑后需要调用父类接口
	 * 
	 * @param protocol
	 * @return true: 表示协议被处理; false: 协议未被处理, 未监听错误
	 */
	public boolean onProtocol(Protocol protocol) {
		boolean processed = super.invokeProtocol(this, protocol);
		for (Entry<Integer, ObjModule> entry : objModules.entrySet()) {
			try {
				if (entry.getValue().isListenProto(protocol.getType())) {
					entry.getValue().onProtocol(protocol);
					processed |= true;
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return processed;
	}
}
