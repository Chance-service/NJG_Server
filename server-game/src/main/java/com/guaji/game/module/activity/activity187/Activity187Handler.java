package com.guaji.game.module.activity.activity187;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.AdjustEventCfg;
import com.guaji.game.config.RechargeConfig;
import com.guaji.game.config.RechargeConfig.RechargeItem;
import com.guaji.game.config.ReleaseStepGiftCfg;
import com.guaji.game.config.TimeGiftCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.ActivityExchangeReq;
import com.guaji.game.protocol.Activity5.GiftReq;
import com.guaji.game.protocol.Activity5.GiftResp;
import com.guaji.game.protocol.Activity5.JumpGiftInfo;
import com.guaji.game.protocol.Activity5.MaxJumpGiftReq;
import com.guaji.game.protocol.Activity5.MaxJumpGiftResp;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Shop.GoodsNotice;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;

/**
 * 加強彈跳協定
 */
public class Activity187Handler implements IProtocolHandler {
	static final int Sync = 0;
	static final int Buy_Verify = 1;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub
		
		Player player = (Player) appObj;

		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY187_MaxJump_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析接受到的数据
		MaxJumpGiftReq request = protocol.parseProtocol(MaxJumpGiftReq.getDefaultInstance());

		int action = request.getAction();
				
		// 数据人错误
		if ( action < 0 || action > 1) {
			// 无效参数
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
		
//		if (action != Sync) {
//			if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.free1500_Unlock)){
//				player.sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
//				return true;
//			}
//		}
				
		int stageId = timeConfig.getStageId();
		
		Activity187Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity187Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		// 重置禮包使用期數不同購買次數
		Map<Object,TimeGiftCfg> cfgMap = ConfigManager.getInstance().getConfigMap(TimeGiftCfg.class);
		boolean needfix = false;
		for (TimeGiftCfg tcfg : cfgMap.values()) {
			int goodsId = tcfg.getRechargeid();
			int useTime =  status.getGiftUseTime(goodsId);
			if (useTime != -1) {
				if (useTime != tcfg.getUseTime()) {
					if (status.removeGiftCount(goodsId)) {
						needfix = true;
					}
				}
			}
		}
		
		if (needfix) {
			player.getPlayerData().updateActivity(timeConfig.getActivityId(), timeConfig.getStageId(), true);
		}
				
		// 业务分支处理
		switch (action) {
		case Sync:
			SyncInfo(action,player,status);
			break;
		case  Buy_Verify:
			BuyVerify(protocol,action,player,status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	
	private static void BuyVerify(Protocol protocol,int action,Player player, Activity187Status status) {
		
		MaxJumpGiftReq request = protocol.parseProtocol(MaxJumpGiftReq.getDefaultInstance());
		
		int goodsId = request.getGoodsId();
		
		TimeGiftCfg giftCfg = ConfigManager.getInstance().getConfigByKey(TimeGiftCfg.class, goodsId);
		
		if (giftCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		
		if (!giftCfg.isActive()) {
			// 過期了
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return ;
		}
		
		if (status.getGiftCount(goodsId) >= giftCfg.getCount()) {
			// 超過限制
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return ;
		}
		
		if ((giftCfg.getSignid() == GsConst.SignMark.Four_Fighting) && (player.isSigned(GsConst.SignMark.Four_Fighting))) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return ;
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
            return;
		}
		                
        GoodsNotice.Builder builder = GoodsNotice.newBuilder();
        builder.setGoodsId(goodsId);
        
        player.sendProtocol(Protocol.valueOf(HP.code.GOODS_VERIFY_S, builder));
	}
	
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	public static void SyncInfo(int action,Player player, Activity187Status status) {
		MaxJumpGiftResp.Builder builder = getBuilder(action,player,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY187_MAXJUMP_GIFT_S, builder));
	}
	
	private static MaxJumpGiftResp.Builder getBuilder(int action,Player player,Activity187Status status) {
		// 返回包
		MaxJumpGiftResp.Builder response = MaxJumpGiftResp.newBuilder();
		
		response.setAction(action);
		
		Map<Object,TimeGiftCfg> cfgMap = ConfigManager.getInstance().getConfigMap(TimeGiftCfg.class);
		
		for (TimeGiftCfg tcfg : cfgMap.values()) {
			if (tcfg.isActive()) {
				JumpGiftInfo.Builder jumpInfo = JumpGiftInfo.newBuilder();
				int goodsId = tcfg.getRechargeid();
				jumpInfo.setGoodsId(goodsId);
				if (tcfg.getCount() == 0) { // 無購買限制
					jumpInfo.setCount(0);
				} else { 
					if ((tcfg.getSignid() == GsConst.SignMark.Four_Fighting) && (player.isSigned(GsConst.SignMark.Four_Fighting))) {
						jumpInfo.setCount(1);
					} else {
						jumpInfo.setCount(status.getGiftCount(goodsId));
					}
				}
				jumpInfo.setLeftTime(tcfg.calcSurplusTime());
				response.addInfo(jumpInfo);
			}
		}
		
		return response;
	}

}
