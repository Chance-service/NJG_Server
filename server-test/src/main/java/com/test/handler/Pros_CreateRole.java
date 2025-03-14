package com.test.handler;

import org.guaji.net.protocol.Protocol;

import com.test.robot.Robot;

public class Pros_CreateRole implements IRespProtocolHandler{

	@Override
	public void handler(Protocol protocol, Robot robot) {
		// TODO Auto-generated method stub
		robot.setCreated(true);
	}

}
