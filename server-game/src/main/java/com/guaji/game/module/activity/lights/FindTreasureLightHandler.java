package com.guaji.game.module.activity.lights;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPFindTreasureLight;
import com.guaji.game.protocol.Activity2.HPFindTreasureLightRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FindTreasureCfg;
import com.guaji.game.config.FindTreasureLightCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class FindTreasureLightHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.FIND_TREASURE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		FindTreasureStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), FindTreasureStatus.class);
		
		HPFindTreasureLight req = protocol.parseProtocol(HPFindTreasureLight.getDefaultInstance());
		int findType = req.getType();
		int findTimes = req.getTimes();
		
		FindTreasureCfg typeCfg = null;
		List<FindTreasureCfg> typeCfgs = ConfigManager.getInstance().getConfigList(FindTreasureCfg.class);
		for(FindTreasureCfg tmpTypeCfg : typeCfgs){
			if(tmpTypeCfg.getType() == findType){
				typeCfg = tmpTypeCfg;
				break;
			}
		}
		
		if(typeCfg == null || findTimes < 1 || findTimes > 10){
			// 参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
		
		int alreadyTime = status.getTodayFindTimes(findType);
		if(alreadyTime + findTimes  > typeCfg.getOneDayMaxTimes()){
			// 次数不足
			player.sendError(protocol.getType(), Status.error.LIGHT_TIMES_NOT_ENOUGH);
			return true;
		}
		
		int costMoney = typeCfg.getOneTimeCost() * findTimes;
		if(typeCfg.getCurrency() == Const.buyMoneyType.MONEY_COIN_VALUE){
			if(player.getCoin() < costMoney){
				// 金币不足
				player.sendError(protocol.getType(), Status.error.COINS_NOT_ENOUGH_VALUE);
				return true;
			}
			player.consumeCoin(costMoney, Action.FIND_TREASURE_LIGHT);
			ConsumeItems.valueOf(changeType.CHANGE_COIN, costMoney).pushChange(player);
		}else{
			if(player.getGold() < costMoney){
				// 钻石不足
				player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
				return true;
			}
			player.consumeGold(costMoney, Action.FIND_TREASURE_LIGHT);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, costMoney).pushChange(player);
		}
		
		int alreadyTotalTime = status.getTotalFindTimes(findType);
		AwardItems totalAwards = new AwardItems();
		
		HPFindTreasureLightRet.Builder ret = HPFindTreasureLightRet.newBuilder();
		ret.setType(findType);
		for(int i = 0; i < findTimes; i++){
			FindTreasureLightCfg findTreasureLightCfg = FindTreasureLightCfg.findLight(findType, alreadyTotalTime + i);
			AwardItems tmp = AwardItems.valueOf(findTreasureLightCfg.getItems());
			totalAwards.addItemInfos(tmp.getAwardItemInfos());
			ret.addRewardId(findTreasureLightCfg.getItemNo());
		}
		status.addTimes(findType, findTimes);
		totalAwards.rewardTakeAffectAndPush(player, Action.FIND_TREASURE_LIGHT, 0);
		
		ret.setLeftSearchTimes(Math.max(0, typeCfg.getOneDayMaxTimes() - status.getTodayFindTimes(findType)));
		player.sendProtocol(Protocol.valueOf(HP.code.FIND_TREASURE_SEARCH_S, ret));
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		BehaviorLogger.log4Platform(player, Action.FIND_TREASURE_LIGHT, Params.valueOf("findType", findType),
				Params.valueOf("findTimes", findTimes), Params.valueOf("redEnvelopeGold", totalAwards.toDbString()));
		return true;
	}
}
