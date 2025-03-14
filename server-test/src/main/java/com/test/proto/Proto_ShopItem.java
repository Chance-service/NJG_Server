package com.test.proto;

import com.guaji.game.protocol.Const.ShopType;
import com.guaji.game.protocol.Shop.ShopItemInfoRequest;
import com.test.robot.Robot;

public class Proto_ShopItem implements IProtoGeneror {

	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		ShopItemInfoRequest.Builder builder = ShopItemInfoRequest.newBuilder();
		builder.setType(1);
		builder.setShopType(ShopType.ARENA_MARKET);
		ret = builder.build().toByteArray();
		return ret;
	}

}
