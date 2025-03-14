package com.guaji.game.module.activity.activity134;

import com.guaji.game.config.ActivityCfg;
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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;

public class Activity134InfoHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY134_WEEKEND_GIFT_VALUE;
        ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (activityTimeCfg == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        ActivityCfg.ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
        if (activityItem == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }
        Activity134WeekendGiftInfoRes.Builder builder = Activity134WeekendGiftInfoRes.newBuilder();
        Activity134Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity134Status.class);
        long currentTime = System.currentTimeMillis();

        if (currentTime > status.getPreOpenTime() && currentTime < status.getStartTime()) {
            builder.setPreOpenTime((int) (status.getStartTime() - currentTime) / 1000);
            builder.setTodayLeftTime(0);
            builder.setActivityLefttime((int) (status.getRestTime() - currentTime) / 1000);
        } else if (currentTime > status.getStartTime() && currentTime < status.getRestTime()) {
            builder.setPreOpenTime(0);
            builder.setTodayLeftTime((int) (Timestamp.valueOf(LocalDate.now().plusDays(1).atStartOfDay()).getTime() - currentTime) / 1000);
            builder.setActivityLefttime((int) (status.getRestTime() - currentTime) / 1000);
        } else {
            builder.setPreOpenTime(0);
            builder.setTodayLeftTime(0);
            builder.setActivityLefttime(0);
        }
        LocalDate today = LocalDate.now();
        int dayOfWeek = today.getDayOfWeek().getValue();
        Activity134Item.Builder item = null;
        for (Map.Entry<Integer, Activity134StatusItem> statusItem : status.getStatusMap().entrySet()) {
            item = Activity134Item.newBuilder();
            item.setCfgId(statusItem.getValue().getCfgId());
            item.setCount(statusItem.getValue().getCount());
            item.setIsLottery(statusItem.getValue().isLottery());
            item.setIsGot(statusItem.getValue().isGot());
            item.setMultipleNum(statusItem.getValue().getRate());
            if (statusItem.getKey() == dayOfWeek) {
                item.setIsToday(true);
            } else {
                item.setIsToday(false);
            }
            builder.addItems(item);
        }

        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY134_WEEKEND_GIFT_INFO_S_VALUE, builder));
        return true;
    }
}
