package org.guaji.net;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.guaji.app.App;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;

/**
 * mina对应的io处理句柄
 */
public class GuaJiIoHandler extends IoHandlerAdapter {
	/**
	 * ip用途定义
	 */
	public static class IpUsage {
		/**
		 * ip白名单
		 */
		public static int WHITE_IPTABLES = 1;
		/**
		 * ip黑名单
		 */
		public static int BLACK_IPTABLES = 1;
	}

	/**
	 * 用途
	 */
	protected int ipUsage = 1;

	/**
	 * 默认构造
	 */
	public GuaJiIoHandler() {
	}

	/**
	 * 带用途handler构造
	 * 
	 * @param usage
	 */
	public GuaJiIoHandler(int ipUsage) {
		this.ipUsage = ipUsage;
	}

	/**
	 * 设置用途
	 * 
	 * @param usage
	 */
	public void setIpUsage(int ipUsage) {
		this.ipUsage = ipUsage;
	}

	/**
	 * 会话创建
	 */
	@Override
	public void sessionCreated(IoSession session) {
		NetStatistics.getInstance().onSessionCreated();
		// 获取ip信息
		String ipaddr = "0.0.0.0";
		try {
			ipaddr = session.getRemoteAddress().toString().split(":")[0].substring(1);
		} catch (Exception e) {
			MyException.catchException(e);
		}
		
		// 白名单校验
		if ((ipUsage & IpUsage.WHITE_IPTABLES) != 0) {
			if (!GuaJiNetManager.getInstance().checkWhiteIptables(ipaddr)) {
				Log.logPrintln(String.format("session closed by white iptables, ipaddr: %s", ipaddr));
				session.close(false);
				return;
			}
		}

		// 黑名单校验
		if ((ipUsage & IpUsage.BLACK_IPTABLES) != 0) {
			if (GuaJiNetManager.getInstance().checkBlackIptables(ipaddr)) {
				Log.logPrintln(String.format("session closed by black iptables, ipaddr: %s", ipaddr));
				session.close(false);
				return;
			}
		}

		try {
			GuaJiSession guaJiSession = new GuaJiSession();
			if (guaJiSession != null) {
				if (!guaJiSession.onOpened(session)) {
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
			e.printStackTrace();
		}
	}

	/**
	 * 消息接收回调
	 */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		try {
			GuaJiSession guaJiSession = (GuaJiSession) session.getAttribute(GuaJiSession.SESSION_ATTR);
			if (guaJiSession != null) {
				// 系统协议提前处理
				if (message instanceof Protocol) {
					Protocol protocol = (Protocol) message;
					if (GuaJiNetManager.getInstance().onSysProtocol(protocol)) {
						return;
					}
				}
				
				guaJiSession.onReceived(message);
				// 通知接收到协议对象
				NetStatistics.getInstance().onRecvProto();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 消息发送成功
	 */
	@Override
	public void messageSent(IoSession session, Object message) {
		// 通知已发送协议对象
		NetStatistics.getInstance().onSendProto();
	}

	/**
	 * 空闲回调
	 */
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		try {
			GuaJiSession guaJiSession = (GuaJiSession) session.getAttribute(GuaJiSession.SESSION_ATTR);
			if (guaJiSession != null) {
				guaJiSession.onIdle(status);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异常回调
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		try {
			session.close(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭回调
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		NetStatistics.getInstance().onSessionClosed();
		
		if (App.getInstance().getAppCfg().isDebug()) {
			String ipaddr = session.getRemoteAddress().toString().split(":")[0].substring(1);
			Log.logPrintln(String.format("session closed, ipaddr: %s, total: %d", ipaddr, NetStatistics.getInstance().getCurSession()));
		}
		
		try {
			GuaJiSession guaJiSession = (GuaJiSession) session.getAttribute(GuaJiSession.SESSION_ATTR);
			if (guaJiSession != null) {
				guaJiSession.onClosed();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
