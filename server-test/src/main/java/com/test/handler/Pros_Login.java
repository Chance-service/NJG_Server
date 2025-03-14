package com.test.handler;

import org.guaji.net.protocol.Protocol;

import com.guaji.game.protocol.Login.HPLoginRet;
import com.test.robot.Robot;

public class Pros_Login implements IRespProtocolHandler {

	@Override
	public void handler(Protocol protocol, Robot robot) {
		// TODO Auto-generated method stub
		HPLoginRet ret = protocol.parseProtocol(HPLoginRet.getDefaultInstance());
		int playerId = ret.getPlayerId();
		robot.setPlayerId(playerId);
		robot.setLogined(true);
	}

}
