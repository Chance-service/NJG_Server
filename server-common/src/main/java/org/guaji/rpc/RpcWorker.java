package org.guaji.rpc;

import org.guaji.net.protocol.Protocol;
import org.guaji.zmq.GuaJiZmq;
import org.guaji.zmq.GuaJiZmqManager;
import org.zeromq.ZMQ;

public abstract class RpcWorker {
	/**
	 * zmq响应对象
	 */
	GuaJiZmq responser;

	/**
	 * 初始化(地址eg: tcp://ip:addr, inproc://dealer_addr)
	 * 
	 * @param addr
	 * @return
	 */
	public boolean init(String addr) {
		responser = GuaJiZmqManager.getInstance().createZmq(ZMQ.REP);
		if (!responser.connect(addr)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 关闭对象
	 */
	public void close() {
		GuaJiZmqManager.getInstance().closeZmq(responser);
		responser = null;
	}

	/**
	 * 是否有请求事件
	 * 
	 * @param timeout
	 * @return
	 */
	public boolean hasRequest(int timeout) {
		if (responser != null) {
			return responser.pollEvent(GuaJiZmq.HZMQ_EVENT_READ, timeout) > 0;
		}
		return false;
	}

	/**
	 * 请求响应
	 * 
	 * @param protocol
	 * @param timeout
	 * @return
	 */
	public void response() {
		if (responser != null) {
			Protocol request = responser.recvProtocol(0);
			if (request != null) {
				Protocol response = response(request);
				responser.sendProtocol(response, 0);
			}
		}
	}

	/**
	 * 请求响应接口
	 * 
	 * @param protocol
	 * @return
	 */
	public abstract Protocol response(Protocol protocol);
}
