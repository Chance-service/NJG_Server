package com.guaji.game.module.activity.activity165;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.GoldMineCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.MiningActivityManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.Activity165MiningReq;
import com.guaji.game.protocol.Activity5.Activity165MiningRes;
import com.guaji.game.protocol.Activity5.Activity165RankItem;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.TapDBUtil.TapDBSource;

public class Activity165Handler implements IProtocolHandler {

    static final int Sync = 0;
    static final int Minging = 1;
    static final int RankInfo = 2;
    static final int MAXMINE = 16;

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY165_MINING_VALUE;
        ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (activityTimeCfg == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }
        // 剩余时间
        int leftTime = activityTimeCfg.calcActivitySurplusTime();
        if (leftTime <= 0) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        Activity165MiningReq req = protocol.parseProtocol(Activity165MiningReq.getDefaultInstance());
        
		int action = req.getAction();
				
		// 数据人错误
		if ( action < 0 || action > 1) {
			// 无效参数
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}

        Activity165Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                activityTimeCfg.getStageId(), Activity165Status.class);
        
       
        if (status == null) {
            player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
            return true;
        }
        
		// 业务分支处理
		switch (action) {
		case Sync:
			SyncInfo(action,player,status);
			break;
		case  Minging:
			StartMining(protocol,action,player,status);
			break;
		case RankInfo:
			GetRankInfo(action,player,status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
        
    }
    
	private static void StartMining(Protocol protocol,int action,Player player, Activity165Status status) {
		
		Activity165MiningReq req = protocol.parseProtocol(Activity165MiningReq.getDefaultInstance());
		
		GoldMineCfg cfg = GoldMineCfg.getGoldMineCfgByLv(player.getLevel());
		
		if (cfg == null) {
			player.sendError(protocol.getType(),Status.error.CONFIG_NOT_FOUND_VALUE); 
			return;
		}
		
		List<Integer> mineList =  req.getMineIdList();
		
		if ((mineList == null) || (mineList.size() == 0) || ((mineList.size()+status.getMineSet().size()) > MAXMINE)) {
			 player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			 return;
		}
		
		// 去重
		LinkedHashSet<Integer> hashSet = new LinkedHashSet<>(mineList);
		
		for (int mid :hashSet) {
			// 有區塊開採過
			if (status.getMineSet().contains(mid)){
				 player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				 return;
			}
			// 開採區域不合法
			if ((mid == 0) || (mid > MAXMINE)) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
		}
		
		int count = hashSet.size();
		
		String costItems = SysBasicCfg.getInstance().getGoldMineCost();
		
		if (costItems.isEmpty()) {
			player.sendError(protocol.getType(),Status.error.ITEM_NOT_FOUND_VALUE); 
			return;
		}
		
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		ItemInfo itemInfo = ItemInfo.valueOf(costItems);
		itemInfo.setQuantity(itemInfo.getQuantity()*count);
		
		List<ItemInfo> itemList = new ArrayList<ItemInfo>();
		itemList.add(itemInfo);
		boolean isAdd = false ;
		isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
		
		if (isAdd && consumeItems.checkConsume(player)) {
			if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY165_MINING)) {
				player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			} 
		} else {
			player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
			return; 
		}
					
		// 下发奖励
		String allaward = "";
		for (int mId : hashSet) {
			status.setMineSet(mId);
 			if (allaward.isEmpty()) {
 				allaward = cfg.getRandomAwardStr();
 			} else {
 				allaward = allaward+","+cfg.getRandomAwardStr();
 			}
		}
		

		
		AwardItems awards = AwardItems.valueOf(allaward);
		awards.rewardTakeAffectAndPush(player, Action.ACTIVITY165_MINING, 2);
		
		// 挖光了
		if (status.getMineSet().size() >= MAXMINE) {
			int score = 1;
			status.setScore(status.getScore() + score);
      
			if (GuaJiTime.getMillisecond() >= status.getScoreTime()) {
				// 修改时间
				long calcTime = GuaJiTime.getNextWeekAM0Date();
				status.setScoreTime(calcTime);
			}

			// 更新排行
			MiningActivityManager.getInstance().updateRankSet(player.getPlayerData());

			if (status.getScore() > 0) {
				Msg msg = Msg.valueOf(GsConst.MsgType.MINING_RANK_ADD_SCORE,
						GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.Mining_RANK_MANAGER));
				msg.pushParam(player.getPlayerData());
				GsApp.getInstance().postMsg(msg);
			}
			status.clearMinSet();
		}
		
        // 更新status
		int activityId = Const.ActivityId.ACTIVITY165_MINING_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		Activity165MiningRes.Builder response =  getBuilder(action,status);
		response.addAllGotId(status.getMineSet());
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY165_MINING_S, response));
		
//		BehaviorLogger.log4Platform(player, Action.ACTIVITY164_GROWTH_TW, Params.valueOf("cfgId", cfgId),
//				Params.valueOf("free", free),
//				Params.valueOf("cost", cost),
//				Params.valueOf("allaward", allaward));

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY165_MINING,Params.valueOf("cfgId", cfg.getId()),
				Params.valueOf("mineList", mineList),
				Params.valueOf("count", count),
				Params.valueOf("allaward", allaward));
	}    
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(int action,Player player, Activity165Status status) {
		Activity165MiningRes.Builder builder = getBuilder(action,status);
		builder.addAllGotId(status.getMineSet());
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY165_MINING_S, builder));
	}
	
	/**
	 * 打包訊息
	 * @param action
	 * @param status
	 * @return
	 */
	private static Activity165MiningRes.Builder getBuilder(int action,Activity165Status status) {
		// 返回包
		Activity165MiningRes.Builder response = Activity165MiningRes.newBuilder();
		
		response.setAction(action);
		response.setScore(status.getScore());		
		return response;
	}
	
	private static void GetRankInfo(int action,Player player, Activity165Status status) {
        // 获取排名
		Activity165MiningRes.Builder builder =  getBuilder(action,status);
        List<Activity165RankItem.Builder> rankItemList = MiningActivityManager.getInstance().getRankTop(MiningActivityManager.getInstance().getMaxDisplayNum());
        Activity165RankItem.Builder ownRankItem = Activity165RankItem.newBuilder();
        ownRankItem.setRank(MiningActivityManager.getInstance().getPlayerRank(player.getId()));
        ownRankItem.setScore(status.getScore());
        ownRankItem.setPlayerId(player.getId());
        ownRankItem.setName(player.getName());
        builder.setOwnItem(ownRankItem);
        for (Activity165RankItem.Builder item : rankItemList) {
            builder.addItem(item);
        }
        player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY165_MINING_S, builder));
	}
}
