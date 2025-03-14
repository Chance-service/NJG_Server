package com.guaji.game.module.activity.exchange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.net.GuaJiNetManager;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.DoExchange;
import com.guaji.game.protocol.Activity2.HPDoExchangeRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ExchangeSevenCfg;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 限时兑换for77
 */
public class DoExchangeHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.EXCHANGE_DOUBLE_SEVEN_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		if (timeCfg.isEnd()) {
			// 活动已结束
			player.sendError(protocol.getType(), Status.error.ACTIVITY_END_VALUE);
			return true;
		}

		DoExchange request = protocol.parseProtocol(DoExchange.getDefaultInstance());
		ExchangeStatus exchangeStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				timeCfg.getStageId(), ExchangeStatus.class);

		// 要兑换的道具id
		String exchangeId = request.getExchangeId();
		// 要兑换的道具个数
		int times = request.getExchangeTimes();

		if (times == 0 || exchangeId == null) {
			return true;
		}

		if(times>=100)
		{//外掛送錯誤參數鎖帳號5年
			player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
			Calendar calendar = GuaJiTime.getCalendar();
			calendar.add(Calendar.YEAR, 5);
			PlayerEntity playerEntity = null;
			playerEntity = player.getPlayerData().getPlayerEntity();
			playerEntity.setForbidenTime(calendar.getTime());
			playerEntity.notifyUpdate(false);
			// 从竞技场排行榜删除
			ArenaManager.getInstance().removeArenaRank(player.getPlayerData().getId());
			GuaJiNetManager.getInstance().addBlackIp(player.getIp());
			GuaJiNetManager.getInstance().addBlackDevice(player.getDevice());
			// 日志记录
			BehaviorLogger.log4GM(String.valueOf(player.getPlayerData().getId()), Source.GM_OPERATION, Action.GM_FORBIDEN, 
					Params.valueOf("EXCHANGE_SEVEN_exchangeId", exchangeId), Params.valueOf("times", times), Params.valueOf("ip", player.getIp()));
			
			// 踢出玩家
			if (player != null) {
				player.kickout(Const.kickReason.LOGIN_FORBIDEN_VALUE);
			}
			return false;
		}

		ExchangeSevenCfg exchangeCfg = ConfigManager.getInstance().getConfigByKey(ExchangeSevenCfg.class, exchangeId);

		List<ItemInfo> consumeInfo = new ArrayList<ItemInfo>();
		// 根据兑换次数修正兑换消耗数目
		for (ItemInfo item : exchangeCfg.getExchangeConsumeInfo()) {
			ItemInfo newItem = item.clone();
			newItem.setQuantity(newItem.getQuantity() * times);
			consumeInfo.add(newItem);
		}

		if (exchangeCfg.getMaxExchangeTimes() != 0)// 有限制
		{
			if (times + exchangeStatus.getExchangeCount(exchangeId) > exchangeCfg.getMaxExchangeTimes()) {
				player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);

				return true;
			} else// 兑换
			{
				ConsumeItems consumeItems = new ConsumeItems();
				if (!consumeItems.addConsumeInfo(player.getPlayerData(), consumeInfo)) {
					player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
					return true;
				}

				if (!consumeItems.checkConsume(player, protocol.getType())) {
					return true;
				}

				consumeItems.consumeTakeAffect(player, Action.EXCHANGE_SEVEN);
			}
		} else {
			// 兑换
			ConsumeItems consumeItems = new ConsumeItems();
			if (!consumeItems.addConsumeInfo(player.getPlayerData(), consumeInfo)) {
				player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return true;
			}

			if (!consumeItems.checkConsume(player, protocol.getType())) {
				return true;
			}

			consumeItems.consumeTakeAffect(player, Action.EXCHANGE_SEVEN);
		}

		AwardItems awardItems = new AwardItems();
		// 根据兑换次数修正兑换消耗数目
		for (Item item : exchangeCfg.getExchangeAwardInfo().getAwardItems()) {
			Item newItem = item.clone();
			newItem.setCount(newItem.getCount() * times);
			awardItems.addItem(newItem);
		}
		awardItems.rewardTakeAffectAndPush(player, Action.EXCHANGE_SEVEN, 2);// 记录领取日志改

		exchangeStatus.modifyExchangeCount(exchangeId, times + exchangeStatus.getExchangeCount(exchangeId));

		player.getPlayerData().updateActivity(Const.ActivityId.EXCHANGE_DOUBLE_SEVEN_VALUE, timeCfg.getStageId(), true);

		int lastTime = timeCfg.calcActivitySurplusTime();

		// 发送服务器礼包协议
		player.sendProtocol(Protocol.valueOf(HP.code.EXCHANGE_INFO_S_VALUE,
				BuilderUtil.genExchangeStatus(lastTime, exchangeStatus)));

		HPDoExchangeRet.Builder builder = HPDoExchangeRet.newBuilder();
		builder.setExchangeId(exchangeId);
		builder.setExchangeTimes(exchangeStatus.getExchangeCount(exchangeId));
		player.sendProtocol(Protocol.valueOf(HP.code.DO_EXCHANGE_S, builder));
		return true;
	}
}
