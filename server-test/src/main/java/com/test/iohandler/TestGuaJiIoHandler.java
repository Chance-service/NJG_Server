package com.test.iohandler;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.guaji.net.GuaJiSession;
import org.guaji.net.NetStatistics;

public class TestGuaJiIoHandler  extends IoHandlerAdapter{
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		try {
			GuaJiSession guaJiSession = (GuaJiSession) session.getAttribute(GuaJiSession.SESSION_ATTR);
			if (guaJiSession != null) {
				guaJiSession.onReceived(message);
				// 閫氱煡鎺ユ敹鍒板崗璁璞�
				NetStatistics.getInstance().onRecvProto();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(session, cause);
		cause.printStackTrace();
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		try {
			GuaJiSession guaJiSession = new GuaJiSession();
			guaJiSession.onOpenedClientSide(session);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
}
