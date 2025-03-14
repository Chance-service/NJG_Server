package com.guaji.game.module;

import java.util.Date;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.WeeklyQuestCfg;
import com.guaji.game.config.WeeklyQuestPointCfg;
import com.guaji.game.entity.WeeklyQuestEntity;
import com.guaji.game.entity.WeeklyQuestItem;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.DailyQuest.DailyPointCore;
import com.guaji.game.protocol.DailyQuest.HPDailyQuestInfo;
import com.guaji.game.protocol.DailyQuest.HPDailyQuestInfoRet;
import com.guaji.game.protocol.DailyQuest.HPTakeDailyPointAward;
import com.guaji.game.protocol.DailyQuest.HPTakeDailyPointAwardRet;
import com.guaji.game.protocol.DailyQuest.HPTakeDailyQuestAward;
import com.guaji.game.protocol.DailyQuest.QuestItem;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.TapDBUtil.TapDBSource;

public class PlayerWeeklyQuestModule extends PlayerModule {

	private Date lastDate;

	public PlayerWeeklyQuestModule(Player player) {
		super(player);

		listenProto(HP.code.WEEKLY_QUEST_INFO_C_VALUE);// 请求所有任务信息
		listenProto(HP.code.TAKE_WEEKLY_QUEST_AWARD_C_VALUE);// 某个任务完成奖励
		listenProto(HP.code.TAKE_WEEKLY_QUEST_POINT_AWARD_C_VALUE);
		
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
		// 跨天檢查
		if (!GuaJiTime.isSameDay(lastDate.getTime(), curDate.getTime())) {
			WeeklyQuestEntity QuestEntity = player.getPlayerData().loadWeeklyQuestEntity();

			if (QuestEntity.getQuestMap().size() == 0 || QuestEntity.getQuestMap() == null) {
				initWeeklyQuest();
				QuestEntity.reConvert();
				QuestEntity.notifyUpdate();
			}
			this.lastDate = curDate;
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
			onWeeklyQuestCount(msg);
			return true;
		
		} else if ((msg.getMsg() == GsConst.MsgType.DailyQuestMsg.ON_RECHARGE)||
				(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.ON_RECHARGE_LIMIT)){
			onRecharge(msg);
			return true;
//		}else if ((msg.getMsg() == GsConst.MsgType.DailyQuestMsg.FACE_BOOK_SHARE)||
//				(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.EIGHTEENPRINCESCHANGE)){
//			onWeeklyQuestComplete(msg);
//			return true;
		}
		
		return super.onMessage(msg);
	}
	/**
	 * 計數數量達成
	 * @param msg
	 * @return
	 */
	private boolean onWeeklyQuestCount(Msg msg) {
		int count = (Integer) msg.getParams().get(0);

		WeeklyQuestEntity QuestEntity = player.getPlayerData().loadWeeklyQuestEntity();

		Map<Integer, WeeklyQuestItem> map = QuestEntity.getQuestMap();

		for (Map.Entry<Integer, WeeklyQuestItem> entry : map.entrySet()) {
			int id = entry.getValue().getId();
			WeeklyQuestCfg QuestCfg = ConfigManager.getInstance().getConfigByKey(WeeklyQuestCfg.class, id);

			if (QuestCfg == null) {
				continue;
			}
			
			int QuestType = msg.getMsg() % GsConst.MsgType.DailyQuestMsg.MSG_BASE;

			if (QuestCfg.getType() == QuestType)
			{
				entry.getValue().setCompleteCount(entry.getValue().getCompleteCount() + count);

				if (entry.getValue().getCompleteCount() >= QuestCfg.getCompleteCountCfg()) {
					entry.getValue().setQuestStatus(1);
				}
			}

		}

		QuestEntity.reConvert();
		QuestEntity.notifyUpdate();

		sendAllWeeklyQuestInfo(HP.code.WEEKLY_QUEST_INFO_S_VALUE);

		return true;
	}

//	private boolean onWeeklyQuestComplete(Msg msg) {
//		WeeklyQuestEntity QuestEntity = player.getPlayerData().loadWeeklyQuestEntity();
//
//		Map<Integer, WeeklyQuestItem> map = QuestEntity.getQuestMap();
//
//		for (Map.Entry<Integer, WeeklyQuestItem> entry : map.entrySet()) {
//			int id = entry.getValue().getId();
//			WeeklyQuestCfg QuestCfg = ConfigManager.getInstance().getConfigByKey(WeeklyQuestCfg.class, id);
//
//			if (QuestCfg == null) {
//				continue;
//			}
//			
//			int QuestType = msg.getMsg() % GsConst.MsgType.DailyQuestMsg.MSG_BASE;
//
//
//			if (QuestCfg.getType() == QuestType) 
//			{
//				Log.logPrintln(player.getId() + "===WeeklyQuestComplete===" + entry.getValue().getQuestStatus() + "==Count=="
//						+ entry.getValue().getCompleteCount());
//				if (entry.getValue().getQuestStatus() != 1) {
//					entry.getValue().setQuestStatus(1);
//					entry.getValue().setCompleteCount(1);
//					continue;
//				}
//			}
//
//		}
//
//		QuestEntity.reConvert();
//		QuestEntity.notifyUpdate();
//
//		sendAllWeeklyQuestInfo(HP.code.WEEKLY_QUEST_INFO_S_VALUE);
//
//		return true;
//	}

