package com.guaji.game.module.activity.consumeitem;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.module.activity.consume.AccConsumeStatus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity.AccConsumeItem;
import com.guaji.game.protocol.Activity.HPAccConsumeInfoRet;
import com.guaji.game.protocol.Activity.HPAccConsumeItemInfo;
import com.guaji.game.protocol.Activity.HPAccConsumeItemInfoRet;
import com.guaji.game.protocol.Activity2.HPDiscountGetRewardReq;
import com.guaji.game.protocol.Activity2.HPHaremExchangeReq;
import com.guaji.game.util.ActivityUtil;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年4月7日 下午5:53:02 类说明
 */
public class AccConsumeItemInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;

		// 检测活动是否开放
		int activityId = Const.ActivityId.ACCUMULATIVE_CONSUMEITEM_VALUE;

		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		AccConItemStatus accConsumeStatues = ActivityUtil.getAccConItemStatus(player.getPlayerData());

		if (accConsumeStatues == null)
			return true;

		HPAccConsumeItemInfoRet.Builder ret = HPAccConsumeItemInfoRet.newBuilder();
		ret.setSurplusTime(timeCfg.calcActivitySurplusTime());
		ret.setCfgId(timeCfg.getStageId());

		Map<Integer, ConsumeItem> items = accConsumeStatues.getConsumeItems();

		for (ConsumeItem item : items.values()) {

			AccConsumeItem.Builder messageItem = AccConsumeItem
					.newBuilder();
			messageItem.setBuyTime(item.getBuytime());
			messageItem.setGoodId(item.getGoodId());
			messageItem.setPrizeTime(item.getPrizeTime());
			ret.addItem(messageItem);
		}
		player.sendProtocol(Protocol.valueOf(HP.code.ACC_CONSUMEITEM_INFO_S_VALUE, ret));
		
		return true;
	}

}
