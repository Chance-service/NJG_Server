package com.guaji.game.module.activity.activity161;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SupportCalendarCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.SupportCalendarRep;
import com.guaji.game.protocol.Activity5.SupportCalendarReq;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.SupportCalendarAction;
import com.guaji.game.protocol.Const.SupportCalendarType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/**
 * 累计登录签到奖励领取协议
 */
public class Activity161Handler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY161_SUPPORT_CALENDER_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析接受到的数据
		SupportCalendarReq request = protocol.parseProtocol(SupportCalendarReq.getDefaultInstance());

		int action = request.getAction();
		
		int type  = request.getType();

		// 数据人错误
		if (SupportCalendarAction.valueOf(action) == null || SupportCalendarType.valueOf(type) == null) {
			// 无效参数
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
		
		int stageId = timeConfig.getStageId();
		Activity161Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity161Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		// 业务分支处理
		switch (action) {
		case SupportCalendarAction.SYNC_VALUE:
			SyncInfo(action,type,player,status);
			break;
//		case  SupportCalendarAction.BUY_VALUE:
//			BuyActivity(protocol,action,type,player,status); // 測試用出去關掉
//			break;
		case  SupportCalendarAction.SIGN_VALUE:
			SignedAward(protocol,action,type,player,status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	/**
	 * 購買活動
	 * @param action
	 * @param type
	 * @param player
	 * @param status
	 */
	
	private static void BuyActivity(Protocol protocol,int action,int type,Player player, Activity161Status status) {
		if (status.isBuy(type)) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		String costItems = SysBasicCfg.getInstance().getSupportCalendarCost(type);
		
		if (costItems.isEmpty()) {
			player.sendError(protocol.getType(),Status.error.ITEM_NOT_FOUND_VALUE); 
			return;
		}
		
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		ItemInfo itemInfo = ItemInfo.valueOf(costItems);
		itemInfo.setQuantity(itemInfo.getQuantity());
		
		List<ItemInfo> itemList = new ArrayList<ItemInfo>();
		itemList.add(itemInfo);
		boolean isAdd = false ;
		isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
		
		if (isAdd && consumeItems.checkConsume(player)) {
			if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY161_SUPPORT_CALENDAR)) {
				player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			} 
		} else {
			player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
			return; 
		}
		
		status.reSet(type);
		
		status.setBuy(type,1);
		
		int activityId = Const.ActivityId.ACTIVITY161_SUPPORT_CALENDER_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY161_SUPPORT_CALENDAR,Params.valueOf("action", action),
				Params.valueOf("costItems", costItems),
				Params.valueOf("type", type));
		
		SupportCalendarRep.Builder builder =  getBuilder(action,type,status);
		
		player.sendProtocol(Protocol.valueOf(HP.code.SUPPORT_CALENDAR_ACTION_S_VALUE, builder));
	}
	
	private static void SignedAward(Protocol protocol,int action,int type,Player player, Activity161Status status) {
		if (!status.isBuy(type)) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int lastday = GuaJiTime.daysCountOfMonth();
		
		if (status.getSignedDays(type).size() >= lastday) {
			// 已經領光
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return ;
		} 
		
		int nowMonth= GuaJiTime.getNowMonth() ;
		
		int today = GuaJiTime.getMonthDay();
		
		String allaward = "";
		
		List<Integer> dayList = new ArrayList<>();
		List<Integer> cfgList = new ArrayList<>();
		// 签到奖励
		for (int rewardDay = 1 ; rewardDay <= today ; rewardDay++) {
			// 查找累计登录配置数据
			
			if (status.getSignedDays(type).contains(rewardDay)){
				continue;
			}
			
			SupportCalendarCfg loginSignedConf = SupportCalendarCfg
					.getLoginSignedCfg(type,nowMonth,rewardDay);
			if (loginSignedConf == null) {
				// 活动奖励不存在
				continue;
				//player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			}
			
			dayList.add(rewardDay);
			cfgList.add(loginSignedConf.getId());
			status.addSignedDay(type,rewardDay);
			
 			if (allaward.isEmpty()) {
 				allaward = loginSignedConf.getAwards();
 			} else {
 				allaward = allaward+","+loginSignedConf.getAwards();
 			}
		}
		
		if (dayList.isEmpty()) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int activityId = Const.ActivityId.ACTIVITY161_SUPPORT_CALENDER_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		
		// 下发奖励
		AwardItems awards = AwardItems.valueOf(allaward);
		awards.rewardTakeAffectAndPush(player, Action.ACTIVITY161_SUPPORT_CALENDAR, 2);
		
		SupportCalendarRep.Builder response =  getBuilder(action,type,status);
		
		player.sendProtocol(Protocol.valueOf(HP.code.SUPPORT_CALENDAR_ACTION_S_VALUE, response));
		
//		BehaviorLogger.log4Platform(player, Action.ACTIVITY161_SUPPORT_CALENDAR, 
//				Params.valueOf("type", type),
//				Params.valueOf("dayList", dayList),
//				Params.valueOf("awardsCfgId", cfgList), Params.valueOf("awards", allaward));

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY161_SUPPORT_CALENDAR,Params.valueOf("action", action),
				Params.valueOf("type", type),
				Params.valueOf("dayList", dayList), Params.valueOf("awardsCfgId", cfgList),
				Params.valueOf("awards",allaward));
	}
	
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(int action,int type,Player player, Activity161Status status) {
		SupportCalendarRep.Builder builder = getBuilder(action,type,status);
		player.sendProtocol(Protocol.valueOf(HP.code.SUPPORT_CALENDAR_ACTION_S_VALUE, builder));
	}
	
	private static SupportCalendarRep.Builder getBuilder(int action,int type,Activity161Status status) {
		// 返回包
		SupportCalendarRep.Builder response = SupportCalendarRep.newBuilder();
		
		response.setAction(action);
		response.setBuy(status.isBuy(type));
		response.setType(type);
		response.setCurMonth(GuaJiTime.getNowMonth());
		if (GuaJiTime.getNowMonth() == status.getCurMonth()) {
			response.addAllSignedDays(status.getSignedDays(type));
		}
		
		return response;
	}

}
