package com.guaji.game.module.activity.fragmentExchange;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.FragmentExchangeReq;
import com.guaji.game.protocol.Activity2.FragmentExchangeRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FragmentExchangeCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.soul.ConsumeRoleSoul;
import com.guaji.game.soul.RoleSoulInfo;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Reward.RewardInfo;
import com.guaji.game.protocol.Status;

/**
 * 万能碎片限时兑换活动
 */
public class FragmentExchangeHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.FRAGMENT_EXCHANGE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeToEndCfg(activityId);
		// 活动已关闭
		if (timeCfg == null) {
			player.sendError(HP.code.SYNC_FRAGMENT_EXCHANGE_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析数据
		FragmentExchangeReq request = protocol.parseProtocol(FragmentExchangeReq.getDefaultInstance());

		int id = request.getId(); // 请求配表唯一ID
		String costFragment = request.getFragment(); // 消耗的碎片单价
		int multiple = request.getMultiple(); // 消耗倍数
		if (StringUtils.isBlank(costFragment) || multiple == 0) {
			player.sendError(HP.code.FRAGMENT_EXCHANGE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return true;
		}

		// 提取玩家数据
		FragmentExchangeStatus fragmentExchangeStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), FragmentExchangeStatus.class);
		if (null == fragmentExchangeStatus) {
			player.sendError(HP.code.FRAGMENT_EXCHANGE_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}

		FragmentExchangeCfg cfg = ConfigManager.getInstance().getConfigByKey(FragmentExchangeCfg.class, id);
		if (null == cfg) {
			player.sendError(HP.code.FRAGMENT_EXCHANGE_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}

		// 判断消耗的佣兵碎片存不存在于配表中
		Map<Integer, List<String>> costFragmentMap = cfg.getCostFragmentMap();
		List<String> fragmentList = costFragmentMap.get(id);
		if (null == fragmentList || !fragmentList.contains(costFragment)) {
			player.sendError(HP.code.FRAGMENT_EXCHANGE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return true;
		}

		// 检测道具消耗
		String costItem = cfg.getCostItem();
		List<ItemInfo> costItemList = calItemInfoList(costItem, multiple);
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		consumeItems.addConsumeInfo(player.getPlayerData(), costItemList);
		if (!consumeItems.checkConsume(player, HP.code.FRAGMENT_EXCHANGE_C_VALUE))
			return true;
		
		// 检测佣兵碎片
		String roleSoulString = calItemInfo(costFragment, multiple);
		RoleSoulInfo roleSoulInfo = RoleSoulInfo.valueOf(roleSoulString);
		ConsumeRoleSoul consumeRoleSoul = ConsumeRoleSoul.valueOf();
		int checkResult = consumeRoleSoul.checkConsume(player, roleSoulInfo);
		// 消耗佣兵碎片
		if (checkResult != 0) {
			player.sendError(HP.code.FRAGMENT_EXCHANGE_C_VALUE, checkResult);
			return true;
		}
		consumeRoleSoul.consumeRoleSoul(player, roleSoulInfo, Action.FRAGMENT_EXCHANGE);
		
		// 消耗道具
		consumeItems.consumeTakeAffect(player, Action.FRAGMENT_EXCHANGE);
		
		// 下发奖励并弹框
		String rewardFragment = cfg.getRewardFragement();
		rewardFragment = calItemInfo(rewardFragment, multiple);
		AwardItems awardItems = AwardItems.valueOf(rewardFragment);
		RewardInfo.Builder rewardBuilder = awardItems.rewardTakeAffectAndPush(player, Action.FRAGMENT_EXCHANGE, 2);

		// 返回数据包
		FragmentExchangeRes.Builder response = FragmentExchangeRes.newBuilder();
		response.setRewardFragment(rewardBuilder.build());
		player.sendProtocol(Protocol.valueOf(HP.code.FRAGMENT_EXCHANGE_S_VALUE, response));
		return true;
	}


	/**
	 * 计算出需要消耗的道具或货币
	 * 
	 * @param univalence 单价，格式：30000_205101_2,30000_205201_2
	 * @param multiple 倍数
	 */
	private List<ItemInfo> calItemInfoList(String univalence, int multiple) {
		if (StringUtils.isBlank(univalence))
			return null;
		List<ItemInfo> list = ItemInfo.valueListOf(univalence);
		if (multiple == 1)
			return list;
		for (Iterator<ItemInfo> iterator = list.iterator(); iterator.hasNext();) {
			ItemInfo itemInfo = (ItemInfo) iterator.next();
			itemInfo.setQuantity(itemInfo.getQuantity() * multiple);
		}
		return list;
	}

	private String calItemInfo(String univalence, int multiple) {
		if (StringUtils.isBlank(univalence))
			return null;
		ItemInfo itemInfo = ItemInfo.valueOf(univalence);
		itemInfo.setQuantity(itemInfo.getQuantity() * multiple);
		return itemInfo.toString();
	}
}
