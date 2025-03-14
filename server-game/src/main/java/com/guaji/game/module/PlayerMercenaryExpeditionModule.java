package com.guaji.game.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.MercenaryExpeditionCfg;
import com.guaji.game.config.ResetTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.VipPrivilegeCfg;
import com.guaji.game.entity.ExpeditionTask;
import com.guaji.game.entity.MercenaryExpeditionEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.MailManager;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.module.sevendaylogin.SevenDayQuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.MercenaryExpedition.HPMercenaryDispatch;
import com.guaji.game.protocol.MercenaryExpedition.HPMercenaryExpeditionFast;
import com.guaji.game.protocol.MercenaryExpedition.HPMercenaryExpeditionFinishRet;
import com.guaji.game.protocol.MercenaryExpedition.HPMercenaryExpeditionGiveUp;
import com.guaji.game.protocol.MercenaryExpedition.HPMercenaryExpeditionInfo;
import com.guaji.game.protocol.MercenaryExpedition.HPMercenaryExpeditionInfoRet;
import com.guaji.game.protocol.MercenaryExpedition.TaskItem;
import com.guaji.game.protocol.Status;

public class PlayerMercenaryExpeditionModule extends PlayerModule {
	private List<Integer> taskSeedList = null;
	private List<Integer> taskWeightList = null;
	private static ArrayList<Integer> refreshTimeList = null;

	public PlayerMercenaryExpeditionModule(Player player) {
		super(player);

		listenProto(HP.code.MERCENERY_DISPATCH_C_VALUE);// 佣兵派遣
//		listenProto(HP.code.MERCENERY_EXPEDITION_FAST_C_VALUE);// 快速完成
		listenProto(HP.code.MERCENERY_EXPEDITION_GIVEUP_C_VALUE);// 放弃远征
		listenProto(HP.code.MERCENERY_EXPEDITION_INFO_C_VALUE);// 远征信息
		listenProto(HP.code.MERCENERY_EXPEDITION_REFRESH_C_VALUE);// 远征信息

	}

	@Override
	public boolean onTick() {
		
		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().getMercenaryExpeditionEntity();
		if (mercenaryExpeditionEntity != null)// 玩家在线
		{
			if (taskSeedList == null || taskWeightList == null) {
				return super.onTick();

			}
			
			if (taskSeedList.size() == 0 || taskWeightList.size() == 0) {
				return super.onTick();
			}

			int flag = refreshExpeditionStatus();

			if (flag > 0) {
				mercenaryExpeditionEntity.reConvert();
				mercenaryExpeditionEntity.notifyUpdate();
				if (flag > 1) {
					sendClientExpeditionInfo(HP.code.MERCENERY_EXPEDITION_FINISH_S_VALUE);
				}
			}

		}
		return super.onTick();
	}

	@Override
	public boolean onMessage(Msg msg) {
		// 事件触发处理
		// 时间这里应该监听首次登录上面的是没办法
		return super.onMessage(msg);
	}

	@Override
	public boolean onProtocol(Protocol protocol) {

		if (protocol.checkType(HP.code.MERCENERY_DISPATCH_C_VALUE)) {
			onDispatch(protocol.parseProtocol(HPMercenaryDispatch.getDefaultInstance()), protocol.getType());
			return true;
		}

//		if (protocol.checkType(HP.code.MERCENERY_EXPEDITION_FAST_C_VALUE)) {
//			onFastComplete(protocol.parseProtocol(HPMercenaryExpeditionFast.getDefaultInstance()), protocol.getType());
//			return true;
//		}

		if (protocol.checkType(HP.code.MERCENERY_EXPEDITION_GIVEUP_C_VALUE)) {
			onGiveUpExpeditionTask(protocol.parseProtocol(HPMercenaryExpeditionGiveUp.getDefaultInstance()), protocol.getType());
			return true;
		}

		if (protocol.checkType(HP.code.MERCENERY_EXPEDITION_INFO_C_VALUE)) {
			onQuestExpeditionInfo(protocol.parseProtocol(HPMercenaryExpeditionInfo.getDefaultInstance()), protocol.getType());
			return true;
		}

		if (protocol.checkType(HP.code.MERCENERY_EXPEDITION_REFRESH_C_VALUE)) {
			onRefreshExpeditionTask(protocol.getType());
			return true;
		}

		return super.onProtocol(protocol);
	}

