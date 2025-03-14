package org.guaji.security;

import org.guaji.net.GuaJiNetManager;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

/**
 * 会话安全组件
 */
public class GuaJiSecurity {
	/**
	 * 每秒协议数统计
	 */
	private int protocolCount = 0;
	/**
	 * 上次开始统计的时间(毫秒)
	 */
	private long statisticsTime = 0;

	/**
	 * 协议接收控制, 返回true表示正常, false表示异常(会关闭连接)
	 * 
	 * @param session
	 * @param protocol
	 * @return
	 */
	public boolean update(GuaJiSession session, Protocol protocol) {
		// 1秒内的统计
		if (statisticsTime + 1000 > GuaJiTime.getMillisecond()) {
			protocolCount++;
			if (GuaJiNetManager.getInstance().getSessionPPS() > 0 &&
				protocolCount > GuaJiNetManager.getInstance().getSessionPPS()) {
				return false;
			}
		} else {
			protocolCount = 1;
			statisticsTime = GuaJiTime.getMillisecond();
		}
		return true;
	}
}
