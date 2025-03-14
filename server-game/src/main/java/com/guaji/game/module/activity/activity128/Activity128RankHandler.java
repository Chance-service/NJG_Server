package com.guaji.game.module.activity.activity128;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.manager.UrRankActivityManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.*;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

public class Activity128RankHandler implements IProtocolHandler {

    static final int RANK_NOW = 1;
    static final int RANK_YESTERDAY = 2;

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
        Activity128RankReq req = protocol.parseProtocol(Activity128RankReq.getDefaultInstance());
        if (req.getType() != RANK_NOW && req.getType() != RANK_YESTERDAY) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }

        Activity128RankRes.Builder builder = Activity128RankRes.newBuilder();
        builder.setType(req.getType());
        if (req.getType() == RANK_NOW) {
            // 获取排名
            List<Activity128RankItem.Builder> rankItemList = UrRankActivityManager.getInstance().getRankTop(10);
            Activity128RankItem.Builder ownRankItem = Activity128RankItem.newBuilder();
            ownRankItem.setRank(UrRankActivityManager.getInstance().getPlayerRank(player.getId()));
            Activity128Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityTimeCfg.getActivityId(), activityTimeCfg.getStageId(), Activity128Status.class);
            ownRankItem.setScore(status.getScore());
            ownRankItem.setPlayerId(player.getId());
            ownRankItem.setName(player.getName());
            ownRankItem.setProf(player.getProf());
            ownRankItem.setHeaderId(player.getPlayerData().getPlayerEntity().getHeadIcon());
            builder.setOwnItem(ownRankItem);
            for (Activity128RankItem.Builder item : rankItemList) {
                builder.addItem(item);
            }
        }
        if (req.getType() == RANK_YESTERDAY) {
            // 获取排名
            List<Activity128RankItem.Builder> rankItemList = UrRankActivityManager.getInstance().getRankHistoryTop();
            Activity128RankItem.Builder ownRankItem = Activity128RankItem.newBuilder();
            Activity128Rank myRank = UrRankActivityManager.getInstance().getPlayerHistoryRank(player.getId());
            if (myRank != null) {
                ownRankItem.setRank(myRank.getRank());
                ownRankItem.setScore(myRank.getScore());
            } else {
                ownRankItem.setRank(0);
                ownRankItem.setScore(0);
            }
            ownRankItem.setPlayerId(player.getId());
            ownRankItem.setName(player.getName());
            ownRankItem.setProf(player.getProf());
            ownRankItem.setHeaderId(player.getPlayerData().getPlayerEntity().getHeadIcon());
            builder.setOwnItem(ownRankItem);
            for (Activity128RankItem.Builder item : rankItemList) {
                builder.addItem(item);
            }
        }
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY128_UR_RANK_RANK_S_VALUE, builder));

        return true;
    }

}
