package com.guaji.game.module.activity.starEvaluation;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPStarEvaluationRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.StarEvaluationCfg;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.ActivityId;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class StarEvaluationHandler implements IProtocolHandler{

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player)appObj;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.STAR_EVALUATION_VALUE);
		if(timeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE_VALUE);
			return true;
		}
		
		StarEvaluationStatus evaluationStatus = (StarEvaluationStatus)ActivityUtil.getActivityStatus(player.getPlayerData(), ActivityId.STAR_EVALUATION_VALUE, timeCfg.getStageId(), StarEvaluationStatus.class);
		if(evaluationStatus == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		HPStarEvaluationRet.Builder starEvaBuilder = HPStarEvaluationRet.newBuilder();
		switch (protocol.getType()) {
			case HP.code.STAR_EVALUATION_INFO_C_VALUE:
				// 请求活动状态
				starEvaBuilder.setStatus(evaluationStatus.getStatus());
				player.sendProtocol(Protocol.valueOf(HP.code.STAR_EVALUATION_INFO_S_VALUE,starEvaBuilder));
				break;
			case HP.code.STAR_EVALUATION_INFO_CLICK_C_VALUE:
				// 点击五星评价
				if(evaluationStatus.getStatus() != StarEvaluationStatus.STATUS_REWARD) {
					evaluationStatus.setStatus(StarEvaluationStatus.STATUS_CLICK);
					player.getPlayerData().updateActivity(ActivityId.STAR_EVALUATION_VALUE, timeCfg.getStageId());
				} else {
					evaluationStatus.setStatus(StarEvaluationStatus.STATUS_REWARD);
				}
				starEvaBuilder.setStatus(evaluationStatus.getStatus());
				player.sendProtocol(Protocol.valueOf(HP.code.STAR_EVALUATION_INFO_CLICK_S_VALUE,starEvaBuilder));
				break;
			case HP.code.STAR_EVALUATION_REWARD_C_VALUE:
				// 发送奖励
				StarEvaluationCfg starEvaluationCfg = StarEvaluationCfg.getStarEvaluationCfg();
				if(starEvaluationCfg == null || starEvaluationCfg.getAwardItems() == null) {
					player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
					return true;
				}
				
				if(evaluationStatus.getStatus() == StarEvaluationStatus.STATUS_REWARD){
					player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT_VALUE);
					return true;
				}
				
				starEvaluationCfg.getAwardItems().rewardTakeAffectAndPush(player, Action.STAR_EVALUATION,1);
				
				evaluationStatus.setStatus(StarEvaluationStatus.STATUS_REWARD);
				player.getPlayerData().updateActivity(ActivityId.STAR_EVALUATION_VALUE, timeCfg.getStageId());
				starEvaBuilder.setStatus(evaluationStatus.getStatus());
				player.sendProtocol(Protocol.valueOf(HP.code.STAR_EVALUATION_REWARD_S_VALUE,starEvaBuilder));
				break;
			default:
				break;
		}
		return false;
	}

}
