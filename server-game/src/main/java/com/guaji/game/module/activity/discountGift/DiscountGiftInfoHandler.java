package com.guaji.game.module.activity.discountGift;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity2.DiscountInfoReq;
import com.guaji.game.protocol.Activity2.HPDiscountInfoRet;
import com.guaji.game.protocol.HP;
import com.guaji.game.util.ActivityUtil;

/**
 * 获取折扣礼包活动信息
 *
 */

public class DiscountGiftInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		
		DiscountInfoReq req = protocol.parseProtocol(DiscountInfoReq.getDefaultInstance());
		int activityId = req.getActId();
		
		DiscountGiftData data = ActivityUtil.getDiscountGiftData(player.getPlayerData());

		// 发送服务器礼包协议
		HPDiscountInfoRet.Builder builder = data.toBuilder(player.getPlayerData(),activityId);

		player.sendProtocol(Protocol.valueOf(HP.code.DISCOUNT_GIFT_INFO_S, builder));

		return true;
	}
}
