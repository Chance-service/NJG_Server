package com.guaji.game.module.activity.activity152;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.HeroDramaReq;
import com.guaji.game.protocol.Activity4.HeroDramaRes;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/*** @author 作者 Ting Lin
* @version 创建时间：2023年6月15日
* 类说明
*/
public class Activity152Handler implements IProtocolHandler{
	static final int Hero_Drama_Gift_Sync = 0;
	static final int Hero_Get_Gift = 1;

	@Override // RELEASE_UR_INFO_C
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
				
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY152_DRAMA_GIFT_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		HeroDramaReq req = protocol.parseProtocol(HeroDramaReq.getDefaultInstance());
		int action = req.getAction();
		
		if ((action != Hero_Drama_Gift_Sync) && (action != Hero_Get_Gift)) {
			// 行為參數錯誤
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		
		Activity152Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), Activity152Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
						
		// 业务分支处理
		switch (action) {
		case Hero_Drama_Gift_Sync:
			SyncInfo(action,player,status);
			break;
		case Hero_Get_Gift:
			int itemId = req.getHeroId();
			GetDramaGift(action,player,timeCfg,status,itemId);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		
		return true;
	}
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(int action,Player player, Activity152Status status) {
		HeroDramaRes.Builder builder = getBuilder(action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY152_S_VALUE, builder));
	}
	/**
	 * 領取
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void GetDramaGift(int action,Player player,ActivityTimeCfg timeCfg,Activity152Status status, int itemId) {
		
		RoleEntity heroEntity = player.getPlayerData().getMercenaryByItemId(itemId);
		
		if (heroEntity == null || !heroEntity.isHero()) {
			// 此功能英雄獨佔
			player.sendError(HP.code.ACTIVITY152_C_VALUE, Status.error.ROLE_NOT_FOUND_VALUE);
			return ;
		}
		
		// 英雄未獲得
		if (heroEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE) {
			player.sendError(HP.code.ACTIVITY152_C_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		if (status.isAlreadyGot(itemId)) {
			player.sendError(HP.code.ACTIVITY152_C_VALUE, Status.error.AWARD_ALREADY_GOT_ERROR);
			return;
		}
		
		status.addHeroIds(itemId);
		
		int activityId = timeCfg.getActivityId();
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		AwardItems nowawardItems = new AwardItems();
		
		String awradStr = SysBasicCfg.getInstance().getHeroDramaGift();
		ItemInfo nowitemInfos = ItemInfo.valueOf(awradStr);
		nowawardItems.addItem(nowitemInfos);
		nowawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY152_GOT_HERO_DRAMA_GIFT, 0,TapDBSource.Hero_drama_Gift,Params.valueOf("itemId", itemId));
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY152_GOT_HERO_DRAMA_GIFT, 
				Params.valueOf("itemId", itemId),
		        Params.valueOf("awradStr", awradStr),
		        Params.valueOf("GotHeroId", status.getHeroIdList()));
		
		HeroDramaRes.Builder builder = getBuilder(action,status);
		builder.setReward(awradStr);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY152_S_VALUE, builder));
		
	}
	
	private static HeroDramaRes.Builder getBuilder(int action ,Activity152Status status) {
		// 返回包
		HeroDramaRes.Builder response = HeroDramaRes.newBuilder();
		
		response.setAction(action);
		List<Integer>aList = new ArrayList<Integer>(status.getHeroIdList());
		response.addAllGotHero(aList);
		
		return response;
	}
}