	private boolean onRecharge(Msg msg) {
		int count = (Integer) msg.getParams().get(0);

		WeeklyQuestEntity QuestEntity = player.getPlayerData().loadWeeklyQuestEntity();

		Map<Integer, WeeklyQuestItem> map = QuestEntity.getQuestMap();

		for (Map.Entry<Integer, WeeklyQuestItem> entry : map.entrySet()) {
			int id = entry.getValue().getId();
			WeeklyQuestCfg QuestCfg = ConfigManager.getInstance().getConfigByKey(WeeklyQuestCfg.class, id);

			if (QuestCfg == null) {
				continue;
			}
			
			int QuestType = QuestCfg.getType();

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

				if (entry.getValue().getCompleteCount() >= QuestCfg.getCompleteCountCfg()) {
					entry.getValue().setQuestStatus(1);
				}
				continue;
			}
		}

		QuestEntity.reConvert();
		QuestEntity.notifyUpdate();
		sendAllWeeklyQuestInfo(HP.code.WEEKLY_QUEST_INFO_S_VALUE);

		return true;
	}
		
	@Override
	public boolean onProtocol(Protocol protocol) {

		if (protocol.checkType(HP.code.WEEKLY_QUEST_INFO_C_VALUE)) {

			onRequestAllWeeklyQuestInfo(protocol.parseProtocol(HPDailyQuestInfo.getDefaultInstance()));

			return true;
		}

		if (protocol.checkType(HP.code.TAKE_WEEKLY_QUEST_AWARD_C_VALUE)) {

			onTakeWeeklyQuestAward(protocol.parseProtocol(HPTakeDailyQuestAward.getDefaultInstance()));

			return true;
		}

		if (protocol.checkType(HP.code.TAKE_WEEKLY_QUEST_POINT_AWARD_C_VALUE)) {

			onTakeWeeklyQuestPointAward(protocol.parseProtocol(HPTakeDailyPointAward.getDefaultInstance()));

			return true;
		}

		return super.onProtocol(protocol);
	}

	@Override
	protected boolean onPlayerLogin() {

		this.lastDate = GuaJiTime.getCalendar().getTime();

		WeeklyQuestEntity QuestEntity = player.getPlayerData().loadWeeklyQuestEntity();

		if (QuestEntity.getQuestMap().size() == 0 || QuestEntity.getQuestMap() == null)// 第一次登录游戏或者数据被重置
		{
			initWeeklyQuest();
			QuestEntity.reConvert();
			QuestEntity.notifyUpdate();
		}
		return super.onPlayerLogin();
	}

	@Override
	protected boolean onPlayerLogout() {
		return super.onPlayerLogout();
	}

	public void initWeeklyQuest() {
		WeeklyQuestEntity QuestEntity = player.getPlayerData().loadWeeklyQuestEntity();

		for (WeeklyQuestCfg QuestCfg : ConfigManager.getInstance().getConfigMap(WeeklyQuestCfg.class).values()) {
			if (player.getLevel() >= QuestCfg.getMinLevel() && player.getLevel() <= QuestCfg.getMaxlevel()) {
				WeeklyQuestItem item = new WeeklyQuestItem();

				if (QuestCfg.getType() == GsConst.DailyQuestType.LOGIN) {
					item.setQuestStatus(0);
					item.setCompleteCount(1);

				} else {
					item.setQuestStatus(0);
					item.setCompleteCount(0);

				}
				item.setId(QuestCfg.getId());
				item.setTakeStatus(0);
				QuestEntity.addQuest(QuestCfg.getId(), item);
			}
		}
		return;
	}

	public void ChkWeeklyQuest() {
		WeeklyQuestEntity QuestEntity = player.getPlayerData().loadWeeklyQuestEntity();
		int count=0;
		for (WeeklyQuestCfg QuestCfg : ConfigManager.getInstance().getConfigMap(WeeklyQuestCfg.class).values()) {
			if (player.getLevel() >= QuestCfg.getMinLevel() && player.getLevel() <= QuestCfg.getMaxlevel()) {
				WeeklyQuestItem item = new WeeklyQuestItem();
				if (QuestEntity != null
						&& QuestEntity.getQuestMap().containsKey(QuestCfg.getId())) {
					continue;
				}
				if (QuestCfg.getType() == GsConst.DailyQuestType.LOGIN) {
					item.setQuestStatus(0);
					item.setCompleteCount(1);

				} else {
					item.setQuestStatus(0);
					item.setCompleteCount(0);

				}
				item.setId(QuestCfg.getId());
				item.setTakeStatus(0);
				QuestEntity.addQuest(QuestCfg.getId(), item);
				count++;
			}
		}
		if(count>0) {
			QuestEntity.reConvert();
			QuestEntity.notifyUpdate();
		}
	
		return;
	}

	private void sendAllWeeklyQuestInfo(int type) {
		
		//检测下是否新增新任務
		ChkWeeklyQuest();
		
		WeeklyQuestEntity QuestEntity = player.getPlayerData().loadWeeklyQuestEntity();

		HPDailyQuestInfoRet.Builder QuestInfoRet = HPDailyQuestInfoRet.newBuilder();
		Map<Integer, WeeklyQuestItem> map = QuestEntity.getQuestMap();
		
		
		for (Map.Entry<Integer, WeeklyQuestItem> entry : map.entrySet()) {
			QuestItem.Builder questItem = QuestItem.newBuilder();

			WeeklyQuestCfg QuestCfg = ConfigManager.getInstance().getConfigByKey(WeeklyQuestCfg.class,
					entry.getValue().getId());
			if (QuestCfg == null) {
				continue;
			}

			questItem.setQuestId(entry.getValue().getId());
			questItem.setQuestStatus(entry.getValue().getQuestStatus());
			questItem.setTakeStatus(entry.getValue().getTakeStatus());
			questItem.setQuestCompleteCount(entry.getValue().getCompleteCount());
			questItem.setTaskRewards(QuestCfg.getAward());

			QuestInfoRet.addAllDailyQuest(questItem);
		}

		for (int pointNumber : QuestEntity.getPointState().keySet()) {
			DailyPointCore.Builder PointCore = DailyPointCore.newBuilder();
			PointCore.setDailyPointNumber(pointNumber);
			PointCore.setState(QuestEntity.getPointState().get(pointNumber));
			QuestInfoRet.addDailyPointCore(PointCore);
		}

		QuestInfoRet.setDailyPoint(QuestEntity.getPoint());

		player.sendProtocol(Protocol.valueOf(type, QuestInfoRet));

		return;
	}

	/**
	 * 返回所有日常任务信息
	 */
	protected boolean onRequestAllWeeklyQuestInfo(HPDailyQuestInfo protocal) {
		sendAllWeeklyQuestInfo(HP.code.WEEKLY_QUEST_INFO_S_VALUE);
		return true;
	}

	/**
	 * 检查活跃点状态并对完成状态进行更改
	 * 
	 * @param dailyQuestEntity
	 */
	private void checkModifyWeeklyPointState(WeeklyQuestEntity QuestEntity) {
		for (int pointNumber : QuestEntity.getPointState().keySet()) {
			if (QuestEntity.getPointState().get(pointNumber) > 0) {
				continue;
			}

			if (QuestEntity.getPoint() >= pointNumber) {
				QuestEntity.modifyPointState(pointNumber, 1);
			}
		}

		return;
	}

	protected boolean onTakeWeeklyQuestAward(HPTakeDailyQuestAward protocal) {
		WeeklyQuestEntity QuestEntity = player.getPlayerData().loadWeeklyQuestEntity();

		int id = protocal.getQuestId();

		WeeklyQuestItem quest = QuestEntity.getQuest(id);

		if (quest == null) {

			return true;
		}

		if (quest.getQuestStatus() != 1) {
			return true;
		}

		if (quest.getTakeStatus() != 0) {
			return true;
		}

		WeeklyQuestCfg QuestCfg = ConfigManager.getInstance().getConfigByKey(WeeklyQuestCfg.class, id);

		if (QuestCfg == null) {
			return true;
		}

		String award = QuestCfg.getAward();

		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.TAKE_WEEKLY_QUEST_AWARD, 1,TapDBSource.Weekly_Quest,Params.valueOf("id", id));// 记录领取日志

		quest.setTakeStatus(1);

		int point = QuestCfg.getPoint();

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

		QuestEntity.setPoint(point + QuestEntity.getPoint());

		checkModifyWeeklyPointState(QuestEntity);

		QuestEntity.reConvert();

		QuestEntity.notifyUpdate();
		
