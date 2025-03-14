package com.test.proto;

import com.guaji.game.protocol.Recharge.HPFetchShopList;
import com.test.robot.Robot;

public class Proto_FetchShopList implements IProtoGeneror {

	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		HPFetchShopList.Builder builder = HPFetchShopList.newBuilder();
		builder.setPlatform("default");
		ret = builder.build().toByteArray();
		return ret;
	}

}
