package com.guaji.game.module;

import java.util.List;
import java.util.Map;

import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.ClassSoulCfg;
import com.guaji.game.config.ElementSoulCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.LeaderSoulCfg;
import com.guaji.game.config.SpriteSoulCfg;
import com.guaji.game.config.StarSoulCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.PlayerStarSoulEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.StarSoul.ActiveStarSoul;
import com.guaji.game.protocol.StarSoul.ActiveStarSoulRet;
import com.guaji.game.protocol.StarSoul.SyncStarSoul;
import com.guaji.game.protocol.StarSoul.SyncStarSoulRet;
import com.guaji.game.protocol.Status;

/**
 * 星魂模块(聖所)
 */
public class PlayerStarSoulModule extends PlayerModule {

	public PlayerStarSoulModule(Player player) {
		super(player);
		// 注册监听协议
		listenProto(HP.code.SYNC_STAR_SOUL_C);
		listenProto(HP.code.ACTIVE_STAR_SOUL_C);
		
		
		
		listenProto(HP.code.SYNC_SPRITE_SOUL_C);
		listenProto(HP.code.ACTIVE_SPRITE_SOUL_C);
		listenProto(HP.code.SYNC_LEADER_SOUL_C);
		listenProto(HP.code.ACTIVE_LEADER_SOUL_C);
		
		listenProto(HP.code.SYNC_CLASS_SOUL_C);
		listenProto(HP.code.ACTIVE_CLASS_SOUL_C);
		listenProto(HP.code.SYNC_ELEMENT_SOUL_C);
		listenProto(HP.code.ACTIVE_ELEMENT_SOUL_C);
	}

	/**
	 * 协议响应
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		// 模块内协议预判，本来约定读取一个开关值的
		/*if (isListenProto(protocol.getType())) {
			int level = player.getPlayerData().getPlayerEntity().getLevel();
			final int LIMIT = 40;
			if (level < LIMIT) {
				sendError(protocol.getType(), Status.error.LEVEL_NOT_LIMIT);
				return true;
			}
		}*/
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.LeaderSoul_Unlock)){
			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
			return true;
		}
		
		if (protocol.checkType(HP.code.SYNC_STAR_SOUL_C)) {
			onSyncStarSoul(protocol);
			return true;
		} else if (protocol.checkType(HP.code.ACTIVE_STAR_SOUL_C)) {
			onActiveStarSoul(protocol);
			return true;
		} else if (protocol.checkType(HP.code.SYNC_SPRITE_SOUL_C)) {
			onSyncSpriteSoul(protocol);
			return true;
		} else if (protocol.checkType(HP.code.ACTIVE_SPRITE_SOUL_C)) {
			onActiveSpriteSoul(protocol);
			return true;
		} else if (protocol.checkType(HP.code.SYNC_LEADER_SOUL_C)) {
			onSyncLeaderSoul(protocol);
			return true;
		} else if (protocol.checkType(HP.code.ACTIVE_LEADER_SOUL_C)) {
			onActiveLeaderSoul(protocol);
			return true;
		} else if (protocol.checkType(HP.code.SYNC_CLASS_SOUL_C)) {
			onSyncClassSoul(protocol);
			return true;
		} else if (protocol.checkType(HP.code.ACTIVE_CLASS_SOUL_C)) {
			onActiveClassSoul(protocol);
			return true;
		} else if (protocol.checkType(HP.code.SYNC_ELEMENT_SOUL_C)) {
			onSyncElementSoul(protocol);
			return true;
		} else if (protocol.checkType(HP.code.ACTIVE_ELEMENT_SOUL_C)) {
			onActiveElementSoul(protocol);
			return true;
		}
		return super.onProtocol(protocol);
	}

	/**
	 * 定时更新
	 */
	@Override
	public boolean onTick() {
		return super.onTick();
	}

	@Override
	protected boolean onPlayerLogin() {
		player.getPlayerData().loadPlayerStarSoulEntity();
		return super.onPlayerLogin();
	}

	/**
	 * 同步星魂信息
	 */
