package com.guaji.game.module.activity.activity128;

import com.guaji.game.GsApp;
import com.guaji.game.config.*;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.manager.UrRankActivityManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.*;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;

import org.guaji.app.AppObj;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import java.util.ArrayList;
import java.util.List;

public class Activity128LotteryHandler implements IProtocolHandler {

    static final int TIMES_TYPE_SINGLE = 1;
    static final int TIMES_TYPE_TEN = 10;

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

        Activity128LotteryReq req = protocol.parseProtocol(Activity128LotteryReq.getDefaultInstance());
        // 验证次数
        if (req.getCount() != TIMES_TYPE_SINGLE && req.getCount() != TIMES_TYPE_TEN) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }

        Activity128Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                activityTimeCfg.getStageId(), Activity128Status.class);
        ReleaseURTimesCfg128 timesCfg = ReleaseURTimesCfg128.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }
        
        // 验证免费次数
        if (req.getFreeLottery()) {
            if (req.getCount() != TIMES_TYPE_SINGLE) {
                player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
                return true;
            }
            int freeLeftTime = status.getFreeLeftTime();
            if (freeLeftTime > 0) {
                player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
                return true;
            }
        } else {
            int costGold = req.getCount() == TIMES_TYPE_TEN ? timesCfg.getTenCost() : timesCfg.getSingleCost();
         
            if (costGold > player.getGold()) {
                player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
                return true;
            }
            // 扣除钻石
            if (costGold > 0) {
                player.consumeGold(costGold, BehaviorLogger.Action.Activity128_UR);
                ConsumeItems.valueOf(Const.changeType.CHANGE_GOLD, costGold).pushChange(player);
            }
        }

        // 增加物品
        List<AwardItems> awardsList = new ArrayList<AwardItems>();
        int searchTimes = req.getCount();
        for (int i = 1; i <= searchTimes; i++) {
            AwardItems awards = new AwardItems();
            // 总次数
            int totalTimes = status.getTotalTimes();
            // 掉落物品
            List<ItemInfo> searchItems = ReleaseURDropCfg128.treasureRaiderDropItem(totalTimes + 1);
            awards.addItemInfos(searchItems);
            awardsList.add(awards);

            status.setTotalTimes(totalTimes + 1);
        }
        for (AwardItems item : awardsList) {
            item.rewardTakeAffectAndPush(player, BehaviorLogger.Action.Activity128_UR, 0);
        }

        // 双倍积分
        int score = SysBasicCfg.getInstance().getActivity128UrGiveLucky() * req.getCount();
        if (!req.getFreeLottery()) {
            score = Activity128Manager.calcMultipleScore(score);
        }

        status.setScore(status.getScore() + score);
        status.setTotalScore(status.getTotalScore() + score);

        if (GuaJiTime.getMillisecond() >= status.getScoreTime()) {
            // 修改时间
            long todayCalcTime = GuaJiTime.getTimeHourMinute(SysBasicCfg.getInstance().getActivity128UrRankCalcTime());
            long calcTime = todayCalcTime;
            if (GuaJiTime.getMillisecond() >= todayCalcTime) {
                calcTime += 24 * 60 * 60 * 1000;
            }
            status.setScoreTime(calcTime);
        }

        // 更新排行
        UrRankActivityManager.getInstance().updateRankSet(player.getPlayerData());

        if (req.getFreeLottery()) {
            // 更新免费时间
            status.setNextFreeTime(System.currentTimeMillis() + timesCfg.getFreeCD() * 1000 * 60 * 60);
        }

        if (status.getScore() > 0) {
            Msg msg = Msg.valueOf(GsConst.MsgType.UR_RANK_ADD_SCORE,
                    GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.UR_RANK));
            msg.pushParam(player.getPlayerData());
            GsApp.getInstance().postMsg(msg);
            // 更新status
            player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());

        }

        Activity128LotteryRes.Builder builder = Activity128LotteryRes.newBuilder();
        for (AwardItems item : awardsList) {
            builder.addReward(item.toString());
        }
        Activity128OwnInfo.Builder ownInfo = Activity128Manager.getOwnInfo(player, activityTimeCfg);
        builder.setOwnInfo(ownInfo);
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY128_UR_RANK_LOTTERY_S_VALUE, builder));
        return false;
    }
}