	private static void initRefreshTimeList() {
		List<ResetTimeCfg> resetList = ConfigManager.getInstance().getConfigList(ResetTimeCfg.class);
		refreshTimeList = new ArrayList<Integer>();
		refreshTimeList.add(0);

		for (ResetTimeCfg cfg : resetList) {
			if (cfg.getId() >= Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_1_VALUE
					&& cfg.getId() <= Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_12_VALUE) {
				refreshTimeList.add(cfg.getResetHour() * 3600 + cfg.getResetMin() * 60);
			}

		}

		refreshTimeList.add(24 * 3600 - 1);
	}

	@Override
	protected boolean onPlayerLogin() {
		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();
		// 登录的时候申城mercenaryExpeditionEntity,主线程响应ontick改变taskSeedList,下面又改变，是否引发同步问题？
		initTaskSeedList();

		initRefreshTimeList();

		int flag = refreshExpeditionStatus();

		if (flag > 0) {
			mercenaryExpeditionEntity.reConvert();
			mercenaryExpeditionEntity.notifyUpdate();
			if (flag > 1) {
				sendClientExpeditionInfo(HP.code.MERCENERY_EXPEDITION_FINISH_S_VALUE);
			}
		}

		// 容错
		for (RoleEntity curRole : player.getPlayerData().getHero()) {
			boolean find = false;

			if (curRole.getStatus() == Const.RoleStatus.EXPEDITION_VALUE ||
					curRole.getStatus() == Const.RoleStatus.MIXTASK_VALUE) {
				for (ExpeditionTask task : mercenaryExpeditionEntity.getExpeditionTaskList()) {
					
					for (int roleId : task.getDoingRoleId()) {
						if (roleId == curRole.getId()) {							
							find = true;
							break;
						}
					}
				
				}

				if (!find) {
					curRole.decStatus(Const.RoleStatus.EXPEDITION_VALUE);
					curRole.notifyUpdate();
					BehaviorLogger.log4Service(player.getId(), Source.SYS_OPERATION, Action.FIX_STATUS_EXPEDITION,
							Params.valueOf("curRole", curRole.getId()));
				}
			}
		}

		return super.onPlayerLogin();
	}

	@Override
	protected boolean onPlayerLogout() {
		//
		// MercenaryExpeditionEntity mercenaryExpeditionEntity =
		// player.getPlayerData().loadMercenaryExpeditionEntity();
		//
		// mercenaryExpeditionEntity.reConvert();
		// mercenaryExpeditionEntity.notifyUpdate(false);
		return super.onPlayerLogout();
	}

	/**
	 * 初始化随机种子库for任务列表
	 */
	private void initTaskSeedList() {
		taskSeedList = Collections.synchronizedList(new LinkedList<Integer>());
		taskWeightList = Collections.synchronizedList(new LinkedList<Integer>());

		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();

		int arg = mercenaryExpeditionEntity.getRefreshCount();

		for (MercenaryExpeditionCfg mercenaryExpeditionCfg : ConfigManager.getInstance().getConfigMap(MercenaryExpeditionCfg.class).values()) {
			if (mercenaryExpeditionEntity.getExpeditionTaskPos(mercenaryExpeditionCfg.getId()) != -1) {
				continue;
			} else {
				taskSeedList.add(mercenaryExpeditionCfg.getId());
				switch (arg) {
				case 0:
				case 1:
					taskWeightList.add(mercenaryExpeditionCfg.getWeight1());
					break;
				case 2:
					taskWeightList.add(mercenaryExpeditionCfg.getWeight2());
					break;
				case 3:
					taskWeightList.add(mercenaryExpeditionCfg.getWeight3());
					break;
				case 4:
					taskWeightList.add(mercenaryExpeditionCfg.getWeight4());
					break;
				case 5:
					taskWeightList.add(mercenaryExpeditionCfg.getWeight5());
					break;
				case 6:
					taskWeightList.add(mercenaryExpeditionCfg.getWeight6());
					break;
				case 7:
					taskWeightList.add(mercenaryExpeditionCfg.getWeight7());
					break;
				case 8:
					taskWeightList.add(mercenaryExpeditionCfg.getWeight8());
					break;
				case 9:
					taskWeightList.add(mercenaryExpeditionCfg.getWeight9());
					break;
				case 10:
					taskWeightList.add(mercenaryExpeditionCfg.getWeight10());
					break;
				default:
					taskWeightList.add(mercenaryExpeditionCfg.getWeight10());
					break;
				}

			}
		}
	}

