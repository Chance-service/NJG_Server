package org.guaji.rpc;

import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;
import org.guaji.zmq.GuaJiZmq;
import org.guaji.zmq.GuaJiZmqManager;
import org.zeromq.ZMQ;

/**
 * rpc客户端封装
 */
public class RpcClient {
	/**
	 * 请求zmq对象
	 */
	GuaJiZmq requester;

	/**
	 * 初始化(地址eg: tcp://ip:port, inproc://router_addr)
	 * 
	 * @param addr
	 * @return
	 */
	public boolean init(String addr) {
		requester = GuaJiZmqManager.getInstance().createZmq(ZMQ.REQ);
		if (!requester.connect(addr)) {
			return false;
		}
		return true;
	}

	/**
	 * 关闭对象
	 */
	public void close() {
		GuaJiZmqManager.getInstance().closeZmq(requester);
		requester = null;
	}

	/**
	 * 返回请求zmq对象
	 * 
	 * @return
	 */
	public GuaJiZmq getRequester() {
		return requester;
	}

	/**
	 * 请求调用
	 * 
	 * @param protocol
	 * @param timeout
	 * @return
	 */
	public Protocol request(Protocol protocol, int timeout) throws Exception {
		if (requester != null) {
			// 抛弃之前超时的数据, 接收, 丢弃
			requester.discardMsg();
			// 发送请求
			if (requester.sendProtocol(protocol, 0)) {
				// 接收响应
				if (requester.pollEvent(GuaJiZmq.HZMQ_EVENT_READ, timeout) > 0) {
					Protocol response = requester.recvProtocol(0);
					if (response != null && response.getReserve() == protocol.getReserve()) {
						return response;
					}
				} else {
					throw new MyException(String.format("rpc request timeout, protocol: %d, timeout: %d", protocol.getType(), timeout));
				}
			}
		}
		return null;
	}
}
