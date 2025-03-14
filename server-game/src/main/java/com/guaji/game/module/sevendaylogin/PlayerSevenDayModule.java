package com.guaji.game.module.sevendaylogin;

import java.util.Map;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.SevenDayQuestCfg;
import com.guaji.game.config.SevenDayQuestPointCfg;
import com.guaji.game.entity.SevenDayQuestEntity;
import com.guaji.game.entity.SevenDayQuestItem;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.SevenDayQuest.QuestItemInfo;
import com.guaji.game.protocol.SevenDayQuest.SevenDayLoginAwardRep;
import com.guaji.game.protocol.SevenDayQuest.SevenDayLoginAwardReq;
import com.guaji.game.protocol.SevenDayQuest.SevenDayPointAwardRep;
import com.guaji.game.protocol.SevenDayQuest.SevenDayPointAwardReq;
import com.guaji.game.protocol.SevenDayQuest.SevenDayPointCore;
import com.guaji.game.protocol.SevenDayQuest.SevenDayQuestAwardRep;
import com.guaji.game.protocol.SevenDayQuest.SevenDayQuestAwardReq;
import com.guaji.game.protocol.SevenDayQuest.SevenDayQuestRep;
import com.guaji.game.protocol.SevenDayQuest.SevenDayQuestReq;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年1月24日 下午5:32:50 类说明
 */
public class PlayerSevenDayModule extends PlayerModule {

	public PlayerSevenDayModule(Player player) {
		super(player);
		listenProto(HP.code.SEVENDAY_QUEST_INFO_C_VALUE); // 请求7日
		listenProto(HP.code.SEVENDAY_QUEST_AWARD_C_VALUE); // 请求任务领奖
		listenProto(HP.code.SEVENDAY_QUEST_ACHIEVE_AWARD_C_VALUE);// 请求成就领奖
		listenProto(HP.code.SEVENDAY_LOGIN_AWARD_C_VALUE);// 请求成就领奖
	}

	@Override
	protected boolean onPlayerLogin() {
		SevenDayQuestEntity questEntity = player.getPlayerData().loadSevenDayQuestEntity();
		if (questEntity != null) {
			questEntity.loadQuest();
			// 登陆
			SevenDayQuestEventBus.fireQuestEventOneTime(SevenDayEventType.LOGIN, player.getXid());
		}

		return super.onPlayerLogin();
	}

	/**
	 * 返回所有日常任务信息
	 */
	protected boolean onRequestAllQuestInfo(SevenDayQuestReq protocal) {
			
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.Newbie_Unlock)){
			player.sendError(HP.code.SEVENDAY_QUEST_INFO_C_VALUE, Status.error.CONDITION_NOT_ENOUGH);
			return false;
		}
		
		int surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
		
		if (surplusTime == 0) {
			return false;
		}
		
		if (surplusTime < 0) {			
			player.getPlayerData().getStateEntity().setNewbieDate(GuaJiTime.getCalendar().getTime());
			surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
		}
		
		SevenDayQuestEntity questEntity = player.getPlayerData().loadSevenDayQuestEntity();

		SevenDayQuestRep.Builder questInfoRet = SevenDayQuestRep.newBuilder();
		Map<Integer, SevenDayQuestItem> map = questEntity.getQuestMap();
		
		// fix sevendayquest
		boolean passMapFix = false;
		boolean longinFix = false;
		for (Map.Entry<Integer, SevenDayQuestItem> entry : map.entrySet()) {
			SevenDayQuestCfg questCfg = ConfigManager.getInstance().getConfigByKey(SevenDayQuestCfg.class,
					entry.getValue().getId());
			if (questCfg == null) {
				continue;
			}
			if ((questCfg.getTargetType() == SevenDayEventType.PASS_MISSION_VALUE)&&(!passMapFix)) {
				if (entry.getValue().getStatus() == 1) {// 進行中
					// passMapId 不對 
					if (player.getPlayerData().getStateEntity().getPassMapId() > (int) entry.getValue().getFinishNum()) {
						SevenDayEvent event = new SevenDayEvent(SevenDayEventType.PASS_MISSION, player.getPlayerData().getStateEntity().getPassMapId());
						player.getPlayerData().onSevenDayQuestEvent(event);
						passMapFix = true;
					}
				}
			}
			
			if ((questCfg.getTargetType() == SevenDayEventType.LOGIN_VALUE)&&(!longinFix)) {
				if (entry.getValue().getStatus() == 1) {// 進行中
					if ((questCfg.getNeedCount() == 1)&&(entry.getValue().getFinishNum() == 0)) {
						// 登入後活動才開啟
						SevenDayEvent event = new SevenDayEvent(SevenDayEventType.LOGIN,1);
						player.getPlayerData().onSevenDayQuestEvent(event);
						longinFix = true;
					}
				}
			}
		}
		

