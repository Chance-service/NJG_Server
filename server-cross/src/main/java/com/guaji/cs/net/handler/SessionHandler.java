package com.guaji.cs.net.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.guaji.app.App;
import org.guaji.log.Log;
import org.guaji.net.GuaJiNetManager;
import org.guaji.net.NetStatistics;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;

import com.google.protobuf.InvalidProtocolBufferException;
import com.guaji.game.protocol.CsBattle.ServerRegister;

/**
 * 协议处理句柄
 */
public final class SessionHandler extends IoHandlerAdapter {
	
	/**
	 * 会话表
	 */
	private Map<String, GuaJiSession> sessionMap;
	
	/**
	 * 会话表锁
	 */
	private Lock sessionLock;

	/**
	 * 单例对象
	 */
	private static final SessionHandler instance = new SessionHandler();
	
	/**
	 * 构造
	 */
	private SessionHandler() {
		sessionMap = new HashMap<String, GuaJiSession>();
		sessionLock = new ReentrantLock();
	}

	/**
	 * 获取单例对象
	 */
	public static SessionHandler getInstance() {
		return instance;
	}

	/**
	 * 获取会话
	 * 
	 * @param identify
	 * @return
	 */
	public GuaJiSession getSession(String identify) {
		try {
			sessionLock.lock();
			return sessionMap.get(identify);
		} finally {
			sessionLock.unlock();
		}
	}
	
	/**
	 * 服务器注册
	 * 
	 * @param packet
	 * @throws InvalidProtocolBufferException 
	 */
	public boolean onRegister(Protocol packet) throws InvalidProtocolBufferException {
		ServerRegister proto = packet.parseProtocol(ServerRegister.getDefaultInstance());
		packet.getSession().getIoSession().setAttribute("identify", proto.getServerIdentify());
		this.addSession(proto.getServerIdentify(), packet.getSession());
		if (packet.getSession() != null) {
			String ipAddress = packet.getSession().getIoSession().getRemoteAddress().toString();
			Log.logPrintln(String.format("Game service register OK, Identify: %s, IpAddr: %s", proto.getServerIdentify(), ipAddress));
		}
		return true;
	}

	/**
	 * 添加会话
	 * 
	 * @param identify
	 * @param session
	 * @return
	 */
	private void addSession(String identify, GuaJiSession session) {
		try {
			sessionLock.lock();
			sessionMap.put(identify, session);
		} finally {
			sessionLock.unlock();
		}
	}

	/**
	 * 移除会话
	 * 
	 * @param identify
	 * @return
	 */
	private void removeSession(String identify) {
		try {
			sessionLock.lock();
			sessionMap.remove(identify);
		} finally {
			sessionLock.unlock();
		}
	}
	
	/**
	 * 发送协议
	 */
	public int getSessionSize() {
		try {
			sessionLock.lock();
			return sessionMap.size();
		} finally {
			sessionLock.unlock();
		}
	}

	/**
	 * 发送协议
	 * 
	 * @param identify
	 * @param packet
	 */
	public void sendPacket(String identify, Protocol packet) {
		try {
			sessionLock.lock();
			GuaJiSession session = sessionMap.get(identify);
			if (session != null) {
				session.sendProtocol(packet);
			}
		} finally {
			sessionLock.unlock();
		}
	}

	/**
	 * 广播协议
	 * 
	 * @param packet
	 */
	public void broadcastPacket(Protocol packet) {
		try {
			sessionLock.lock();
			for (Entry<String, GuaJiSession> entry : sessionMap.entrySet()) {
				entry.getValue().sendProtocol(packet);
			}
		} finally {
			sessionLock.unlock();
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		session.close(false);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		Protocol msg = (Protocol) message;
		PacketHandler.getInstance().onRecvPacket(msg);
	}

	/**
	 * 会话关闭
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		if (session == null) {
			return;
		}
		Log.logPrintln("SessionClose, IpAddr: " + session);
		Object identify = session.getAttribute("identify");
		if (identify != null && identify instanceof String) {
			removeSession((String) identify);
		}
		GuaJiSession guajiSession = (GuaJiSession) session.getAttribute(GuaJiSession.SESSION_ATTR);
		if (guajiSession != null) {
			guajiSession.onClosed();
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// 获取ip信息
		String ipaddr = "0.0.0.0";
		try {
			ipaddr = session.getRemoteAddress().toString().split(":")[0].substring(1);
		} catch (Exception e) {
			e.printStackTrace();
			MyException.catchException(e);
		}
		try {
			GuaJiSession guajiSession = new GuaJiSession();
			if (guajiSession != null) {
				if (!guajiSession.onOpened(session)) {
					session.close(false);
					return;
				}
				// 最大会话数控制
				if (GuaJiNetManager.getInstance().getSessionMaxSize() > 0) {
					int curSession = NetStatistics.getInstance().getCurSession();
					if (curSession >= GuaJiNetManager.getInstance().getSessionMaxSize()) {
						Log.errPrintln(String.format("session maxsize limit, ipaddr: %s, total: %d", ipaddr, curSession));
						session.close(false);
						return;
					}
				}
				if (App.getInstance().getAppCfg().isDebug()) {
					Log.logPrintln(String.format("session opened, ipaddr: %s, total: %d", ipaddr, NetStatistics.getInstance().getCurSession()));
				}
			}
		} catch (Exception e) {
		}
        Log.logPrintln("SessionOpen, IpAddr: " + session.getRemoteAddress().toString());
	}
}
