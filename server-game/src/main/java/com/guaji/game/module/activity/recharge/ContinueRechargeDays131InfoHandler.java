package com.guaji.game.module.activity.recharge;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.*;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import java.util.Date;

public class ContinueRechargeDays131InfoHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        // 检测活动是否开放
        int activityId = Const.ActivityId.CONTINUE_RECHARGE131_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }
        int surplusTime = timeCfg.calcActivitySurplusTime();
        if ( surplusTime <= 0){
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        ContinueRechargeDays131Status continueRechargeStatues = ActivityUtil.getActivityStatus(player.getPlayerData(),
                activityId, timeCfg.getStageId(), ContinueRechargeDays131Status.class);
        HPContinueRecharge131InfoRet.Builder ret = HPContinueRecharge131InfoRet.newBuilder();
        ret.setContinueRechargedays(continueRechargeStatues.getContinueRechargeDays());
        ret.addAllGotAwardCfgId(continueRechargeStatues.getGotAwardCfgIds());
        ret.setSurplusTime(surplusTime);
        player.sendProtocol(Protocol.valueOf(HP.code.CONTINUE_RECHARGE131_INFO_S_VALUE, ret));
        return true;
    }

}
