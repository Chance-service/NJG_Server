package com.test.proto;

import com.guaji.game.protocol.ActionLog.HPActionRecord;
import com.test.robot.Robot;

public class Proto_ActionRecord implements IProtoGeneror {

	@Override
	public byte[] genBuilder(Robot robot) {
		byte[] ret = null;
		HPActionRecord.Builder builder = HPActionRecord.newBuilder();
		builder.setActionType(1);
		builder.setActivityId(27);
		ret = builder.build().toByteArray();
		return ret;
	}

}
