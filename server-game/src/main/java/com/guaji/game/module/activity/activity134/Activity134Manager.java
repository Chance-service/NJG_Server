package com.guaji.game.module.activity.activity134;

import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.*;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.util.ActivityUtil;
import org.guaji.net.protocol.Protocol;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.Map;

public class Activity134Manager {

    public static void restActivity134Status(Player player) {
        try {
            if (player == null || player.getPlayerData() == null) {
                return;
            }

            int activityId = Const.ActivityId.ACTIVITY134_WEEKEND_GIFT_VALUE;

            ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
            if (activityTimeCfg == null) {
                // 活动已关闭
                return;
            }

            ActivityCfg.ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
            if (activityItem == null) {
                // 活动已关闭
                return;
            }

            Activity134Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity134Status.class);

            if (status.getRestTime() == 0 || status.getPreOpenTime() == 0 || status.getStartTime() == 0 || status.getRestTime() <= System.currentTimeMillis()) {

                String openDay = activityItem.getParam("openDay");
                String[] arrayDay = openDay.split(",");
                int dayCount = arrayDay.length;
                if (dayCount <= 0) {
                    return;
                }

                Integer preOpenDay = activityItem.getParam("preOpenDay");
                Integer firstDay = Integer.valueOf(arrayDay[0]);
                Integer lastDay = Integer.valueOf(arrayDay[dayCount - 1]);
                LocalDate today = LocalDate.now();
                int dayOfWeek = today.getDayOfWeek().getValue();
                int mondayValue = DayOfWeek.MONDAY.getValue();
                LocalDate currentWeekFirstDate = today.plusDays(mondayValue - dayOfWeek);

                LocalDate preOpenDate = currentWeekFirstDate.plusDays(preOpenDay - mondayValue);
                LocalDate startDate = null;
                LocalDate lastDate = null;

                if (firstDay > lastDay) {
                    if (dayOfWeek <= lastDay) {
                        preOpenDate = currentWeekFirstDate.plusDays(preOpenDay - mondayValue - 7);
                        startDate = currentWeekFirstDate.plusDays(firstDay - mondayValue - 7);
                        lastDate = currentWeekFirstDate.plusDays(lastDay - mondayValue);
                    } else {
                        startDate = currentWeekFirstDate.plusDays(firstDay - mondayValue);
                        lastDate = currentWeekFirstDate.plusDays(7 + lastDay - mondayValue);
                    }
                } else {
                    startDate = currentWeekFirstDate.plusDays(firstDay - mondayValue);
                    lastDate = currentWeekFirstDate.plusDays(lastDay - mondayValue);
                }

                status.setPreOpenTime(Timestamp.valueOf(preOpenDate.atStartOfDay()).getTime());
                status.setStartTime(Timestamp.valueOf(startDate.atStartOfDay()).getTime());
                status.setRestTime(Timestamp.valueOf(lastDate.plusDays(1).atStartOfDay()).getTime());

                for (Activity134StatusItem item : status.getStatusMap().values()) {
                    item.setCount(0);
                    item.setLottery(false);
                    item.setGot(false);
                    item.setRate(0);
                }
                player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pushActivity134Info(Player player) {
        try {
            if (player == null || player.getPlayerData() == null) {
                return;
            }
            int activityId = Const.ActivityId.ACTIVITY134_WEEKEND_GIFT_VALUE;
            ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
            if (activityTimeCfg == null) {
                // 活动已关闭
                return;
            }

            ActivityCfg.ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
            if (activityItem == null) {
                // 活动已关闭
                return;
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
