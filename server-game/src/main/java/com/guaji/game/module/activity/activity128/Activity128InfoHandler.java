package com.guaji.game.module.activity.activity128;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ReleaseURTimesCfg128;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.*;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

public class Activity128InfoHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY128_UR_VALUE;
        ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (activityTimeCfg == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }
        // 剩余时间
        int leftTime = activityTimeCfg.calcActivitySurplusTime();
        if (leftTime <= 0) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }
        ReleaseURTimesCfg128 timesCfg = ReleaseURTimesCfg128.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }
        Activity128OwnInfo.Builder ownInfo = Activity128Manager.getOwnInfo(player, activityTimeCfg);
        Activity128InfoRes.Builder builder = Activity128InfoRes.newBuilder();
        builder.setLeftTime(leftTime);
        builder.setOneTimeCost(timesCfg.getSingleCost());
        builder.setTenTimesCost(timesCfg.getTenCost());
        builder.setOwnInfo(ownInfo);
     
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY128_UR_RANK_INFO_S_VALUE, builder));
        return true;
    }
}
