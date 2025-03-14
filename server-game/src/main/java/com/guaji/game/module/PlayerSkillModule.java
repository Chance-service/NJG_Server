package com.guaji.game.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.battle.SkillUtil;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.config.NewSkillCfg;
import com.guaji.game.config.RoleCfg;
import com.guaji.game.config.SkillCfg;
import com.guaji.game.config.SkillEnhanceCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.SkillEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Const.itemType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Skill.HPSkillCarry;
import com.guaji.game.protocol.Skill.HPSkillCarryRet;
import com.guaji.game.protocol.Skill.HPSkillChangeOrder;
import com.guaji.game.protocol.Skill.HPSkillChangeOrderRet;
import com.guaji.game.protocol.SkillEnhance.HPSkillEnhanceOpenState;
import com.guaji.game.protocol.SkillEnhance.HPSkillLevelup;
import com.guaji.game.protocol.SkillEnhance.HPSkillLevelupRet;
import com.guaji.game.protocol.Status;

/**
 * 技能模块
 */
public class PlayerSkillModule extends PlayerModule {
	private final Comparator<NewSkillCfg> SKILL_COMPARATOR = new Comparator<NewSkillCfg>() {
		@Override
		public int compare(NewSkillCfg o1, NewSkillCfg o2) {
			return o1.getLevel() - o2.getLevel();
		}

	};

	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerSkillModule(Player player) {
		super(player);

		//listenProto(HP.code.SKILL_CHANGE_ORDER_C);
		//listenProto(HP.code.ROLE_CARRY_SKILL_C);
	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	@Override
	public boolean onTick() {
		return super.onTick();
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		return super.onMessage(msg);
	}

	/**
	 * 响应新任务;
	 * 
	 * @param msg
	 */
	@MessageHandlerAnno(code = GsConst.MsgType.NEW_QUEST_EVENT)
	private void onNewQuestEvent(Msg msg) {
		QuestEventType eventType = msg.getParam(0);
		// 技能专精
		if (eventType == QuestEventType.SKILL_ENHANCE_LEVEL) {
			// 推送技能专精等级事件
			QuestEventBus.fireQuestEvent(QuestEventType.SKILL_ENHANCE_LEVEL, player.getPlayerData().getSpecializeLevel(), player.getXid());
		}
	}

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		if (protocol.checkType(HP.code.SKILL_CHANGE_ORDER_C)) {
			//onSkillChangeOrder(protocol.parseProtocol(HPSkillChangeOrder.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.ROLE_CARRY_SKILL_C)) {
			//onRoleCarrySkill(protocol.parseProtocol(HPSkillCarry.getDefaultInstance()));
			return true;
		}
		return super.onProtocol(protocol);
	}

