package org.guaji.net.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.guaji.net.GuaJiNetManager;
import org.guaji.net.GuaJiSession;
import org.guaji.os.MyException;

/**
 * 客户端io处理器
 */
public class ClientIoHandler extends IoHandlerAdapter {
	/**
	 * 开启回调
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// 读写通道无操作进入空闲状态
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, GuaJiNetManager.getInstance().getSessionIdleTime());
		try {
			ClientSession clientSession = (ClientSession) session.getAttribute(GuaJiSession.SESSION_ATTR);
			if (clientSession != null) {
				clientSession.onOpened();
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}

	/**
	 * 消息接收回调
	 */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		try {
			ClientSession clientSession = (ClientSession) session.getAttribute(GuaJiSession.SESSION_ATTR);
			if (clientSession != null) {
				clientSession.onReceived(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 空闲回调
	 */
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// 空闲即关闭会话
		if (session.getConfig().getBothIdleTimeInMillis() > 0) {
			session.close(false);
		}
	}

	/**
	 * 异常回调
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		// 异常即关闭会话
		try {
			session.close(false);
		} catch (Exception e) {
		}
	}

	/**
	 * 关闭回调
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		try {
			ClientSession clientSession = (ClientSession) session.getAttribute(GuaJiSession.SESSION_ATTR);
			if (clientSession != null) {
				clientSession.onClosed();
			}
		} catch (Exception e) {
		}
	}
}
