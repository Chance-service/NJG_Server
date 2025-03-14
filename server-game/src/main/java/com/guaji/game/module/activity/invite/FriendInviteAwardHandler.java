package com.guaji.game.module.activity.invite;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPInviteFriendGetRewardInfo;
import com.guaji.game.protocol.Activity.HPInviteFriendGetRewardInfoRet;
import com.guaji.game.config.InviteAwardCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.ActivityManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class FriendInviteAwardHandler implements IProtocolHandler {

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
		
		HPInviteFriendGetRewardInfo req = protocol.parseProtocol(HPInviteFriendGetRewardInfo.getDefaultInstance());
		int getCfgId = req.getCfgId();
		
		InviteStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, -1, InviteStatus.class);
		if(status.getGotConfigIds().contains((Integer)getCfgId)){
			// 奖励已经领取过
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT_VALUE);
			return true;
		}
		
		InviteAwardCfg awardcfg = ConfigManager.getInstance().getConfigByKey(InviteAwardCfg.class, getCfgId);
		if(awardcfg == null){
			// 奖励配置不存在
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}
		
		if(awardcfg.getInviteAmount() > status.getCurInviteAmount()){
			// 邀请好友数量不足
			player.sendError(protocol.getType(), Status.error.INVITE_FRIEND_AMOUNT_LUCK);
			return true;
		}
		
		AwardItems awards = AwardItems.valueOf(awardcfg.getAwards());
		awards.rewardTakeAffectAndPush(player, Action.INVITE_FRIEND_AWARDS,1);
		status.addGotAwardCfgId(getCfgId);
		player.getPlayerData().updateActivity(activityId, -1);
		
		HPInviteFriendGetRewardInfoRet.Builder ret = HPInviteFriendGetRewardInfoRet.newBuilder();
		ret.addAllGotAwardCfgId(status.getGotConfigIds());
		player.sendProtocol(Protocol.valueOf(HP.code.INVITE_FRIEND_REWARD_S, ret));
		return true;
	}

}
