package com.guaji.game.module;

import java.util.Date;
import java.util.Map;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.protocol.DailyQuest.DailyPointCore;
import com.guaji.game.protocol.DailyQuest.HPDailyQuestInfo;
import com.guaji.game.protocol.DailyQuest.HPDailyQuestInfoRet;
import com.guaji.game.protocol.DailyQuest.HPTakeDailyPointAward;
import com.guaji.game.protocol.DailyQuest.HPTakeDailyPointAwardRet;
import com.guaji.game.protocol.DailyQuest.HPTakeDailyQuestAward;
import com.guaji.game.protocol.DailyQuest.QuestItem;
import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.DailyQuestCfg;
import com.guaji.game.config.DailyQuestPointCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.DailyQuestEntity;
import com.guaji.game.entity.DailyQuestItem;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.module.activity.ActiveCompliance.ActiveStatus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.TapDBUtil.TapDBSource;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class PlayerDailyQuestModule extends PlayerModule {

	private Date lastDate;

	public PlayerDailyQuestModule(Player player) {
		super(player);

		listenProto(HP.code.DAILY_QUEST_INFO_C_VALUE);// 请求所有任务信息
		listenProto(HP.code.TAKE_DAILY_QUEST_AWARD_C_VALUE);// 某个任务完成奖励
		listenProto(HP.code.TAKE_DAILY_QUEST_POINT_AWARD_C_VALUE);
		
		listenMsg(GsConst.MsgType.DailyQuestMsg.LOGIN_DAY);
		listenMsg(GsConst.MsgType.DailyQuestMsg.ON_RECHARGE);
		listenMsg(GsConst.MsgType.DailyQuestMsg.ON_RECHARGE_LIMIT);
		listenMsg(GsConst.MsgType.DailyQuestMsg.FAST_FIGHT);
		//listenMsg(GsConst.MsgType.DailyQuestMsg.FACE_BOOK_SHARE);
		listenMsg(GsConst.MsgType.DailyQuestMsg.ELITE_MISSION_WIN);
		//listenMsg(GsConst.MsgType.DailyQuestMsg.NOR_MISSION_WIN);
		listenMsg(GsConst.MsgType.DailyQuestMsg.ROLE_UPGRADE_STAR);
		listenMsg(GsConst.MsgType.DailyQuestMsg.JING_JI_CHANG_FIGHT);
		listenMsg(GsConst.MsgType.DailyQuestMsg.EQUIP_ENHANCE);
		listenMsg(GsConst.MsgType.DailyQuestMsg.SMELT_EQUIP);
		listenMsg(GsConst.MsgType.DailyQuestMsg.ROLE_EXPEDITION_COUNT);
		listenMsg(GsConst.MsgType.DailyQuestMsg.EIGHTEENPRINCESCHANGE);
		listenMsg(GsConst.MsgType.DailyQuestMsg.ROLE_UPGRADE_LEVEL);
		listenMsg(GsConst.MsgType.DailyQuestMsg.EQUIP_FORGE);
		listenMsg(GsConst.MsgType.DailyQuestMsg.TAKE_FIGHT_AWARD);
		listenMsg(GsConst.MsgType.DailyQuestMsg.BADGE_FUSION);
		listenMsg(GsConst.MsgType.DailyQuestMsg.MONEY_COLLETION);
		listenMsg(GsConst.MsgType.DailyQuestMsg.WORLD_SPEAK);
		listenMsg(GsConst.MsgType.DailyQuestMsg.GIVE_FIRENDSHIP);
		listenMsg(GsConst.MsgType.DailyQuestMsg.CALL_HERO);
		// for glory hole
//		listenMsg(GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_JOIN_TIMES);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_LOGIN_DAY);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_TAKE_FIGHT_AWARD);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_GIVE_FIRENDSHIP);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_SCORE);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_EQUIP_FORGE);

	}

	@Override
	public boolean onTick() {

		Date curDate = GuaJiTime.getCalendar().getTime();
		// 在线跨天
		if (!GuaJiTime.isSameDay(lastDate.getTime(), curDate.getTime())) {
			DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();

			if (dailyQuestEntity.getDailyQuestMap().size() == 0 || dailyQuestEntity.getDailyQuestMap() == null) {
				initDailyQuest();
				dailyQuestEntity.reConvert();
				dailyQuestEntity.notifyUpdate();
			}
			// 活跃度达标过期重置
//			ActiveStatus activeStatus = ActivityUtil.getActiveComplianceStatus(player.getPlayerData());
//			if (activeStatus != null && activeStatus.calcActivitySurplusTime() <= 0) {
//				int activityId = Const.ActivityId.ACTIVECOMPLIANCE_VALUE;
//				ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
//				if (activityTimeCfg != null) {
//					activeStatus.refresh();
//					player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
//				}
//			}

		}

		return super.onTick();
	}

	@Override
	public boolean onMessage(Msg msg) {
		if ((msg.getMsg() == GsConst.MsgType.DailyQuestMsg.LOGIN_DAY)||
			//(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.ELITE_MISSION_WIN)||
			//(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.ELITE_MISSION_WIN)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.FAST_FIGHT)||
			//(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.FACE_BOOK_SHARE)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.ELITE_MISSION_WIN)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.NOR_MISSION_WIN)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.ROLE_UPGRADE_STAR)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.JING_JI_CHANG_FIGHT)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.EQUIP_ENHANCE)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.SMELT_EQUIP)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.ROLE_EXPEDITION_COUNT)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.ROLE_UPGRADE_LEVEL)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.EQUIP_FORGE)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.TAKE_FIGHT_AWARD)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.BADGE_FUSION)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.MONEY_COLLETION)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.WORLD_SPEAK) ||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.GIVE_FIRENDSHIP)||
			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.CALL_HERO)) {
//			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_JOIN_TIMES)||
//			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_LOGIN_DAY)||
//			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_TAKE_FIGHT_AWARD)||
//			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_GIVE_FIRENDSHIP) ||
//			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_SCORE)||
//			(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_EQUIP_FORGE)){
			onDailyQuestCount(msg);
			return true;
		} else if ((msg.getMsg() == GsConst.MsgType.DailyQuestMsg.ON_RECHARGE)||
				(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.ON_RECHARGE_LIMIT)){
			onRecharge(msg);
			return true;
		}else if ((msg.getMsg() == GsConst.MsgType.DailyQuestMsg.FACE_BOOK_SHARE)||
				(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.EIGHTEENPRINCESCHANGE)){
			onDailyQuestComplete(msg);
			return true;
		}
		
		return super.onMessage(msg);
	}
	/**
	 * 計數數量達成
	 * @param msg
	 * @return
	 */
	private boolean onDailyQuestCount(Msg msg) {
		int count = (Integer) msg.getParams().get(0);

		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();

		Map<Integer, DailyQuestItem> map = dailyQuestEntity.getDailyQuestMap();

		for (Map.Entry<Integer, DailyQuestItem> entry : map.entrySet()) {
			int id = entry.getValue().getId();
			DailyQuestCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(DailyQuestCfg.class, id);

			if (dailyQuestCfg == null) {
				continue;
			}
			
			int QuestType = msg.getMsg() % GsConst.MsgType.DailyQuestMsg.MSG_BASE;

			if (dailyQuestCfg.getType() == QuestType)
			{
				entry.getValue().setCompleteCount(entry.getValue().getCompleteCount() + count);

				if (entry.getValue().getCompleteCount() >= dailyQuestCfg.getCompleteCountCfg()) {
					entry.getValue().setQuestStatus(1);
				}
			}

		}

		dailyQuestEntity.reConvert();
		dailyQuestEntity.notifyUpdate();

		sendAllDailyQuestInfo(HP.code.DAILY_QUEST_INFO_S_VALUE);

		return true;
	}

	private boolean onDailyQuestComplete(Msg msg) {
		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();

		Map<Integer, DailyQuestItem> map = dailyQuestEntity.getDailyQuestMap();

		for (Map.Entry<Integer, DailyQuestItem> entry : map.entrySet()) {
			int id = entry.getValue().getId();
			DailyQuestCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(DailyQuestCfg.class, id);

			if (dailyQuestCfg == null) {
				continue;
			}
			
			int QuestType = msg.getMsg() % GsConst.MsgType.DailyQuestMsg.MSG_BASE;


			if (dailyQuestCfg.getType() == QuestType) 
			{
				Log.logPrintln(player.getId() + "===DailyQuestComplete===" + entry.getValue().getQuestStatus() + "==Count=="
						+ entry.getValue().getCompleteCount());
				if (entry.getValue().getQuestStatus() != 1) {
					entry.getValue().setQuestStatus(1);
					entry.getValue().setCompleteCount(1);
					continue;
				}
			}

		}

		dailyQuestEntity.reConvert();
		dailyQuestEntity.notifyUpdate();

		sendAllDailyQuestInfo(HP.code.DAILY_QUEST_INFO_S_VALUE);

		return true;
	}

	private boolean onRecharge(Msg msg) {
		int count = (Integer) msg.getParams().get(0);

		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();

		Map<Integer, DailyQuestItem> map = dailyQuestEntity.getDailyQuestMap();

		for (Map.Entry<Integer, DailyQuestItem> entry : map.entrySet()) {
			int id = entry.getValue().getId();
			DailyQuestCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(DailyQuestCfg.class, id);

			if (dailyQuestCfg == null) {
				continue;
			}
			
			int QuestType = dailyQuestCfg.getType();

			if (QuestType == GsConst.DailyQuestType.ON_RECHARAGE)// 单笔不限额
			{
				if (entry.getValue().getQuestStatus() != 1) {
					entry.getValue().setQuestStatus(1);
					entry.getValue().setCompleteCount(1);
					continue;
				}
			}

			if (QuestType == GsConst.DailyQuestType.ON_RECHARAGE_LIMILT)// 限制额度
			{
				entry.getValue().setCompleteCount(entry.getValue().getCompleteCount() + count);

				if (entry.getValue().getCompleteCount() >= dailyQuestCfg.getCompleteCountCfg()) {
					entry.getValue().setQuestStatus(1);
				}
				continue;
			}
		}

		dailyQuestEntity.reConvert();
		dailyQuestEntity.notifyUpdate();
		sendAllDailyQuestInfo(HP.code.DAILY_QUEST_INFO_S_VALUE);

		return true;
	}
		
	@Override
	public boolean onProtocol(Protocol protocol) {

		if (protocol.checkType(HP.code.DAILY_QUEST_INFO_C_VALUE)) {

			onRequestAllDailyQuestInfo(protocol.parseProtocol(HPDailyQuestInfo.getDefaultInstance()));

			return true;
		}

		if (protocol.checkType(HP.code.TAKE_DAILY_QUEST_AWARD_C_VALUE)) {

			onTakeDailyQuestAward(protocol.parseProtocol(HPTakeDailyQuestAward.getDefaultInstance()));

			return true;
		}

		if (protocol.checkType(HP.code.TAKE_DAILY_QUEST_POINT_AWARD_C_VALUE)) {

			onTakeDailyQuestPointAward(protocol.parseProtocol(HPTakeDailyPointAward.getDefaultInstance()));

			return true;
		}

		return super.onProtocol(protocol);
	}

	@Override
	protected boolean onPlayerLogin() {

		this.lastDate = GuaJiTime.getCalendar().getTime();

		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();

		if (dailyQuestEntity.getDailyQuestMap().size() == 0 || dailyQuestEntity.getDailyQuestMap() == null)// 第一次登录游戏或者数据被重置
		{
			initDailyQuest();
			dailyQuestEntity.reConvert();
			dailyQuestEntity.notifyUpdate();
		}

//		ActiveStatus activeStatus = ActivityUtil.getActiveComplianceStatus(player.getPlayerData());
//		if (activeStatus != null) {
//			int activityId = Const.ActivityId.ACTIVECOMPLIANCE_VALUE;
//			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
//			if (activityTimeCfg != null) {
//				if (activeStatus.calcActivitySurplusTime() <= 0) { // 時間到不重置
//
//					activeStatus.refresh();
//					player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
//
//				} else {
//					// 若是第一次且满足达标活跃度 兼容版本更新已达标用户
//					if (activeStatus.isIsfirst() == true && dailyQuestEntity.getDailyPoint() >= SysBasicCfg
//							.getInstance().getActiveCompliancePoint()) {
//						activeStatus.setDays(activeStatus.getDays() + 1);
//						activeStatus.setIsfirst(false);
//						player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
//					}
//				}
//			}
//
//		}

		return super.onPlayerLogin();
	}

	@Override
	protected boolean onPlayerLogout() {
		return super.onPlayerLogout();
	}

	public void initDailyQuest() {
		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();

		for (DailyQuestCfg dailyQuestCfg : ConfigManager.getInstance().getConfigMap(DailyQuestCfg.class).values()) {
			if (player.getLevel() >= dailyQuestCfg.getMinLevel() && player.getLevel() <= dailyQuestCfg.getMaxlevel()) {
				DailyQuestItem item = new DailyQuestItem();

				if (dailyQuestCfg.getType() == GsConst.DailyQuestType.LOGIN) {
					item.setQuestStatus(1);
					item.setCompleteCount(1);

				} else {
					item.setQuestStatus(0);
					item.setCompleteCount(0);

				}
				item.setId(dailyQuestCfg.getId());
				item.setTakeStatus(0);
				dailyQuestEntity.addDailyQuest(dailyQuestCfg.getId(), item);
			}
		}
		return;
	}

	public void ChkDailyQuest() {
		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();
		int count=0;
		for (DailyQuestCfg dailyQuestCfg : ConfigManager.getInstance().getConfigMap(DailyQuestCfg.class).values()) {
			if (player.getLevel() >= dailyQuestCfg.getMinLevel() && player.getLevel() <= dailyQuestCfg.getMaxlevel()) {
				DailyQuestItem item = new DailyQuestItem();
				if (dailyQuestEntity != null
						&& dailyQuestEntity.getDailyQuestMap().containsKey(dailyQuestCfg.getId())) {
					continue;
				}
				if (dailyQuestCfg.getType() == GsConst.DailyQuestType.LOGIN) {
					item.setQuestStatus(1);
					item.setCompleteCount(1);

				} else {
					item.setQuestStatus(0);
					item.setCompleteCount(0);

				}
				item.setId(dailyQuestCfg.getId());
				item.setTakeStatus(0);
				dailyQuestEntity.addDailyQuest(dailyQuestCfg.getId(), item);
				count++;
			}
		}
		if(count>0) {
			dailyQuestEntity.reConvert();
			dailyQuestEntity.notifyUpdate();
		}
	
		return;
	}

	private void sendAllDailyQuestInfo(int type) {
		
		//检测下是否
		ChkDailyQuest();
		
		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();

		HPDailyQuestInfoRet.Builder dailyQuestInfoRet = HPDailyQuestInfoRet.newBuilder();
		Map<Integer, DailyQuestItem> map = dailyQuestEntity.getDailyQuestMap();
		
		
		for (Map.Entry<Integer, DailyQuestItem> entry : map.entrySet()) {
			QuestItem.Builder questItem = QuestItem.newBuilder();

			DailyQuestCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(DailyQuestCfg.class,
					entry.getValue().getId());
			if (dailyQuestCfg == null) {
				continue;
			}

			questItem.setQuestId(entry.getValue().getId());
			questItem.setQuestStatus(entry.getValue().getQuestStatus());
			questItem.setTakeStatus(entry.getValue().getTakeStatus());
			questItem.setQuestCompleteCount(entry.getValue().getCompleteCount());
			questItem.setTaskRewards(dailyQuestCfg.getAward());

			dailyQuestInfoRet.addAllDailyQuest(questItem);
		}

		for (int pointNumber : dailyQuestEntity.getDailyPointState().keySet()) {
			DailyPointCore.Builder dailyPointCore = DailyPointCore.newBuilder();
			dailyPointCore.setDailyPointNumber(pointNumber);
			dailyPointCore.setState(dailyQuestEntity.getDailyPointState().get(pointNumber));
			dailyQuestInfoRet.addDailyPointCore(dailyPointCore);
		}

		dailyQuestInfoRet.setDailyPoint(dailyQuestEntity.getDailyPoint());

		player.sendProtocol(Protocol.valueOf(type, dailyQuestInfoRet));

		return;
	}

	/**
	 * 返回所有日常任务信息
	 */
	protected boolean onRequestAllDailyQuestInfo(HPDailyQuestInfo protocal) {
		sendAllDailyQuestInfo(HP.code.DAILY_QUEST_INFO_S_VALUE);
		return true;
	}

	/**
	 * 检查活跃点状态并对完成状态进行更改
	 * 
	 * @param dailyQuestEntity
	 */
	private void checkModifyDailyPointState(DailyQuestEntity dailyQuestEntity) {
		for (int pointNumber : dailyQuestEntity.getDailyPointState().keySet()) {
			if (dailyQuestEntity.getDailyPointState().get(pointNumber) > 0) {
				continue;
			}

			if (dailyQuestEntity.getDailyPoint() >= pointNumber) {
				dailyQuestEntity.modifyDailyPointState(pointNumber, 1);
			}
		}

		return;
	}

	protected boolean onTakeDailyQuestAward(HPTakeDailyQuestAward protocal) {
		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();

		int id = protocal.getQuestId();

		DailyQuestItem quest = dailyQuestEntity.getDailyQuest(id);

		if (quest == null) {

			return true;
		}

		if (quest.getQuestStatus() != 1) {
			return true;
		}

		if (quest.getTakeStatus() != 0) {
			return true;
		}

		DailyQuestCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(DailyQuestCfg.class, id);

		if (dailyQuestCfg == null) {
			return true;
		}

		String award = dailyQuestCfg.getAward();

		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.TAKE_DAILY_QUEST_AWARD, 1,TapDBSource.Daily_Quest,Params.valueOf("id", id));// 记录领取日志

		quest.setTakeStatus(1);

		int point = dailyQuestCfg.getPoint();

		// 触发活跃度达标