	/**
	 * 领取一个任务
	 * 
	 * @param pos
	 * @param id
	 */
	private void addExpeditionTask(int pos, int id) {
		int index;
		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();
		LinkedList<ExpeditionTask> taskList = mercenaryExpeditionEntity.getExpeditionTaskList();

		ExpeditionTask task = new ExpeditionTask();
		task.setCompleteTime(-1);// 没有完成时间
		task.clearDoingRoleId();
		task.setId(id);
		task.setStatus(0);
		taskList.set(pos, task);

		index = taskSeedList.indexOf(id);

		taskSeedList.remove(index);

		taskWeightList.remove(index);

		return;
	}

	/**
	 * 完成一个任务
	 */
	private void completeExpeditionTask(int pos, int id) {
		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();
		LinkedList<ExpeditionTask> taskList = mercenaryExpeditionEntity.getExpeditionTaskList();

		for (int roleId : taskList.get(pos).getDoingRoleId()) {
			RoleEntity role = player.getPlayerData().getRoleById(roleId);
			role.decStatus(Const.RoleStatus.EXPEDITION_VALUE);
			role.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(role.getId());
		}



		// 待回收对象完结处理
		taskList.get(pos).setCompleteTime(-1);
		taskList.get(pos).clearDoingRoleId();
		taskList.get(pos).setStatus(0);

		taskList.set(pos, null);

		taskSeedList.add(id);

		MercenaryExpeditionCfg mercenaryExpeditionCfg = ConfigManager.getInstance().getConfigByKey(MercenaryExpeditionCfg.class, id);

		int refreshCount = mercenaryExpeditionEntity.getRefreshCount();

		switch (refreshCount) {
		case 0:
		case 1:
			taskWeightList.add(mercenaryExpeditionCfg.getWeight1());
			break;
		case 2:
			taskWeightList.add(mercenaryExpeditionCfg.getWeight2());
			break;
		case 3:
			taskWeightList.add(mercenaryExpeditionCfg.getWeight3());
			break;
		case 4:
			taskWeightList.add(mercenaryExpeditionCfg.getWeight4());
			break;
		case 5:
			taskWeightList.add(mercenaryExpeditionCfg.getWeight5());
			break;
		case 6:
			taskWeightList.add(mercenaryExpeditionCfg.getWeight6());
			break;
		case 7:
			taskWeightList.add(mercenaryExpeditionCfg.getWeight7());
			break;
		case 8:
			taskWeightList.add(mercenaryExpeditionCfg.getWeight8());
			break;
		case 9:
			taskWeightList.add(mercenaryExpeditionCfg.getWeight9());
			break;
		case 10:
			taskWeightList.add(mercenaryExpeditionCfg.getWeight10());
			break;
		default:
			taskWeightList.add(mercenaryExpeditionCfg.getWeight10());
			break;
		}

		return;
	}

