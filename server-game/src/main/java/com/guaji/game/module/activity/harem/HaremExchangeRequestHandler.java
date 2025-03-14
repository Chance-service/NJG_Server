package com.guaji.game.module.activity.harem;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.net.GuaJiNetManager;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPHaremExchangeReq;
import com.guaji.game.protocol.Activity2.HPHaremScorePanelRes;
import com.guaji.game.config.HaremExchangeCfg;
import com.guaji.game.entity.HaremActivityEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 王的后宫,积分兑换
 */
public class HaremExchangeRequestHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		HPHaremExchangeReq req = protocol.parseProtocol(HPHaremExchangeReq.getDefaultInstance());
		int exchangeId = req.getId();
		int times =  req.getTimes();

		Map<Object, HaremExchangeCfg> exchangeCfgMap = ConfigManager.getInstance().getConfigMap(HaremExchangeCfg.class);

		HaremExchangeCfg exchangeCfg = exchangeCfgMap.get(exchangeId);
		if (exchangeCfgMap == null || exchangeCfgMap.size() == 0 || exchangeCfg == null) {
			// 配置没找到
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return false;
		}
		if(times>=100)
		{//外掛送錯誤參數鎖帳號5年
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
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
					Params.valueOf("HAREM_EXCHANGE_exchangeId", exchangeId), Params.valueOf("times", times), Params.valueOf("ip", player.getIp()));
			
			// 踢出玩家
			if (player != null) {
				player.kickout(Const.kickReason.LOGIN_FORBIDEN_VALUE);
			}
			return false;
		}

		// 活动实体
		HaremActivityEntity entity = player.getPlayerData().getHaremActivityEntity();
		Map<Integer, Integer> exchangeMap = entity.getExchangeMap();
		// 已兑换次数
		Integer exchangeTimes = 0;
		if (exchangeMap != null && exchangeMap.size() > 0) {
			exchangeTimes = exchangeMap.get(exchangeId);
			exchangeTimes = exchangeTimes == null ? 0 : exchangeTimes;
		}

		// 验证兑换次数
		if (exchangeTimes + times > exchangeCfg.getLimitTimes()) {
			// 没有兑换次数
			player.sendError(protocol.getType(), Status.error.DAYLI_TIMES_LIMIT);
			return false;
		}
		
		// 消耗积分
		int costCredits = exchangeCfg.getCostCredits();
		// 消耗道具
		String costItems = exchangeCfg.getCostItems();

		if ((costItems == null || costItems.length() == 0) && costCredits == 0) {
			// 配置没找到
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return false;
		}
		
		//组装消耗道具结构体
		List<ItemInfo> costItemList = ItemInfo.valueListOf(costItems);
		for (Iterator<ItemInfo> iterator = costItemList.iterator(); iterator.hasNext();) {
			ItemInfo itemInfo = iterator.next();
			itemInfo.setQuantity(itemInfo.getQuantity() * times);
		}
		// 奖励
		String reward = exchangeCfg.getExchangeItems();
		List<ItemInfo> rewardList = ItemInfo.valueListOf(reward);
		for (Iterator<ItemInfo> iterator = rewardList.iterator(); iterator.hasNext();) {
			ItemInfo itemInfo = iterator.next();
			itemInfo.setQuantity(itemInfo.getQuantity() * times);
		}
		AwardItems awardItems = new AwardItems();
		awardItems.addItemInfos(rewardList);

		ConsumeItems consumeItems = ConsumeItems.valueOf();
		// 验证道具数量
		if (costCredits == 0) {
			boolean isCost = consumeItems.addConsumeInfo(player.getPlayerData(),costItemList );
			if (!isCost) {
				// 道具不足
				player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return false;
			}
			if (!consumeItems.checkConsume(player, protocol.getType())) {
				player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return false;
			}
			//消耗道具
			consumeItems.consumeTakeAffect(player,  Action.HAREM_EXCHANGE);
			
		} else {
			int needCredites = exchangeCfg.getCostCredits()*times;
			if (entity.getScore() < needCredites) {
				// 活动积分不足
				player.sendError(protocol.getType(), Status.error.ROULETTE_CREDITS_LACK_VALUE);
				return false;
			}
			//消耗积分
			entity.setScore(entity.getScore()-needCredites);
		}

		awardItems.rewardTakeAffectAndPush(player, Action.HAREM_EXCHANGE, 1);
		entity.getExchangeMap().put(exchangeId, exchangeTimes + times);
		entity.notifyUpdate();

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.HAREM_EXCHANGE, Params.valueOf("exchangeId", exchangeId),
				Params.valueOf("times", times),Params.valueOf("curTimes", entity.getExchangeMap().get(exchangeId)));
		
		// 组装协议
		HPHaremScorePanelRes.Builder builder = BuilderUtil.getHaremExchangeBuilders(entity, exchangeCfgMap,times);

		player.sendProtocol(Protocol.valueOf(HP.code.HAREM_EXCHANGE_S_VALUE, builder));
		return true;
	}
}
