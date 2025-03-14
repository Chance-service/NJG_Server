package com.guaji.game.module.activity.activity132;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.LevelGiftAward132Cfg;
import com.guaji.game.config.SysBasicCfg;
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
import org.guaji.os.MyException;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;


public class Activity132InfoHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY132_LEVEL_GIFT_VALUE;
        ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (activityTimeCfg == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        Activity132LevelGiftInfoRes.Builder builder = Activity132LevelGiftInfoRes.newBuilder();
        Activity132Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity132Status.class);
        int hours = 0;
        Map<Object, LevelGiftAward132Cfg> cfgList = ConfigManager.getInstance().getConfigMap(LevelGiftAward132Cfg.class);
        for (LevelGiftAward132Cfg cfgItem : cfgList.values()) {
            LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();
            info.setCfgId(cfgItem.getId());
            if ((player.getLevel() >= cfgItem.getMinLevel()) && (player.getLevel() <= cfgItem.getMaxLevel())) {
            	hours = cfgItem.getHours();
            }
            if (status.isAlreadyGot(cfgItem.getId())) {
                info.setIsGot(true);
            } else {
            	info.setIsGot(false);
//                try {
//                    // 免费礼包对老用户不可见
//                    Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
//                    String oldUserTime = SysBasicCfg.getInstance().getActivity132OldUserTime();
//                    int level = SysBasicCfg.getInstance().getActivity132OldUserLevel();
//                    Date startDate = ActivityTimeCfg.DATE_FORMAT.parse(oldUserTime);
//                    if (cfgItem.getCost() == 0 && registerDate.getTime() < startDate.getTime() && player.getLevel() >= level) {
//                        info.setIsGot(true);
//                    } else {
//                        info.setIsGot(false);
//                    }
//                } catch (ParseException e) {
//                    MyException.catchException(e);
//                }
            }
            builder.addInfo(info);
        }
        builder.setLimitDate(status.calcGiftSurplusTime(hours));
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY132_LEVEL_GIFT_INFO_S_VALUE, builder));
        return true;
    }
}
