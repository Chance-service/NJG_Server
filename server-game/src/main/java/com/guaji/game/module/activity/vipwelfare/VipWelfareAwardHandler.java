package com.guaji.game.module.activity.vipwelfare;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity.HPVipWelfareRewardRet;
import com.guaji.game.config.VipWelfareCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class VipWelfareAwardHandler implements IProtocolHandler{
	
	//1:已领取, 0:未领取
	public static final int GOTED = 1, NOTGET = 0;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		
		Player player = (Player) appObj;
		VipWelfareCfg cfg = ConfigManager.getInstance().getConfigByKey(VipWelfareCfg.class, player.getVipLevel());
		if(cfg == null){
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		VipWelfareStatus welfareStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), Const.ActivityId.VIP_WELFARE_VALUE, 0, VipWelfareStatus.class);
		if(welfareStatus.getLastAwareTime() != null && GuaJiTime.isSameDay(welfareStatus.getLastAwareTime().getTime(), 
				GuaJiTime.getCalendar().getTime().getTime())){
			// 奖励已领取
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return true;
		}
		AwardItems awardItems = AwardItems.valueOf(cfg.getAwardStr());
		awardItems.rewardTakeAffectAndPush(player, Action.VIP_WELFARE_REWARD,2);
		
		welfareStatus.setAwareStatus(GOTED);
		welfareStatus.setLastAwareTime(GuaJiTime.getCalendar().getTime());
		player.getPlayerData().updateActivity(Const.ActivityId.VIP_WELFARE_VALUE, 0);
		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.VIP_WELFARE_REWARD, Params.valueOf("content", cfg.getAwardStr()));
		BehaviorLogger.log4Platform(player, Action.VIP_WELFARE_REWARD);
		
		HPVipWelfareRewardRet.Builder builder = HPVipWelfareRewardRet.newBuilder();
		builder.setAwardStatus(welfareStatus.getAwareStatus());
		player.sendProtocol(Protocol.valueOf(HP.code.VIP_WELFARE_AWARD_S, builder));
		
		return true;
	}

}