	/**
	 * 刷新任务状态(补充+完成替换)
	 */
	private int refreshExpeditionStatus() {
		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();
		LinkedList<ExpeditionTask> taskList = mercenaryExpeditionEntity.getExpeditionTaskList();

		int flag = 0;
		
		int totalWeight = 0;
		for (int j = 0; j < taskWeightList.size(); j++) {
			totalWeight += taskWeightList.get(j);
		}
		
		if (totalWeight == 0) {
			return flag;
		}

		for (int i = 0; i < GsConst.MercenaryExpedition.EXPEDITION_TASK_SIZE; i++) 
		{
			int id;

			if (taskList.get(i) == null)
			{
				// 自动添加				
				if (totalWeight == 0) {
					throw new RuntimeException(String.format("random weight object exception playerId == %d ,ids = %s,weight = %s ",player.getId(),taskSeedList.toString(),taskWeightList.toString()));
				}
				
				id = GuaJiRand.randonWeightObject(taskSeedList, taskWeightList);
				addExpeditionTask(i, id);

				flag = 1;// 为空（未完成的跨天）替换
			} else {
				ExpeditionTask task = taskList.get(i);

				if (task.getStatus() != 1)// 不是进行中的
				{
					continue;
				}

				if (task.getCompleteTime() < GuaJiTime.getCalendar().getTimeInMillis()) {
					// 完成移出
					completeExpeditionTask(i, task.getId());
					MercenaryExpeditionCfg mercenaryExpeditionCfg = ConfigManager.getInstance().getConfigByKey(MercenaryExpeditionCfg.class,
							task.getId());

					// 奖励
					AwardItems awardItems = AwardItems.valueOf(mercenaryExpeditionCfg.getAward());
					QuestEventBus.fireQuestEventOneTime(QuestEventType.HERO_EXPEND_TIMES, player.getXid());

					Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.ROLE_EXPEDITION_COUNT, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
					hawkMsg.pushParam(1);
					GsApp.getInstance().postMsg(hawkMsg);

					MailManager.createMail(player.getEntity().getId(), Mail.MailType.Reward_VALUE, GsConst.MailId.EXPEDITION_TASK, "", awardItems,
							mercenaryExpeditionCfg.getName());

					sendOneTaskInfo(HP.code.MERCENERY_EXPEDITION_FAST_S_VALUE, task.getId());

					// 自动添加
					id = GuaJiRand.randonWeightObject(taskSeedList, taskWeightList);
					addExpeditionTask(i, id);

					flag = 2;// 完成替换

				} else {
					// 不变
				}
			}
		}

		return flag;
	}

	/**
	 * 计算下一个刷新时间点
	 * 
	 * @return
	 */
	private static int getNextRefreshQuestTime() {

		int var = (int) (GuaJiTime.getMillisecond() - GuaJiTime.getAM0Date().getTime()) / 1000;
		int nextRefreshTime = 0;

		if (refreshTimeList != null) {
			for (int time : refreshTimeList) {
				if (var <= time) {
					nextRefreshTime = time - var;
					return nextRefreshTime;
				}
			}
			nextRefreshTime = 24 * 3600 - 1 - var + refreshTimeList.get(1);
		}

		return nextRefreshTime;
	}

	/**
	 * 重置远征任务（补充+未开始的替换）
	 */
	public boolean resetExpeditionStatus() {
		boolean flag = false;
		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();
		LinkedList<ExpeditionTask> taskList = mercenaryExpeditionEntity.getExpeditionTaskList();

		for (int i = 0; i < GsConst.MercenaryExpedition.EXPEDITION_TASK_SIZE; i++) {
			int id;

			if (taskList.get(i) == null) {
				// 自动添加
				id = GuaJiRand.randonWeightObject(taskSeedList, taskWeightList);
				addExpeditionTask(i, id);

				flag = true;

			} else {
				// 以下直接替换
				ExpeditionTask task = taskList.get(i);

				if (task.getStatus() == 0) {
					// 重置的移出（未开始）
					completeExpeditionTask(i, task.getId());

					// 自动添加
					id = GuaJiRand.randonWeightObject(taskSeedList, taskWeightList);
					addExpeditionTask(i, id);

					flag = true;
				} else {
					// 不变
				}
			}
		}

		return flag;
	}

