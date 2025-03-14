package com.guaji.game.module.activity.activity147;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.WishingWellDraw;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/*** @author 作者 Ting Lin
* @version 创建时间：2023年5月8日
* 类说明
*/
public class Activity147Handler implements IProtocolHandler{
	static final int SUN_WELL = 1;
	static final int MOON_WELL = 2;
	static final int STAR_WELL = 3;
	static final int SINGLE_DRAW = 1;
	static final int TEN_DRAW = 2;
	static final int LUCKY_AWARD = 3;
	static final int FREE_REFRESH = 4;
	static final int COST_REFRESH = 5;
	static final int FREE_DRAW = 6;

	@Override // RELEASE_UR_INFO_C
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.Wish_Unlock)){
			player.sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
			return true;
		}
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY147_WISHING_WELL_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		WishingWellDraw req = protocol.parseProtocol(WishingWellDraw.getDefaultInstance());
		int kind = req.getKind(); // 1.日 2.月 3.星
		int action = req.getAction();
		if ((kind < SUN_WELL) || (kind > STAR_WELL)) {
			// 活动参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		
		// 获取活动数据
		int stageId = timeCfg.getStageId();
		Activity147Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity147Status.class);
		
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		
		if (status.gameEnd(kind)) {
			Activity147WishingManager.initWishing(kind, player, status, activityId, stageId);
		}
				
		// 业务分支处理
		switch (action) {
		case SINGLE_DRAW:
			Activity147WishingManager.WishingDraw(action,kind,player,status);
			break;
		case TEN_DRAW:
			Activity147WishingManager.WishingDraw(action,kind,player,status);
			break;
		case FREE_DRAW:
			Activity147WishingManager.WishingDraw(action,kind,player,status);
			break;
		case LUCKY_AWARD:
			Activity147WishingManager.takeLuckyAward(action, kind, player, status);
			break;
//		case FREE_REFRESH:
//			Activity147WishingManager.RefreshPool(action, kind, player, status);
//			break;
//		case COST_REFRESH:
//			Activity147WishingManager.RefreshPool(action, kind, player, status);
//			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
		
//		Activity147Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), Activity147Status.class);
		// 使用免费抽奖的时间
//		long lastFreeTime = status.getLastFreeTime();
//		int singleCost = SysBasicCfg.getInstance().getChosenOneSingleCost();
//		int tenCost = SysBasicCfg.getInstance().getChosenOneTenCost();
//		// 当前系统时间
//		long currentTime = System.currentTimeMillis();
//		// 计算实际花费钻石数量
//		int payGold = 0;
//
//		if (searchTimes == TIMES_TYPE_TEN) {
//			payGold = tenCost;
//		} else {
//			if(!GuaJiTime.isSameDay(lastFreeTime,currentTime)) {
//				payGold = 0;
//				payTimes = 0;
//				status.setLastFreeTime(currentTime);
//			} else {
//				payGold = singleCost;
//			}
//		}
//		if (payGold > player.getGold()) {
//			// 钻石不足
//			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
//			return true;
//		}
//		// 扣除钻石
//		if (payGold > 0) {
//			player.consumeGold(payGold, Action.ACTIVITY146_CHOSEN_ONE);
//			ConsumeItems.valueOf(changeType.CHANGE_GOLD, payGold).pushChange(player);
//		}
//		List<AwardItems> awardsList = new ArrayList<AwardItems>();
//		//List<Integer> multiples = new ArrayList<Integer>();
//		// 执行抽奖逻辑
//		for (int i = 1; i <= searchTimes; i++) {
//			AwardItems awards = new AwardItems();
//			// 总次数
//			int totalTimes = status.getTotalTimes();
//			
//			int poolid = ReleaseURDropCfg146.POOL_TYPE_NONE;
//			if  ((SysBasicCfg.getInstance().getChosenOneGuarant()-1) == totalTimes) { // 第69次了
//				poolid = ReleaseURDropCfg146.POOL_TYPE_BOX;
//			} else { // 普通池
//				poolid = ReleaseURDropCfg146.POOL_TYPE_SEARCH;
//			}
//			ReleaseURDropCfg146 dropCfg = ReleaseURDropCfg146.RandomReward(poolid);
//			
//			if (dropCfg == null) {
//				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
//				return true;
//		    }
//			
//			// 掉落物品
//			List<ItemInfo> searchItems = ReleaseURDropCfg146.getDropItem(dropCfg.getId());
//			awards.addItemInfos(searchItems);
//			
//			awardsList.add(awards);
//		
//			status.incTotalTimes();
//			
//			if ((dropCfg.getLimitTimes() == 1)||(status.getTotalTimes() == SysBasicCfg.getInstance().getChosenOneGuarant())) {
//				status.setTotalTimes(0);
//			}
//			
//			awards.rewardTakeAffectAndPush(player, Action.ACTIVITY146_CHOSEN_ONE, 0);
//		}
//
//		Activity146Info.Builder builder = Activity146InfoHandler.generateInfo(player);
//
//		for (AwardItems item : awardsList) {
//			builder.addReward(item.toString());
//		}
//		
//		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY146_CHOSEN_DRAW_S_VALUE, builder));
//		// 更新玩家的活动数据
//		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
//		// BI 日志 (freeTimes 本次寻宝开始前今日剩余的免费次数)
//		BehaviorLogger.log4Platform(player, Action.ACTIVITY146_CHOSEN_ONE, Params.valueOf("searchTimes", searchTimes),
//		        Params.valueOf("freeTimes", searchTimes - payTimes), Params.valueOf("costGold", payGold), Params.valueOf("awards", awardsList));
//		return true;
	}

}
