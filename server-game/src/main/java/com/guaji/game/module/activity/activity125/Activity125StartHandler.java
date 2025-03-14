package com.guaji.game.module.activity.activity125;

import com.guaji.game.config.ShootCost125Cfg;
import com.guaji.game.config.ShootReward125Cfg;
import com.guaji.game.item.AwardItems;
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
import org.guaji.os.MyException;

import java.sql.Timestamp;
import java.util.Map;

public class Activity125StartHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY125_WEAPON_VALUE;
        int defaultStageId = 1;

        Activity125WeaponStartReq req = protocol.parseProtocol(Activity125WeaponStartReq.getDefaultInstance());

        // 获取配置
        Map<Object, ShootCost125Cfg> typeCfgMap = ConfigManager.getInstance().getConfigMap(ShootCost125Cfg.class);
        ShootCost125Cfg typeCfg = typeCfgMap.get(req.getType());
        if (typeCfg == null) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }

        // 获取活动状态
        Activity125Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, defaultStageId, Activity125Status.class);
        Activity125WeaponStartRes.Builder ret = Activity125WeaponStartRes.newBuilder();
        AwardItems totalAwards = new AwardItems();

        boolean isLow = req.getType() == Activity125TypeEnum.LOW_TYPE.getTypeId();
        boolean isMedium = req.getType() == Activity125TypeEnum.MEDIUM_TYPE.getTypeId();
        boolean isHigh = req.getType() == Activity125TypeEnum.HIGH_TYPE.getTypeId();

        if (isLow) {

            // 初级场验证总次数（无免费次数）
            int leftCount = status.getLowLeftCount(typeCfg.getMaxCount());
            boolean verifyCount = req.getCount() > typeCfg.getMaxCount() || leftCount < req.getCount();
            if (verifyCount) {
                player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
                return true;
            }
            int costMoney = req.getCount() == typeCfg.getMaxCount() ? typeCfg.getTenTimeCost() : typeCfg.getOneTimeCost();

            // 验证金币消耗
            if (player.getCoin() < costMoney) {
                player.sendError(protocol.getType(), Status.error.COINS_NOT_ENOUGH);
                return true;
            }

            //抽奖
            int totalTimes = status.getLowTotalCount();
            getRewards(ret, req.getType(), req.getCount(), totalTimes, totalAwards);

            //消耗金币
            player.consumeCoin(costMoney, BehaviorLogger.Action.WEAPON_DRAW);
            ConsumeItems.valueOf(Const.changeType.CHANGE_COIN, costMoney).pushChange(player);

            //记录次数
            int drawCount = status.getLowTodayCount();
            status.setLowTodayCount(drawCount + req.getCount());
            status.setLowTotalCount(totalTimes + req.getCount());

            //更新状态
            player.getPlayerData().updateActivity(activityId, defaultStageId, true);

            //获取最新Info
            Activity125Info.Builder info = Activity125Info.newBuilder();
            info.setType(req.getType());
            info.setOneTimePrice(typeCfg.getOneTimeCost());
            info.setMultiTimePrice(typeCfg.getTenTimeCost());
            info.setRewardStateId(typeCfg.getPoolType());
            info.setLeftCount(status.getLowLeftCount(typeCfg.getMaxCount()));
            ret.setInfo(info);

        } else if (isMedium || isHigh) {

            long freeTime = isMedium ? status.getMediumNextFreeTime() : status.getHighNextFreeTime();

            // 中高级场验证免费次数
            if (req.getFreeType() && freeTime > System.currentTimeMillis()) {
                player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
                return true;
            }

            // 验证钻石消耗
            int costMoney = 0;
            if (!req.getFreeType()) {
                costMoney = req.getCount() == typeCfg.getMaxCount() ? typeCfg.getTenTimeCost() : typeCfg.getOneTimeCost();
                if (player.getGold() < costMoney) {
                    player.sendError(protocol.getType(), Status.error.COINS_NOT_ENOUGH);
                    return true;
                }
            }

            //抽奖
            int totalTimes = isMedium ? status.getMediumTotalCount() : status.getHighTotalCount();
            getRewards(ret, req.getType(), req.getCount(), totalTimes, totalAwards);

            //消耗钻石
            if (!req.getFreeType()) {
                player.consumeGold(costMoney, BehaviorLogger.Action.WEAPON_DRAW);
                ConsumeItems.valueOf(Const.changeType.CHANGE_GOLD, costMoney).pushChange(player);
            } else {
                long nextTime = 1000 * 60 * 60 * typeCfg.getFreeRefreshTime();
                long nextFreeTime = System.currentTimeMillis() + nextTime;
                if (isMedium) {
                    status.setMediumNextFreeTime(nextFreeTime);
                }
                if (isHigh) {
                    status.setHighNextFreeTime(nextFreeTime);
                }
            }

            // 记录总次数
            if (isMedium) {
                status.setMediumTotalCount(totalTimes + req.getCount());
            }
            if (isHigh) {
                status.setHighTotalCount(totalTimes + req.getCount());
            }

            //更新状态
            player.getPlayerData().updateActivity(activityId, defaultStageId, true);

            //获取最新Info
            Activity125Info.Builder info = Activity125Info.newBuilder();
            info.setType(req.getType());
            info.setOneTimePrice(typeCfg.getOneTimeCost());
            info.setMultiTimePrice(typeCfg.getTenTimeCost());
            info.setRewardStateId(typeCfg.getPoolType());
            long nextTime = isMedium ? status.getMediumNextFreeTime() : status.getHighNextFreeTime();
            info.setFreeTime((int) (nextTime - System.currentTimeMillis()) / 1000);
            ret.setInfo(info);

        }

        //发放奖励
        totalAwards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.SHOOT_ACTIVITY, 0);

        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY125_WEAPON_START_S_VALUE, ret));

        BehaviorLogger.log4Platform(player, BehaviorLogger.Action.WEAPON_DRAW, BehaviorLogger.Params.valueOf("drawType", req.getType()),
                BehaviorLogger.Params.valueOf("drawCount", req.getCount()), BehaviorLogger.Params.valueOf("weaponAwards", totalAwards.toDbString()));

        return true;
    }

    private void getRewards(Activity125WeaponStartRes.Builder ret, int typeId, int drawCount, int drawTotalCount, AwardItems totalAwards) {
        for (int i = 0; i < drawCount; i++) {
            ShootReward125Cfg rewardCfg = ShootReward125Cfg.getShootReward125(typeId, drawTotalCount);
            String[] rewardItems = rewardCfg.getItems().split(",");
            try {
                int rewardIndex = GuaJiRand.randInt(0, rewardItems.length - 1);
                if (rewardIndex < rewardItems.length) {
                    AwardItems tmp = AwardItems.valueOf(rewardItems[rewardIndex]);
                    totalAwards.addItemInfos(tmp.getAwardItemInfos());
                    ret.addReward(rewardItems[rewardIndex]);
                    drawTotalCount++;
                }
            } catch (MyException e) {
                e.printStackTrace();
            }
        }
    }

}