	/**
	 * 派遣
	 * 
	 * @param protocal
	 * @return
	 */
	protected boolean onDispatch(HPMercenaryDispatch protocal, int protocalId) {
		// 从id索引位置
		int id = protocal.getTaskId();
		List<Integer> roleList = protocal.getMercenaryIdList();

		int roleCount = roleList.size();
		
		if ((roleCount == 0)||(roleCount > GsConst.MercenaryExpedition.EXPEDITION_ONCE_ROLE )){
			sendError(protocalId, Status.error.TASK_ROLE_COUNT_ERROR);
			return false;
		}
		
		long time;
		
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.mercenary_Unlock)){
			player.sendError(protocalId, Status.error.CONDITION_NOT_ENOUGH);
			return true;
		}

		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();

		if (mercenaryExpeditionEntity == null) {
			return true;
		}

		ExpeditionTask task = mercenaryExpeditionEntity.getExpeditionTaskFromId(id);

		VipPrivilegeCfg vipPrivilegeCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());

		if (mercenaryExpeditionEntity.getDispatchCount() >= vipPrivilegeCfg.getBountyTimes()) {
			// 派遣次数达到上限
			sendError(protocalId, Status.error.DISPATCH_COUNT);

			return true;
		}

		if (task == null) {
			// 任务没有找到
			return true;
		}

		if (task.getStatus() != 0) {
			// 任务状态不对
			sendError(protocalId, Status.error.TASK_STATUS_ERROR);

			return true;
		}
		
		MercenaryExpeditionCfg mercenaryExpeditionCfg = ConfigManager.getInstance().getConfigByKey(MercenaryExpeditionCfg.class, id);
		
		if (mercenaryExpeditionCfg == null) {
			sendError(protocalId, Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}
		
		boolean starCheck = false; // 星等檢查
		boolean attrCheck = false; // 屬性檢查
		boolean classCheck = false;// 職業檢查
		
		for (int roleId : roleList) {
			RoleEntity role = player.getPlayerData().getRoleById(roleId);
			if (role == null  || role.getStatus() == Const.RoleStatus.EXPEDITION_VALUE
					|| role.getStatus() == Const.RoleStatus.MIXTASK_VALUE
					|| !role.isHero())	{
				// 非休息状态
				sendError(protocalId, Status.error.ROLE_STATUS_ERROR);
				return true;
			}
			if (role.getStarLevel() >= mercenaryExpeditionCfg.getMercenaryStar()) {
				starCheck = true;
			}
			
			if (role.getAttr() == mercenaryExpeditionCfg.getmercenaryAttr()) {
				attrCheck = true;
			}
			
			if (role.getProfession() == mercenaryExpeditionCfg.getmercenaryClass()) {
				classCheck = true;
			}
		}
		
		if (!starCheck || !attrCheck || !classCheck){
			sendError(protocalId, Status.error.ROLE_STATUS_ERROR);
			return true;			
		}

		// 一個英雄
//		int count = 0;
//		if (player.getPlayerData().getHero().size() >= 1) {
//			
//			for (RoleEntity curRole : player.getPlayerData().getHero()) {
//				if ((curRole.getStatus() == Const.RoleStatus.EXPEDITION_VALUE)||
//				(curRole.getStatus() == Const.RoleStatus.MIXTASK_VALUE)){
//					count++;
//				}
//			}
//
//			if (count >= GsConst.MercenaryExpedition.EXPEDITION_TASK_SIZE) {
//				// 已经最多有10个英雄在远征
//				sendError(protocalId, Status.error.TASK_ROLE_COUNT_ERROR);
//
//				return true;
//			}
//		}
		
		time = GuaJiTime.getCalendar().getTimeInMillis() + mercenaryExpeditionCfg.getAliveTime() * 60000;
		task.setCompleteTime(time);
		for (int roleId : roleList) {
			task.setDoingRoleId(roleId);
		}
		task.setStatus(1);// 进行中
		
		//7日之诗 快速战斗
		SevenDayQuestEventBus.fireQuestEventOneTime(SevenDayEventType.DEPUTYTRAVEL, player.getXid());
		
		mercenaryExpeditionEntity.increaseDispatchCount();

		
		for (int roleId : roleList) {
			RoleEntity role = player.getPlayerData().getRoleById(roleId);
			role.incStatus(Const.RoleStatus.EXPEDITION_VALUE);
			role.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(role.getId());
		}

		sendClientExpeditionInfo(HP.code.MERCENERY_DISPATCH_S_VALUE);

		mercenaryExpeditionEntity.reConvert();

		mercenaryExpeditionEntity.notifyUpdate();

		return true;
	}

	/**
	 * 快速结束
	 * 
	 * @param protocal
	 * @return
	 */
	protected boolean onFastComplete(HPMercenaryExpeditionFast protocal, int protocalId) {
		// 为了不改变约定好的协议，只能从id到位置索引了
		int id = protocal.getTaskId();
		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();
		ExpeditionTask task = mercenaryExpeditionEntity.getExpeditionTaskFromId(id);

		if (task == null) {
			// 任务没有找到
			return true;
		}

		if (task.getStatus() != 1) {
			// 任务状态不对
			sendError(protocalId, Status.error.TASK_STATUS_ERROR);

			return true;
		}

		long lastTime = task.getCompleteTime() - GuaJiTime.getCalendar().getTimeInMillis();

		if (lastTime < 0) {
			return true;
		}

		double lastMin = Math.ceil(lastTime / 60000);

		int fastCompleteCost = (int) Math.floor(Math.pow(lastMin, 0.75) * 0.5 + 0.5);
		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD, fastCompleteCost);
		if (!consumeItems.checkConsume(player, HP.code.MERCENERY_EXPEDITION_FAST_C_VALUE)) {
			return true;
		}

		consumeItems.consumeTakeAffect(player, Action.FAST_COMPLETE_EXPEDITION_TASK);

		// 完成一个
		int index = mercenaryExpeditionEntity.getExpeditionTaskPos(task.getId());
		completeExpeditionTask(index, task.getId());
		MercenaryExpeditionCfg mercenaryExpeditionCfg = ConfigManager.getInstance().getConfigByKey(MercenaryExpeditionCfg.class, task.getId());

		// 奖励
		AwardItems awardItems = AwardItems.valueOf(mercenaryExpeditionCfg.getAward());
		QuestEventBus.fireQuestEventOneTime(QuestEventType.HERO_EXPEND_TIMES, player.getXid());

		MailManager.createMail(player.getEntity().getId(), Mail.MailType.Reward_VALUE, GsConst.MailId.EXPEDITION_TASK, "", awardItems,
				mercenaryExpeditionCfg.getName());

		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.ROLE_EXPEDITION_COUNT, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(1);
		GsApp.getInstance().postMsg(hawkMsg);
		
		// 自动添加一个
		id = GuaJiRand.randonWeightObject(taskSeedList, taskWeightList);
		addExpeditionTask(index, id);

		sendOneTaskInfo(HP.code.MERCENERY_EXPEDITION_FAST_S_VALUE, task.getId());

		sendClientExpeditionInfo(HP.code.MERCENERY_EXPEDITION_INFO_S_VALUE);

		mercenaryExpeditionEntity.reConvert();

		mercenaryExpeditionEntity.notifyUpdate();

		return true;
	}

	/**
	 * 放弃任务
	 * 
	 * @param protocal
	 * @return
	 */
	protected boolean onGiveUpExpeditionTask(HPMercenaryExpeditionGiveUp protocal, int protocalId) {
		// 为了不改变约定好的协议，只能从id到位置索引了
		int id = protocal.getTaskId();
		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();
		ExpeditionTask task = mercenaryExpeditionEntity.getExpeditionTaskFromId(id);

		if (task == null) {
			// 任务没有找到
			return true;
		}

		if (task.getStatus() != 1) {
			// 任务状态不对
			sendError(protocalId, Status.error.TASK_STATUS_ERROR);

			return true;
		}
		
		for (int roleId :task.getDoingRoleId()) {
			RoleEntity role = player.getPlayerData().getRoleById(roleId);
			role.decStatus(Const.RoleStatus.EXPEDITION_VALUE);
			role.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(role.getId());
		} 

		task.setCompleteTime(-1);
		task.clearDoingRoleId();
		task.setStatus(0);// 进行中

		sendClientExpeditionInfo(HP.code.MERCENERY_EXPEDITION_GIVEUP_S_VALUE);

		mercenaryExpeditionEntity.reConvert();

		mercenaryExpeditionEntity.notifyUpdate();

		return true;
	}

	/**
	 * 重置远征任务
	 * 
	 * @return
	 */
	protected boolean onRefreshExpeditionTask(int protocalId) {
		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();

		int refreshCost = mercenaryExpeditionEntity.getNextRefreshCost();
		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD, refreshCost);
		if (!consumeItems.checkConsume(player, HP.code.MERCENERY_EXPEDITION_REFRESH_C_VALUE)) {
			return true;
		}

		consumeItems.consumeTakeAffect(player, Action.REFRESH_EXPEDITION_TASK);

		resetExpeditionStatus();

		mercenaryExpeditionEntity.increaseRefreshCount();

		int arg = mercenaryExpeditionEntity.getRefreshCount();

		if (arg == 4) {
			this.initTaskSeedList();
		}

		if (arg == 7) {
			this.initTaskSeedList();

		}

		sendClientExpeditionInfo(HP.code.MERCENERY_EXPEDITION_REFRESH_S_VALUE);

		mercenaryExpeditionEntity.reConvert();

		mercenaryExpeditionEntity.notifyUpdate();

		return true;
	}

	/**
	 * 派发远征任务信息(詳細)
	 */
	protected void sendClientExpeditionInfo(int proType) {
		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();

		VipPrivilegeCfg vipPrivilegeCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());

		int maxDispatchCount = vipPrivilegeCfg.getExpeditionTaskCount();
		int disPatchCount = mercenaryExpeditionEntity.getDispatchCount();
		int refreshCost = mercenaryExpeditionEntity.getNextRefreshCost();

		HPMercenaryExpeditionInfoRet.Builder mercenaryExpeditionInfoRetBuilder = HPMercenaryExpeditionInfoRet.newBuilder();

		LinkedList<ExpeditionTask> taskList = mercenaryExpeditionEntity.getExpeditionTaskList();
		
		if (taskList != null) {
			for (int i = 0; i < GsConst.MercenaryExpedition.EXPEDITION_TASK_SIZE; i++) {
				MercenaryExpeditionCfg mercenaryExpeditionCfg = ConfigManager.getInstance().getConfigByKey(MercenaryExpeditionCfg.class,
						taskList.get(i).getId());
	
				if (taskList.get(i) == null) {
					continue;
	
				} else {
	
					TaskItem.Builder item = TaskItem.newBuilder();
					item.setTaskId(taskList.get(i).getId());
					item.setTaskStatus(taskList.get(i).getStatus());
					item.setTaskRewards(mercenaryExpeditionCfg.getAward());
					item.addAllMercenaryId(taskList.get(i).getDoingRoleId());
	
					if (taskList.get(i).getCompleteTime() != -1) {
						item.setLastTimes((int) (taskList.get(i).getCompleteTime() - GuaJiTime.getCalendar().getTimeInMillis()));
					} else {
						item.setLastTimes(-1);
					}
					mercenaryExpeditionInfoRetBuilder.addAllTask(item);
				}
			}
		}
		mercenaryExpeditionInfoRetBuilder.setAllTimes(maxDispatchCount);
		mercenaryExpeditionInfoRetBuilder.setCurTimes(disPatchCount);
		mercenaryExpeditionInfoRetBuilder.setRefreshCost(refreshCost);
		mercenaryExpeditionInfoRetBuilder.setNextRefreshTime(getNextRefreshQuestTime());

		player.sendProtocol(Protocol.valueOf(proType, mercenaryExpeditionInfoRetBuilder));

		return;
	}
	
	/**
	 * 派发远征任务信息(簡略)
	 */
	protected void sendSimpleExpeditionInfo(int proType) {
		MercenaryExpeditionEntity mercenaryExpeditionEntity = player.getPlayerData().loadMercenaryExpeditionEntity();

		VipPrivilegeCfg vipPrivilegeCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());

		int maxDispatchCount = vipPrivilegeCfg.getExpeditionTaskCount();
		int disPatchCount = mercenaryExpeditionEntity.getDispatchCount();
		int refreshCost = mercenaryExpeditionEntity.getNextRefreshCost();
		
		HPMercenaryExpeditionInfoRet.Builder mercenaryExpeditionInfoRetBuilder = HPMercenaryExpeditionInfoRet.newBuilder();
		
		mercenaryExpeditionInfoRetBuilder.setAllTimes(maxDispatchCount);
		mercenaryExpeditionInfoRetBuilder.setCurTimes(disPatchCount);
		mercenaryExpeditionInfoRetBuilder.setRefreshCost(refreshCost);
		mercenaryExpeditionInfoRetBuilder.setNextRefreshTime(getNextRefreshQuestTime());

		player.sendProtocol(Protocol.valueOf(proType, mercenaryExpeditionInfoRetBuilder));
	}

	/**
	 * 派发一个远征任务信息
	 */
	public void sendOneTaskInfo(int proType, int taskId) {
		HPMercenaryExpeditionFinishRet.Builder fastRet = HPMercenaryExpeditionFinishRet.newBuilder();
		fastRet.setTaskId(taskId);

		player.sendProtocol(Protocol.valueOf(proType, fastRet));

		return;
	}

	/**
	 * 请求任务信息
	 * 
	 * @param protocal
	 * @return
	 */

	protected boolean onQuestExpeditionInfo(HPMercenaryExpeditionInfo protocal, int protocalId) {
		if (protocal.getAction() == 2) {
			sendSimpleExpeditionInfo(HP.code.MERCENERY_EXPEDITION_INFO_S_VALUE);
		} else {
			sendClientExpeditionInfo(HP.code.MERCENERY_EXPEDITION_INFO_S_VALUE);
		}
		return true;
	}

}
