package com.test.proto;

import java.util.Random;

import com.guaji.game.protocol.Player.HPRoleCreate;
import com.test.robot.Robot;

public class Proto_CreateRole implements IProtoGeneror {
	private static final Random random = new Random();
	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		HPRoleCreate.Builder builder = HPRoleCreate.newBuilder();
		builder.setRoleItemId(random.nextInt(3)+1);
		builder.setRoleName(robot.getPuid());
		ret = builder.build().toByteArray();
		return ret;
	}

}
