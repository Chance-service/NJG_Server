package com.guaji.game.module.activity.activity143;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;
import com.guaji.game.protocol.Activity3.Activity143PirateRep;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.PirateBoxDropCfg;
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
 * 海盜活动管理类
 */
public class Activity143PirateManager {

	private static final int OPERATE_SYNC = 0;// 同步
	private static final int OPERATE_OPEN_BOX = 1;  //	開箱
	private static final int OPERATE_Get_REWARD = 2; //	領獎

	private static final int OPERATE_GIVEUP = 3; 	//	放棄

	/**
	 * 同步
	 */
	static void sync(Player player, ActivityTimeCfg timeCfg, Activity143Status status) {
		Activity143PirateRep.Builder response = getBuilder(player, timeCfg, status);
		if (null != response) {
			response.setType(OPERATE_SYNC);
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY143_S_VALUE, response));
		}
	}

	/**
	 * 開箱
	 */
	static void openbox(Player player, ActivityTimeCfg timeCfg, Activity143Status status, int ransom) {

		// 如果活动进入展示期，只能同步和兑换
		if (timeCfg.isEnd()) {
			player.sendError(HP.code.ACTIVITY143_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return;
		}
		// 获取消耗信息;
		String itemStr = SysBasicCfg.getInstance().getPirateUseItem();
		if (itemStr == null || itemStr.isEmpty()) {
			player.sendError(HP.code.ACTIVITY143_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}
		// 检测擲骰条件
		/** 单次擲骰消耗 **/
		String costItems = itemStr;
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		List<ItemInfo> itemInfoList = ItemInfo.valueListOf(costItems);

		// boolean isFree = false;

		/** 优先判断抽奖券消耗 **/
		
		if ((status.getlevel() <= 0) || (status.getlevel() > 20)) { // 還未開局
			boolean isCost; 
			isCost = consumeItems.addConsumeInfo(player.getPlayerData(),itemInfoList); 
			if (isCost && consumeItems.checkConsume(player)) { 
				if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY143_PIRATE)) {
					player.sendError(HP.code.ACTIVITY143_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
					return; 
				} 
			} else {
				player.sendError(HP.code.ACTIVITY143_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			}
			status.setlevel(1); // 開局
			status.setisfail(false);
			status.setrenew(false);
			status.setrewards("");
			// 更新活动实体
			player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY143_PIRATE_VALUE, timeCfg.getStageId());
			
			BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY143_PIRATE,Params.valueOf("level", status.getlevel()),
					Params.valueOf("openBox", "initBox"));
			// 返回消息包
			Activity143PirateRep.Builder response = getBuilder(player, timeCfg, status);
			if (null != response) {
				response.setType(OPERATE_OPEN_BOX);
				response.clearOwnreward();
				response.clearReward();
				
				player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY143_S_VALUE, response));
			}
			return;
		}
		int costMoney =0;
		int costGold = 0;
		if (status.getisfail()) {
			if (ransom <= 0 || ransom > 2) { // 無此操作
				return;
			} else if (ransom == 1) { // by coin
				
	            costMoney = PirateBoxDropCfg.getLevelCfg(status.getlevel()).getCoinConsume();

	            // 验证金币消耗
	            if (player.getCoin() < costMoney) {
	                player.sendError(HP.code.ACTIVITY143_C_VALUE, Status.error.COINS_NOT_ENOUGH);
	                return ;
	            }

	            //消耗金币
	            player.consumeCoin(costMoney,Action.ACTIVITY143_PIRATE);
	            ConsumeItems.valueOf(Const.changeType.CHANGE_COIN, costMoney).pushChange(player);				
			} else if (ransom == 2) { // by diamond 
	            costGold = PirateBoxDropCfg.getLevelCfg(status.getlevel()).getDiamondConsume();
	            
	            if (costGold > player.getGold()) {
	                player.sendError(HP.code.ACTIVITY143_C_VALUE,Status.error.GOLD_NOT_ENOUGH_VALUE); 
	                return ;
	            }
	            // 扣除钻石
	            if (costGold > 0) {
	                player.consumeGold(costGold, Action.ACTIVITY143_PIRATE);
	                ConsumeItems.valueOf(Const.changeType.CHANGE_GOLD, costGold).pushChange(player);
	            }
			}
			status.setisfail(false);
			status.setrenew(true);
			// 更新活动实体
			player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY143_PIRATE_VALUE, timeCfg.getStageId());
			
			BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY143_PIRATE,Params.valueOf("level", status.getlevel()),
					Params.valueOf("ransom", ransom),
					Params.valueOf("costMoney", costMoney),
					Params.valueOf("costGold", costGold),
					Params.valueOf("renew", status.getrenew()));
					
			// 返回消息包
			Activity143PirateRep.Builder response = getBuilder(player, timeCfg, status);
			if (null != response) {
				response.setType(OPERATE_OPEN_BOX);

				player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY143_S_VALUE, response));
			}
			return;
		}
		// 随机物品，进入特殊奖池和普通奖池的历史记录是分开计算
		try {
			PirateBoxDropCfg levelcfg = PirateBoxDropCfg.getLevelCfg(status.getlevel());
			boolean renew = status.getrenew();
			String nowreward = levelcfg.RandomReward(renew);
			AwardItems nowawardItems = null;
			AwardItems ownawardItems = new AwardItems();
			if(StringUtils.isEmpty(nowreward)) {
				Log.errPrintln("activity143PirateCfg reward is null");
			}
			
			int oldLevel = status.getlevel();
			String dbreward = "";
			if (nowreward.equals("0")){
				status.setisfail(true);
			}else {

				// 本次取得獎勵
				ItemInfo nowitemInfos = ItemInfo.valueOf(nowreward);
				nowawardItems = new AwardItems(); 
				nowawardItems.addItem(nowitemInfos);
				// 加入擁有獎勵
				if (!status.getrewards().isEmpty()) { // 需要用加入的
					List<ItemInfo> ownitemInfos = ItemInfo.valueListOf(status.getrewards()); 
					ownawardItems.addItemInfos(ownitemInfos); // 原本的物品
					ownawardItems.addItem(nowitemInfos);	// 本次物品
				} else { //原本沒東西就覆蓋的
					ownawardItems.addItem(nowitemInfos);
				}
				dbreward = ownawardItems.toString();
				status.setrewards(dbreward);
				status.setlevel(status.getlevel() + 1);
				status.setrenew(false);
			}
			  
			// 更新活动实体
			player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY143_PIRATE_VALUE, timeCfg.getStageId());
			
			BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY143_PIRATE,Params.valueOf("oldLevel", oldLevel),
					Params.valueOf("level", status.getlevel()),
					Params.valueOf("fail", status.getisfail()),
					Params.valueOf("reward", dbreward));
			// 返回消息包
			Activity143PirateRep.Builder response = getBuilder(player, timeCfg, status);
			if (null != response) {
				response.setType(OPERATE_OPEN_BOX);
				response.clearReward();
				if (nowawardItems != null)
					response.addReward(nowawardItems.toString());

				player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY143_S_VALUE, response));
			}
			
		  }catch (Exception e) {
				MyException.catchException(e);
		  }	 
	}

	/**
	 * 获取返回结构体
	 */
	static Activity143PirateRep.Builder getBuilder(Player player, ActivityTimeCfg timeCfg, Activity143Status status) {
		List<AwardItems> awardItemsList = new ArrayList<AwardItems>();
		
		if (StringUtils.isNotEmpty(status.getrewards())){
			List<ItemInfo> itemInfos = ItemInfo.valueListOf(status.getrewards());
			AwardItems awardItems = new AwardItems(); 
			awardItems.addItemInfos(itemInfos);
			awardItemsList.add(awardItems);
		}
		
		// 返回包
		Activity143PirateRep.Builder response = Activity143PirateRep.newBuilder();
		response.setLevel(status.getlevel());
		response.setFree(status.getfree());
		response.setIsfail(status.getisfail());
		response.setRenew(status.getrenew());
		for (AwardItems item : awardItemsList) {
			response.addOwnreward(item.toString());
		}

		return response;
	}
	/**
	 * 領取獎勵
	 */
	static void takegift(Player player, ActivityTimeCfg timeCfg, Activity143Status status) {
		// 如果活动进入展示期，只能同步和兑换
		// 失敗不能兌換
		if (status.getisfail()) {
			//player.sendError(HP.code.ACTIVITY143_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return ;
		}
		// 有獎品才能兌換
		String rewardStr = status.getrewards();
		AwardItems ownawardItems = new AwardItems();
		if (!rewardStr.isEmpty()) {
			List<ItemInfo> ownitemInfos = ItemInfo.valueListOf(rewardStr); 
			ownawardItems.addItemInfos(ownitemInfos); // 獲得的物品
			ownawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY143_PIRATE, 0);
			status.setlevel(0);
			//status.setfree(0);
			status.setisfail(false);
			status.setrenew(false);
			status.setrewards("");
			
			player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY143_PIRATE_VALUE, timeCfg.getStageId());
			// BI日志
			BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY143_PIRATE,
					Params.valueOf("takegift", "takegift"),
					Params.valueOf("reward", rewardStr));
			// 返回消息包
			Activity143PirateRep.Builder response = getBuilder(player, timeCfg, status);
			if (null != response) {
				response.setType(OPERATE_Get_REWARD);
				response.clearReward();
				
				player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY143_S_VALUE, response));
			}
		}
	}
	/**
	 * 放棄
	 */
	static void giveup(Player player, ActivityTimeCfg timeCfg, Activity143Status status) {
		// 如果活动进入展示期，只能同步和兑换
		if (timeCfg.isEnd()) {
			//player.sendError(HP.code.ACTIVITY143_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return ;
		}
		// 失敗才有放棄選項
		int oldLevel = status.getlevel();
		String rewardStr = status.getrewards();
		if (status.getisfail()) {
			
			status.setlevel(0);
			//status.setfree(0);
			status.setisfail(false);
			status.setrewards("");
			// 更新活动实体
			player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY143_PIRATE_VALUE, timeCfg.getStageId());
			// BI日志
			BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY143_PIRATE,
					Params.valueOf("giveup", "giveup"),
					Params.valueOf("Level", oldLevel),
					Params.valueOf("CancelReward", rewardStr));
			// 返回消息包
			Activity143PirateRep.Builder response = getBuilder(player, timeCfg, status);
			if (null != response) {
				response.setType(OPERATE_GIVEUP);
				response.clearReward();
				
				player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY143_S_VALUE, response));
			}
		}
	}
}
