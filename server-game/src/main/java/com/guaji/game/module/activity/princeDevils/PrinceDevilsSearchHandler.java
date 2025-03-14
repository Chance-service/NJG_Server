package com.guaji.game.module.activity.princeDevils;

import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.protocol.Activity2.HPPrinceDevilsPanelInfoRes;
import com.guaji.game.protocol.Activity2.HPPrinceDevilsSearchReq;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.PrinceDevilsCostCfg;
import com.guaji.game.config.PrinceDevilsDropCfg;
import com.guaji.game.entity.PlayerPrinceDevilsEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Reward.RewardType;
import com.guaji.game.protocol.Status;

/**
 * 魔王的宝藏活动
 */
public class PrinceDevilsSearchHandler implements IProtocolHandler {

	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		//本玩法奖励有三档，普通奖励，幸运奖励，终极奖励。本玩法需要满足如下需求
		//1.奖池有三类奖池，每次抽奖先运算奖池概率
		//2.最高档奖励的奖池概率是动态衰减
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.PRINCE_DEVILS_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null || timeCfg.isEnd()) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		//新一期活动数据修正
		PlayerPrinceDevilsEntity entity = player.getPlayerData().loadPrinceDevilsEntity();
		if (entity.getStageId() != timeCfg.getStageId()) {
			entity.resetInfo(timeCfg.getStageId());
			entity.notifyUpdate();
		}
		// 获取参数配置
		PrinceDevilsCostCfg config = ConfigManager.getInstance().getConfigByIndex(PrinceDevilsCostCfg.class, 0);
		if (config == null) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		}
		int cellCount = config.getCellCount();
		Map<Integer,String> curRoundAaward = entity.getRewardInfoMap();
		int totalCount = entity.getCount();
		int curRoundCount = totalCount%cellCount;
		
		HPPrinceDevilsSearchReq req = protocol.parseProtocol(HPPrinceDevilsSearchReq.getDefaultInstance());
		int drawIndex = req.getDevilsIndex();
		if (drawIndex <= 0 || drawIndex > cellCount || curRoundAaward.containsKey(drawIndex)) {
			// 参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}

		boolean isFree = false;
		ConsumeItems consumeItems = ConsumeItems.valueOf();

		// 每次扣除的钻石
		int costGold = PrinceDevilsCostCfg.getPrinceDevilsGoldMap(curRoundCount==0?1:(curRoundCount+1));

		// 判断是否在免费时间内
		if (config.getRefreshTime() < GuaJiTime.getSeconds() - entity.getFreeTime()) {
			isFree = true;
			entity.setFreeTime(GuaJiTime.getSeconds());
		} else {
			consumeItems.addChangeInfo(changeType.CHANGE_GOLD, costGold);
			if (!consumeItems.checkConsume(player, protocol.getType())) {
				return true;
			}
		}

		if (!isFree) {
			consumeItems.consumeTakeAffect(player, Action.PRINCE_DEVILS);
		}
		//随机数
		int random = 0;
		try {
			random = GuaJiRand.randInt(1, 10000);
		} catch (MyException e) {
			e.printStackTrace();
		}
		int poolID = entity.getPoolIDByRate(random);
		// 魔王奖励
		AwardItems awards = new AwardItems();
		AwardItems luckAwards = new AwardItems();
		//普通奖励
		List<ItemInfo> searchItems = PrinceDevilsDropCfg.snowSearch(entity.getCount(),1);
		awards.addItemInfos(searchItems);
		awards.rewardTakeAffectAndPush(player, Action.PRINCE_DEVILS, 1);
		entity.addRewardInfo(drawIndex, awards.toString());
		//是否触发终极奖励
		boolean isFinalAward = false;
		//是否触发幸运奖励
		isFinalAward = config.isBigAward(poolID);
		if (isFinalAward) {
			// 给大奖
			searchItems = PrinceDevilsDropCfg.snowSearch(totalCount,poolID);
			luckAwards.addItemInfos(searchItems);
			luckAwards.rewardTakeAffectAndPush(player, Action.PRINCE_DEVILS, 2, RewardType.PRINCE_DEVILS_TYPE);
			entity.updatePoolRate();
		} 
		entity.setScore(entity.getScore() + PrinceDevilsCostCfg.getPrinceDevilsScoreMap(entity.getRewardInfoMap().size()));
		entity.setRefreshTime(GuaJiTime.getMillisecond());
		entity.addSearchTimes(false);
		boolean isReset = false;
		if(entity.getRewardInfoMap().size()==cellCount){
			//重置奖励
			entity.resetReward();
			entity.resetPoolRate();
			isReset = true;
		}
		entity.notifyUpdate();
		HPPrinceDevilsPanelInfoRes.Builder ret = BuilderUtil.princeDevilsInfoBuilder(entity, isReset, timeCfg,luckAwards.toString());
		player.sendProtocol(Protocol.valueOf(HP.code.PRINCE_DEVILS_OPEN_S_VALUE, ret));

		// 日志记录
		BehaviorLogger.log4Platform(player, Action.PRINCE_DEVILS, Params.valueOf("stageId", entity.getStageId()),
				Params.valueOf("searchindex", drawIndex), Params.valueOf("isFree", isFree),
				Params.valueOf("score", entity.getScore()), Params.valueOf("awards", awards.toString()),
				Params.valueOf("isBigAward", isFinalAward), Params.valueOf("bigAwards", luckAwards.toString()));
		return true;
	}
}
