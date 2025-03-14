package com.test.proto;

import com.guaji.game.protocol.ProfRank.HPProfRankingList;
import com.test.ServerTest;
import com.test.robot.Robot;

public class Proto_ProRank implements IProtoGeneror {
	
	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		HPProfRankingList.Builder builder = HPProfRankingList.newBuilder();
		int type = ServerTest.RANDOM.nextInt(4);
		builder.setProfType(type);
		ret = builder.build().toByteArray();
		return ret;
	}

}
