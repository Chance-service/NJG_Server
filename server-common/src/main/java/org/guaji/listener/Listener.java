package org.guaji.listener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.app.AppObj;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.msg.IMsgHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.ProtocolTimer;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.MyException;

import com.google.protobuf.ProtocolMessageEnum;

/**
 * 消息&协议的监听器
 */
public class Listener {
	/**
	 * 注解方法
	 */
	class AnnoMethods {
		/**
		 * 消息注解方法列表
		 */
		Map<Integer, Method> msgAnnoMethods;
		/**
		 * 协议注解方法列表
		 */
		Map<Integer, Method> protoAnnoMethods;

		/**
		 * 构造函数
		 */
		AnnoMethods() {
			msgAnnoMethods = new HashMap<Integer, Method>();
			protoAnnoMethods = new HashMap<Integer, Method>();
		}

		/**
		 * 判断是否监听消息
		 * 
		 * @param msg
		 */
		boolean isListenMsg(int msg) {
			return msgAnnoMethods.containsKey(msg);
		}

		/**
		 * 判断是否监听协议
		 * 
		 * @param proto
		 */
		boolean isListenProto(int proto) {
			return protoAnnoMethods.containsKey(proto);
		}
	}

	static Map<Class<?>, AnnoMethods> classListenAnnoMethods = new ConcurrentHashMap<Class<?>, AnnoMethods>();

	/**
	 * 监听消息列表
	 */
	protected Map<Integer, IMsgHandler> iMsgHandlers;
	/**
	 * 监听协议列表
	 */
	protected Map<Integer, IProtocolHandler> protoHandlers;

	/**
	 * 默认构造函数
	 */
	public Listener() {
		scanAnnotation();
		iMsgHandlers = new HashMap<Integer, IMsgHandler>();
		protoHandlers = new HashMap<Integer, IProtocolHandler>();
	}

	/**
	 * 注册消息监听
	 * 
	 * @param msg
	 */
	public void listenMsg(int msg) {
		if (this.iMsgHandlers.containsKey(msg)) {
			throw new RuntimeException("duplication listen message");
		}
		this.iMsgHandlers.put(msg, null);
	}

	/**
	 * 注册消息监听
	 * 
	 * @param msg
	 */
	public void listenMsg(ProtocolMessageEnum msg) {
		if (this.iMsgHandlers.containsKey(msg.getNumber())) {
			throw new RuntimeException("duplication listen message");
		}
		this.iMsgHandlers.put(msg.getNumber(), null);
	}

	/**
	 * 注册消息监听
	 * 
	 * @param msg
	 * @param handler
	 */
	public void listenMsg(int msg, IMsgHandler handler) {
		if (this.iMsgHandlers.containsKey(msg)) {
			throw new RuntimeException("duplication listen message");
		}
		this.iMsgHandlers.put(msg, handler);
	}

	/**
	 * 注册消息监听
	 * 
	 * @param msg
	 * @param handler
	 */
	public void listenMsg(ProtocolMessageEnum msg, IMsgHandler handler) {
		if (this.iMsgHandlers.containsKey(msg.getNumber())) {
			throw new RuntimeException("duplication listen message");
		}
		this.iMsgHandlers.put(msg.getNumber(), handler);
	}

	/**
	 * 注册协议监听
	 * 
	 * @param proto
	 */
	public void listenProto(int proto) {
		if (this.protoHandlers.containsKey(proto)) {
			throw new RuntimeException("duplication listen protocol");
		}
		this.protoHandlers.put(proto, null);
	}

	/**
	 * 注册协议监听
	 * 
	 * @param proto
	 */
	public void listenProto(ProtocolMessageEnum proto) {
		if (this.protoHandlers.containsKey(proto.getNumber())) {
			throw new RuntimeException("duplication listen protocol");
		}
		this.protoHandlers.put(proto.getNumber(), null);
	}

	/**
	 * 注册协议监听
	 * 
	 * @param proto
	 * @param handler
	 */
	public void listenProto(int proto, IProtocolHandler handler) {
		if (this.protoHandlers.containsKey(proto)) {
			throw new RuntimeException("duplication listen protocol");
		}
		this.protoHandlers.put(proto, handler);
	}

	/**
	 * 注册协议监听
	 * 
	 * @param proto
	 * @param handler
	 */
	public void listenProto(ProtocolMessageEnum proto, IProtocolHandler handler) {
		if (this.protoHandlers.containsKey(proto.getNumber())) {
			throw new RuntimeException("duplication listen protocol");
		}
		this.protoHandlers.put(proto.getNumber(), handler);
	}