	/**
	 * 请求技能升级;
	 * 
	 * @param parseProtocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.SKILL_LEVELUP_C_VALUE)
	private void onSkillLevelup(Protocol protocol) {

		HPSkillLevelup levelupRequest = protocol.parseProtocol(HPSkillLevelup.getDefaultInstance());
		// 技能专精是否开启;
		if (!player.getEntity().isSkillEnhanceOpen()) {
			sendError(HP.code.SKILL_LEVELUP_C_VALUE, Status.error.SKILL_NOT_ENHANCE_LEVEL);
			return;
		}

		int itemId = levelupRequest.getItemId();
		int itemCount = levelupRequest.getItemCount();
		if (itemCount < 1) {
			sendError(HP.code.SKILL_LEVELUP_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 检查物品是否是升级所需的
		SkillEntity skillEntity = player.getPlayerData().getSkillByItemId(levelupRequest.getSkillId());
		if (skillEntity == null) {
			sendError(HP.code.SKILL_LEVELUP_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		// 是否满级
		if (skillEntity.getRealSkillLevel() >= SysBasicCfg.getInstance().getMaxSkillLevel()) {
			// 已经达到顶级;
			sendError(HP.code.SKILL_LEVELUP_C_VALUE, Status.error.ITEM_LEVEL_UP_TARGET_EMPTY);
			return;
		}
		// 获取本级技能升级模板
		SkillEnhanceCfg currentLevelConfig = getSkillLevelupConfig(skillEntity.getItemId(), skillEntity.getRealSkillLevel());
		if (currentLevelConfig == null) {
			sendError(HP.code.SKILL_LEVELUP_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		// 不是升级所需物品;
		if (!isLevelupItem(currentLevelConfig, itemId)) {
			sendError(HP.code.SKILL_LEVELUP_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		// 检查物品是否足够;
		if (!player.getPlayerData().checkItemEnough(itemType.TOOL, itemId, itemCount)) {
			sendError(HP.code.SKILL_LEVELUP_C_VALUE, Status.error.ITEM_NOT_ENOUGH);
			return;
		}
		// 消耗的物品配置;
		ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemId);
		if (itemCfg == null || itemCfg.getSkillExp() <= 0) {
			return;
		}
		// 物品经验是否足够触发升级?
		boolean isLevelup = onSkillLevelup(skillEntity, itemCfg, itemCount, protocol.getType());
		// 持久化
		skillEntity.notifyUpdate(true);
		// 同步技能信息;
		player.getPlayerData().syncSkillInfo(skillEntity.getId());

		player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
		// 调用刷新方法,把技能专精作为属性存储到人物身上
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), player.getPlayerData().getMainRole());

		HPSkillLevelupRet.Builder levelupResponse = HPSkillLevelupRet.newBuilder();
		levelupResponse.setIsLevelup(isLevelup);
		levelupResponse.setSkillId(skillEntity.getItemId());
		player.sendProtocol(Protocol.valueOf(HP.code.SKILL_LEVELUP_S, levelupResponse));
	}

	/**
	 * 升级指定的技能;会出现跨级的情况;
	 * 
	 * @param skillEntity
	 * @param addExp
	 *            添加的经验;
	 * @return true 表示升级成功, false 表示升级失败;
	 */
	private boolean onSkillLevelup(SkillEntity skillEntity, ItemCfg itemCfg, int itemCount, int code) {

		// 本次物品可添加的经验值
		int addExp = itemCfg.getSkillExp() * itemCount;
		if (addExp <= 0) {
			return false;
		}
		// 使用全部物品以后的经验
		int afterUseAllItemExp = skillEntity.getExp() + addExp;
		int currentLevel = skillEntity.getRealSkillLevel();
		int nextLevel = currentLevel + 1;
		// 获取本级技能升级模板(读下一等级2017/03/11 modify by callan)
		SkillEnhanceCfg nextLevelCfg = getSkillLevelupConfig(skillEntity.getItemId(), nextLevel);
		if (nextLevelCfg == null) {
			sendError(HP.code.SKILL_LEVELUP_C_VALUE, Status.error.CONFIG_NOT_FOUND);
			return false;
		}
		// 升到下级需要的总经验
		int nextNeedExp = nextLevelCfg.getSkillExp();
		// 升到下级需要添加的exp
		double nextLevelNeedAddExp = nextNeedExp - skillEntity.getExp();
		// 是否可以升到下级;
		if (addExp < nextLevelNeedAddExp) {
			skillEntity.setExp(afterUseAllItemExp);
			// 消耗物品
			consumeLevelupItems(itemCfg.getId(), itemCount);
			return false;
		}
		// 是否可以跨级;
		boolean isLevelup = false;
		while (true) {
			currentLevel = skillEntity.getRealSkillLevel();
			nextLevel = currentLevel + 1;
			nextLevelCfg = getSkillLevelupConfig(skillEntity.getItemId(), nextLevel);
			if (nextLevelCfg == null) {
				sendError(HP.code.SKILL_LEVELUP_C_VALUE, Status.error.CONFIG_NOT_FOUND);
				break;
			}
			nextNeedExp = nextLevelCfg.getSkillExp();
			nextLevelNeedAddExp = nextNeedExp - skillEntity.getExp();
			// 判断玩家等级限制和专精等级限制
			if (player.getLevel() < nextLevelCfg.getRoleLevel()) {
				sendError(HP.code.SKILL_LEVELUP_C_VALUE, Status.error.LEVEL_NOT_LIMIT);
				break;
			}
			if (player.getPlayerData().getSpecializeLevel() < nextLevelCfg.getSpecializeLevel()) {
				sendError(HP.code.SKILL_LEVELUP_C_VALUE, Status.error.SKILL_NOT_ENHANCE_LEVEL);
				break;
			}
			// 是否可以使用当前物品
			if (!isLevelupItem(nextLevelCfg, itemCfg.getId())) {
				return isLevelup;
			}
			if (itemCount < 1) {
				return isLevelup;
			}
			// 经验计算
			if (addExp >= nextLevelNeedAddExp) {
				// 需要消耗的物品个数计算
				int needCostItemCount = (int) Math.ceil(nextLevelNeedAddExp / itemCfg.getSkillExp());
				addExp -= nextLevelNeedAddExp;
				itemCount -= needCostItemCount;
				skillEntity.setSkillLevel(nextLevel);
				// 推送技能专精等级事件
				QuestEventBus.fireQuestEvent(QuestEventType.SKILL_ENHANCE_LEVEL, player.getPlayerData().getSpecializeLevel(), player.getXid());
				isLevelup = true;
				// 消耗物品, 需要根据规则计算消耗个数
				consumeLevelupItems(itemCfg.getId(), needCostItemCount);
				skillEntity.setExp((needCostItemCount * itemCfg.getSkillExp()) - (int) nextLevelNeedAddExp);
			} else {
				skillEntity.setExp(addExp + skillEntity.getExp());
				break;
			}
		}
		return isLevelup;
	}

