package com.guaji.game.callback;

import java.util.Set;

import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.util.GuaJiCallback;

import com.guaji.game.GsApp;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.HPPlayerKickout;

public class ShutdownCallback extends GuaJiCallback {
	// 停服回调
	public int invoke(Object args) {
		try {	
			GuaJiScript script = GuaJiScriptManager.getInstance().getScript("onshutdown");
			if (script != null) {
				script.action(null, null);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		
		boolean notify = (boolean) args;
		if (!notify) {
			Set<GuaJiSession> sessions = GsApp.getInstance().getActiveSessions();
			for (GuaJiSession session : sessions) {
				if (session != null && session.isActive()) {
					try {
						HPPlayerKickout.Builder builder = HPPlayerKickout.newBuilder();
						builder.setReason(Const.kickReason.SERVER_RESTART_VALUE);
						session.sendProtocol(Protocol.valueOf(HP.code.PLAYER_KICKOUT_S, builder));
					} catch (Exception e) {
						MyException.catchException(e);
					}
				}
			}
			OSOperator.osSleep(1000);
		}
		return 0;
	}
}