	/**
	 * 判断是否监听消息
	 * 
	 * @param msg
	 */
	public boolean isListenMsg(int msg) {
		if (this.iMsgHandlers.containsKey(msg)) {
			return true;
		}

		AnnoMethods annoMethods = classListenAnnoMethods.get(this.getClass());
		if (annoMethods != null && annoMethods.isListenMsg(msg)) {
			return true;
		}

		return false;
	}

	/**
	 * 判断是否监听协议
	 * 
	 * @param proto
	 */
	public boolean isListenProto(int proto) {
		if (this.protoHandlers.containsKey(proto)) {
			return true;
		}

		AnnoMethods annoMethods = classListenAnnoMethods.get(this.getClass());
		if (annoMethods != null && annoMethods.isListenProto(proto)) {
			return true;
		}

		return false;
	}

	/**
	 * 获取消息处理句柄
	 * 
	 * @param msg
	 * @return
	 */
	public IMsgHandler getMsgHandler(int msg) {
		return this.iMsgHandlers.get(msg);
	}

	/**
	 * 获取消息处理句柄
	 * 
	 * @param msg
	 * @return
	 */
	public IMsgHandler getMsgHandler(ProtocolMessageEnum msg) {
		return this.iMsgHandlers.get(msg.getNumber());
	}

	/**
	 * 获取协议处理句柄
	 * 
	 * @param proto
	 * @return
	 */
	public IProtocolHandler getProtoHandler(int proto) {
		return this.protoHandlers.get(proto);
	}

	/**
	 * 获取协议处理句柄
	 * 
	 * @param proto
	 * @return
	 */
	public IProtocolHandler getProtoHandler(ProtocolMessageEnum proto) {
		return this.protoHandlers.get(proto.getNumber());
	}

	/**
	 * 协议和消息处理句柄注解扫描
	 */
	protected void scanAnnotation() {
		// 自身扫描
		if (!classListenAnnoMethods.containsKey(this.getClass())) {
			Method[] methods = this.getClass().getDeclaredMethods();
			AnnoMethods annoMethods = new AnnoMethods();
			for (Method method : methods) {
				try {
					// 方法是否带有协议处理注解
					if (method.isAnnotationPresent(ProtocolHandlerAnno.class)) {
						method.setAccessible(true);
						ProtocolHandlerAnno protocolAnnotation = method.getAnnotation(ProtocolHandlerAnno.class);
						if (protocolAnnotation.code() != null) {
							for (int code : protocolAnnotation.code()) {
								annoMethods.protoAnnoMethods.put(code, method);
							}
						}
					}

					// 方法是否带有消息处理注解
					if (method.isAnnotationPresent(MessageHandlerAnno.class)) {
						method.setAccessible(true);
						MessageHandlerAnno messageAnnotation = method.getAnnotation(MessageHandlerAnno.class);
						if (messageAnnotation.code() != null) {
							for (int code : messageAnnotation.code()) {
								annoMethods.msgAnnoMethods.put(code, method);
							}
						}
					}
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}

			synchronized (classListenAnnoMethods) {
				if (!classListenAnnoMethods.containsKey(this.getClass())) {
					classListenAnnoMethods.put(this.getClass(), annoMethods);
				}
			}
		}
	}

	/**
	 * 消息响应
	 * 
	 * @param appObj
	 * @param msg
	 * @return
	 */
	protected boolean invokeMessage(AppObj appObj, Msg msg) {
		try {
			IMsgHandler handler = getMsgHandler(msg.getMsg());
			if (handler != null) {
				handler.onMessage(appObj, msg);
				return true;
			}

			// 消息采用注解模式调用
			AnnoMethods annoMethods = classListenAnnoMethods.get(this.getClass());
			if (annoMethods != null) {
				Method method = annoMethods.msgAnnoMethods.get(msg.getMsg());
				if (method != null) {
					method.invoke(this, msg);
					return true;
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	/**
	 * 协议响应
	 * 
	 * @param appObj
	 * @param protocol
	 * @return
	 */
	protected boolean invokeProtocol(AppObj appObj, Protocol protocol) {
		try {
			IProtocolHandler handler = getProtoHandler(protocol.getType());
			if (handler != null) {
			
				handler.onProtocol(appObj, protocol);

				return true;
			}

			// 协议采用注解模式调用
			AnnoMethods annoMethods = classListenAnnoMethods.get(this.getClass());
			if (annoMethods != null) {
				Method method = annoMethods.protoAnnoMethods.get(protocol.getType());
				if (method != null) {
					method.invoke(this, protocol);
					return true;
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}
}
