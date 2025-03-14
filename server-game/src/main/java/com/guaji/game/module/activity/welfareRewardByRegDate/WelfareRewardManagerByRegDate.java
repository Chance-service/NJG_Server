package com.guaji.game.module.activity.welfareRewardByRegDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.guaji.game.config.ActivityCfg;
import com.guaji.game.util.ActivityUtil;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.WelfareRewardByRegDateCfg;
import com.guaji.game.config.WelfareRewardCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.module.activity.welfareReward.WelfareRewardStatus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity3.WelfareRewardByRegDateRes;
import com.guaji.game.protocol.Activity3.WelfareRewardRes;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年5月24日 下午11:09:01
 * 类说明
 */
public class WelfareRewardManagerByRegDate {
    /**
     * 同步的时候并不做随机
     */
    static void sync(Player player, WelfareRewardStatusByRegDate status, ActivityTimeCfg timeCfg, ActivityCfg.ActivityItem activityItem) {
        // 根据当前的阶段进入哪个奖池
        int pool = status.getCurrentStep();
        int cost = WelfareRewardByRegDateCfg.costMap.get(pool);
        WelfareRewardByRegDateRes.Builder response = WelfareRewardByRegDateRes.newBuilder();
        if (!status.canPlay()) {
            response.setLeftTime(0);
            response.setCanPlay(status.canPlay());
        } else {
            Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
            Integer keepDays = activityItem.getParam("keepDays");
            int leftTime = ActivityUtil.getLeftTimeByRegister(registerDate.getTime(), timeCfg.getlStartTime(), keepDays, player.getPlayerData().getPlayerEntity().getMergeTime());
            response.setLeftTime(leftTime);
            boolean canPlay = leftTime > 0;
            response.setCanPlay(canPlay);
            if (!canPlay) {
                status.setCanPlay(false);
                player.getPlayerData().updateActivity(timeCfg.getActivityId(), timeCfg.getStageId(), true);
            }
        }
        response.setCost(cost);
        response.setGold(0);
        player.sendProtocol(Protocol.valueOf(HP.code.WELFAREBYREGDATE_REWARD_S_VALUE, response));
    }

    /**
     * 只有在抽奖的时候才做随机
     */
    static void play(Player player, WelfareRewardStatusByRegDate status, ActivityTimeCfg timeCfg) {
        // 进入哪个随机
        WelfareRewardByRegDateCfg cfg = getRandomCfg(status);
        if (null == cfg) {
            player.sendError(HP.code.WELFAREBYREGDATE_REWARD_S_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
            return;
        }
        // 判断消耗
        int costGold = cfg.getCost();
        if (player.getGold() < costGold) {
            player.sendError(HP.code.WELFAREBYREGDATE_REWARD_S_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
            return;
        }
        // 随机出奖励多少钻石
        int min = cfg.getMin();
        int max = cfg.getMax();
        int rewardGold = GuaJiRand.random(min, max);
        // 消耗钻石
        player.consumeGold(costGold, Action.WELFAREBYREGDATE_REWARD);
        // 发钻石
        AwardItems awardItems = new AwardItems();
        ItemInfo itemInfo = ItemInfo.valueOf(10000, 1001, rewardGold);
        awardItems.addItem(itemInfo);
        awardItems.rewardTakeAffectAndPush(player, Action.WELFAREBYREGDATE_REWARD, 0);
        // 更新数据
        if (cfg.isMaxStage()) {
            status.setCanPlay(false);
        } else {
            status.setCurrentStep(status.getCurrentStep() + 1);
            // 获取下一次的消耗数据
            cfg = getRandomCfg(status);
            costGold = cfg.getCost();
        }
        player.getPlayerData().updateActivity(timeCfg.getActivityId(), timeCfg.getStageId());
        // 平台数据统计
        BehaviorLogger.log4Platform(player, Action.WELFAREBYREGDATE_REWARD, Params.valueOf("stage", status.getCurrentStep()));
        // 返回同步
        WelfareRewardByRegDateRes.Builder response = WelfareRewardByRegDateRes.newBuilder();
        response.setCanPlay(status.canPlay());
        response.setLeftTime(timeCfg.calcActivitySurplusTime());
        response.setCost(costGold);
        response.setGold(rewardGold);
        player.sendProtocol(Protocol.valueOf(HP.code.WELFAREBYREGDATE_REWARD_S_VALUE, response));
    }

    public static WelfareRewardByRegDateCfg getRandomCfg(WelfareRewardStatusByRegDate status) {
        int pool = status.getCurrentStep();
        List<Integer> rateList = new ArrayList<Integer>();
        List<WelfareRewardByRegDateCfg> cfgList = WelfareRewardByRegDateCfg.poolMap.get(pool);
        for (WelfareRewardByRegDateCfg cfg : cfgList) {
            rateList.add(cfg.getDrawRate());
        }
        // 随机出哪个配置
        return GuaJiRand.randonWeightObject(cfgList, rateList);
    }
}
