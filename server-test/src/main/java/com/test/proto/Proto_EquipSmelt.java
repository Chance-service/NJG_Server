package com.test.proto;

import com.guaji.game.protocol.EquipOpr.HPEquipSmelt;
import com.test.robot.Robot;

public class Proto_EquipSmelt implements IProtoGeneror {

	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		HPEquipSmelt.Builder builder = HPEquipSmelt.newBuilder();
		builder.setIsMass(1);
		builder.addMassQuality(1);
		builder.addMassQuality(2);
		builder.addMassQuality(3);
		builder.addMassQuality(4);
		builder.addMassQuality(5);
		ret = builder.build().toByteArray();
		return ret;
	}

}
