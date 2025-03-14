package com.guaji.game.module.activity.activity134;

import com.guaji.game.config.*;
import com.guaji.game.item.ConsumeItems;
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
import org.guaji.os.GuaJiRand;

import java.time.LocalDate;

public class Activity134LotteryHandler implements IProtocolHandler {

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

        // 验证参数
        Activity134WeekendGiftLotteryReq req = protocol.parseProtocol(Activity134WeekendGiftLotteryReq.getDefaultInstance());
        LocalDate today = LocalDate.now();
        Integer dayOfWeek = today.getDayOfWeek().getValue();
        Activity134Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity134Status.class);
        Activity134StatusItem item = status.getStatusMap().get(dayOfWeek);
        if (item == null || item.getCfgId() != req.getCfgId()) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }

        // 只能领取一次
        if (item.isGot()) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }

        // 消耗钻石
        int times = item.getCount() + 1;
        if (times > 5) {
            times = 5;
        }
        WeekendGiftCost134Cfg cost134Cfg = ConfigManager.getInstance().getConfigByKey(WeekendGiftCost134Cfg.class, times);
        int payGold = cost134Cfg.getCost();
        // 钻石消耗
        if (payGold > 0) {
            if (player.getGold() < payGold) {
                player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH);
                return true;
            }
            player.consumeGold(payGold, BehaviorLogger.Action.ACTIVITY134_WEEKEND_GIFT);
            ConsumeItems.valueOf(Const.changeType.CHANGE_GOLD, payGold).pushChange(player);
        }


        // 抽奖
        int randInt = GuaJiRand.randInt(99) + 1;
        int rate = WeekendGiftRate134Cfg.calcRate(randInt);

        // 更新status
        item.setCount(item.getCount() + 1);
        item.setLottery(true);
        item.setRate(rate);
        player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId(), true);

        Activity134WeekendGiftLotteryRes.Builder builder = Activity134WeekendGiftLotteryRes.newBuilder();
        builder.setCfgId(req.getCfgId());
        builder.setMultipleNum(rate);
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY134_WEEKEND_GIFT_LOTTERY_S_VALUE, builder));
        return true;
    }

}
