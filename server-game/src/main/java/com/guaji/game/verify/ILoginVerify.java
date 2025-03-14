package com.guaji.game.verify;

import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;

public interface ILoginVerify {
	public boolean loginVerify(GuaJiSession session,Protocol protocol);
}
