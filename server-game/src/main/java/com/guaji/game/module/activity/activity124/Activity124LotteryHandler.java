package com.guaji.game.module.activity.activity124;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RechargeReturnCost124Cfg;
import com.guaji.game.config.RechargeReturnLotteryCfg;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity3.Activity124LotteryRep;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;

import java.util.Map;

public class Activity124LotteryHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {

        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY124_RECHARGE_RETURN_VALUE;
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

        int ticketId = 0;
        Activity124Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity124Status.class);
        //是否已经使用过每天只能使用一次
        if (status.getUsed()) {
            player.sendError(protocol.getType(), Status.error.RECHARGE_RETURN_TODAY_GOT);
            return true;
        } else {

            int times = status.getCount() + 1;
            int size = ConfigManager.getInstance().getConfigMap(RechargeReturnCost124Cfg.class).size();
            RechargeReturnCost124Cfg cost124Cfg = null;
            if (times > size) {
                cost124Cfg = ConfigManager.getInstance().getConfigByKey(RechargeReturnCost124Cfg.class, size);
            } else {
                // 钻石消耗
                cost124Cfg = ConfigManager.getInstance().getConfigByKey(RechargeReturnCost124Cfg.class, times);
            }

            int payGold = cost124Cfg.getCost();
            if (payGold > 0) {
                if (player.getGold() < payGold) {
                    player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH);
                    return true;
                }
                player.consumeGold(payGold, BehaviorLogger.Action.ACTIVITY124_RECHARGE_RETURN);
                ConsumeItems.valueOf(Const.changeType.CHANGE_GOLD, payGold).pushChange(player);
            }

            //抽奖
            int randInt = GuaJiRand.randInt(99) + 1;
            RechargeReturnLotteryCfg cfg = null;
            Map<Object, RechargeReturnLotteryCfg> configMap = ConfigManager.getInstance().getConfigMap(RechargeReturnLotteryCfg.class);
            for (RechargeReturnLotteryCfg item : configMap.values()) {
                if (randInt >= item.getStartRand() && randInt <= item.getEndRand()) {
                    cfg = item;
                    break;
                }
            }
            if (cfg == null) {
                player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
                return true;
            }
            ticketId = cfg.getId();
            status.setTicketGetDate(GuaJiTime.getDateString());
            status.setUsed(false);
            status.setGotTicket(true);
            status.setTicketId(ticketId);
            status.setCount(status.getCount() + 1);
            //保存玩家数据
            player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId(), true);

            Activity124LotteryRep.Builder builder = Activity124LotteryRep.newBuilder();
            builder.setCount(status.getCount());
            builder.setType(ticketId);
            player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY124_RECHARGE_RETURN_LOTTERY_S_VALUE, builder));
            return true;
        }
    }

}
