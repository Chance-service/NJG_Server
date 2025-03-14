package com.test.proto;

import com.guaji.game.protocol.Login.HPLogin;
import com.test.robot.Robot;

public class Proto_Login implements IProtoGeneror{

	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		HPLogin.Builder builder = HPLogin.newBuilder();
		builder.setPuid(robot.getPuid());
		builder.setWallet("");
		builder.setIsGuest(1);
		builder.setDeviceId("device id is empty");
		// phonetype#osversion#OS#platform#channel#
		builder.setPlatform("test#test#test#test#test");
		builder.setToken("test");
		builder.setServerId(6);
		builder.setIsReLogin(false);
		builder.setPasswd("888888");
		ret = builder.build().toByteArray();
		return ret;
	}
}
