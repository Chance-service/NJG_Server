package com.test.proto;

import com.guaji.game.protocol.Arena.HPArenaRankingList;
import com.test.robot.Robot;

public class Proto_ArenaRankList implements IProtoGeneror {

	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		HPArenaRankingList.Builder builder = HPArenaRankingList.newBuilder();
		ret = builder.build().toByteArray();
		return ret;
	}

}
