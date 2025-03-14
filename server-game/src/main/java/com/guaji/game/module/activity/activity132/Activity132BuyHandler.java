package com.guaji.game.module.activity.activity132;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.LevelGiftAward132Cfg;
import com.guaji.game.config.RechargeConfig;
import com.guaji.game.config.RechargeReturnLotteryCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.RechargeConfig.RechargeItem;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.*;
import com.guaji.game.protocol.Shop.GoodsNotice;
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


public class Activity132BuyHandler implements IProtocolHandler {

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
        Activity132LevelGiftBuyReq req = protocol.parseProtocol(Activity132LevelGiftBuyReq.getDefaultInstance());
        LevelGiftAward132Cfg cfg = ConfigManager.getInstance().getConfigMap(LevelGiftAward132Cfg.class).get(req.getCfgId());
        if (cfg == null) {
            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
            return true;
        }
        int userLevel = player.getLevel();
        int goodsId = cfg.getCost();

        if (userLevel < cfg.getMinLevel() || userLevel > cfg.getMaxLevel()) {
        	player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }

        //验证等级条件
        if (goodsId == 0) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return true;
        }
        
        String platform = player.getPlatform(); //使用自身平台
		RechargeConfig rechargeConfig = RechargeConfig.getRechargeConfig(platform);

		if (rechargeConfig == null) {
		    //logger.info("recharge config cannot found, platform: {}", new Object[] { platform });
			throw new RuntimeException("recharge config cannot found");
		}
		
		
		Map<Integer,RechargeItem> allRechargeItem =rechargeConfig.getAllrechargeCfg();
		
		if (allRechargeItem == null || !allRechargeItem.containsKey(goodsId)) {
            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
            return true;
		}
       
        Activity132Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity132Status.class);
        if (status.isAlreadyGot(req.getCfgId())) {
            // 活动奖励已领取
            player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
            return true;
        }
        
        if (status.calcGiftSurplusTime(cfg.getHours()) <= 0) {
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }
        
        GoodsNotice.Builder builder = GoodsNotice.newBuilder();
        builder.setGoodsId(goodsId);
        
        player.sendProtocol(Protocol.valueOf(HP.code.GOODS_VERIFY_S, builder));
        return true;
    }
}
