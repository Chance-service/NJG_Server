package com.guaji.game.module.activity.activity197;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.GachaListCfg;
import com.guaji.game.config.PackBoxCfg;
import com.guaji.game.config.pickUpGacha_listCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.DropItems.Item;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity6.SuperPickUpDraw;
import com.guaji.game.protocol.Activity6.SuperPickUpList;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;

/*** @author 作者 Ting Lin
* @version 创建时间：2023年5月8日
* 类说明
*/
public class Activity197Handler implements IProtocolHandler{
	static final int TIMES_TYPE_SINGLE = 1;
	static final int TIMES_TYPE_TEN = 10;

	@Override // RELEASE_UR_INFO_C
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY197_SUPER_PICKUP_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		SuperPickUpDraw req = protocol.parseProtocol(SuperPickUpDraw.getDefaultInstance());
		int Id = req.getId();
		int searchTimes = req.getTimes();
		int payTimes = searchTimes;
		if (searchTimes != TIMES_TYPE_SINGLE && searchTimes != TIMES_TYPE_TEN) {
			// 活动参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		
		pickUpGacha_listCfg gachaCfg = ConfigManager.getInstance().getConfigByKey(pickUpGacha_listCfg.class, Id);
		
		if (gachaCfg == null) {
			// config 有誤
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}
		
		if (!gachaCfg.isActive()) {
			// 活动池關閉
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		Activity197Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), Activity197Status.class);

		if (status == null) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		
		if (status.checkDailyClear()) {
			// 更新玩家的活动数据
			player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		}
		
		ItemInfo tenItemInfo = null;
		ItemInfo itemInfo =  null;
		boolean useTicket = (!gachaCfg.getTicket().isEmpty()); 
		
		if (useTicket) {
			itemInfo =  ItemInfo.valueOf(gachaCfg.getTicket());
			tenItemInfo = new  ItemInfo(itemInfo.getType(),itemInfo.getItemId(),itemInfo.getQuantity()*10);	
		}
		
		//long lastFreeTime = status.getLastFreeTime();
		int singleCost = gachaCfg.getSingleCost();
		int tenCost = gachaCfg.getTenCost();
		// 当前系统时间
		// long currentTime = System.currentTimeMillis();
		// 计算实际花费钻石数量
		int payGold = 0;
		boolean isCost = false;
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		List<ItemInfo> itemInfoList = new ArrayList<>();//ItemInfo.valueListOf(costItems);
		if (searchTimes == TIMES_TYPE_TEN) {
			if (useTicket) {
				
				itemInfoList.add(tenItemInfo);
				
				isCost = consumeItems.addConsumeInfo(player.getPlayerData(),itemInfoList); 
				if (isCost && consumeItems.checkConsume(player)) { 
					if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY197_SUPER_PICKUP)) {
						payGold = tenCost;
					} 
				} else {
					payGold = tenCost;
				}
			} else {
				payGold = tenCost;
			}
		} else {
			if(status.getUsefree(Id) < gachaCfg.getFreeCount()) { // 使用免費
				payGold = 0;
				payTimes = 0;
				status.incUsefree(Id);
			} else { // 使用券
				if (useTicket) {
					
					itemInfoList.add(itemInfo);
				
					isCost = consumeItems.addConsumeInfo(player.getPlayerData(),itemInfoList); 
					if (isCost && consumeItems.checkConsume(player)) { 
						if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY197_SUPER_PICKUP)) {
							payGold = singleCost;
						} 
					} else {
						payGold = singleCost;
					}
				} else {
					payGold = singleCost;
				}
			}
		}
		
		// 扣除钻石
	    if (payGold > 0) {
			if (payGold > player.getGold()) {
				// 钻石不足
				player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
				return true;
			}
			player.consumeGold(payGold, Action.ACTIVITY197_SUPER_PICKUP);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, payGold).pushChange(player);
		}
		List<String> awardsList = new ArrayList<>();
		List<Integer> recordId = new ArrayList<Integer>();
				
		// 执行抽奖逻辑
		int boxId = 0;
		AwardItems awards = new AwardItems();
		for (int i = 1; i <= searchTimes; i++) {
			
			// 总次数
			int totalTimes = status.getCounter(Id);
			
			if  ((gachaCfg.getGuarant()-1) == totalTimes) { //第69次了
				boxId = gachaCfg.getRandomBoxId(GachaListCfg.BOX_TYPE_ASSURE);
			}else if ((searchTimes == TIMES_TYPE_TEN) && (i == 10)) { // 進入十抽池
				boxId = gachaCfg.getRandomBoxId(GachaListCfg.BOX_TYPE_TEN);
			} else {
				boxId = gachaCfg.getRandomBoxId(GachaListCfg.BOX_TYPE_SINGLE);
			}
			
			if (gachaCfg.getGuarant() > 0) {
				if (((gachaCfg.getGuarant()-1) == totalTimes) ||
					(gachaCfg.getPackBosList(GachaListCfg.BOX_TYPE_ASSURE).contains(boxId)))
				{
					status.clearCounter(Id);
				} else {
					status.incCounter(Id);
				}
			}
		
			PackBoxCfg dropCfg = null;

			dropCfg = ConfigManager.getInstance().getConfigByKey(PackBoxCfg.class, boxId);
			
			if (dropCfg == null) {
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
				return true;
		    }
			// 掉落物品
			Item boxItem = dropCfg.getBoxDropItems().calcDropByOnly();
			
			awards.addItem(boxItem);
			awardsList.add(boxItem.toString());
			recordId.add(dropCfg.getId());
		
			// 抽獎禮,有抽有送
			ItemInfo gachaItem = ItemInfo.valueOf(gachaCfg.getGachaGift());
			if (gachaItem != null) {
				awards.addItem(gachaItem);;
			}
		}
		
		awards.rewardTakeAffectAndPush(player, Action.ACTIVITY197_SUPER_PICKUP, 0);
		
		// 更新玩家的活动数据
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		SuperPickUpList.Builder builder = Activity197InfoHandler.generateInfo(player,timeCfg,Id,status,awardsList);
				
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY197_SUPER_PICKUP_DRAW_S, builder));
		
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.CALL_HERO,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(searchTimes);
		GsApp.getInstance().postMsg(hawkMsg);
		
		// BI 日志 (freeTimes 本次寻宝开始前今日剩余的免费次数)
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY197_SUPER_PICKUP, Params.valueOf("searchTimes", searchTimes),Params.valueOf("Id", Id),
				Params.valueOf("packId", recordId),Params.valueOf("freeTimes", searchTimes - payTimes), Params.valueOf("costGold", payGold), Params.valueOf("awards", awardsList));
		return true;
	}
}
