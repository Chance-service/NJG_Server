package com.guaji.game.module.activity.luckyTreasure;

import java.util.TreeMap;

import org.guaji.config.ConfigManager;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPLuckBoxExchange;
import com.guaji.game.protocol.Activity.HPLuckBoxExchangeRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.LuckyTreasureCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class LuckyTreasureRewardHandler implements IProtocolHandler{

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.LUCK_BOX_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		HPLuckBoxExchange req = protocol.parseProtocol(HPLuckBoxExchange.getDefaultInstance());
		LuckyTreasureStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				activityId, timeCfg.getStageId(), LuckyTreasureStatus.class);
		
		// 最大奖励id
		TreeMap<Object, LuckyTreasureCfg> luckyTreasureCfgs = (TreeMap<Object, LuckyTreasureCfg>)
				ConfigManager.getInstance().getConfigMap(LuckyTreasureCfg.class);
		int maxCfgId = luckyTreasureCfgs.lastEntry().getValue().getDays();
		// 领取奖励id修正
		int getCfgId = Math.min(maxCfgId, status.getRewardTimes() + 1);
		if(status.todayIsGet() || req.getCfgId() != getCfgId){
			// 奖励已经领取
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT_VALUE);
			return true;
		}
		
		LuckyTreasureCfg cfg = ConfigManager.getInstance().getConfigByKey(LuckyTreasureCfg.class, getCfgId);
		if(cfg == null){
			TreeMap<Object, LuckyTreasureCfg> cfgs = (TreeMap<Object, LuckyTreasureCfg>)
					ConfigManager.getInstance().getConfigMap(LuckyTreasureCfg.class);
			LuckyTreasureCfg maxDaysCfg = cfgs.lastEntry().getValue();
			if(getCfgId > maxDaysCfg.getDays()){
				cfg = maxDaysCfg;
			}else{
				// 奖励配置不存在
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
				return true;
			}
		}
		
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, Action.LUCKY_TREASURE_AWARDS,2);
		status.setTodayAwardGot();
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		HPLuckBoxExchangeRet.Builder ret = HPLuckBoxExchangeRet.newBuilder();
		ret.setLeftTime(timeCfg.calcActivitySurplusTime());
		player.sendProtocol(Protocol.valueOf(HP.code.LUCKBOX_EXCHANGE_S, ret));
		return true;
	}

}
