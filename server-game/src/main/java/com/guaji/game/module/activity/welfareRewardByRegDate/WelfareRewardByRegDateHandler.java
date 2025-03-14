package com.guaji.game.module.activity.welfareRewardByRegDate;

import com.guaji.game.config.ActivityCfg;
import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity3.WelfareRewardByRegDateReq;
import com.guaji.game.util.ActivityUtil;

import java.util.Date;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年5月24日 下午11:09:20 类说明
 */
public class WelfareRewardByRegDateHandler implements IProtocolHandler {
    private static final int HANDLER_SYNC = 0;
    private static final int HANDLER_PLAY = 1;

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        // 检测活动是否开放
        int activityId = Const.ActivityId.WELFAREBYREGDATE_REWARD_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.WELFAREBYREGDATE_REWARD_VALUE);
        if (activityItem == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        WelfareRewardStatusByRegDate status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), WelfareRewardStatusByRegDate.class);
        if (status == null) {
            player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
            return true;
        }

        // 解析请求
        WelfareRewardByRegDateReq request = protocol.parseProtocol(WelfareRewardByRegDateReq.getDefaultInstance());
        int type = request.getType();
        switch (type) {
            case HANDLER_SYNC:
                WelfareRewardManagerByRegDate.sync(player, status, timeCfg, activityItem);
                break;
            case HANDLER_PLAY:
                Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
                Integer keepDays = activityItem.getParam("keepDays");
                if (status.canPlay() && status.isCanPlayByDate(registerDate.getTime(), timeCfg.getlStartTime(), keepDays, player.getPlayerData().getPlayerEntity().getMergeTime())) {
                    WelfareRewardManagerByRegDate.play(player, status, timeCfg);
                } else {
                    player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
                }
                break;
            default:
                player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
                break;
        }
        return true;
    }

}
