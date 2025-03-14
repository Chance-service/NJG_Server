package com.guaji.game.module.activity.activity141;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;
import com.guaji.game.protocol.Activity3.Activity141RichManRep;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RichManDropCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 万圣节活动管理类
 */
public class Activity141RichManManager {

	private static final int OPERATE_SYNC = 0;// 同步
	private static final int OPERATE_ROLL_DICE = 1;// 擲骰
	private static final int OPERATE_ROUN_BONUS = 5;	//每5圈多給bonus
	private static final int POOL_TYPE_DOUBLE = 4; // 獎勵加倍

	/**
	 * 同步
	 */
	static void sync(Player player, ActivityTimeCfg timeCfg, Activity141Status status) {
		Activity141RichManRep.Builder response = getBuilder(player, timeCfg, status);
		if (null != response) {
			response.setType(OPERATE_SYNC);
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY141_S_VALUE, response));
		}
	}

	/**
	 * 擲骰
	 */
	static void roll(Player player, ActivityTimeCfg timeCfg, Activity141Status status) {

		// 如果活动进入展示期，只能同步和兑换
		if (timeCfg.isEnd()) {
			player.sendError(HP.code.ACTIVITY141_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return;
		}
		// 获取消耗信息;
		String itemStr = SysBasicCfg.getInstance().getRichManUseItem();
		if (itemStr == null || itemStr.isEmpty()) {
			player.sendError(HP.code.ACTIVITY141_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}
		// 检测擲骰条件
		/** 单次擲骰消耗 **/
		String costItems = itemStr;
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		List<ItemInfo> itemInfoList = ItemInfo.valueListOf(costItems);

		// boolean isFree = false;

		/** 优先判断抽奖券消耗 **/
		
		boolean isCost; 
		isCost = consumeItems.addConsumeInfo(player.getPlayerData(),itemInfoList); 
		if (isCost && consumeItems.checkConsume(player)) { 
			if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY141_RICHMAN)) {
				player.sendError(HP.code.ACTIVITY141_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			} 
		} else {
			player.sendError(HP.code.ACTIVITY141_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
			return; 
		}

		// 随机物品，进入特殊奖池和普通奖池的历史记录是分开计算
		try {
			int dice = GuaJiRand.randInt(1,6);
			List<RichManDropCfg> cfgList = RichManDropCfg.dropReward(status, dice);
			// 发奖
			List<AwardItems> awardItemsList = new ArrayList<AwardItems>();
			  
			for (RichManDropCfg cfg : cfgList) {
				if (cfg.getPoolId() == POOL_TYPE_DOUBLE)
				{
					status.setisdouble(true);
					continue;
				}
				
				String reward = cfg.getRewards(); 
				if(StringUtils.isEmpty(reward)) {
					Log.errPrintln("activity141DropCfg reward is null"); 
					  continue; 
				}
				  
				List<ItemInfo> itemInfos = ItemInfo.valueListOf(reward); 
				for (Iterator<ItemInfo> iterator = itemInfos.iterator(); iterator.hasNext();) {
					ItemInfo itemInfo = iterator.next();
					int multiple = Math.min(20, status.getfinish()/OPERATE_ROUN_BONUS);
					//圈數額外加乘
					if (multiple > 0)
					{
						int bonus = Integer.valueOf(cfg.getLimitTimes(multiple-1));
						itemInfo.setQuantity(itemInfo.getQuantity() + bonus);
					}
					//加倍
					if(status.isdouble())
					{
						status.setisdouble(false);
						itemInfo.setQuantity(itemInfo.getQuantity() * 2);
					}
				}
				AwardItems awardItems = new AwardItems(); 
				awardItems.addItemInfos(itemInfos);
				awardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY141_RICHMAN, 0);
					  
				awardItemsList.add(awardItems); 			
			}
			// 更新活动实体
			player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY141_RICHMAN_VALUE, timeCfg.getStageId());
			// 返回消息包
			Activity141RichManRep.Builder response = getBuilder(player, timeCfg, status);
			if (null != response) {
				response.setType(OPERATE_ROLL_DICE);
				response.setStep(dice);
				for (AwardItems item : awardItemsList) {
					response.addReward(item.toString());
				}
				player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY141_S_VALUE, response));
			}
			// BI日志
			BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY141_RICHMAN, Params.valueOf("dice", dice));
		  }catch (Exception e) {
				MyException.catchException(e);
		  }	 
	}

	/**
	 * 是否免费抽奖
	 */
	/*
	 * public static boolean canFreeDraw(HalloweenStatus status, HalloweenConstCfg
	 * constCfg) { int freeCD = getFreeCD(status, constCfg); if (freeCD == 0) return
	 * true; return false; }
	 */

	/**
	 * 清空每日兑换数据
	 */
	/*
	 * public static void initExchangeMap(HalloweenStatus status) { Map<Object,
	 * HalloweenExchangeCfg> exchangeCfgMap =
	 * ConfigManager.getInstance().getConfigMap(HalloweenExchangeCfg.class);
	 * Map<Integer, Integer> map = status.getExchangeMap(); for
	 * (HalloweenExchangeCfg cfg : exchangeCfgMap.values()) { map.put(cfg.getId(),
	 * 0); } }
	 */

	/**
	 * 获取免费抽奖CD时间
	 */
	/*
	 * private static int getFreeCD(HalloweenStatus status, HalloweenConstCfg
	 * constCfg) { int lastFreeTime = status.getLastFreeTime(); int freeConst =
	 * constCfg.getFreeCD() * 3600; int now = GuaJiTime.getSeconds(); int freeCD =
	 * (int) Math.max(freeConst - (now - lastFreeTime), 0); return freeCD; }
	 */

	/**
	 * 获取返回结构体
	 */
	static Activity141RichManRep.Builder getBuilder(Player player, ActivityTimeCfg timeCfg, Activity141Status status) {
		List<RichManDropCfg> cfgList = RichManDropCfg.getfinishReward();
		List<AwardItems> awardItemsList = new ArrayList<AwardItems>();
		  
		for (RichManDropCfg cfg : cfgList) {
			
			String reward = cfg.getRewards(); 
			if(StringUtils.isEmpty(reward)) {
				Log.errPrintln("activity141DropCfg reward is null"); 
				  continue; 
			}
			  
			List<ItemInfo> itemInfos = ItemInfo.valueListOf(reward); 
			for (Iterator<ItemInfo> iterator = itemInfos.iterator(); iterator.hasNext();) {
				ItemInfo itemInfo = iterator.next();
				int multiple = Math.min(20, (status.getfinish()+1)/OPERATE_ROUN_BONUS);
				//圈數額外加乘
				if (multiple > 0)
				{
					int bonus = Integer.valueOf(cfg.getLimitTimes(multiple-1));
					itemInfo.setQuantity(itemInfo.getQuantity() + bonus);
				}
			}
			AwardItems awardItems = new AwardItems(); 
			awardItems.addItemInfos(itemInfos);
			awardItemsList.add(awardItems); 			
		}

		// 返回包
		Activity141RichManRep.Builder response = Activity141RichManRep.newBuilder();
		response.setIndex(status.getIndex());
		response.setFinish(status.getfinish());
		response.setDouble(status.isdouble());
		response.setFree(status.getfree());
		response.setStep(0);
		for (AwardItems item : awardItemsList) {
			response.addFinishreward(item.toString());
		}

		return response;
	}
}
