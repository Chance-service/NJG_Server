package com.guaji.game.module.activity.consumeitem;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.GsApp;
import com.guaji.game.config.AccConsumeCfg;
import com.guaji.game.config.AccConsumeItemCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.module.activity.consume.AccConsumeStatus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity.AccConsumeItem;

import com.guaji.game.protocol.Activity.HPGetAccConsumeItemAward;
import com.guaji.game.protocol.Activity.HPGetAccConsumeItemAwardRet;
import com.guaji.game.util.ActivityUtil;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年4月7日 下午5:52:45 类说明
 */
public class AccConsumeItemAwardHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.ACCUMULATIVE_CONSUMEITEM_VALUE;
		HPGetAccConsumeItemAward request = protocol.parseProtocol(HPGetAccConsumeItemAward.getDefaultInstance());
		int goodId = request.getGoodid();
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		
		AccConsumeItemCfg itemCfg = AccConsumeItemCfg.getConsumeItemById(goodId);
		if (itemCfg == null) {// 活动已关闭
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;

		}

		AccConItemStatus accConsumeStatues = ActivityUtil.getAccConItemStatus(player.getPlayerData());
		if (accConsumeStatues == null)
			return true;

		ConsumeItem goodItem = accConsumeStatues.getConsumeItems().get(goodId);
		if (goodItem == null) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}

		int canPrizeTime = goodItem.getBuytime() - goodItem.getPrizeTime();
		if (canPrizeTime < itemCfg.getNeedTimes()) {
			// 累计购买次数还未达到
			player.sendError(protocol.getType(), Status.error.ACC_CONSUME_NOT_REACH);
			return true;
		}

		// 累计领奖系数
		int multiple = canPrizeTime / itemCfg.getNeedTimes();
		goodItem.setPrizeTime(goodItem.getPrizeTime() + multiple * itemCfg.getNeedTimes());
		AwardItems awardItems = AwardItems.valueOf(itemCfg.getAwards());
		for (Item item : awardItems.getAwardItems()) {
			item.count = item.count * multiple;
		}
		awardItems.rewardTakeAffectAndPush(player, Action.ACC_CONSUME_AWARDS, 2);
		// 更新活动数据
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		HPGetAccConsumeItemAwardRet.Builder builder = HPGetAccConsumeItemAwardRet.newBuilder();
		builder.setState(1);// 成功
		AccConsumeItem.Builder conItem = AccConsumeItem.newBuilder();
		conItem.setGoodId(goodItem.getGoodId());
		conItem.setBuyTime(goodItem.getBuytime());
		conItem.setPrizeTime(goodItem.getPrizeTime());
		builder.setItem(conItem);
		player.sendProtocol(Protocol.valueOf(HP.code.GET_ACC_CONSUMEITEM_AWARD_S, builder));
		return true;

	}

}