	/**
	 * 消耗升级物品;
	 * 
	 * @param itemId
	 * @param itemCount
	 */
	private void consumeLevelupItems(int itemId, int itemCount) {
		// 消耗物品
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);
		if (itemEntity == null) {
			return;
		}
		// 添加经验
		consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemId, itemCount);
		consumeItems.consumeTakeAffect(player, Action.SKILL_LEVELUP);
	}

	/**
	 * 是否是当前升级所需的物品;
	 * 
	 * @param currentLevelConfig
	 * @param itemId
	 * @return
	 */
	private boolean isLevelupItem(SkillEnhanceCfg currentLevelConfig, int itemId) {
		for (ItemInfo eachInfo : currentLevelConfig.getLimitItemList()) {
			if (eachInfo.getItemId() == itemId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取升级模板;
	 * 
	 * @param skillId
	 * @param currentLevel
	 * @return
	 */
	private SkillEnhanceCfg getSkillLevelupConfig(int skillId, int currentLevel) {
		Collection<SkillEnhanceCfg> skills = ConfigManager.getInstance().getConfigMap(SkillEnhanceCfg.class).values();
		List<SkillEnhanceCfg> currentSkills = new ArrayList<SkillEnhanceCfg>();
		for (SkillEnhanceCfg eachConfig : skills) {
			if (eachConfig.getSkillId() == skillId) {
				currentSkills.add(eachConfig);
			}
		}
		for (SkillEnhanceCfg eachConfig : currentSkills) {
			if (eachConfig.getSkillLevel() == currentLevel) {
				return eachConfig;
			}
		}
		return null;
	}

	/**
	 * 请求开启技能专精;
	 * 
	 * @param parseProtocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.SKILL_ENHANCE_OPEN_C_VALUE)
	private void onOpenSkillEnhance(Protocol protocol) {
		// 当前是否开启专精
		if (player.getEntity().isSkillEnhanceOpen()) {
			return;
		}

		if (player.getLevel() < SysBasicCfg.getInstance().getSkillUpgradeLevel()) {
			sendError(HP.code.SKILL_ENHANCE_OPEN_C_VALUE, Status.error.NOT_LEVEL_OVER_LIMIT);
			return;
		}

		// 获取消耗信息;
		String itemStr = SysBasicCfg.getInstance().getOpenSkillEnhanceCostItems();
		if (itemStr == null || itemStr.isEmpty()) {
			sendError(HP.code.SKILL_ENHANCE_OPEN_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}

		List<ItemInfo> itemInfos = ItemInfo.valueListOf(itemStr);
		// 消耗物品
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		if (!consumeItems.addConsumeInfo(player.getPlayerData(), itemInfos)) {
			sendError(HP.code.SKILL_ENHANCE_OPEN_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
			return;
		}
		if (!consumeItems.checkConsume(player)) {
			sendError(HP.code.SKILL_ENHANCE_OPEN_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
			return;
		}
		consumeItems.consumeTakeAffect(player, Action.SKILL_OPEN_ENHANCE);
		// 同步信息
		player.getEntity().setSkillEnhanceOpen(true);
		player.getEntity().notifyUpdate(true);

		// 调用刷新方法,把技能专精作为属性存储到人物身上
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), player.getPlayerData().getMainRole());

		player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
		// 同步技能专精信息;
		HPSkillEnhanceOpenState.Builder skillEnhanceState = HPSkillEnhanceOpenState.newBuilder();
		skillEnhanceState.setIsOpen(player.getEntity().isSkillEnhanceOpen());
		player.sendProtocol(Protocol.valueOf(HP.code.SKILL_ENHANCE_OPEN_STATE_S, skillEnhanceState));
	}

	/**
	 * 玩家登陆处理(数据同步)
	 */
	@Override
	protected boolean onPlayerLogin() {
		// 从db加载
		//player.getPlayerData().loadSkillEntities();
		return true;
	}

	@Override
	protected boolean onPlayerAssemble() {
//		RoleEntity mainRoleEntity = player.getPlayerData().getMainRole();
//
//		// 修复技能所属角色id
//		List<SkillEntity> skillEntities = player.getPlayerData().getSkillEntities();
//		for (SkillEntity skillEntity : skillEntities) {
//			if (skillEntity.getRoleId() == 0 && mainRoleEntity != null) {
//				skillEntity.setRoleId(mainRoleEntity.getId());
//			}
//		}
//
//		// 解锁并修复
//		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
//		for (RoleEntity roleEntity : roleEntities) {
//			if (roleEntity.isHero()) {
//				continue;
//			}
//			// 解锁新技能
//			//RoleCfg roleCfg = roleEntity.getRoleCfg();
//			List<Integer> mainSkills = roleEntity.getRoleCfg().getSkillList();
//			List<Integer> secondSkills = roleEntity.getRoleCfg().getPassiveList();
//			//List<Integer> skillIds = roleEntity.getClassCfg().getSkillList();
//			List<NewSkillCfg> skillCfgs = new LinkedList<NewSkillCfg>();
//			for (Integer skillId : mainSkills) { 
//				NewSkillCfg skillCfg = ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, skillId);
//				if (skillCfg != null) {
//					skillCfgs.add(skillCfg);
//				}
//			}
//			
//			for (Integer skillId : secondSkills) { 
//				NewSkillCfg skillCfg = ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, skillId);
//				if (skillCfg != null) {
//					skillCfgs.add(skillCfg);
//				}
//			}
//			Collections.sort(skillCfgs, SKILL_COMPARATOR);
//			for (NewSkillCfg skillCfg : skillCfgs) {
//				// 已学
//				SkillEntity skillEntity = player.getPlayerData().getSkillByItemId(skillCfg.getId());
//				if (skillEntity != null && skillEntity.getRoleId() == roleEntity.getId()) {
//					// 已学技能不符合等级和技能限制的, 清除
//					if	(skillCfg.getLevel() > GameUtil.getRoleSkillLimitLevel(roleEntity)) {
//						// 清理已学的技能
//						player.getPlayerData().removeSkillByItemId(skillEntity.getItemId());
//
//						// 清理携带的技能
//						for (int i = 0; i < GsConst.MAX_SKILL_COUNT; i++) {
//							if (roleEntity != null && roleEntity.getSkillId(i) == skillEntity.getId()) {
//								roleEntity.setSkillId(i, 0);
//								roleEntity.notifyUpdate(true);
//							}
//						}
//					}
//					continue;
//				}
//
//				// 等级条件&职业限制达到
//				if	(skillCfg.getLevel() <= GameUtil.getRoleSkillLimitLevel(roleEntity)) {
//					player.getPlayerData().createSkill(roleEntity, skillCfg.getId());
//				}
//			}
//
//			// 清除身上错误的技能
//			for (int i = 0; roleEntity != null && i < GsConst.MAX_SKILL_COUNT; i++) {
//				if (roleEntity.getSkillId(i) != 0 && player.getPlayerData().getSkillById(roleEntity.getSkillId(i)) == null) {
//					roleEntity.setSkillId(i, 0);
//					roleEntity.notifyUpdate(true);
//				}
//			}
//		}
//
//		// 技能同步
//		player.getPlayerData().syncSkillInfo(0);
//
//		// 技能套数修复
//		List<Integer> skillId2List = mainRoleEntity.getSkill2idList();
//		if (skillId2List.size() == 0) {
//			// 如果技能为空 同步第一套的技能
//			int skillSlotNum = GameUtil.getSkillSlotNumByLevel(Const.roleType.MAIN_ROLE_VALUE);
//			for (int i = 0; i < GsConst.MAX_SKILL_COUNT; i++) {
//				if (mainRoleEntity != null && mainRoleEntity.getSkillId(i) >= 0 && i < skillSlotNum) {
//					skillId2List.add(mainRoleEntity.getSkillId(i));
//				}
//			}
//			mainRoleEntity.setSkill2idList(skillId2List);
//			mainRoleEntity.notifyUpdate(true);
//		}
//
//		List<Integer> skillId3List = mainRoleEntity.getSkill3idList();
//		if (skillId3List.size() == 0) {
//			// 如果技能为空 同步第一套的技能
//			int skillSlotNum = GameUtil.getSkillSlotNumByLevel(Const.roleType.MAIN_ROLE_VALUE);
//			for (int i = 0; i < GsConst.MAX_SKILL_COUNT; i++) {
//				if (mainRoleEntity != null && mainRoleEntity.getSkillId(i) >= 0 && i < skillSlotNum) {
//					skillId3List.add(mainRoleEntity.getSkillId(i));
//				}
//			}
//			mainRoleEntity.setSkill3idList(skillId3List);
//			mainRoleEntity.notifyUpdate(true);
//		}
//
//		// 清除身上错误的技能
//		for (int i = 0; mainRoleEntity != null && i < GsConst.MAX_SKILL_COUNT; i++) {
//			if (i >= skillId2List.size()) {
//				break;
//			}
//			if (skillId2List.get(i) != 0 && player.getPlayerData().getSkillById(skillId2List.get(i)) == null) {
//				skillId2List.set(i, 0);
//			}
//		}
//
//		for (int i = 0; mainRoleEntity != null && i < GsConst.MAX_SKILL_COUNT; i++) {
//			if (i >= skillId3List.size()) {
//				break;
//			}
//			if (skillId3List.get(i) != 0 && player.getPlayerData().getSkillById(skillId3List.get(i)) == null) {
//				skillId3List.set(i, 0);
//			}
//		}
//		// fix add mainskill
//		if ((mainRoleEntity != null)&&(mainRoleEntity.isNoSkill())) {
//			List<Integer> mainSkills = mainRoleEntity.getRoleCfg().getSkillList();
//			for (Integer skillId : mainSkills) { 
//				NewSkillCfg skillCfg = ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, skillId);
//				if (skillCfg != null) {
//					SkillEntity sEntity= player.getPlayerData().getSkillByItemId(skillCfg.getId());
//					if (sEntity != null) {
//						mainRoleEntity.addRoleSkill(sEntity.getId());
//					}
//				}
//			}
//		}
//
//		mainRoleEntity.notifyUpdate(true);
//
//		// 同步技能套数信息
//		SkillUtil.pushSkillInfo(player, null);

		return true;
	}

	/**
	 * 改变技能顺序
	 * 
	 * @param protocol
	 * @return
	 */
	protected boolean onSkillChangeOrder(HPSkillChangeOrder protocol) {
//		RoleEntity roleEntity = player.getPlayerData().getRoleById(protocol.getRoleId());
//		if (roleEntity == null) {
//			return false;
//		}
//
//		if (roleEntity.isHero()) {
//			int srcSkillId = roleEntity.getSkillId(protocol.getSrcOrder());
//			int dstSkillId = roleEntity.getSkillId(protocol.getDstOrder());
//			if (srcSkillId <= 0 || dstSkillId <= 0) {
//				sendError(HP.code.SKILL_CHANGE_ORDER_C_VALUE, Status.error.ROLE_SKILL_NOT_FOUND_VALUE);
//				return false;
//			}
//			roleEntity.setSkillId(protocol.getSrcOrder(), dstSkillId);
//			roleEntity.setSkillId(protocol.getDstOrder(), srcSkillId);
//
//			// 更新并同步
//			roleEntity.notifyUpdate(true);
//		} else {
//			int skillBag = protocol.getSkillBagId();
//			if (skillBag == 1) {
//				int srcSkillId = roleEntity.getSkillId(protocol.getSrcOrder());
//				int dstSkillId = roleEntity.getSkillId(protocol.getDstOrder());
//				if (srcSkillId <= 0 || dstSkillId <= 0) {
//					sendError(HP.code.SKILL_CHANGE_ORDER_C_VALUE, Status.error.ROLE_SKILL_NOT_FOUND_VALUE);
//					return false;
//				}
//				roleEntity.setSkillId(protocol.getSrcOrder(), dstSkillId);
//				roleEntity.setSkillId(protocol.getDstOrder(), srcSkillId);
//
//				// 更新并同步
//				roleEntity.notifyUpdate(true);
//			} else if (skillBag == 2) {
//				List<Integer> skillList = roleEntity.getSkill2idList();
//				int srcSkillId = skillList.get(protocol.getSrcOrder());
//				int dstSkillId = skillList.get(protocol.getDstOrder());
//				if (srcSkillId <= 0 || dstSkillId <= 0) {
//					sendError(HP.code.SKILL_CHANGE_ORDER_C_VALUE, Status.error.ROLE_SKILL_NOT_FOUND_VALUE);
//					return false;
//				}
//				skillList.set(protocol.getSrcOrder(), dstSkillId);
//				skillList.set(protocol.getDstOrder(), srcSkillId);
//				roleEntity.setSkill2idList(skillList);
//				roleEntity.setSkill3idList(skillList);
//
//				roleEntity.notifyUpdate(true);
//			} else if (skillBag == 3) {
//				List<Integer> skillList = roleEntity.getSkill3idList();
//				int srcSkillId = skillList.get(protocol.getSrcOrder());
//				int dstSkillId = skillList.get(protocol.getDstOrder());
//				if (srcSkillId <= 0 || dstSkillId <= 0) {
//					sendError(HP.code.SKILL_CHANGE_ORDER_C_VALUE, Status.error.ROLE_SKILL_NOT_FOUND_VALUE);
//					return false;
//				}
//				skillList.set(protocol.getSrcOrder(), dstSkillId);
//				skillList.set(protocol.getDstOrder(), srcSkillId);
//				roleEntity.setSkill3idList(skillList);
//
//				roleEntity.notifyUpdate(true);
//			}
//		}
//
//		sendProtocol(Protocol.valueOf(HP.code.SKILL_CHANGE_ORDER_S_VALUE, HPSkillChangeOrderRet.newBuilder().setVersion(1)));
//
//		SkillUtil.pushSkillInfo(player, roleEntity);

		return true;
	}

	protected boolean onRoleCarrySkill(HPSkillCarry protocol) {
//		RoleEntity roleEntity = player.getPlayerData().getRoleById(protocol.getRoleId());
//		if (roleEntity == null) {
//			sendError(HP.code.ROLE_CARRY_SKILL_C_VALUE, Status.error.ROLE_SKILL_NOT_FOUND_VALUE);
//			return false;
//		}
//		
//		if (roleEntity.isHero()) { // 限制英雄不能使用了
//			sendError(HP.code.ROLE_CARRY_SKILL_C_VALUE, Status.error.ROLE_SKILL_NOT_FOUND_VALUE);
//			return false;
//		}
//
//		// 开启的技能格子数
//		int skillSlotNum = GameUtil.getSkillSlotNumByLevel(roleEntity);
//		List<Integer> skillIds = new LinkedList<Integer>();
//		for (int i = 0; i < protocol.getSkillIdCount(); i++) {
//			int skillId = protocol.getSkillId(i);
//			if (skillId == 0) {
//				skillIds.add(0);
//				continue;
//			}
//
//			SkillEntity skillEntity = player.getPlayerData().getSkillById(skillId);
//			if (skillEntity == null || skillEntity.getRoleId() != roleEntity.getId()) {
//				sendError(HP.code.ROLE_CARRY_SKILL_C_VALUE, Status.error.ROLE_SKILL_NOT_FOUND_VALUE);
//				return false;
//			}
//
//			// 技能栏位限制
//			if (skillIds.size() >= skillSlotNum) {
//				break;
//			}
//
//			if (!skillIds.contains(skillId)) {
//				skillIds.add(skillId);
//			} else {
//				skillIds.add(0);
//			}
//		}
//
//		String oldSkills = "";
//		String newSkills = "";
//		if (roleEntity.isHero()) {
//			// 英雄使用預設技能不用配置
////			oldSkills = getRoleSkillItemIds(player, roleEntity);
////			if (skillIds.size() > 0) {
////				for (int i = 0; i < GsConst.MAX_SKILL_COUNT; i++) {
////					if (i < skillIds.size()) {
////						roleEntity.setSkillId(i, skillIds.get(i));
////					} else {
////						roleEntity.setSkillId(i, 0);
////					}
////				}
////				roleEntity.notifyUpdate(true);
////			}
//		} else {
//			int skillBag = protocol.getSkillBagId();
//			if (skillBag == 1) {
//				// 设置上阵技能
//				oldSkills = getRoleSkillItemIds(player, roleEntity);
//				if (skillIds.size() > 0) {
//					for (int i = 0; i < GsConst.MAX_SKILL_COUNT; i++) {
//						if (i < skillIds.size()) {
//							roleEntity.setSkillId(i, skillIds.get(i));
//						} else {
//							roleEntity.setSkillId(i, 0);
//						}
//					}
//					roleEntity.notifyUpdate(true);
//				}
//				newSkills = getRoleSkillItemIds(player, roleEntity);
//			}
////			} else if (skillBag == 2) {
////				oldSkills = GameUtil.join(roleEntity.getSkill2idList(), ",");
////				roleEntity.setSkill2idList(skillIds);
////				roleEntity.setSkill3idList(skillIds);
////				roleEntity.notifyUpdate(true);
////				newSkills = GameUtil.join(roleEntity.getSkill2idList(), ",");
////			} else if (skillBag == 3) {
////				oldSkills = GameUtil.join(roleEntity.getSkill3idList(), ",");
////				roleEntity.setSkill3idList(skillIds);
////				roleEntity.notifyUpdate(true);
////				newSkills = GameUtil.join(roleEntity.getSkill3idList(), ",");
////			}
//		}
//
//		HPSkillCarryRet.Builder builder = HPSkillCarryRet.newBuilder();
//		builder.setRoleId(protocol.getRoleId());
//		sendProtocol(Protocol.valueOf(HP.code.ROLE_CARRY_SKILL_S_VALUE, builder));
//
//		SkillUtil.pushSkillInfo(player, roleEntity);
//		
//		// 调用刷新方法,把技能专精作为属性存储到人物身上
//		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
//
//		// 在线玩家快照刷新
//		player.getPlayerData().refreshOnlinePlayerSnapshot();
//
//		BehaviorLogger.log4Platform(player, Action.SKILL_CARRAY, Params.valueOf("roleId", roleEntity.getItemId()),
//				Params.valueOf("oldSkills", oldSkills), Params.valueOf("newSkills", newSkills));

		return true;
	}

	private String getRoleSkillItemIds(Player player, RoleEntity roleEntity) {
//		int[] result = roleEntity.getSkillIds();
//		for (int i = 0; i < result.length; i++) {
//			int skillId = result[i];
//			if (skillId > 0) {
//				SkillEntity skillEntity = player.getPlayerData().getSkillById(skillId);
//				if (skillEntity != null) {
//					result[i] = skillEntity.getItemId();
//				}
//			}
//		}
		return GameUtil.join(Arrays.asList(roleEntity.getSkillAll()), ",");
	}
}