//	@ProtocolHandlerAnno(code = HP.code.SYNC_STAR_SOUL_C_VALUE)
	private void onSyncStarSoul(Protocol protocol) {
		// 解析协议
		SyncStarSoul request = protocol.parseProtocol(SyncStarSoul.getDefaultInstance());
		// 获取当前星脉
		int group = request.getGroup();
		// 只同步当前星脉的等级
		PlayerStarSoulEntity starSoulEntity = player.getPlayerData().getPlayerStarSoulEntity();
		Map<Integer, Integer> starSoulMap = starSoulEntity.getStarSoulMap();
		Integer level = starSoulMap.get(group);
		StarSoulCfg cfg = null;
		if (null == level) {
			// 初始的时候是没有记录的,更新到数据库
			level = 0;
			// 首次同步判断的是有没有这个星脉，防止客户端作弊
			cfg = StarSoulCfg.getCfg(group, level);
			if (null == cfg) {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
			starSoulMap.put(group, level);
			starSoulEntity.setStarSoulMap(starSoulMap);
			starSoulEntity.notifyUpdate();
		} else {
			// 是不是真的存在
			cfg = StarSoulCfg.getCfg(group, level);
			if (null == cfg) {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
		}
		// 构造响应包
		SyncStarSoulRet.Builder response = SyncStarSoulRet.newBuilder();
		response.setId(cfg.getId());
		sendProtocol(Protocol.valueOf(HP.code.SYNC_STAR_SOUL_S_VALUE, response));
	}

	/**
	 * 激活星魂
	 */
//	@ProtocolHandlerAnno(code = HP.code.ACTIVE_STAR_SOUL_C_VALUE)
	private void onActiveStarSoul(Protocol protocol) {
		ActiveStarSoul request = protocol.parseProtocol(ActiveStarSoul.getDefaultInstance());
		// 读取当前需要激活的Id
		int id = request.getId();
		StarSoulCfg cfg = StarSoulCfg.getStarSoulCfg(id);
		if (null == cfg) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		// 数据库记录是否是上一个星魂
		PlayerStarSoulEntity starSoulEntity = player.getPlayerData().getPlayerStarSoulEntity();
		Map<Integer, Integer> starSoulMap = starSoulEntity.getStarSoulMap();
		int group = cfg.getGroup();
		Integer level = starSoulMap.get(group);
		if (null == level){
			sendError(protocol.getType(), Status.error.STAR_SOUL_GROUP_ILLEGAL);
			return;
		}
		// 必须是相邻的两个
		if ((cfg.getLevel() - level) != 1) {
			sendError(protocol.getType(), Status.error.STAR_SOUL_LEVEL_ILLEGAL);
			return;
		}
		// 检测并消耗物品
		List<ItemInfo> costList = cfg.getActiveCostList();
		if (costList.size() > 0){
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			if(!consumeItems.addConsumeInfo(player.getPlayerData(), costList)){
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}
			if (!consumeItems.checkConsume(player)) {
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}
			consumeItems.consumeTakeAffect(player, Action.STAR_SOUL);
		}
		// 更新数据库
		starSoulMap.put(cfg.getGroup(), cfg.getLevel());
		starSoulEntity.setStarSoulMap(starSoulMap);
		starSoulEntity.notifyUpdate();
		RoleEntity mainRole = player.getPlayerData().getMainRole();
		Attribute oldAttribute = mainRole.getAttribute().clone();
		// 刷新英雄属性
		List<RoleEntity> herolist = player.getPlayerData().getRoleEntities();
		for (RoleEntity ahero : herolist) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), ahero);
		}
		player.getPlayerData().syncRoleInfo(0);
		// 主角属性飘字推送
		Attribute newAttribute = mainRole.getAttribute();
		PlayerUtil.pushAttrChange(player, oldAttribute, newAttribute);
		// 返回消息
		ActiveStarSoulRet.Builder response = ActiveStarSoulRet.newBuilder();
		response.setId(id);
		sendProtocol(Protocol.valueOf(HP.code.ACTIVE_STAR_SOUL_S_VALUE, response));
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.STAR_SOUL, Params.valueOf("group", group),	Params.valueOf("level", level));
	}
	/**
	 * 同步精靈星魂信息
	 */
