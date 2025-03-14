package com.guaji.game.module.activity.activity176;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.GuaJiNetManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ActivityExchange176;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.ActivityExchangeInfo;
import com.guaji.game.protocol.Activity5.ActivityExchangeReq;
import com.guaji.game.protocol.Activity5.ActivityExchangeResp;
import com.guaji.game.protocol.Activity5.RequestExchange;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * 限时兑换for77
 */
public class Activity176ExchangeHandler implements IProtocolHandler {
	static final int actExchange_Sync = 0;
	static final int actExchange_Do = 1;
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY176_Activity_Exchange_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return false;
		}

		if (timeCfg.isEnd()) {
			// 活动已结束
			player.sendError(protocol.getType(), Status.error.ACTIVITY_END_VALUE);
			return false;
		}
		
		// 解析接受到的数据
		ActivityExchangeReq request = protocol.parseProtocol(ActivityExchangeReq.getDefaultInstance());

		int action = request.getAction();
		
		int exchangeActId = request.getActivityId();
		
		//對應活動已經結束
		if (!ActivityUtil.isActivityOpen(exchangeActId)) {
			// 活动已结束
			player.sendError(protocol.getType(), Status.error.ACTIVITY_END_VALUE);
			return false;
		}
		
		int stageId = timeCfg.getStageId();

		Activity176ExchangeStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				stageId, Activity176ExchangeStatus.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return false;
		}
		
		switch (action) {
		case actExchange_Sync: 
			SyncInfo(exchangeActId,timeCfg,action,player,status);
			break;
		case actExchange_Do: 
			DoExchange(protocol, timeCfg, player,action, status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	private static void DoExchange(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity176ExchangeStatus status) {
		ActivityExchangeReq allreq = protocol.parseProtocol(ActivityExchangeReq.getDefaultInstance());
		
		RequestExchange request = allreq.getExchangeObj();
		
		// 要兑换的道具id
		int exchangeId = request.getExchangeId();
		
		int actId = allreq.getActivityId();
		
		// 要兑换的道具个数
		int times = request.getExchangeTimes();

		if (times == 0 || exchangeId <= 0) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return ;
		}
		
		if(times>=100) {//外掛送錯誤參數鎖帳號5年
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
			return;
		}

		ActivityExchange176 exchangeCfg = ConfigManager.getInstance().getConfigByKey(ActivityExchange176.class, exchangeId);
		
		if (exchangeCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		
		if (actId != exchangeCfg.getActivityID()) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		List<ItemInfo> consumeInfo = new ArrayList<ItemInfo>();
		// 根据兑换次数修正兑换消耗数目
		for (ItemInfo item : exchangeCfg.getExchangeConsumeInfo()) {
			ItemInfo newItem = item.clone();
			newItem.setQuantity(newItem.getQuantity() * times);
			consumeInfo.add(newItem);
		}

		if (exchangeCfg.getMaxExchangeTimes() != 0) {// 有限制
			if (times + status.getExchangeCount(actId,exchangeId) > exchangeCfg.getMaxExchangeTimes()) {
				
				player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			} else {// 兑换
				
				ConsumeItems consumeItems = new ConsumeItems();
				if (!consumeItems.addConsumeInfo(player.getPlayerData(), consumeInfo)) {
					player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
					return;
				}

				if (!consumeItems.checkConsume(player, protocol.getType())) {
					return ;
				}

				consumeItems.consumeTakeAffect(player, Action.ACTIVITY176_ACT_EXCHANGE);
			}
		} else {
			// 兑换
			ConsumeItems consumeItems = new ConsumeItems();
			if (!consumeItems.addConsumeInfo(player.getPlayerData(), consumeInfo)) {
				player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}

			if (!consumeItems.checkConsume(player, protocol.getType())) {
				return ;
			}

			consumeItems.consumeTakeAffect(player, Action.ACTIVITY176_ACT_EXCHANGE);
		}

		AwardItems awardItems = new AwardItems();
		// 根据兑换次数修正兑换消耗数目
		for (Item item : exchangeCfg.getExchangeAwardInfo().getAwardItems()) {
			Item newItem = item.clone();
			newItem.setCount(newItem.getCount() * times);
			awardItems.addItem(newItem);
		}
		awardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY176_ACT_EXCHANGE, 2, TapDBSource.Act_Exchange,
				Params.valueOf("activity id",actId),
				Params.valueOf("exchangeId",exchangeId)
				);// 记录领取日志改

		status.modifyExchangeCount(actId,exchangeId, times + status.getExchangeCount(actId,exchangeId));

		player.getPlayerData().updateActivity(timeConfig.getActivityId(), timeConfig.getStageId(), true);
		
		SyncInfo(actId,timeConfig,action,player,status);
	}
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(int exchangeActId,ActivityTimeCfg timeConfig,int action,Player player, Activity176ExchangeStatus status) {
		ActivityExchangeResp.Builder builder = getBuilder(exchangeActId,timeConfig,player,action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY176_ACTIVITY_EXCHANGE_S, builder));
	}
	
	private static ActivityExchangeResp.Builder getBuilder(int exchangeActId,ActivityTimeCfg timeConfig,Player player,int action,Activity176ExchangeStatus status) {
		// 返回包
		ActivityExchangeResp.Builder response = ActivityExchangeResp.newBuilder();
		
		response.setAction(action);
		response.setActivityId(exchangeActId);
		
		Map<Object,ActivityExchange176> exchangeCfgs = ConfigManager.getInstance().getConfigMap(ActivityExchange176.class);
		
		for (ActivityExchange176 aCfg : exchangeCfgs.values()) {
			if (aCfg.getActivityID() ==  exchangeActId) {
				ActivityExchangeInfo.Builder exchangeInfo = ActivityExchangeInfo.newBuilder();
				exchangeInfo.setExchangeId(aCfg.getExchangeId());
				exchangeInfo.setExchangeItem(aCfg.getAwardInfo());
				exchangeInfo.setConsumeItem(aCfg.getConsumeInfo());
				if (status.getExchangeInfo().containsKey(exchangeActId)) {
					if (status.getExchangeInfo().get(exchangeActId).containsKey(aCfg.getExchangeId())) {
						exchangeInfo.setGotCount(status.getExchangeInfo().get(exchangeActId).get(aCfg.getExchangeId()));
					} else {
						exchangeInfo.setGotCount(0);
					}
				} else {
					exchangeInfo.setGotCount(0);
				}
				
				response.addExchangeInfo(exchangeInfo);
			}
		}		
		return response;
	}
}
