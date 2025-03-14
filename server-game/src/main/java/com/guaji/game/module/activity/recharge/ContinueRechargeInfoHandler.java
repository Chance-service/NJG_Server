package com.guaji.game.module.activity.recharge;

import java.util.Date;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPContinueRechargeInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class ContinueRechargeInfoHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        // 检测活动是否开放
        int activityId = Const.ActivityId.CONTINUE_RECHARGE_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

        Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
        int surplusTime = ActivityUtil.clacContinueRechargeSurplusTime(registerDate, player.getPlayerData().getPlayerEntity().getMergeTime());
        if (timeCfg == null || surplusTime <= 0) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        ContinueRechargeStatus continueRechargeStatues = ActivityUtil.getActivityStatus(player.getPlayerData(),
                activityId, timeCfg.getStageId(), ContinueRechargeStatus.class);
        HPContinueRechargeInfoRet.Builder ret = HPContinueRechargeInfoRet.newBuilder();
        ret.setContinueRechargedays(continueRechargeStatues.getContinueRechargeDays());
        ret.addAllGotAwardCfgId(continueRechargeStatues.getGotAwardCfgIds());
        ret.setSurplusTime(surplusTime);
        player.sendProtocol(Protocol.valueOf(HP.code.CONTINUE_RECHARGE_INFO_S, ret));
        return true;
    }

}
