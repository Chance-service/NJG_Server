package com.guaji.game.module.activity.activity128;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ReleaseURDropCfg128;
import com.guaji.game.config.ReleaseURMultipleCfg128;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.manager.UrRankActivityManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.*;
import com.guaji.game.util.ActivityUtil;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import java.util.Map;

public class Activity128Manager {


    public static Activity128OwnInfo.Builder getOwnInfo(Player player, ActivityTimeCfg activityTimeCfg) {
        Activity128Status status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                activityTimeCfg.getActivityId(), activityTimeCfg.getStageId(), Activity128Status.class);
        Activity128OwnInfo.Builder ownInfo = Activity128OwnInfo.newBuilder();
        int freeLeftTime = status.getFreeLeftTime();
        ownInfo.setFreeLeftTime(freeLeftTime);
        // 每日结算时间
        long calcTime = GuaJiTime.getTimeHourMinute(SysBasicCfg.getInstance().getActivity128UrRankCalcTime());
        ownInfo.setCurrentScore(status.getScore());
        ownInfo.setRank(UrRankActivityManager.getInstance().getPlayerRank(player.getId()));
        int totalTimes = status.getTotalTimes();
        int genLeftTimes = ReleaseURDropCfg128.getLimitTimes(totalTimes);
        int tenLeftTimes = (totalTimes / 10 + 1) * 10;
        if (genLeftTimes == -1) {
            genLeftTimes = tenLeftTimes;
        }
        int leftLimitTimes = Math.min(genLeftTimes - totalTimes, tenLeftTimes - totalTimes);
        ownInfo.setLeftAwardTimes(leftLimitTimes);
        ownInfo.setTotalScore(status.getTotalScore());
        ownInfo.addAllBoxId(status.getBoxArrayList());
        return ownInfo;
    }

    public static int calcMultipleScore(int score) {
        Map<Object, ReleaseURMultipleCfg128> releaseURMultipleCfg128Map = ConfigManager.getInstance()
                .getConfigMap(ReleaseURMultipleCfg128.class);
        boolean isInMultipleTime = false;
        ReleaseURMultipleCfg128 releaseURMultipleCfg128 = null;
        for (ReleaseURMultipleCfg128 item : releaseURMultipleCfg128Map.values()) {
            long currentTime = System.currentTimeMillis();
            long startTime = GuaJiTime.getTimeHourMinute(item.getStartTime());
            long endTime = GuaJiTime.getTimeHourMinute(item.getEndTime());
            if (startTime <= currentTime && currentTime <= endTime) {
                isInMultipleTime = true;
                releaseURMultipleCfg128 = item;
                break;
            }
        }
        if (isInMultipleTime) {
            score = score * releaseURMultipleCfg128.getRate();
        }
        return score;
    }

}
