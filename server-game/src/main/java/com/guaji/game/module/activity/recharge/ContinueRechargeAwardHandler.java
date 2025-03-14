package com.guaji.game.module.activity.recharge;

import java.util.Date;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPGetContinueRechargeAward;
import com.guaji.game.protocol.Activity.HPGetContinueRechargeAwardRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ContinueRechargeCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class ContinueRechargeAwardHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;

        // 检测活动是否开放
        int activityId = Const.ActivityId.CONTINUE_RECHARGE_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
        int surplusTime = ActivityUtil.clacContinueRechargeSurplusTime(registerDate,player.getPlayerData().getPlayerEntity().getMergeTime());

        if (timeCfg == null || surplusTime <= 0) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        HPGetContinueRechargeAward request = protocol.parseProtocol(HPGetContinueRechargeAward.getDefaultInstance());
        int cfgId = request.getAwardCfgId();
        ContinueRechargeCfg cfg = ConfigManager.getInstance().getConfigByKey(ContinueRechargeCfg.class, cfgId);
        if (cfg == null) {
            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
            return true;
        }

        ContinueRechargeStatus continueRechargeStatues = ActivityUtil.getActivityStatus(player.getPlayerData(),
                activityId, timeCfg.getStageId(), ContinueRechargeStatus.class);
        if (continueRechargeStatues.getContinueRechargeDays() < cfg.getDay()) {
            // 连续充值天数未达到
            player.sendError(protocol.getType(), Status.error.RECHARGE_NUM_LACK);
            return true;
        }

        if (continueRechargeStatues.isAlreadyGot(cfgId)) {
            // 活动奖励已领取
            player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
            return true;
        }

        // 发放奖励并推送前端
        AwardItems awardItems = AwardItems.valueOf(cfg.getAwards());
        awardItems.rewardTakeAffectAndPush(player, Action.CONTINUE_RECHARGE_AWARDS, 2);

        continueRechargeStatues.addGotAwardCfgId(cfgId);
        player.getPlayerData().updateActivity(Const.ActivityId.CONTINUE_RECHARGE_VALUE, timeCfg.getStageId());

        HPGetContinueRechargeAwardRet.Builder ret = HPGetContinueRechargeAwardRet.newBuilder();
        ret.setGotAwardCfgId(cfgId);
        ret.setSurplusTime(surplusTime);
        player.sendProtocol(Protocol.valueOf(HP.code.GET_CONTINUE_RECHARGE_AWARD_S, ret));
        return true;
    }
}
