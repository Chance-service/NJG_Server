package com.guaji.game.module.activity.activity125;

import com.guaji.game.config.ShootCost125Cfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.*;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.util.ActivityUtil;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;


import java.util.Map;

public class Activity125InfoHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY125_WEAPON_VALUE;
        int defaultStageId = 1;
        Activity125Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, defaultStageId, Activity125Status.class);

        // 获取配置
        Map<Object, ShootCost125Cfg> typeCfgMap = ConfigManager.getInstance().getConfigMap(ShootCost125Cfg.class);
        Activity125WeaponInfoRes.Builder builder = Activity125WeaponInfoRes.newBuilder();
        for (Activity125TypeEnum item : Activity125TypeEnum.values()) {
            ShootCost125Cfg typeCfg = typeCfgMap.get(item.getTypeId());
            if (typeCfg == null) {
                continue;
            }
            Activity125Info.Builder info = Activity125Info.newBuilder();
            info.setType(item.getTypeId());
            info.setOneTimePrice(typeCfg.getOneTimeCost());
            info.setMultiTimePrice(typeCfg.getTenTimeCost());
            info.setRewardStateId(typeCfg.getPoolType());
            if (item.getTypeId() == Activity125TypeEnum.LOW_TYPE.getTypeId()) {
                info.setLeftCount(status.getLowLeftCount(typeCfg.getMaxCount()));
            } else {
                long freeLastTime = 0;
                if (item.getTypeId() == Activity125TypeEnum.MEDIUM_TYPE.getTypeId() && status.getMediumNextFreeTime() > System.currentTimeMillis()) {
                    freeLastTime = (int) (status.getMediumNextFreeTime() - System.currentTimeMillis()) / 1000;
                }
                if (item.getTypeId() == Activity125TypeEnum.HIGH_TYPE.getTypeId() && status.getHighNextFreeTime() > System.currentTimeMillis()) {
                    freeLastTime = (int) (status.getHighNextFreeTime() - System.currentTimeMillis()) / 1000;
                }
                info.setFreeTime(freeLastTime);
            }
            builder.addInfo(info);
        }
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY125_WEAPON_INFO_S_VALUE, builder));
        return true;
    }
}
