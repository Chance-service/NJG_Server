package com.guaji.game.module.activity.activity128;


import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ReleaseURBoxCfg128;
import com.guaji.game.config.ReleaseURDropCfg123;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.*;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import java.util.List;

public class Activity128BoxHandler implements IProtocolHandler {

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

        Activity128BoxReq req = protocol.parseProtocol(Activity128BoxReq.getDefaultInstance());
        Activity128Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity128Status.class);
        ReleaseURBoxCfg128 urBoxCfg128 = ConfigManager.getInstance().getConfigByKey(ReleaseURBoxCfg128.class, req.getBoxId());
        if (urBoxCfg128 == null) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }
        if (status.getTotalScore() < urBoxCfg128.getSum()) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }
        // 记录boxId
        List<Integer> boxIdList = status.getBoxArrayList();
        if(boxIdList.contains(urBoxCfg128.getId())) {
       	 player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
       }
        boxIdList.add(urBoxCfg128.getId());
        status.setBoxArray(boxIdList);

        AwardItems awards = AwardItems.valueOf(urBoxCfg128.getAwards());
        //发放奖励
        awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.Activity128_UR, 2);

        // 更新status
        player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());

        Activity128BoxRes.Builder builder = Activity128BoxRes.newBuilder();
        Activity128OwnInfo.Builder ownInfo = Activity128Manager.getOwnInfo(player, activityTimeCfg);
        builder.setOwnInfo(ownInfo);
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY128_UR_RANK_BOX_S_VALUE, builder));
        return true;
    }
}
