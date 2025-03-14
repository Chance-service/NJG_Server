package com.guaji.game.module.activity.activity151;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RechargeConfig;
import com.guaji.game.config.RechargeConfig.RechargeItem;
import com.guaji.game.config.StageGiftAward151Cfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.Activity132LevelGiftBuyReq;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Shop.GoodsNotice;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;


public class Activity151BuyHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
    	// 改儲值購買,這裡僅作禮包驗證
        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY151_STAGE_GIFT_VALUE;
        ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (activityTimeCfg == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }
        Activity132LevelGiftBuyReq req = protocol.parseProtocol(Activity132LevelGiftBuyReq.getDefaultInstance());
        StageGiftAward151Cfg cfg = ConfigManager.getInstance().getConfigMap(StageGiftAward151Cfg.class).get(req.getCfgId());
        if (cfg == null) {
            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
            return true;
        }
        int userLevel = player.getPassMapId();
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
		
		
		Activity151Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity151Status.class);
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
        

        // 发放奖励
//        AwardItems awards = AwardItems.valueOf(cfg.getAwards());
//        awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY151_STAGE_GIFT, 2);
//        // 更新status
//        status.addGiftIds(req.getCfgId());
//        player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId(), true);
        
		// BI 日志 ()
//		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY151_STAGE_GIFT, 
//				Params.valueOf("CfgId", req.getCfgId()),
//				Params.valueOf("PassMapId", userLevel),
//				Params.valueOf("payGold", payGold),
//		        Params.valueOf("awards", cfg.getAwards()));

        // 同步领取状态
//        Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
//        Map<Object, StageGiftAward151Cfg> cfgList = ConfigManager.getInstance().getConfigMap(StageGiftAward151Cfg.class);
//        for (StageGiftAward151Cfg cfgItem : cfgList.values()) {
//            LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();
//            info.setCfgId(cfgItem.getId());
//            
//            info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
//
//            builder.addInfo(info);
//        }
//
//        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY151_STAGE_GIFT_BUY_S_VALUE, builder));
        return true;
    }
}
