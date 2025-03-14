package com.guaji.game.module.activity.activity137;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.Activity137InfoRep;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

@Deprecated
public class Activity137InfoHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        // 检测活动是否开放
        int activityId = Const.ActivityId.ACTIVITY137_RECHARGE_RETURN_VALUE;
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

        Activity137Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity137Status.class);
        Activity137InfoRep.Builder builder = Activity137InfoRep.newBuilder();
        builder.setLoginCount(player.getPlayerData().getStateEntity().getTodayLoginCount());
        long thisEndTime = status.getActivityTime() + SysBasicCfg.getInstance().getActivity137OpenTime() * 1000 * 60;
        long currentTime = System.currentTimeMillis();
        builder.setLefttime(thisEndTime > currentTime ? (int) (thisEndTime - currentTime) / 1000 : 0);
        builder.setCount(status.getLotteryCount());
        builder.setFirstLine(status.getFirstLine());
        builder.setSecondLine(status.getSecondLine());
        builder.setThirdLine(status.getThirdLine());
        builder.setIsUsed(status.isUsed());
        builder.setLoginTimes(status.getLoginTimes());
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY137_SLOT_RETURN_INFO_S_VALUE, builder));
        return true;
    }
}
