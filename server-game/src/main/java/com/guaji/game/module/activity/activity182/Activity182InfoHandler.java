package com.guaji.game.module.activity.activity182;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityGiftAward182Cfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.Activity132LevelGiftInfoRes;
import com.guaji.game.protocol.Activity4.LevelGiftInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;


public class Activity182InfoHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY182_JumpGift_VALUE;
        ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (activityTimeCfg == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        Activity132LevelGiftInfoRes.Builder builder = Activity132LevelGiftInfoRes.newBuilder();
        Activity182Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity182Status.class);
        
        Map<Object, ActivityGiftAward182Cfg> cfgList = ConfigManager.getInstance().getConfigMap(ActivityGiftAward182Cfg.class);
        
        for (ActivityGiftAward182Cfg cfgItem : cfgList.values()) {
            LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();
            info.setCfgId(cfgItem.getId());
            info.setIsGot(status.isAlreadyGot(cfgItem.getId()));

            builder.addInfo(info);
        }
        // 活動是限時的直接抓activityTime的時間
        builder.setLimitDate(ActivityUtil.calTargetTime(activityTimeCfg.getlEndTime()));
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY182_ACTIVITY_GIFT_INFO_S, builder));
        return true;
    }
}
