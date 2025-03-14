package com.guaji.game.module.activity.activity134;

import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.WeekendGiftReward134Cfg;
import com.guaji.game.item.AwardItems;
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

import java.time.LocalDate;

public class Activity134GetHandler implements IProtocolHandler {

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
        Activity134WeekendGiftGetReq req = protocol.parseProtocol(Activity134WeekendGiftGetReq.getDefaultInstance());
        LocalDate today = LocalDate.now();
        Integer dayOfWeek = today.getDayOfWeek().getValue();
        Activity134Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity134Status.class);
        Activity134StatusItem item = status.getStatusMap().get(dayOfWeek);
        if (item == null || item.getCfgId() != req.getCfgId()) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }

        // 验证抽奖
        if (!item.isLottery() || item.getRate() == 0 || item.isGot()) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }

        // 发放奖励
        WeekendGiftReward134Cfg cfg = ConfigManager.getInstance().getConfigMap(WeekendGiftReward134Cfg.class).get(req.getCfgId());
        if (cfg == null) {
            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
            return true;
        }
        // 多倍发奖
        AwardItems awards = AwardItems.valueOf(cfg.getAwards());
        for (AwardItems.Item awardItem : awards.getAwardItems()) {
            int count = (int)awardItem.getCount() * item.getRate();
            awardItem.setCount(count);
        }
        awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY134_WEEKEND_GIFT, 2);

        // 更新status
        item.setGot(true);
        player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId(), true);

        Activity134WeekendGiftGetRes.Builder builder = Activity134WeekendGiftGetRes.newBuilder();
        builder.setCfgId(req.getCfgId());
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY134_WEEKEND_GIFT_GET_S_VALUE, builder));
        return true;
    }

}
