package com.guaji.game.module.activity.invite;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPExchangeInviteCode;
import com.guaji.game.protocol.Activity.HPExchangeInviteCodeRet;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.ActivityManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class ExchangeInviteCodeHandler implements IProtocolHandler{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.FRIEND_INVITE_VALUE;
		ActivityItem ativityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if(ativityItem == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		if(player.getLevel() < SysBasicCfg.getInstance().getFriendInviteLevelLimit()){
			// 等级不足
			player.sendError(protocol.getType(), Status.error.LEVEL_NOT_LIMIT);
			return true;
		}
		
		HPExchangeInviteCode req = protocol.parseProtocol(HPExchangeInviteCode.getDefaultInstance());
		String friendInviteCode = req.getInviteCode().trim();
		InviteStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, -1, InviteStatus.class);
		if(!status.exchangeInviteCode(friendInviteCode)){
			// 兑换好友邀请码失败（邀请码非法或已经兑换）
			player.sendError(protocol.getType(), Status.error.EXCHANGE_CODE_FAIL);
			return true;
		}
		
		player.getPlayerData().updateActivity(activityId, -1);
		AwardItems awards = AwardItems.valueOf(SysBasicCfg.getInstance().getFriendInviteExchangeAwards());
		awards.rewardTakeAffectAndPush(player, Action.INVITE_FRIEND_EXCHANGE_AWARDS,1);
		
		HPExchangeInviteCodeRet.Builder ret = HPExchangeInviteCodeRet.newBuilder();
		ret.setInviteAmount(status.getCurInviteAmount());
		player.sendProtocol(Protocol.valueOf(HP.code.EXCHANGE_INVITE_CODE_S_VALUE, ret));
		return true;
	}
}
