package com.guaji.game.module.activity.activity172;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.GachaListCfg;
import com.guaji.game.config.PackBoxCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.DropItems.Item;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.ActivityCallDraw;
import com.guaji.game.protocol.Activity4.ActivityCallInfo;
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
public class Activity172Handler implements IProtocolHandler{
	static final int TIMES_TYPE_SINGLE = 1;
	static final int TIMES_TYPE_TEN = 10;

	@Override // RELEASE_UR_INFO_C
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY172_PICKUP_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		ActivityCallDraw req = protocol.parseProtocol(ActivityCallDraw.getDefaultInstance());
		int searchTimes = req.getTimes();
		int payTimes = searchTimes;
		if (searchTimes != TIMES_TYPE_SINGLE && searchTimes != TIMES_TYPE_TEN) {
			// 活动参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		
		Activity172Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), Activity172Status.class);
		// 使用免费抽奖的时间
		
		ItemInfo tenItemInfo =  ItemInfo.valueOf(SysBasicCfg.getInstance().getPickUpTenItemCost());
		ItemInfo itemInfo =  ItemInfo.valueOf(SysBasicCfg.getInstance().getPickUpItemCost());
		
		long lastFreeTime = status.getLastFreeTime();
		int singleCost = SysBasicCfg.getInstance().getChosenOneSingleCost();
		int tenCost = SysBasicCfg.getInstance().getChosenOneTenCost();
		// 当前系统时间
//		long currentTime = System.currentTimeMillis();
		// 计算实际花费钻石数量
		int payGold = 0;
		boolean isCost = false;
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		List<ItemInfo> itemInfoList = new ArrayList<>();//ItemInfo.valueListOf(costItems);
		if (searchTimes == TIMES_TYPE_TEN) {
			if (tenItemInfo != null) {
				itemInfoList.add(tenItemInfo);
			}
			isCost = consumeItems.addConsumeInfo(player.getPlayerData(),itemInfoList); 
			if (isCost && consumeItems.checkConsume(player)) { 
				if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY172_PICK_UP)) {
					payGold = tenCost;
				} 
			} else {
				payGold = tenCost;
			}
		} else {
//			if(!GuaJiTime.isSameDay(lastFreeTime,currentTime)) {
//				payGold = 0;
//				payTimes = 0;
//				status.setLastFreeTime(currentTime);
//			} else {
			if (itemInfo != null) {
				itemInfoList.add(itemInfo);
			}
			isCost = consumeItems.addConsumeInfo(player.getPlayerData(),itemInfoList); 
			if (isCost && consumeItems.checkConsume(player)) { 
				if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY172_PICK_UP)) {
					payGold = singleCost;
				} 
			} else {
				payGold = singleCost;
			}
//			}
		}
		
		// 扣除钻石
	    if (payGold > 0) {
			if (payGold > player.getGold()) {
				// 钻石不足
				player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
				return true;
			}
			player.consumeGold(payGold, Action.ACTIVITY172_PICK_UP);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, payGold).pushChange(player);
		}
		List<String> awardsList = new ArrayList<>();
		List<Integer> recordId = new ArrayList<Integer>();
		
		GachaListCfg gachaCfg = ConfigManager.getInstance().getConfigByKey(GachaListCfg.class, activityId);
		
		if (gachaCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}
		
		// 执行抽奖逻辑
		int boxId = 0;
		AwardItems awards = new AwardItems();
		
		for (int i = 1; i <= searchTimes; i++) {
			
			// 总次数
			int totalTimes = status.getTotalTimes();
			
			if  ((SysBasicCfg.getInstance().getChosenOneGuarant()-1) == totalTimes) { //第69次了
				boxId = gachaCfg.getRandomBoxId(GachaListCfg.BOX_TYPE_ASSURE);
			}else if ((searchTimes == TIMES_TYPE_TEN) && (i == 10)) { // 進入十抽池
				boxId = gachaCfg.getRandomBoxId(GachaListCfg.BOX_TYPE_TEN);
			} else {
				boxId = gachaCfg.getRandomBoxId(GachaListCfg.BOX_TYPE_SINGLE);
			}
			
			if (((SysBasicCfg.getInstance().getChosenOneGuarant()-1) == totalTimes) ||
				(gachaCfg.getPackBosList(GachaListCfg.BOX_TYPE_ASSURE).contains(boxId)))
			{
				status.setTotalTimes(0);
			} else {
				status.incTotalTimes();
			}
		
			
//			if 	(poolid == ReleaseURDropCfg172.POOL_TYPE_SEARCH) {
//				StateEntity stateEntity = player.getPlayerData().getStateEntity();
//				if (stateEntity.getRechargeluckey() != 0) { // 進入儲值幸運抽
//					int index = 10000;
//					try {
//						index = GuaJiRand.randInt(0, 10000);
//					} catch (MyException e) {
//						e.printStackTrace();
//					}
//					if (index <= stateEntity.getRechargeluckey()) { 
//						poolid = ReleaseURDropCfg172.POOL_TYPE_BOX;
//						stateEntity.setLuckyTime(GuaJiTime.getCalendar().getTime());
//						stateEntity.setRechargeluckey(0);
//					} else {
//						stateEntity.setRechargeluckey(stateEntity.getRechargeluckey()+SysBasicCfg.getInstance().getReChargeLucky(GsConst.RechargeLuckyType.ADD));
//					}
//					stateEntity.notifyUpdate(true);
//				} 
//			}
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
		ActivityCallInfo.Builder builder = Activity172InfoHandler.generateInfo(player);

		for (String itemStr : awardsList) {
			builder.addReward(itemStr);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY172_PICKUP_DRAW_S, builder));
		
		awards.rewardTakeAffectAndPush(player, Action.ACTIVITY172_PICK_UP, 0);
		
		// 更新玩家的活动数据
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.CALL_HERO,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(searchTimes);
		GsApp.getInstance().postMsg(hawkMsg);
		
		// BI 日志 (freeTimes 本次寻宝开始前今日剩余的免费次数)
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY172_PICK_UP, Params.valueOf("searchTimes", searchTimes),
				Params.valueOf("cfgId", recordId),Params.valueOf("freeTimes", searchTimes - payTimes), Params.valueOf("costGold", payGold), Params.valueOf("awards", awardsList));
		return true;
	}
}
