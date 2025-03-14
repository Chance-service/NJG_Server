package com.guaji.game.module.activity.activity137;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RechargeReturnCost137Cfg;
import com.guaji.game.config.RechargeReturnLottery137Cfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.Activity137LotteryRep;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;


public class Activity137LotteryHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {

        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY137_RECHARGE_RETURN_VALUE;
        ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (activityTimeCfg == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        // 检测活动是否开启
        Activity137Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity137Status.class);
        long thisEndTime = status.getActivityTime() + SysBasicCfg.getInstance().getActivity137OpenTime() * 1000 * 60;
        long currentTime = System.currentTimeMillis();
        long activityEndTime = activityTimeCfg.getlEndTime();
        // 如果活动结束未过期延长活动时间
        if (thisEndTime > activityEndTime) {
            activityEndTime = thisEndTime;
        }
        if (activityEndTime < currentTime) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        // 已经使用或过期
        boolean isUsed = thisEndTime < currentTime || status.isUsed();
        if (isUsed) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        int lotteryCount = status.getLotteryCount() + 1;
        int maxId = ConfigManager.getInstance().getConfigMap(RechargeReturnCost137Cfg.class).size();
        RechargeReturnCost137Cfg cost137Cfg = null;
        if (lotteryCount > maxId) {
            cost137Cfg = ConfigManager.getInstance().getConfigByKey(RechargeReturnCost137Cfg.class, maxId);
        } else {
            cost137Cfg = ConfigManager.getInstance().getConfigByKey(RechargeReturnCost137Cfg.class, lotteryCount);
        }

        // 消耗钻石
        int payGold = cost137Cfg.getCost();
        if (payGold > 0) {
            if (player.getGold() < payGold) {
                player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH);
                return true;
            }
            player.consumeGold(payGold, BehaviorLogger.Action.ACTIVITY137_RECHARGE_RETURN);
            ConsumeItems.valueOf(Const.changeType.CHANGE_GOLD, payGold).pushChange(player);
        }

        // 抽奖
        for (int i = 1; i <= 3; i++) {
            int randInt = GuaJiRand.randInt(99) + 1;
            int multiple = RechargeReturnLottery137Cfg.calcRate(randInt);

            if (i == 1) {
                status.setFirstLine(multiple);
            }
            if (i == 2) {
                status.setSecondLine(multiple);
            }
            if (i == 3) {
                status.setThirdLine(multiple);
            }
        }
        status.setLotteryCount(lotteryCount);
        player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());

        Activity137LotteryRep.Builder builder = Activity137LotteryRep.newBuilder();
        builder.setCount(lotteryCount);
        builder.setFirstLine(status.getFirstLine());
        builder.setSecondLine(status.getSecondLine());
        builder.setThirdLine(status.getThirdLine());

        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY137_SLOT_RETURN_LOTTERY_S_VALUE, builder));
        return true;
    }
}