//		ActiveStatus activeStatus = ActivityUtil.getActiveComplianceStatus(player.getPlayerData());
//		if (activeStatus != null && activeStatus.getDays() < SysBasicCfg.getInstance().getActiveCanAwardDays()) {
//			int activityId = Const.ActivityId.ACTIVECOMPLIANCE_VALUE;
//			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
//			if (activityTimeCfg != null) {
//				int oldDailyPoint = dailyQuestEntity.getDailyPoint();
//				int newDailyPoint = dailyQuestEntity.getDailyPoint() + point;
//				int targetPoint = SysBasicCfg.getInstance().getActiveCompliancePoint();
//				// 有变化且满足达标点数累加达标天数
//				if (oldDailyPoint < targetPoint && newDailyPoint >= targetPoint) {
//					activeStatus.setDays(activeStatus.getDays() + 1);
//					if (activeStatus.isIsfirst()) {
//						activeStatus.setIsfirst(false);
//					}
//					// 给客户端通知累计消费活动小红点
//					PlayerActivityModule activityModule = (PlayerActivityModule) player
//							.getModule(GsConst.ModuleType.ACTIVITY_MODULE);
//					if (activityModule != null) {
//						activityModule.pushClientShowRedPointByID(Const.ActivityId.ACTIVECOMPLIANCE_VALUE);
//					}
//					player.getPlayerData().updateActivity(Const.ActivityId.ACTIVECOMPLIANCE_VALUE,
//							activityTimeCfg.getStageId());
//				}
//			}
//
//		}

		dailyQuestEntity.setDailyPoint(point + dailyQuestEntity.getDailyPoint());

		checkModifyDailyPointState(dailyQuestEntity);

		dailyQuestEntity.reConvert();

		dailyQuestEntity.notifyUpdate();
		
		if (point > 0) {
			Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.DAILY_POINT,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
			hawkMsg.pushParam(point);
			onDailyQuestCount(hawkMsg);
		}

		// sendDailyQuestInfo(id,award);
		sendAllDailyQuestInfo(HP.code.TAKE_DAILY_QUEST_AWARD_S_VALUE);
		
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.TAKE_DAILY_QUEST_AWARD,
				Params.valueOf("id", id),
				Params.valueOf("award", award),
				Params.valueOf("addpoint", point),
				Params.valueOf("nowpoint", dailyQuestEntity.getDailyPoint()));
		
		return true;
	}

	protected boolean onTakeDailyQuestPointAward(HPTakeDailyPointAward protocal) {
		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();

		int count = protocal.getPointCount();

		DailyQuestPointCfg dailyQuestPointCfg = ConfigManager.getInstance().getConfigByKey(DailyQuestPointCfg.class,
				count);
		if (dailyQuestPointCfg == null) {
			player.sendError(HP.code.TAKE_DAILY_QUEST_POINT_AWARD_C_VALUE, Status.error.CONFIG_ERROR);
			return false;
		}

		if (dailyQuestEntity.getDailyPointState().get(count) != 1)// 已完成未领取
		{
			HPTakeDailyPointAwardRet.Builder takeDailyPointAwardRet = HPTakeDailyPointAwardRet.newBuilder();

			takeDailyPointAwardRet.setFlag(0);
			takeDailyPointAwardRet.setPointCount(count);
			takeDailyPointAwardRet.setState(dailyQuestEntity.getDailyPointState().get(count));

			player.sendProtocol(Protocol.valueOf(HP.code.TAKE_DAILY_QUEST_POINT_AWARD_S_VALUE, takeDailyPointAwardRet));

			return true;
		}

		String award = dailyQuestPointCfg.getAward();

		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.TAKE_DAILY_QUEST_AWARD, 2,TapDBSource.Daily_Point,Params.valueOf("pointNum",count));// 记录领取日志

		// dailyQuestEntity.setDailyPoint(dailyQuestEntity.getDailyPoint()-count);

		dailyQuestEntity.modifyDailyPointState(count, 2);// 已领取

		dailyQuestEntity.reConvert();

		dailyQuestEntity.notifyUpdate();

		HPTakeDailyPointAwardRet.Builder takeDailyPointAwardRet = HPTakeDailyPointAwardRet.newBuilder();

		takeDailyPointAwardRet.setFlag(1);
		takeDailyPointAwardRet.setPointCount(count);
		takeDailyPointAwardRet.setState(2);

		player.sendProtocol(Protocol.valueOf(HP.code.TAKE_DAILY_QUEST_POINT_AWARD_S_VALUE, takeDailyPointAwardRet));
		
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.TAKE_DAILY_QUEST_AWARD,
				Params.valueOf("count", count),
				Params.valueOf("award", award));
	
		return true;
	}

}
