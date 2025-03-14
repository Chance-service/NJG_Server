package com.guaji.game.module.activity.activity124;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity3.Activity124InfoRep;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

public class Activity124InfoHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        // 检测活动是否开放
        int activityId = Const.ActivityId.ACTIVITY124_RECHARGE_RETURN_VALUE;
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
        //是否已经抽奖过
        Activity124Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity124Status.class);
        Activity124InfoRep.Builder builder = Activity124InfoRep.newBuilder();
        builder.setLefttime(leftTime);
        builder.setCount(status.getCount());
        if (status.getGotTicket()) {
            builder.setType(status.getTicketId());
            builder.setIsUsed(status.getUsed());
        }
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY124_RECHARGE_RETURN_INFO_S_VALUE, builder));
        return true;
    }
}
