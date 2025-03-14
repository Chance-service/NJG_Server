package com.test.proto;

import com.guaji.game.protocol.Mail.OPMailGet;
import com.test.robot.Robot;

public class Proto_MailGet implements IProtoGeneror {

	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		OPMailGet.Builder builder = OPMailGet.newBuilder();
		builder.setId(0);
		builder.setType(1);
		builder.setMailClassify(1);
		ret = builder.build().toByteArray();
		return ret;
	}

}