//		int surplusTime = ActivityUtil
//				.clacSevenDaySurplusTime(player.getPlayerData().getPlayerEntity().getCreateTime());
		int registerDays = GuaJiTime.calcBetweenDays(player.getPlayerData().getStateEntity().getNewbieDate(),
				GuaJiTime.getCalendar().getTime()) + 1;
		questInfoRet.setSurplusTime(surplusTime);
		questInfoRet.setRegisterDay(registerDays);
		questInfoRet.setAwardstate((questEntity.isAwardstate() ? 1 : 0));
		
		Map<Integer, SevenDayQuestItem> amap = questEntity.getQuestMap();
		// 任务列表
		for (Map.Entry<Integer, SevenDayQuestItem> entry : amap.entrySet()) {

			QuestItemInfo.Builder itemInfo = QuestItemInfo.newBuilder();
			SevenDayQuestCfg questCfg = ConfigManager.getInstance().getConfigByKey(SevenDayQuestCfg.class,
					entry.getValue().getId());
			if (questCfg == null) {
				continue;
			}
			itemInfo.setQuestId(entry.getValue().getId());
			itemInfo.setState(entry.getValue().getStatus());
			itemInfo.setFinishCount((int) entry.getValue().getFinishNum());
			questInfoRet.addAllQuest(itemInfo);

		}

		for (int pointNumber : questEntity.getPointState().keySet()) {
			SevenDayPointCore.Builder pointCore = SevenDayPointCore.newBuilder();
			pointCore.setPointNumber(pointNumber);
			pointCore.setState(questEntity.getPointState().get(pointNumber));
			questInfoRet.addPointCore(pointCore);
		}
		// 当前成就点数
		questInfoRet.setHasPoint(questEntity.getPoint());
		player.sendProtocol(Protocol.valueOf(HP.code.SEVENDAY_QUEST_INFO_S_VALUE, questInfoRet));
		return true;
	}

	/**
	 * 领取7日之诗任务奖励
	 */
	/**
	 * @param protocal
	 * @return
	 */
	protected boolean onTakeQuestAward(SevenDayQuestAwardReq protocal) {
			
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.Newbie_Unlock)){
			player.sendError(HP.code.SEVENDAY_QUEST_AWARD_C_VALUE, Status.error.CONDITION_NOT_ENOUGH);
			return false;
		}
		
		int surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
		
		if (surplusTime == 0) {
			return false;
		}
		
		if (surplusTime < 0) {			
			player.getPlayerData().getStateEntity().setNewbieDate(GuaJiTime.getCalendar().getTime());
			surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
		}
				
		SevenDayQuestEntity questEntity = player.getPlayerData().loadSevenDayQuestEntity();

		int id = protocal.getQuestId();

		SevenDayQuestItem quest = questEntity.getQuest(id);

		if (quest == null) {

			return true;
		}

	

		// 任务配置
		SevenDayQuestCfg questCfg = ConfigManager.getInstance().getConfigByKey(SevenDayQuestCfg.class, id);

		if (questCfg == null) {
			player.sendError(HP.code.SEVENDAY_QUEST_AWARD_C_VALUE, Status.error.CONFIG_ERROR);
			return false;
		}

		// 若是优惠折扣
		if (questCfg.getTargetType() == SevenDayEventType.BUYDISCOUNT_VALUE) {
			if (questCfg.getParam().equals("")) {
				player.sendError(HP.code.SEVENDAY_QUEST_AWARD_C_VALUE, Status.error.CONFIG_ERROR);
				return false;
			} else {
				int price = Integer.parseInt(questCfg.getParam());
				// 钻石够不够
				if (price > player.getGold()) {
					sendError(HP.code.HERO_TOKEN_BUY_INFO_C_VALUE, Status.error.GOLD_NOT_ENOUGH);
					return false;
				}
				quest.setFinishNum(quest.getFinishNum()+1);
				// 符合购买条件
				ConsumeItems.valueOf(changeType.CHANGE_GOLD, price).consumeTakeAffect(player,
						Action.ACC_LOGIN_SEVENDAY_TASK_AWARDS);
			}

		}else
		{
			// 已领取过或未完成
			if (quest.getStatus() != 2) {
				return true;
			}
		}

		quest.setStatus(3);
		questEntity.setPoint(questEntity.getPoint() + questCfg.getPoint());
		checkModifyPointState(questEntity);
		questEntity.reConvert();
		questEntity.notifyUpdate();
		String award = questCfg.getAward();
		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.ACC_LOGIN_SEVENDAY_TASK_AWARDS, 2,TapDBSource.Seven_Day,Params.valueOf("id", id));// 记录领取日志

		SevenDayQuestAwardRep.Builder builder = SevenDayQuestAwardRep.newBuilder();
		builder.setAddPoint(questCfg.getPoint());
		builder.setFlag(1);
		builder.setQuestId(id);
		builder.setState(3);
		player.sendProtocol(Protocol.valueOf(HP.code.SEVENDAY_QUEST_AWARD_S_VALUE, builder));
		
		
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.ACC_LOGIN_SEVENDAY_TASK_AWARDS,
				Params.valueOf("id", id),
				Params.valueOf("award", award),
				Params.valueOf("addpoint", questCfg.getPoint()),
				Params.valueOf("nowpoint", questEntity.getPoint()));
		
		return true;
	}

	/**
	 * 检查活跃点状态并对完成状态进行更改
	 * 
	 * @param dailyQuestEntity
	 */
	private void checkModifyPointState(SevenDayQuestEntity questEntity) {
		for (int pointNumber : questEntity.getPointState().keySet()) {
			if (questEntity.getPointState().get(pointNumber) > 0) {
				continue;
			}

			if (questEntity.getPoint() >= pointNumber) {
				questEntity.modifyPointState(pointNumber, 1);
			}
		}

		return;
	}

	/**
	 * 领取成就点对应的奖励
	 */
	protected boolean onTakeQuestPointAward(SevenDayPointAwardReq protocal) {
				
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.Newbie_Unlock)){
			player.sendError(HP.code.SEVENDAY_QUEST_ACHIEVE_AWARD_C_VALUE, Status.error.CONDITION_NOT_ENOUGH);
			return false;
		}
		
		int surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
		
		if (surplusTime == 0) {
			return false;
		}
		
		if (surplusTime < 0) {			
			player.getPlayerData().getStateEntity().setNewbieDate(GuaJiTime.getCalendar().getTime());
			surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
		}
				
		SevenDayQuestEntity questEntity = player.getPlayerData().loadSevenDayQuestEntity();

		int pointLevel = protocal.getPointCount();
		if (pointLevel < 0)
			return false;
		SevenDayQuestPointCfg questPointCfg = ConfigManager.getInstance().getConfigByKey(SevenDayQuestPointCfg.class,
				pointLevel);
		if (questPointCfg == null) {
			player.sendError(HP.code.SEVENDAY_QUEST_ACHIEVE_AWARD_C_VALUE, Status.error.CONFIG_ERROR);
			return false;
		}

		if (questEntity.getPointState().get(pointLevel) != 1)// 已完成未领取
		{
			SevenDayPointAwardRep.Builder takePointAwardRet = SevenDayPointAwardRep.newBuilder();

			takePointAwardRet.setFlag(0);
			takePointAwardRet.setPointCount(pointLevel);
			takePointAwardRet.setState(questEntity.getPointState().get(pointLevel));

			player.sendProtocol(Protocol.valueOf(HP.code.SEVENDAY_QUEST_ACHIEVE_AWARD_S_VALUE, takePointAwardRet));

			return true;
		}

		String award = questPointCfg.getAward();

		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.ACC_LOGIN_SEVENDAY_POING_AWARDS, 2,TapDBSource.Seven_Day_Point,Params.valueOf("pointNumber", pointLevel));// 记录领取日志

		questEntity.modifyPointState(pointLevel, 2);// 已领取

		questEntity.reConvert();

		questEntity.notifyUpdate();

		SevenDayPointAwardRep.Builder takePointAwardRet = SevenDayPointAwardRep.newBuilder();

		takePointAwardRet.setFlag(1);
		takePointAwardRet.setPointCount(pointLevel);
		takePointAwardRet.setState(2);

		player.sendProtocol(Protocol.valueOf(HP.code.SEVENDAY_QUEST_ACHIEVE_AWARD_S_VALUE, takePointAwardRet));
		
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.ACC_LOGIN_SEVENDAY_POING_AWARDS,
				Params.valueOf("pointLevel", pointLevel),
				Params.valueOf("award", award));

		return true;
	}

	/**
	 * 领取成就点对应的奖励
	 */
	protected boolean onTakeLoginAward(SevenDayLoginAwardReq protocal) {
		
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.Newbie_Unlock)){
			player.sendError(HP.code.SEVENDAY_LOGIN_AWARD_C_VALUE, Status.error.CONDITION_NOT_ENOUGH);
			return false;
		}
		
		int surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
		
		if (surplusTime == 0) {
			return false;
		}
		
		if (surplusTime < 0) {			
			player.getPlayerData().getStateEntity().setNewbieDate(GuaJiTime.getCalendar().getTime());
			surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
		}
				

		
		SevenDayQuestEntity questEntity = player.getPlayerData().loadSevenDayQuestEntity();

		SevenDayLoginAwardRep.Builder takeLoginAwardRet = SevenDayLoginAwardRep.newBuilder();
		// 已经领取奖励
		if (questEntity.isAwardstate()) {
			takeLoginAwardRet.setFlag(0);
			// player.sendError(HP.code.TAKE_DAILY_QUEST_POINT_AWARD_C_VALUE,
			// Status.error.CONFIG_ERROR);
			player.sendProtocol(Protocol.valueOf(HP.code.SEVENDAY_LOGIN_AWARD_S_VALUE, takeLoginAwardRet));

			return false;
		}
		SevenDayQuestPointCfg questPointCfg = ConfigManager.getInstance().getConfigByKey(SevenDayQuestPointCfg.class,
				-1);
		if (questPointCfg == null) {
			takeLoginAwardRet.setFlag(0);
			player.sendError(HP.code.SEVENDAY_LOGIN_AWARD_C_VALUE, Status.error.CONFIG_ERROR);
			return false;
		}

		// 奖励
		AwardItems awardItems = AwardItems.valueOf(questPointCfg.getAward());
		awardItems.rewardTakeAffectAndPush(player, Action.ACC_LOGIN_SEVENDAY_AWARDS, 2);// 记录领取日志
		takeLoginAwardRet.setFlag(1);
		questEntity.setAwardstate(true);

		questEntity.reConvert();

		questEntity.notifyUpdate();

		player.sendProtocol(Protocol.valueOf(HP.code.SEVENDAY_LOGIN_AWARD_S_VALUE, takeLoginAwardRet));

		return true;
	}

	@Override
	public boolean onProtocol(Protocol protocol) {
		// 详细信息
		if (protocol.checkType(HP.code.SEVENDAY_QUEST_INFO_C)) {
			onRequestAllQuestInfo(protocol.parseProtocol(SevenDayQuestReq.getDefaultInstance()));
			return true;

		} else if (protocol.checkType(HP.code.SEVENDAY_QUEST_AWARD_C)) {
			onTakeQuestAward(protocol.parseProtocol(SevenDayQuestAwardReq.getDefaultInstance()));
			return true;

		} else if (protocol.checkType(HP.code.SEVENDAY_QUEST_ACHIEVE_AWARD_C)) {
			onTakeQuestPointAward(protocol.parseProtocol(SevenDayPointAwardReq.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.SEVENDAY_LOGIN_AWARD_C_VALUE)) {
			onTakeLoginAward(protocol.parseProtocol(SevenDayLoginAwardReq.getDefaultInstance()));
			return true;
		}
		return super.onProtocol(protocol);
	}

	@Override
	public boolean onMessage(Msg msg) {
		return super.onMessage(msg);
	}

	@Override
	protected boolean onPlayerAssemble() {
		
		return super.onPlayerAssemble();
	}

	@Override
	protected boolean onPlayerLogout() {
		return super.onPlayerLogout();
	}

	@MessageHandlerAnno(code = GsConst.MsgType.SEVENDAY_EVENT)
	private void onQuestEvent(Msg msg) {
		SevenDayEvent event = msg.getParam(0);
		player.getPlayerData().onSevenDayQuestEvent(event);
	}

	@Override
	public boolean onTick() {
		// TODO Auto-generated method stub
		return super.onTick();
	}
	
	
}
