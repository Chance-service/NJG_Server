package com.guaji.game.module.activity.invite;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPInviteFriendInfoRet;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.manager.ActivityManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class FriendInviteHandler implements IProtocolHandler{
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
		
		InviteStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, -1, InviteStatus.class);
		HPInviteFriendInfoRet.Builder ret = HPInviteFriendInfoRet.newBuilder();
		ret.setMyInviteCode(status.getMyInviteCode());
		ret.setInviteAmount(status.getCurInviteAmount());
		ret.addAllGotAwardCfgId(status.getGotConfigIds());
		ret.setIsExchanged(status.isExchanged());
		player.sendProtocol(Protocol.valueOf(HP.code.INVITE_FRIEND_INFO_S_VALUE, ret));
		player.getPlayerData().updateActivity(activityId, -1);
		return true;
	}

}