//	@ProtocolHandlerAnno(code = HP.code.SYNC_SPRITE_SOUL_C_VALUE)
	private void onSyncSpriteSoul(Protocol protocol) {
		// 解析协议
		SyncStarSoul request = protocol.parseProtocol(SyncStarSoul.getDefaultInstance());
		// 获取当前星脉
		int group = request.getGroup();
		// 只同步当前星脉的等级
		PlayerStarSoulEntity SoulEntity = player.getPlayerData().getPlayerStarSoulEntity();
		Map<Integer, Integer> spriteSoulMap = SoulEntity.getSpriteSoulMap();
		Integer level = spriteSoulMap.get(group);
		SpriteSoulCfg cfg = null;
		if (null == level) {
			// 初始的时候是没有记录的,更新到数据库
			level = 0;
			// 首次同步判断的是有没有这个星脉，防止客户端作弊
			cfg = SpriteSoulCfg.getCfg(group, level);
			if (null == cfg) {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
			spriteSoulMap.put(group, level);
			SoulEntity.setSpriteSoulMap(spriteSoulMap);
			SoulEntity.notifyUpdate();
		} else {
			// 是不是真的存在
			cfg = SpriteSoulCfg.getCfg(group, level);
			if (null == cfg) {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
		}
		// 构造响应包
		SyncStarSoulRet.Builder response = SyncStarSoulRet.newBuilder();
		response.setId(cfg.getId());
		sendProtocol(Protocol.valueOf(HP.code.SYNC_SPRITE_SOUL_S_VALUE, response));
	}

	/**
	 * 激活精靈星魂
	 */
//	@ProtocolHandlerAnno(code = HP.code.ACTIVE_SPRITE_SOUL_C_VALUE)
	private void onActiveSpriteSoul(Protocol protocol) {
		ActiveStarSoul request = protocol.parseProtocol(ActiveStarSoul.getDefaultInstance());
		// 读取当前需要激活的Id
		int id = request.getId();
		SpriteSoulCfg cfg = SpriteSoulCfg.getSpriteSoulCfg(id);
		if (null == cfg) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		// 数据库记录是否是上一个星魂
		PlayerStarSoulEntity SoulEntity = player.getPlayerData().getPlayerStarSoulEntity();
		Map<Integer, Integer> spriteSoulMap = SoulEntity.getSpriteSoulMap();
		int group = cfg.getGroup();
		Integer level = spriteSoulMap.get(group);
		if (null == level){
			sendError(protocol.getType(), Status.error.STAR_SOUL_GROUP_ILLEGAL);
			return;
		}
		// 必须是相邻的两个
		if ((cfg.getLevel() - level) != 1) {
			sendError(protocol.getType(), Status.error.STAR_SOUL_LEVEL_ILLEGAL);
			return;
		}
		// 检测并消耗物品
		List<ItemInfo> costList = cfg.getActiveCostList();
		if (costList.size() > 0){
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			if(!consumeItems.addConsumeInfo(player.getPlayerData(), costList)){
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}
			if (!consumeItems.checkConsume(player)) {
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}
			consumeItems.consumeTakeAffect(player, Action.SPRITE_SOUL);
		}
		// 更新数据库
		spriteSoulMap.put(cfg.getGroup(), cfg.getLevel());
		SoulEntity.setSpriteSoulMap(spriteSoulMap);
		SoulEntity.notifyUpdate();
		RoleEntity mainRole = player.getPlayerData().getMainRole();
		Attribute oldAttribute = mainRole.getAttribute().clone();
		// 刷新英雄属性
		List<RoleEntity> herolist = player.getPlayerData().getRoleEntities();
		for (RoleEntity ahero : herolist) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), ahero);
		}
		player.getPlayerData().syncRoleInfo(0);
		// 主角属性飘字推送
		Attribute newAttribute = mainRole.getAttribute();
		PlayerUtil.pushAttrChange(player, oldAttribute, newAttribute);
		// 返回消息
		ActiveStarSoulRet.Builder response = ActiveStarSoulRet.newBuilder();
		response.setId(id);
		sendProtocol(Protocol.valueOf(HP.code.ACTIVE_SPRITE_SOUL_S_VALUE, response));
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.SPRITE_SOUL, Params.valueOf("group", group),	Params.valueOf("level", level));
	}
	
	/**
	 * 同步玩家主角星魂信息
	 */
