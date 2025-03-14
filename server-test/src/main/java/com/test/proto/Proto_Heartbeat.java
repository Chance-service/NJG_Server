package com.test.proto;

import com.guaji.game.protocol.SysProtocol.HPHeartBeat;
import com.test.robot.Robot;

public class Proto_Heartbeat implements IProtoGeneror{

	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		HPHeartBeat.Builder builder = HPHeartBeat.newBuilder();
		ret = builder.build().toByteArray();
		return ret;
	}

}
