package com.guaji.game.module.activity.consume;

import java.util.Calendar;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPGetAccConsumeAward;
import com.guaji.game.protocol.Activity.HPGetAccConsumeAwardRet;
import com.guaji.game.GsApp;
import com.guaji.game.config.AccConsumeCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class AccConsumeAwardHandler implements IProtocolHandler {
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACCUMULATIVE_CONSUME_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
	
		HPGetAccConsumeAward request = protocol.parseProtocol(HPGetAccConsumeAward.getDefaultInstance());
		int cfgId = request.getAwardCfgId();
		AccConsumeCfg cfg = ConfigManager.getInstance().getConfigByKey(AccConsumeCfg.class, cfgId);
		if(cfg == null){
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		
		int nowMonth = GuaJiTime.getCalendar().get(Calendar.MONTH)+1;
		
		if ((nowMonth % 2) != 0) { // 奇數月
			if  (cfgId > ConfigManager.getInstance().getConfigMap(AccConsumeCfg.class).size()/2) {
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
				return true;	
			}
		} else{ //偶數月
			if  (cfgId <= ConfigManager.getInstance().getConfigMap(AccConsumeCfg.class).size()/2) {
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
				return true;	
			}
		}
		
		AccConsumeStatus accConsumeStatues = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				activityId, timeCfg.getStageId(), AccConsumeStatus.class);
		if(accConsumeStatues.getAccConsumeGold() < cfg.getSum()){
			// 累计消费额度未达到
			player.sendError(protocol.getType(), Status.error.ACC_CONSUME_NOT_REACH);
			return true;
		}
		
		if(accConsumeStatues.isAlreadyGot(cfgId)){
			// 活动奖励已领取
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return true;
		}
		
		// 发放奖励并推送前端
		AwardItems awardItems = AwardItems.valueOf(cfg.getAwards());
		awardItems.rewardTakeAffectAndPush(player, Action.ACC_CONSUME_AWARDS,2);
		accConsumeStatues.addGotAwardCfgId(cfgId);
		player.getPlayerData().updateActivity(Const.ActivityId.ACCUMULATIVE_CONSUME_VALUE, timeCfg.getStageId());
		
		HPGetAccConsumeAwardRet.Builder ret = HPGetAccConsumeAwardRet.newBuilder();
		ret.setGotAwardCfgId(cfgId);
		ret.setSurplusTime(timeCfg.calcActivitySurplusTime());
		player.sendProtocol(Protocol.valueOf(HP.code.GET_ACC_CONSUME_AWARD_S, ret));
		
		// 是否所有奖励都已领取
		if(ActivityUtil.isCancelShowInClient(activityId, timeCfg.getStageId(), player.getPlayerData())){
			Msg msg = Msg.valueOf(GsConst.MsgType.ALL_ACTIVITY_AWARDS_GOT, player.getXid());
			GsApp.getInstance().postMsg(msg);
		}
		return true;
	}

}