//	@ProtocolHandlerAnno(code = HP.code.SYNC_SPRITE_SOUL_C_VALUE)
	private void onSyncLeaderSoul(Protocol protocol) {
		// 解析协议
		SyncStarSoul request = protocol.parseProtocol(SyncStarSoul.getDefaultInstance());
		// 获取当前星脉
		int group = request.getGroup();
		// 只同步当前星脉的等级
		PlayerStarSoulEntity SoulEntity = player.getPlayerData().getPlayerStarSoulEntity();
		Map<Integer, Integer> SoulMap = SoulEntity.getLeaderSoulMap();
		Integer level = SoulMap.get(group);
		LeaderSoulCfg cfg = null;
		if (null == level) {
			// 初始的时候是没有记录的,更新到数据库
			level = 0;
			// 首次同步判断的是有没有这个星脉，防止客户端作弊
			cfg = LeaderSoulCfg.getCfg(group, level);
			if (null == cfg) {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
			SoulMap.put(group, level);
			SoulEntity.setLeaderSoulMap(SoulMap);
			SoulEntity.notifyUpdate();
		} else {
			// 是不是真的存在
			cfg = LeaderSoulCfg.getCfg(group, level);
			if (null == cfg) {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
		}
		// 构造响应包
		SyncStarSoulRet.Builder response = SyncStarSoulRet.newBuilder();
		response.setId(cfg.getId());
		sendProtocol(Protocol.valueOf(HP.code.SYNC_LEADER_SOUL_S, response));
	}

	/**
	 * 激活主角星魂
	 */
//	@ProtocolHandlerAnno(code = HP.code.ACTIVE_SPRITE_SOUL_C_VALUE)
	private void onActiveLeaderSoul(Protocol protocol) {
		ActiveStarSoul request = protocol.parseProtocol(ActiveStarSoul.getDefaultInstance());
		// 读取当前需要激活的Id
		int id = request.getId();
		LeaderSoulCfg cfg = LeaderSoulCfg.getSoulCfg(id);
		if (null == cfg) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		// 数据库记录是否是上一个星魂
		PlayerStarSoulEntity SoulEntity = player.getPlayerData().getPlayerStarSoulEntity();
		Map<Integer, Integer> SoulMap = SoulEntity.getLeaderSoulMap();
		int group = cfg.getGroup();
		Integer level = SoulMap.get(group);
		if (null == level){
			sendError(protocol.getType(), Status.error.STAR_SOUL_GROUP_ILLEGAL);
			return;
		}
		// 必须是相邻的两个
		if ((cfg.getLevel() - level) != 1) {
			sendError(protocol.getType(), Status.error.STAR_SOUL_LEVEL_ILLEGAL);
			return;
		}
		// 检测并消耗物品
		List<ItemInfo> costList = cfg.getActiveCostList();
		if (costList.size() > 0){
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			if(!consumeItems.addConsumeInfo(player.getPlayerData(), costList)){
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}
			if (!consumeItems.checkConsume(player)) {
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}
			consumeItems.consumeTakeAffect(player, Action.SPRITE_SOUL);
		}
		// 更新数据库
		SoulMap.put(cfg.getGroup(), cfg.getLevel());
		SoulEntity.setLeaderSoulMap(SoulMap);
		SoulEntity.notifyUpdate();
		RoleEntity mainRole = player.getPlayerData().getMainRole();
		Attribute oldAttribute = mainRole.getAttribute().clone();
		// 刷新英雄属性
		List<RoleEntity> herolist = player.getPlayerData().getRoleEntities();
		for (RoleEntity ahero : herolist) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), ahero);
		}
		player.getPlayerData().syncRoleInfo(0);
		// 主角属性飘字推送
		Attribute newAttribute = mainRole.getAttribute();
		PlayerUtil.pushAttrChange(player, oldAttribute, newAttribute);
		// 返回消息
		ActiveStarSoulRet.Builder response = ActiveStarSoulRet.newBuilder();
		response.setId(id);
		sendProtocol(Protocol.valueOf(HP.code.ACTIVE_LEADER_SOUL_S, response));
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.LEADER_SOUL, Params.valueOf("group", group),	Params.valueOf("level", level));
	}
	
	/**
	 * 同步職業星魂信息
	 */
	private void onSyncClassSoul(Protocol protocol) {
		// 解析协议
		SyncStarSoul request = protocol.parseProtocol(SyncStarSoul.getDefaultInstance());
		// 获取当前星脉
		int group = request.getGroup();
		// 只同步当前星脉的等级
		PlayerStarSoulEntity starSoulEntity = player.getPlayerData().getPlayerStarSoulEntity();
		Map<Integer, Integer> starSoulMap = starSoulEntity.getClassSoulMap();
		Integer level = starSoulMap.get(group);
		ClassSoulCfg cfg = null;
		if (null == level) {
			// 初始的时候是没有记录的,更新到数据库
			level = 0;
			// 首次同步判断的是有没有这个星脉，防止客户端作弊
			cfg = ClassSoulCfg.getCfg(group, level);
			if (null == cfg) {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
			starSoulMap.put(group, level);
			starSoulEntity.setClassSoulMap(starSoulMap);
			starSoulEntity.notifyUpdate();
		} else {
			// 是不是真的存在
			cfg = ClassSoulCfg.getCfg(group, level);
			if (null == cfg) {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
		}
		// 构造响应包
		SyncStarSoulRet.Builder response = SyncStarSoulRet.newBuilder();
		response.setId(cfg.getId());
		sendProtocol(Protocol.valueOf(HP.code.SYNC_CLASS_SOUL_S_VALUE, response));
	}

	/**
	 * 激活職業聖所
	 */
	private void onActiveClassSoul(Protocol protocol) {
		ActiveStarSoul request = protocol.parseProtocol(ActiveStarSoul.getDefaultInstance());
		// 读取当前需要激活的Id
		int id = request.getId();
		ClassSoulCfg cfg = ClassSoulCfg.getClassSoulCfg(id);
		if (null == cfg) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		// 数据库记录是否是上一个星魂
		PlayerStarSoulEntity starSoulEntity = player.getPlayerData().getPlayerStarSoulEntity();
		Map<Integer, Integer> starSoulMap = starSoulEntity.getClassSoulMap();
		int group = cfg.getGroup();
		Integer level = starSoulMap.get(group);
		if (null == level){
			sendError(protocol.getType(), Status.error.STAR_SOUL_GROUP_ILLEGAL);
			return;
		}
		// 必须是相邻的两个
		if ((cfg.getLevel() - level) != 1) {
			sendError(protocol.getType(), Status.error.STAR_SOUL_LEVEL_ILLEGAL);
			return;
		}
		// 检测并消耗物品
		List<ItemInfo> costList = cfg.getActiveCostList();
		if (costList.size() > 0){
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			if(!consumeItems.addConsumeInfo(player.getPlayerData(), costList)){
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}
			if (!consumeItems.checkConsume(player)) {
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}
			consumeItems.consumeTakeAffect(player, Action.CLASS_SOUL);
		}
		// 更新数据库
		starSoulMap.put(cfg.getGroup(), cfg.getLevel());
		starSoulEntity.setClassSoulMap(starSoulMap);
		starSoulEntity.notifyUpdate();
		RoleEntity mainRole = player.getPlayerData().getMainRole();
		Attribute oldAttribute = mainRole.getAttribute().clone();
		// 刷新英雄属性
		List<RoleEntity> herolist = player.getPlayerData().getRoleEntities();
		for (RoleEntity ahero : herolist) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), ahero);
		}
		player.getPlayerData().syncRoleInfo(0);
		// 主角属性飘字推送
		Attribute newAttribute = mainRole.getAttribute();
		PlayerUtil.pushAttrChange(player, oldAttribute, newAttribute);
		// 返回消息
		ActiveStarSoulRet.Builder response = ActiveStarSoulRet.newBuilder();
		response.setId(id);
		sendProtocol(Protocol.valueOf(HP.code.ACTIVE_CLASS_SOUL_S_VALUE, response));
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.CLASS_SOUL, Params.valueOf("group", group),	Params.valueOf("level", level));
	}
	
	/**
	 * 同步(元素)屬性星魂信息
	 */
	private void onSyncElementSoul(Protocol protocol) {
		// 解析协议
		SyncStarSoul request = protocol.parseProtocol(SyncStarSoul.getDefaultInstance());
		// 获取当前星脉
		int group = request.getGroup();
		// 只同步当前星脉的等级
		PlayerStarSoulEntity starSoulEntity = player.getPlayerData().getPlayerStarSoulEntity();
		Map<Integer, Integer> starSoulMap = starSoulEntity.getElementSoulMap();
		Integer level = starSoulMap.get(group);
		ElementSoulCfg cfg = null;
		if (null == level) {
			// 初始的时候是没有记录的,更新到数据库
			level = 0;
			// 首次同步判断的是有没有这个星脉，防止客户端作弊
			cfg = ElementSoulCfg.getCfg(group, level);
			if (null == cfg) {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
			starSoulMap.put(group, level);
			starSoulEntity.setElementSoulMap(starSoulMap);
			starSoulEntity.notifyUpdate();
		} else {
			// 是不是真的存在
			cfg = ElementSoulCfg.getCfg(group, level);
			if (null == cfg) {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
		}
		// 构造响应包
		SyncStarSoulRet.Builder response = SyncStarSoulRet.newBuilder();
		response.setId(cfg.getId());
		sendProtocol(Protocol.valueOf(HP.code.SYNC_ELEMENT_SOUL_S_VALUE, response));
	}

	/**
	 * 激活(元素)屬性聖所
	 */
	private void onActiveElementSoul(Protocol protocol) {
		ActiveStarSoul request = protocol.parseProtocol(ActiveStarSoul.getDefaultInstance());
		// 读取当前需要激活的Id
		int id = request.getId();
		ElementSoulCfg cfg = ElementSoulCfg.getElementSoulCfg(id);
		if (null == cfg) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		// 数据库记录是否是上一个星魂
		PlayerStarSoulEntity starSoulEntity = player.getPlayerData().getPlayerStarSoulEntity();
		Map<Integer, Integer> starSoulMap = starSoulEntity.getElementSoulMap();
		int group = cfg.getGroup();
		Integer level = starSoulMap.get(group);
		if (null == level){
			sendError(protocol.getType(), Status.error.STAR_SOUL_GROUP_ILLEGAL);
			return;
		}
		// 必须是相邻的两个
		if ((cfg.getLevel() - level) != 1) {
			sendError(protocol.getType(), Status.error.STAR_SOUL_LEVEL_ILLEGAL);
			return;
		}
		// 检测并消耗物品
		List<ItemInfo> costList = cfg.getActiveCostList();
		if (costList.size() > 0){
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			if(!consumeItems.addConsumeInfo(player.getPlayerData(), costList)){
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}
			if (!consumeItems.checkConsume(player)) {
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}
			consumeItems.consumeTakeAffect(player, Action.ELEMENT_SOUL);
		}
		// 更新数据库
		starSoulMap.put(cfg.getGroup(), cfg.getLevel());
		starSoulEntity.setElementSoulMap(starSoulMap);
		starSoulEntity.notifyUpdate();
		RoleEntity mainRole = player.getPlayerData().getMainRole();
		Attribute oldAttribute = mainRole.getAttribute().clone();
		// 刷新英雄属性
		List<RoleEntity> herolist = player.getPlayerData().getRoleEntities();
		for (RoleEntity ahero : herolist) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), ahero);
		}
		player.getPlayerData().syncRoleInfo(0);
		// 主角属性飘字推送
		Attribute newAttribute = mainRole.getAttribute();
		PlayerUtil.pushAttrChange(player, oldAttribute, newAttribute);
		// 返回消息
		ActiveStarSoulRet.Builder response = ActiveStarSoulRet.newBuilder();
		response.setId(id);
		sendProtocol(Protocol.valueOf(HP.code.ACTIVE_ELEMENT_SOUL_S_VALUE, response));
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ELEMENT_SOUL, Params.valueOf("group", group),	Params.valueOf("level", level));
	}
}
