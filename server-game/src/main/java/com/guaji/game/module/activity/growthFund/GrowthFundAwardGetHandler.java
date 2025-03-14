package com.guaji.game.module.activity.growthFund;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPGetGrowthFundRewardReq;
import com.guaji.game.config.GrowthFundCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;

/**
 * 领取成长基金奖励
 * */
public class GrowthFundAwardGetHandler implements IProtocolHandler{
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		GrowthFundStatus status = ActivityUtil.getGrowthFundStatus(player.getPlayerData());
		
		//还未购买过
		if(!status.isBought()){
			return false;
		}
		
		HPGetGrowthFundRewardReq req = protocol.parseProtocol(HPGetGrowthFundRewardReq.getDefaultInstance());
		
		//奖励已领取
		if(status.hasGot(req.getRewardId())){
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return false;
		}
		
		//等级不足
		GrowthFundCfg cfg = ConfigManager.getInstance().getConfigByKey(GrowthFundCfg.class, req.getRewardId());
		if(cfg == null){
			return false;
		}
		if(player.getLevel() < cfg.getLevelLimit()){
			player.sendError(protocol.getType(), Status.error.LEVEL_NOT_LIMIT_VALUE);
			return false;
		}
		
		//领取奖励并推送前端
		AwardItems awardItems = AwardItems.valueOf(cfg.getAwardStr());
		awardItems.rewardTakeAffectAndPush(player, Action.GROWTH_FUND_REWARD,2);
		
		//更新DB
		status.addRewardId(req.getRewardId());
		player.getPlayerData().updateActivity(Const.ActivityId.GROWTH_FUND_VALUE, 0,true);
		
		return true;
	}

}
