package com.test.proto;

import com.guaji.game.protocol.Mail.OPMailInfo;
import com.test.robot.Robot;

public class Proto_Mail implements IProtoGeneror {

	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		OPMailInfo.Builder builder = OPMailInfo.newBuilder();
		builder.setVersion(0);
		ret = builder.build().toByteArray();
		return ret;
	}

}
