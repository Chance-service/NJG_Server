package com.guaji.game.module.activity.recharge;

import java.util.Collection;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.ActivityRechargeItem;
import com.guaji.game.protocol.Activity.HPDoubleRechargeRet;
import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.DoubleRechargeCfg;
import com.guaji.game.config.RechargeRatioCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 充值双倍
 */
public class RechargeDoubleHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		// 充值双倍活动
		int activityId = Const.ActivityId.DOUBLE_RECHARGE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null || timeCfg.isEnd()){
			player.sendError(HP.code.RECHARGE_DOUBLE_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		HPDoubleRechargeRet.Builder doubleRechargeBuilder = HPDoubleRechargeRet.newBuilder();
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.DOUBLE_RECHARGE_VALUE);
		if(activityItem == null) {
			player.sendError(HP.code.RECHARGE_DOUBLE_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}
		Integer type = activityItem.getParam("type");
		if(type == null) {
			player.sendError(HP.code.RECHARGE_DOUBLE_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}
		
		doubleRechargeBuilder.setActivityType(type);
		
		doubleRechargeBuilder.setLeftTimes(timeCfg.calcActivitySurplusTime());
		
		Collection<DoubleRechargeCfg> doubleRechargeCfgs = ConfigManager.getInstance().getConfigMap(DoubleRechargeCfg.class).values();
		// 首充翻倍活动
		FirstRechargeStatus firstRechargeStatus = ActivityUtil.getFirstRechargeStatus(player.getPlayerData());
		DoubleRechargeStatus doubleRechargeStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, 
				timeCfg.getStageId(), DoubleRechargeStatus.class);
		for(DoubleRechargeCfg doubleRechargeCfg : doubleRechargeCfgs) {
			ActivityRechargeItem.Builder itemBuilder = ActivityRechargeItem.newBuilder();
			itemBuilder.setGoodsId(doubleRechargeCfg.getGoodsId());
			RechargeRatioCfg rechargeRatioCfg = ConfigManager.getInstance().getConfigByKey(RechargeRatioCfg.class, doubleRechargeCfg.getGoodsId());
			if(firstRechargeStatus != null && firstRechargeStatus.getRecharge(doubleRechargeCfg.getGoodsId()) <= 0 && rechargeRatioCfg != null) {
				//所有首冲三倍的条件
				itemBuilder.setType(1);
				itemBuilder.setRatio(rechargeRatioCfg.getRatio(player.getPlayerData()) + 1);
			}else if(doubleRechargeStatus != null){
				if(doubleRechargeStatus.canTrigger(doubleRechargeCfg.getGoodsId())) {
					itemBuilder.setGoodsId(doubleRechargeCfg.getGoodsId());
					itemBuilder.setType(2);
					itemBuilder.setRatio(doubleRechargeCfg.getRatio() + 1);
				}else{
					itemBuilder.setGoodsId(doubleRechargeCfg.getGoodsId());
					itemBuilder.setType(3);
					itemBuilder.setRatio(doubleRechargeCfg.getRatio() + 1);
				}
			}
			doubleRechargeBuilder.addItems(itemBuilder);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.RECHARGE_DOUBLE_S_VALUE,doubleRechargeBuilder));
		
		return true;
	}

}
