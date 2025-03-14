package com.test.proto;

import com.guaji.game.protocol.MercenaryExpedition.HPMercenaryExpeditionInfo;
import com.test.robot.Robot;

public class Proto_ME implements IProtoGeneror{

	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		HPMercenaryExpeditionInfo.Builder builder = HPMercenaryExpeditionInfo.newBuilder();
		ret = builder.build().toByteArray();
		return ret;
	}

}