//		if (point > 0) {
//			Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.DAILY_POINT,
//					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
//			hawkMsg.pushParam(point);
//			onWeeklyQuestCount(hawkMsg);
//		}

		// sendDailyQuestInfo(id,award);
		sendAllWeeklyQuestInfo(HP.code.TAKE_WEEKLY_QUEST_AWARD_S_VALUE);
		
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.TAKE_WEEKLY_QUEST_AWARD,
				Params.valueOf("id", id),
				Params.valueOf("award", award),
				Params.valueOf("addpoint", point),
				Params.valueOf("nowpoint", QuestEntity.getPoint()));
		
		return true;
	}

	protected boolean onTakeWeeklyQuestPointAward(HPTakeDailyPointAward protocal) {
		WeeklyQuestEntity QuestEntity = player.getPlayerData().loadWeeklyQuestEntity();

		int count = protocal.getPointCount();

		WeeklyQuestPointCfg QuestPointCfg = ConfigManager.getInstance().getConfigByKey(WeeklyQuestPointCfg.class,
				count);
		if (QuestPointCfg == null) {
			player.sendError(HP.code.TAKE_WEEKLY_QUEST_POINT_AWARD_C_VALUE, Status.error.CONFIG_ERROR);
			return false;
		}

		if (QuestEntity.getPointState().get(count) != 1)// 已完成未领取
		{
			HPTakeDailyPointAwardRet.Builder takeDailyPointAwardRet = HPTakeDailyPointAwardRet.newBuilder();

			takeDailyPointAwardRet.setFlag(0);
			takeDailyPointAwardRet.setPointCount(count);
			takeDailyPointAwardRet.setState(QuestEntity.getPointState().get(count));

			player.sendProtocol(Protocol.valueOf(HP.code.TAKE_WEEKLY_QUEST_POINT_AWARD_S_VALUE, takeDailyPointAwardRet));

			return true;
		}

		String award = QuestPointCfg.getAward();

		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.TAKE_WEEKLY_QUEST_AWARD, 2,TapDBSource.Weekly_Point,Params.valueOf("pointNum",count));// 记录领取日志
		// dailyQuestEntity.setDailyPoint(dailyQuestEntity.getDailyPoint()-count);

		QuestEntity.modifyPointState(count, 2);// 已领取

		QuestEntity.reConvert();

		QuestEntity.notifyUpdate();

		HPTakeDailyPointAwardRet.Builder takeDailyPointAwardRet = HPTakeDailyPointAwardRet.newBuilder();

		takeDailyPointAwardRet.setFlag(1);
		takeDailyPointAwardRet.setPointCount(count);
		takeDailyPointAwardRet.setState(2);

		player.sendProtocol(Protocol.valueOf(HP.code.TAKE_WEEKLY_QUEST_POINT_AWARD_S_VALUE, takeDailyPointAwardRet));
		
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.TAKE_WEEKLY_QUEST_AWARD,
				Params.valueOf("count", count),
				Params.valueOf("award", award));

		return true;
	}

}
