package com.guaji.game.module;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.net.GuaJiNetManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.EquipCfg;
import com.guaji.game.config.EquipForgeCfg;
import com.guaji.game.config.EquipGodCreateCfg;
import com.guaji.game.config.EquipPunchCfg;
import com.guaji.game.config.EquipSmeltCfg;
import com.guaji.game.config.EquipStrengthRatioCfg;
import com.guaji.game.config.FetterEquipCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.GodlyLevelExpCfg;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.config.LevelExpCfg;
import com.guaji.game.config.RoleEquipCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.TitleCfg;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.entity.MutualEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.module.activity.monthcard.MonthCardStatus;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.module.sevendaylogin.SevenDayQuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Const.itemType;
import com.guaji.game.protocol.EquipOpr.EquipForgeReq;
import com.guaji.game.protocol.EquipOpr.EquipForgeRes;
import com.guaji.game.protocol.EquipOpr.EquipOpenMutualReq;
import com.guaji.game.protocol.EquipOpr.EquipOpenMutualResp;
import com.guaji.game.protocol.EquipOpr.ForgeInfo;
import com.guaji.game.protocol.EquipOpr.HPEquipBaptize;
import com.guaji.game.protocol.EquipOpr.HPEquipBaptizeRet;
import com.guaji.game.protocol.EquipOpr.HPEquipCompound;
import com.guaji.game.protocol.EquipOpr.HPEquipCompoundRet;
import com.guaji.game.protocol.EquipOpr.HPEquipCreateRet;
import com.guaji.game.protocol.EquipOpr.HPEquipDecompose;
import com.guaji.game.protocol.EquipOpr.HPEquipDecomposeRet;
import com.guaji.game.protocol.EquipOpr.HPEquipDress;
import com.guaji.game.protocol.EquipOpr.HPEquipDressRet;
import com.guaji.game.protocol.EquipOpr.HPEquipEnhance;
import com.guaji.game.protocol.EquipOpr.HPEquipEnhanceReset;
import com.guaji.game.protocol.EquipOpr.HPEquipEnhanceRet;
import com.guaji.game.protocol.EquipOpr.HPEquipEvolution;
import com.guaji.game.protocol.EquipOpr.HPEquipEvolutionRet;
import com.guaji.game.protocol.EquipOpr.HPEquipExtend;
import com.guaji.game.protocol.EquipOpr.HPEquipExtendRet;
import com.guaji.game.protocol.EquipOpr.HPEquipOneKeyDress;
import com.guaji.game.protocol.EquipOpr.HPEquipOneKeyDressRet;
import com.guaji.game.protocol.EquipOpr.HPEquipOneKeyEnhance;
import com.guaji.game.protocol.EquipOpr.HPEquipOneKeyEnhanceRet;
import com.guaji.game.protocol.EquipOpr.HPEquipPunch;
import com.guaji.game.protocol.EquipOpr.HPEquipPunchRet;
import com.guaji.game.protocol.EquipOpr.HPEquipSell;
import com.guaji.game.protocol.EquipOpr.HPEquipSellRet;
import com.guaji.game.protocol.EquipOpr.HPEquipSmelt;
import com.guaji.game.protocol.EquipOpr.HPEquipSmeltInfoRet;
import com.guaji.game.protocol.EquipOpr.HPEquipSmeltRet;
import com.guaji.game.protocol.EquipOpr.HPEquipSpecialCreate;
import com.guaji.game.protocol.EquipOpr.HPEquipSpecialCreateRet;
import com.guaji.game.protocol.EquipOpr.HPEquipStoneDress;
import com.guaji.game.protocol.EquipOpr.HPEquipStoneDressRet;
import com.guaji.game.protocol.EquipOpr.HPEquipStoneExchange;
import com.guaji.game.protocol.EquipOpr.HPEquipStoneExchangeRet;
import com.guaji.game.protocol.EquipOpr.HPEquipStoneUndress;
import com.guaji.game.protocol.EquipOpr.HPEquipStoneUndressRet;
import com.guaji.game.protocol.EquipOpr.HPEquipSuperBaptize;
import com.guaji.game.protocol.EquipOpr.HPEquipSwallow;
import com.guaji.game.protocol.EquipOpr.HPEquipSwallowRet;
import com.guaji.game.protocol.EquipOpr.HPEquipUpgrade;
import com.guaji.game.protocol.EquipOpr.HPEquipUpgradeRet;
import com.guaji.game.protocol.EquipOpr.OpenMutuaInfolResp;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Reward.RewardInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Status.error;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.AwardUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.EquipUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;
import com.guaji.game.util.WeightUtil;
import com.guaji.game.util.WeightUtil.WeightItem;

/**
 * 装备模块
 * 
 * @author hawk
 */
public class PlayerEquipModule extends PlayerModule {

	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerEquipModule(Player player) {

		super(player);
		//listenProto(HP.code.EQUIP_BAPTIZE_C);
		//listenProto(HP.code.EQUIP_SUPER_BAPTIZE_C);
		listenProto(HP.code.EQUIP_DRESS_C);
		listenProto(HP.code.EQUIP_ONEKEY_DRESS_C);
		listenProto(HP.code.EQUIP_ENHANCE_C);
		//listenProto(HP.code.EQUIP_EXTEND_C);
		//listenProto(HP.code.EQUIP_SWALLOW_C);
		//listenProto(HP.code.EQUIP_SMELT_C);
		//listenProto(HP.code.EQUIP_SMELT_INFO_C);
		//listenProto(HP.code.EQUIP_SMELT_REFRESH_C);
		//listenProto(HP.code.EQUIP_SMELT_CREATE_C);
		//listenProto(HP.code.EQUIP_STONE_UNDRESS_C);
		//listenProto(HP.code.EQUIP_BAG_EXTEND_C);
		//listenProto(HP.code.EQUIP_SELL_C);
		//listenProto(HP.code.EQUIP_PUNCH_C);
		//listenProto(HP.code.EQUIP_STONE_DRESS_C);
		//listenProto(HP.code.EQUIP_SPECIAL_CREATE_C);
		//listenProto(HP.code.EQUIP_COMPOUND_C);
		//listenProto(HP.code.EQUIP_STONE_EXCHANGE_C);
		//listenProto(HP.code.EQUIP_STONE_BUY_C);
		listenProto(HP.code.EQUIP_UPGRADE_C);
		listenProto(HP.code.EQUIP_FORGE_C);
		listenProto(HP.code.EQUIP_ENHANCE_RESET_C);
		
		
		// 裝備相生先關閉
		//listenProto(HP.code.EQUIP_OPEN_MUTUAL_C);
		//listenProto(HP.code.FETCH_OPEN_MUTUAL_INFO_C);
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
		// 强化等级
		if (eventType == QuestEventType.EQUIP_ENHANCE_LEVEL || eventType == QuestEventType.COMMON_GOD_EQUIP_LEVEL
				|| eventType == QuestEventType.HOUNOR_GOD_EQUIP_LEVEL) {
			List<EquipEntity> entities = player.getPlayerData().getEquipEntities();
			for (EquipEntity eachEntity : entities) {
				// 推送装备强化事件
				if (eventType == QuestEventType.EQUIP_ENHANCE_LEVEL) {
					QuestEventBus.fireQuestEvent(QuestEventType.EQUIP_ENHANCE_LEVEL, eachEntity.getStrength(),
							player.getXid());
					continue;
				}
				// 推送普通神器等级事件
				if (eachEntity.isCommonGodly() && eventType == QuestEventType.COMMON_GOD_EQUIP_LEVEL) {
					QuestEventBus.fireQuestEvent(QuestEventType.COMMON_GOD_EQUIP_LEVEL, eachEntity.getStarLevel(),
							player.getXid());
				}
				// 推送普通神器等级事件
				if (eachEntity.isHounorGodly() && eventType == QuestEventType.HOUNOR_GOD_EQUIP_LEVEL) {
					QuestEventBus.fireQuestEvent(QuestEventType.HOUNOR_GOD_EQUIP_LEVEL, eachEntity.getStarLevel2(),
							player.getXid());
				}

			}
		}
	}

	/**
	 * 响应新任务;
	 * 
	 * @param msg
	 */
	@MessageHandlerAnno(code = GsConst.MsgType.REDUCE_PLAYER_EQUIP_EXP)
	private void onReduceEquipExp(Msg msg) {
		Map<Long, Integer[]> map = msg.getParam(0);
		rollbackEquip(map);
	}

	/**
	 * 玩家登陆处理(数据同步)
	 */
	@Override
	protected boolean onPlayerLogin() {

		List<EquipEntity> tmpequipEntities = player.getPlayerData().loadEquipEntities();
		for (EquipEntity equipEntity : tmpequipEntities) {

			equipEntity.convert();
			// 修补传承后不返还强化材料
			if (equipEntity.getStrength() > 0 && equipEntity.getStrengthItemMap().size() <= 0) {
				List<ItemInfo> countItem = new ArrayList<ItemInfo>();
				for (int i = 1; i <= equipEntity.getStrength(); i++) {
					List<ItemInfo> itemList = EquipStrengthRatioCfg.getStrengthConstItem(equipEntity.getEquipId(), i);
					if (itemList.size() > 0) {
						countItem.addAll(itemList);
					}
				}
				equipEntity.addEquipItemCount(countItem);
			}

			EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
			EquipUtil.checkStarExp(player, equipEntity);
		}

		player.getPlayerData().syncDressedEquipInfo();
		// 載入武器相生
		player.getPlayerData().loadMutualEntity();
		return true;
	}

	@Override
	protected boolean onPlayerAssemble() {
		player.getPlayerData().syncUnDressedEquipInfo();
		return true;
	}

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {

		if (protocol.checkType(HP.code.EQUIP_DRESS_C)) {
			// 装备穿戴
			HPEquipDress equipDressParams = protocol.parseProtocol(HPEquipDress.getDefaultInstance());
			onEquipDressRole(protocol.getType(), equipDressParams);
		}else if (protocol.checkType(HP.code.EQUIP_ONEKEY_DRESS_C)) {
			// 装备一鍵穿戴
			HPEquipOneKeyDress equipDressParams = protocol.parseProtocol(HPEquipOneKeyDress.getDefaultInstance());
			onEquipOneKeyDressRole(protocol.getType(), equipDressParams);
		} else if (protocol.checkType(HP.code.EQUIP_ENHANCE_C)) {
			// 装备强化
			HPEquipEnhance equipEnhanceParams = protocol.parseProtocol(HPEquipEnhance.getDefaultInstance());
			onEquipEnhance(protocol.getType(), equipEnhanceParams);
//		} else if (protocol.checkType(HP.code.EQUIP_ONEKEYENHANCE_C_VALUE)) {
//			// 一键装备强化
//			HPEquipOneKeyEnhance equipEnhanceParams = protocol.parseProtocol(HPEquipOneKeyEnhance.getDefaultInstance());
//			onEquipOneKeyEnhance(protocol.getType(), equipEnhanceParams);
//		} else if (protocol.checkType(HP.code.EQUIP_SWALLOW_C)) {
//			// 装备吞噬
//			HPEquipSwallow equipSwallowParams = protocol.parseProtocol(HPEquipSwallow.getDefaultInstance());
//			onEquipSwallow(protocol.getType(), equipSwallowParams);
//		} else if (protocol.checkType(HP.code.EQUIP_EXTEND_C)) {
			// 装备传承
//			HPEquipExtend equipExtendParams = protocol.parseProtocol(HPEquipExtend.getDefaultInstance());
//			onEquipExtend(protocol.getType(), equipExtendParams);
//		} else if (protocol.checkType(HP.code.EQUIP_BAPTIZE_C)) {
//			// 装备洗炼
//			HPEquipBaptize equipBaptizeParams = protocol.parseProtocol(HPEquipBaptize.getDefaultInstance());
//			onEquipBaptize(protocol.getType(), equipBaptizeParams);
//		} else if (protocol.checkType(HP.code.EQUIP_SUPER_BAPTIZE_C)) {
//			// 高级装备洗炼
//			HPEquipSuperBaptize equipBaptizeParams = protocol.parseProtocol(HPEquipSuperBaptize.getDefaultInstance());
//			onEquipSuperBaptize(protocol.getType(), equipBaptizeParams);
//		} else if (protocol.checkType(HP.code.EQUIP_PUNCH_C)) {
//			// 装备打孔
//			HPEquipPunch equipPunchParams = protocol.parseProtocol(HPEquipPunch.getDefaultInstance());
//			onEquipPunch(protocol.getType(), equipPunchParams);
//		} else if (protocol.checkType(HP.code.EQUIP_STONE_DRESS_C)) {
//			// 装备镶嵌宝石
//			HPEquipStoneDress equipStoneDressParams = protocol.parseProtocol(HPEquipStoneDress.getDefaultInstance());
//			onEquipStoneDress(protocol.getType(), equipStoneDressParams);
//		} else if (protocol.checkType(HP.code.EQUIP_SMELT_INFO_C)) {
//			// 装备熔炼信息查询
//			onEquipSmeltInfo(protocol.getType());
//		} else if (protocol.checkType(HP.code.EQUIP_SMELT_REFRESH_C)) {
//			// 装备打造刷新
//			onEquipSmeltRefresh(protocol.getType());
//		} else if (protocol.checkType(HP.code.EQUIP_SMELT_C)) {
//			// 装备熔炼
//			HPEquipSmelt equipSmeltParams = protocol.parseProtocol(HPEquipSmelt.getDefaultInstance());
//
//			if (equipSmeltParams.getIsMass() > 0) { // 批量熔炼
//				List<Integer> equipQualities = equipSmeltParams.getMassQualityList();
//				onMultiEquipSmelt(protocol.getType(), equipQualities);
//			} else {
//				// 手动熔炼
//				onEquipSmelt(protocol.getType(), equipSmeltParams.getEquipIdList());
//			}

//		} else if (protocol.checkType(HP.code.EQUIP_SMELT_CREATE_C)) {
//			// 装备熔炼打造
//			//onEquipSmeltCreate(protocol.getType());
//		} else if (protocol.checkType(HP.code.EQUIP_STONE_UNDRESS_C)) {
//			// 装备宝石卸下
//			HPEquipStoneUndress equipStoneUndressParams = protocol
//					.parseProtocol(HPEquipStoneUndress.getDefaultInstance());
//			onEquipStoneUndress(protocol.getType(), equipStoneUndressParams.getEquipId(),
//					equipStoneUndressParams.getPos());
//		} else if (protocol.checkType(HP.code.EQUIP_BAG_EXTEND_C)) {
//			// 装备背包扩展
//			onEquipBagExtend(protocol.getType());
//		} else if (protocol.checkType(HP.code.EQUIP_SELL_C)) {
//			// 装备出售
//			onEquipSell(protocol.getType(), protocol.parseProtocol(HPEquipSell.getDefaultInstance()));
//		} else if (protocol.checkType(HP.code.EQUIP_SPECIAL_CREATE_C)) {
//			// 神器打造
//			HPEquipSpecialCreate params = protocol.parseProtocol(HPEquipSpecialCreate.getDefaultInstance());
//			int cfgId = params.getCfgId();
//
//			// 等级限制
//			if (player.getLevel() < SysBasicCfg.getInstance().getEquipSpecialLevel()) {
//				sendError(HP.code.EQUIP_SPECIAL_CREATE_C_VALUE, Status.error.NOT_LEVEL_OVER_LIMIT);
//				return false;
//			}
//
//			if (cfgId <= 0) {
//				sendError(HP.code.EQUIP_SPECIAL_CREATE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
//				return true;
//			}
//			if(params.getNum()<=0)
//			{
//				sendError(HP.code.EQUIP_SPECIAL_CREATE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
//				return true;
//			}
//			if(params.getNum()>=100)
//			{//外掛送錯誤參數鎖帳號5年
//				sendError(HP.code.EQUIP_SPECIAL_CREATE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
//				Calendar calendar = GuaJiTime.getCalendar();
//				calendar.add(Calendar.YEAR, 5);
//				PlayerEntity playerEntity = null;
//				playerEntity = player.getPlayerData().getPlayerEntity();
//				playerEntity.setForbidenTime(calendar.getTime());
//				playerEntity.notifyUpdate(false);
//				// 从竞技场排行榜删除
//				ArenaManager.getInstance().removeArenaRank(player.getPlayerData().getId());
//				GuaJiNetManager.getInstance().addBlackIp(player.getIp());
//				GuaJiNetManager.getInstance().addBlackDevice(player.getDevice());
//				// 日志记录
//				BehaviorLogger.log4GM(String.valueOf(player.getPlayerData().getId()), Source.GM_OPERATION, Action.GM_FORBIDEN, Params.valueOf("num", params.getNum()), Params.valueOf("ip", player.getIp()));
//				
//				// 踢出玩家
//				if (player != null) {
//					player.kickout(Const.kickReason.LOGIN_FORBIDEN_VALUE);
//				}
//				return true;
//			}
//			onEquipSpecialCreate(cfgId, params.getNum());
//		} else if (protocol.checkType(HP.code.EQUIP_COMPOUND_C_VALUE)) {
//			// 神器融合
//			HPEquipCompound params = protocol.parseProtocol(HPEquipCompound.getDefaultInstance());
//			long fromEquipId = params.getFromEquipId();
//			long toEquipId = params.getToEquipId();
//			if (fromEquipId <= 0 || toEquipId <= 0) {
//				sendError(HP.code.EQUIP_COMPOUND_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
//				return true;
//			}
//
//			// 等级限制
//			if (player.getLevel() < SysBasicCfg.getInstance().getEquipCompoumdLevel()) {
//				sendError(HP.code.EQUIP_COMPOUND_C_VALUE, Status.error.NOT_LEVEL_OVER_LIMIT);
//				return false;
//			}
//
//			onEquipCompound(fromEquipId, toEquipId);

		} else if (protocol.checkType(HP.code.EQUIP_UPGRADE_C_VALUE)) {
			// 套装升级
			HPEquipUpgrade params = protocol.parseProtocol(HPEquipUpgrade.getDefaultInstance());
			long equipId = params.getEquipId();
			int flag = 0;//cancel params.getFixFlag();
			this.equipUpgrade(equipId, flag);

//		} else if (protocol.checkType(HP.code.EQUIP_STONE_EXCHANGE_C)) {
//			// 旧宝石兑换
//			HPEquipStoneExchange params = protocol.parseProtocol(HPEquipStoneExchange.getDefaultInstance());
//			onEquipStoneExchange(protocol.getType(), params.getStoneId(), params.getNumber());
//		} else if (protocol.checkType(HP.code.EQUIP_STONE_BUY_C)) {
//			// 新宝石购买
		} else if (protocol.checkType(HP.code.EQUIP_FORGE_C)) {
			//裝備再造(合成工坊)
			onEquipForge(protocol.getType(),protocol.parseProtocol(EquipForgeReq.getDefaultInstance()));
		} else if (protocol.checkType(HP.code.EQUIP_ENHANCE_RESET_C)) {
			onEquipEnhanceReset(protocol.getType(), protocol.parseProtocol(HPEquipEnhanceReset.getDefaultInstance()));
//		} else if (protocol.checkType(HP.code.EQUIP_OPEN_MUTUAL_C)) {
//			onEquipOpenMutual(protocol);
//		} else if (protocol.checkType(HP.code.FETCH_OPEN_MUTUAL_INFO_C)) {
//			FetchOpenMutualInfo(protocol);
		}
		
		
		return super.onProtocol(protocol);
	}

	/**
	 * 旧宝石兑换
	 * 
	 * @param gemId
	 */
	private void onEquipStoneExchange(int protocolId, int stoneId, int number) {
		ItemEntity itemEntity = player.getPlayerData().getItemByItemId(stoneId);
		if (itemEntity == null || itemEntity.getItemCount() <= 0) {
			// 宝石不存在
			sendError(protocolId, Status.error.ITEM_NOT_FOUND);
			return;
		}

		ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemEntity.getItemId());
		if (itemCfg == null) {
			// 宝石不存在
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return;
		}
		if (itemCfg.getRecycleItem().equals("") || itemCfg.getRecycleItem().equals("0")) {
			// 新宝石不可以兑换
			sendError(protocolId, Status.error.GEM_EXCHANGE_EXCEPTION);
			return;
		}
		if (itemEntity.getItemCount() < number) {
			// 新宝石不可以兑换
			sendError(protocolId, Status.error.COUNT_ERROR);
			return;
		}
		// 删除旧宝石
		ConsumeItems.valueOf(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), number)
				.consumeTakeAffect(player, Action.EQUIP_STONE_EXCHANGE);
		// 增加兑换的奖励
		AwardItems awardItems = AwardItems.valueOf(itemCfg.getRecycleItem());
		// 奖励
		List<Item> itemList = awardItems.getAwardItems();
		for (Item item : itemList) {
			item.setCount(item.getCount() * number);
		}
		RewardInfo.Builder rewards = awardItems.rewardTakeAffectAndPush(player, Action.EQUIP_STONE_EXCHANGE, 1);
		HPEquipStoneExchangeRet.Builder equipStoneExchangeBuilder = HPEquipStoneExchangeRet.newBuilder();
		equipStoneExchangeBuilder.setRewards(rewards);
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_STONE_EXCHANGE_S, equipStoneExchangeBuilder));
	}

	private void onEquipCompound(long fromEquipId, long toEquipId) {
		EquipEntity fromEquip = player.getPlayerData().getEquipById(fromEquipId);
		EquipEntity toEquip = player.getPlayerData().getEquipById(toEquipId);
		if (fromEquip == null || toEquip == null) {
			sendError(HP.code.EQUIP_COMPOUND_C_VALUE, Status.error.EQUIP_NOT_FOUND_VALUE);
			return;
		}

		EquipCfg fromEquipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, fromEquip.getEquipId());
		EquipCfg toEquipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, toEquip.getEquipId());

		if (fromEquipCfg == null || toEquipCfg == null) {
			sendError(HP.code.EQUIP_COMPOUND_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}

		ConsumeItems consumeItems = ConsumeItems.valueOf();

		if (fromEquip.getGodlyAttrId2() > 0 && toEquip.getGodlyAttrId2() == 0 && toEquip.getGodlyAttrId() > 0) {
			// 计算消耗的总金币
			int costCoin = 200000;
			consumeItems.addChangeInfo(changeType.CHANGE_COIN, costCoin);
			consumeItems.addChangeInfo(changeType.CHANGE_REPUTATION_VALUE,
					SysBasicCfg.getInstance().getEquipCompoumdCostRep());
			if (player.getPlayerData().getPlayerEntity().getReputationValue() < SysBasicCfg.getInstance()
					.getEquipCompoumdCostRep()) {
				sendError(HP.code.EQUIP_COMPOUND_C_VALUE, Status.error.REPUTATION_NOT_ENOUGH);
				return;
			}
			if (!consumeItems.checkConsume(player, HP.code.EQUIP_COMPOUND_C_VALUE)) {
				return;
			}

			consumeItems.consumeTakeAffect(player, Action.EQUIP_COMPOUND);

			toEquip.setGodlyAttrId2(fromEquip.getGodlyAttrId2());
			toEquip.setStarExp2(fromEquip.getStarExp2());
			toEquip.setStarLevel2(fromEquip.getStarLevel2());

			fromEquip.setGodlyAttrId2(0);
			fromEquip.setStarExp2(0);
			fromEquip.setStarLevel2(0);

			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.EQUIP_COMPOUND,
					Params.valueOf("fromEquip", fromEquip.getEquipId()),
					Params.valueOf("toEquip", toEquip.getEquipId()));

		} else if (fromEquip.getGodlyAttrId() > 0 && toEquip.getGodlyAttrId() == 0 && toEquip.getGodlyAttrId2() > 0) {
			// 计算消耗的总金币
			// int costCoin = Double
			// .valueOf(100 * (toEquip.getStarExp2() + 1)
			// * Math.pow((fromEquipCfg.getLevel() +
			// SysBasicCfg.getInstance().getEquipSwallowPartRatio(fromEquipCfg.getPart())),
			// 1.8))
			// .intValue();
			int costCoin = 200000;
			consumeItems.addChangeInfo(changeType.CHANGE_COIN, costCoin);
			consumeItems.addChangeInfo(changeType.CHANGE_REPUTATION_VALUE,
					SysBasicCfg.getInstance().getEquipCompoumdCostRep());
			if (player.getPlayerData().getPlayerEntity().getReputationValue() < SysBasicCfg.getInstance()
					.getEquipCompoumdCostRep()) {
				sendError(HP.code.EQUIP_COMPOUND_C_VALUE, Status.error.REPUTATION_NOT_ENOUGH);
				return;
			}
			if (!consumeItems.checkConsume(player, HP.code.EQUIP_COMPOUND_C_VALUE)) {
				return;
			}

			consumeItems.consumeTakeAffect(player, Action.EQUIP_COMPOUND);

			toEquip.setGodlyAttrId(fromEquip.getGodlyAttrId());
			toEquip.setStarExp(fromEquip.getStarExp());
			toEquip.setStarLevel(fromEquip.getStarLevel());

			fromEquip.setGodlyAttrId(0);
			fromEquip.setStarExp(0);
			fromEquip.setStarLevel(0);
		} else {
			sendError(HP.code.EQUIP_COMPOUND_C_VALUE, Status.error.EQUIP_COMPOUND_NOT_LIMIT);
			return;
		}

		// R2游戏评论
		PlayerUtil.gameComment(player);

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.EQUIP_COMPOUND,
				Params.valueOf("fromEquip", fromEquip.getEquipId()), Params.valueOf("toEquip", toEquip.getEquipId()));

		BehaviorLogger.log4Platform(player, Action.EQUIP_COMPOUND, Params.valueOf("toEquip", toEquip.getId()));
		fromEquip.notifyUpdate(true);
		toEquip.notifyUpdate(true);

		QuestEventBus.fireQuestEventOneTime(QuestEventType.GOD_EQUIP_COMPOUND_TIMES, player.getXid());
		// 刷新属性
		RoleEntity roleEntity = player.getPlayerData().getRoleByEquipId(toEquip.getId());
		if (roleEntity != null) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			// 同步角色信息
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
		}

		RoleEntity _roleEntity = player.getPlayerData().getRoleByEquipId(fromEquip.getId());
		if (_roleEntity != null) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), _roleEntity);
			// 同步角色信息
			player.getPlayerData().syncRoleInfo(_roleEntity.getId());
		}
		// 同步装备信息
		player.getPlayerData().syncEquipInfo(fromEquip.getId(), toEquip.getId());
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_COMPOUND_S_VALUE,
				HPEquipCompoundRet.newBuilder().setFromEquipId(fromEquipId).setToEquipId(toEquipId)));
	}

	// 神器打造
	private void onEquipSpecialCreate(int cfgId) {
		EquipGodCreateCfg equipGodCreateCfg = ConfigManager.getInstance().getConfigByKey(EquipGodCreateCfg.class,
				cfgId);
		if (equipGodCreateCfg == null) {
			sendError(HP.code.EQUIP_SPECIAL_CREATE_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}

		if (player.getLevel() < equipGodCreateCfg.getMinLevel()
				|| player.getLevel() > equipGodCreateCfg.getMaxLevel()) {
			sendError(HP.code.EQUIP_SPECIAL_CREATE_C_VALUE, Status.error.LEVEL_NOT_LIMIT);
			return;
		}

		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_REPUTATION_VALUE,
				equipGodCreateCfg.getCostreputation());
		consumeItems.addChangeInfo(changeType.CHANGE_SMELT_VALUE, equipGodCreateCfg.getCostSmelt());
		if (player.getPlayerData().getPlayerEntity().getReputationValue() < equipGodCreateCfg.getCostreputation()) {
			return;
		}
		if (!consumeItems.checkConsume(player, HP.code.EQUIP_SPECIAL_CREATE_C_VALUE)) {
			return;
		}
		consumeItems.consumeTakeAffect(player, Action.EQUIP_SPECIAL_CREATE);
		EquipEntity equipEntity = null;
		if (equipGodCreateCfg.isGodAttrDouble()) {
			equipEntity = player.increaseEquip(equipGodCreateCfg.getEquipId(), true, true, Action.EQUIP_SPECIAL_CREATE);
		} else {
			equipEntity = player.increaseEquip(equipGodCreateCfg.getEquipId(), false, true,
					Action.EQUIP_SPECIAL_CREATE);
		}
		player.getPlayerData().syncEquipInfo(equipEntity.getId());
		RewardInfo.Builder rewardBuilder = RewardInfo.newBuilder();
		rewardBuilder.addShowItems(BuilderUtil.genEuiqpRewardBuilder(equipEntity));
		PlayerUtil.pushRewards(player, rewardBuilder, 1);

		QuestEventBus.fireQuestEventOneTime(QuestEventType.HOUNOR_MAKE_TIMES, player.getXid());

		BehaviorLogger.log4Platform(player, Action.EQUIP_SPECIAL_CREATE, Params.valueOf("equip:", equipEntity.getId()),
				Params.valueOf("costReputation", equipGodCreateCfg.getCostreputation()),
				Params.valueOf("costSmelt", equipGodCreateCfg.getCostSmelt()));

		sendProtocol(Protocol.valueOf(HP.code.EQUIP_SPECIAL_CREATE_S_VALUE,
				HPEquipSpecialCreateRet.newBuilder().setCfgId(cfgId)));
	}

	// 神器打造
	private void onEquipSpecialCreate(int cfgId, int num) {
		EquipGodCreateCfg equipGodCreateCfg = ConfigManager.getInstance().getConfigByKey(EquipGodCreateCfg.class,
				cfgId);
		if (equipGodCreateCfg == null) {
			sendError(HP.code.EQUIP_SPECIAL_CREATE_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}

		if (player.getLevel() < equipGodCreateCfg.getMinLevel()
				|| player.getLevel() > equipGodCreateCfg.getMaxLevel()) {
			sendError(HP.code.EQUIP_SPECIAL_CREATE_C_VALUE, Status.error.LEVEL_NOT_LIMIT);
			return;
		}
		
		int needCostRepu = equipGodCreateCfg.getCostreputation() * num;
		int needCostSmelt = equipGodCreateCfg.getCostSmelt() * num;
		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_REPUTATION_VALUE, needCostRepu);
		consumeItems.addChangeInfo(changeType.CHANGE_SMELT_VALUE, needCostSmelt);
		if (player.getPlayerData().getPlayerEntity().getReputationValue() < equipGodCreateCfg.getCostreputation()) {
			return;
		}
		if (!consumeItems.checkConsume(player, HP.code.EQUIP_SPECIAL_CREATE_C_VALUE)) {
			return;
		}
		consumeItems.consumeTakeAffect(player, Action.EQUIP_SPECIAL_CREATE);
		RewardInfo.Builder rewardBuilder = RewardInfo.newBuilder();

		EquipEntity equipEntity = null;
		for (int i = 0; i < num; i++) {
			if (equipGodCreateCfg.isGodAttrDouble()) {
				equipEntity = player.increaseEquip(equipGodCreateCfg.getEquipId(), true, true,
						Action.EQUIP_SPECIAL_CREATE);
			} else {
				equipEntity = player.increaseEquip(equipGodCreateCfg.getEquipId(), false, true,
						Action.EQUIP_SPECIAL_CREATE);
			}
			player.getPlayerData().syncEquipInfo(equipEntity.getId());
		
			BehaviorLogger.log4Platform(player, Action.EQUIP_SPECIAL_CREATE,
					Params.valueOf("equip:", equipEntity.getId()),
					Params.valueOf("costReputation", equipGodCreateCfg.getCostreputation()),
					Params.valueOf("costSmelt", equipGodCreateCfg.getCostSmelt()));

		}
		rewardBuilder.addShowItems(BuilderUtil.genEuiqpRewardBuilder(equipEntity,num));
		PlayerUtil.pushRewards(player, rewardBuilder, 1);
		QuestEventBus.fireQuestEvent(QuestEventType.HOUNOR_MAKE_TIMES, Long.parseLong(String.valueOf(num)),
				player.getXid());

		sendProtocol(Protocol.valueOf(HP.code.EQUIP_SPECIAL_CREATE_S_VALUE,
				HPEquipSpecialCreateRet.newBuilder().setCfgId(cfgId)));
	}

	/**
	 * 装备出售
	 * 
	 * @param protocolId
	 * @param protocol
	 */
	protected void onEquipSell(int protocolId, HPEquipSell protocol) {
		long equipId = 0;
		int quality = 0;
		if (protocol.hasEquipId()) {
			equipId = protocol.getEquipId();
			if (equipId <= 0) {
				sendError(protocolId, Status.error.PARAMS_INVALID);
				return;
			}
		} else if (protocol.hasQuality()) {
			quality = protocol.getQuality();
			if (quality <= 0) {
				sendError(protocolId, Status.error.PARAMS_INVALID);
				return;
			}
		}
		AwardItems awardItems = new AwardItems();
		List<EquipEntity> sellEquipEntities = new LinkedList<>();
		@SuppressWarnings("unused")
		String equipIds = "";
		int totalCoin = 0;
		if (equipId > 0) {
			// 出售单个装备
			EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
			if (equipEntity == null) {
				sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
				return;
			}

			if (equipEntity.isGemDressed()) {
				sendError(protocolId, Status.error.EQUIP_STONE_DRESSED_VALUE);
				return;
			}

			if (equipEntity.getGodlyAttrId() > 0) {
				sendError(protocolId, Status.error.EQUIP_GODLY_SELL_LIMIT);
				return;
			}

			if (equipEntity.getGodlyAttrId2() > 0) {
				sendError(protocolId, Status.error.EQUIP_GODLY_SELL_LIMIT);
				return;
			}

			if (player.getPlayerData().getRoleByEquipId(equipEntity.getId()) != null) {
				sendError(protocolId, Status.error.EQUIP_HAS_DRESSED);
				return;
			}

			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
			if (equipCfg == null) {
				sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
				return;
			}

			sellEquipEntities.add(equipEntity);
			totalCoin += equipCfg.getSellPrice();
			if (equipEntity.getStrengthItemMap() != null && equipEntity.getStrengthItemMap().size() > 0) {
				awardItems.addItemInfos(equipEntity.getEquipItemInfo());
			}
		} else if (quality > 0) {
			List<EquipEntity> equipEntityList = player.getPlayerData().getEquipEntities();
			for (EquipEntity equipEntity : equipEntityList) {
				if (player.getPlayerData().getRoleByEquipId(equipEntity.getId()) != null) {
					continue;
				}
				if (equipEntity.isGemDressed()) {
					continue;
				}
				EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class,
						equipEntity.getEquipId());
				if (equipCfg.getQuality() == quality && equipEntity.getGodlyAttrId() <= 0
						&& equipEntity.getGodlyAttrId2() <= 0) {
					totalCoin += equipCfg.getSellPrice();
					if (equipEntity.getStrengthItemMap() != null && equipEntity.getStrengthItemMap().size() > 0) {
						awardItems.addItemInfos(equipEntity.getEquipItemInfo());
					}
					sellEquipEntities.add(equipEntity);
				}
			}
			BehaviorLogger.log4Platform(player, Action.EQUIP_SELL, Params.valueOf("sellQuality", quality),
					Params.valueOf("sellQuantity", sellEquipEntities.size()), Params.valueOf("income", totalCoin));
		}
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		for (EquipEntity e : sellEquipEntities) {
			player.consumeEquip(e.getId(), Action.EQUIP_SELL);
			consumeItems.addChangeInfo(changeType.CHANGE_EQUIP, e.getId(), e.getEquipId(), 1);
			equipIds += e.getId() + ",";
		}
		consumeItems.pushChange(player);
		Integer equipSellRatio = ActivityUtil
				.getEquipSellActivity(player.getPlayerData().getPlayerEntity().getCreateTime());
		if (equipSellRatio != null) {
			awardItems.addCoin(totalCoin * equipSellRatio);
		} else {
			awardItems.addCoin(totalCoin);
		}
		RewardInfo.Builder rewards = awardItems.rewardTakeAffectAndPush(player, Action.EQUIP_SELL, 1);
		HPEquipSellRet.Builder equipSellBuilder = HPEquipSellRet.newBuilder();
		equipSellBuilder.setRewards(rewards);
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_SELL_S, equipSellBuilder));

	}

	/**
	 * equip bag extend
	 * 
	 * @param type
	 */
	private void onEquipBagExtend(int protocolId) {
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity == null) {
			// 数据初始化存在异
			return;
		}
		if (stateEntity.getLeftEquipBagExtendTimes() <= 0) {
			// 装备背包扩展次数已满
			sendError(protocolId, Status.error.EQUIP_BAG_EXTEND_TIMES_FULL);
			return;
		}
		int goldCost = SysBasicCfg.getInstance().getEquipExtendGoldCost(stateEntity.getEquipBagExtendTimes());
		if (player.getGold() < goldCost) {
			sendError(protocolId, Status.error.GOLD_NOT_ENOUGH);
			return;
		}

		player.consumeGold(goldCost, Action.EQUIP_BAG_EXTEND);

		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD, goldCost);
		consumeItems.pushChange(player);

		stateEntity.setEquipBagSize(stateEntity.getEquipBagSize() + SysBasicCfg.getInstance().getEquipExtendSize());
		stateEntity.setEquipBagExtendTimes(stateEntity.getEquipBagExtendTimes() + 1);

		stateEntity.notifyUpdate(true);
		player.getPlayerData().syncStateInfo();

		player.sendStatus(0, Status.error.EQUIP_BAG_EXTEND_SUC_VALUE);

		// if (stateEntity.getEquipBagSize() == 100) {
		// HPActionLog.Builder actionLogBuilder = HPActionLog.newBuilder();
		// actionLogBuilder.setActionType(2);
		// actionLogBuilder.setAndroidKey("53mq01");
		// actionLogBuilder.setIosKey("86w0ct");
		// player.sendProtocol(Protocol.valueOf(HP.code.ACTION_LOG_S_VALUE,
		// actionLogBuilder));
		// }

		BehaviorLogger.log4Platform(player, Action.EQUIP_BAG_EXTEND, Params.valueOf("goldCost", goldCost),
				Params.valueOf("afterBagVolume", stateEntity.getEquipBagSize()));
	}

	/**
	 * 宝石卸下
	 * 
	 * @param session
	 * @param protocolId
	 * @param equipId
	 */
	private void onEquipStoneUndress(int protocolId, long equipId, int position) {
		if (equipId <= 0) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}

		RoleEntity roleEntity = player.getPlayerData().getRoleByEquipId(equipEntity.getId());
		Attribute oldAttr = null;
		if (roleEntity != null) {
			oldAttr = roleEntity.getAttribute().clone();
		}
		AwardItems awardItems = new AwardItems();
		if (position <= 0) {
			for (int i = 1; i <= GsConst.Equip.MAX_PUNCH_SIZE; i++) {
				int gemId = equipEntity.getGemId(i);
				if (gemId > 0) {
					equipEntity.setGemId(i, 0);
					awardItems.addItem(itemType.TOOL_VALUE * GsConst.ITEM_TYPE_BASE, gemId, 1);
				}
			}
		} else if (position <= 4) {
			int gemId = equipEntity.getGemId(position);
			if (gemId > 0) {
				equipEntity.setGemId(position, 0);
				awardItems.addItem(itemType.TOOL_VALUE * GsConst.ITEM_TYPE_BASE, gemId, 1);
			}
		} else {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}

		equipEntity.notifyUpdate(true);
		// 刷新属性
		EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
		player.getPlayerData().syncEquipInfo(equipEntity.getId());

		awardItems.rewardTakeAffectAndPush(player, Action.EQUIP_STONE_DOWN, 1);

		if (roleEntity != null) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
			Attribute newAttr = roleEntity.getAttribute();
			PlayerUtil.pushAllAttrChange(player, oldAttr, newAttr);
		}

		HPEquipStoneUndressRet.Builder equipStoneUndressBuilder = HPEquipStoneUndressRet.newBuilder();
		equipStoneUndressBuilder.setEquipId(equipId);

		sendProtocol(Protocol.valueOf(HP.code.EQUIP_STONE_UNDRESS_S_VALUE, equipStoneUndressBuilder));
	}

	/**
	 * 角色换装
	 * 
	 * @param session
	 * @param protocolId
	 * @param params
	 */
	private void onEquipDressRole(int protocolId, HPEquipDress params) {
		//Const.equipPart.WEAPON2_VALUE 賦魂裝備位置
		long equipId = params.getEquipId();
		int roleId = params.getRoleId();
		boolean isSoul = params.getIsSoul(); // 是否為賦魂行為(裝備其他的人專武)
		// 1 表示穿戴 2 表示卸下 3替換
		int type = params.getType();
		if (equipId <= 0 || roleId <= 0 || type < 1 || type > 3) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
		
		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return;
		}

		RoleEntity roleEntity = player.getPlayerData().getRoleById(roleId);
		if (roleEntity == null) {
			sendError(protocolId, Status.error.ROLE_NOT_FOUND);
			return;
		}
		
		if (!roleEntity.isHero()) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
		
		HPEquipDressRet.Builder equipDressBuilder = HPEquipDressRet.newBuilder();
		//RoleCfg roleCfg = ConfigManager.getInstance().getConfigByKey(RoleCfg.class, roleEntity.getItemId());
		Attribute oldAttr = roleEntity.getAttribute().clone();
		Attribute newAttr = null;
		
		int Part = isSoul ? Const.equipPart.WEAPON2_VALUE : equipCfg.getPart();
		
		// 檢查是否可以裝備專武
		if (Part ==  Const.equipPart.NECKLACE_VALUE) {
			if (!FunctionUnlockCfg.checkUnlock(player,roleEntity,Const.FunctionType.mainequip_Unlock)){
				sendError(protocolId, Status.error.CONDITION_NOT_ENOUGH);
				return;
			}
		}
		
		if (type == 1) {
			
			RoleEntity wearEntity =  player.getPlayerData().getRoleByEquipId(equipId);
			
			if (wearEntity != null) {
				// 欲換裝備已經被其他英雄穿
				sendError(protocolId, Status.error.ROLE_EQUIP_PART_DRESSED);
				return;
			}
			
			long oldEquipId = 0 ;
			
			
			
			if (!isSoul) {
				// 驗證哪個 Role ItemId
				if (!equipCfg.checkProfession(roleEntity.getItemId())) {
					sendError(protocolId, Status.error.EQUIP_PROFESSION_LIMIT);
					return;
				}
			} else {
				
				int equipExclusiveId = equipCfg.getRoleAttrId();
				RoleEquipCfg roleEquipCfg = ConfigManager.getInstance().getConfigByKey(RoleEquipCfg.class, equipExclusiveId);
				if (roleEquipCfg == null || roleEquipCfg.getRoleIdList().size() <= 0) {
					sendError(HP.code.EQUIP_UPGRADE_C_VALUE, error.PARAMS_INVALID);
					return;
				}
			}
			
			if (roleEntity.getPartEquipId(Part) > 0) {
				// 如果角色身上该部位已经有装备了
				sendError(protocolId, Status.error.ROLE_EQUIP_PART_DRESSED);
				return;
			}
			
			oldEquipId = roleEntity.getPartEquipId(Part);
			roleEntity.setPartEquipId(Part, equipEntity.getId());
			
			// 刷新装备属性
			EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
			player.getPlayerData().syncEquipInfo(equipId);
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			newAttr = roleEntity.getAttribute();
			player.getPlayerData().syncRoleInfo(roleId);
			roleEntity.notifyUpdate(true);
			equipDressBuilder.setOnEquipId(equipId);
			equipDressBuilder.setOffEquipId(oldEquipId);
		} else if (type == 2) {
			
			long roleEquipPartId = roleEntity.getPartEquipId(Part);
			if (roleEquipPartId <= 0 || roleEquipPartId != equipId) {
				// 如果角色身上该部位没有该装备了
				sendError(protocolId, Status.error.ROLE_EQUIP_PART_NOT_DRESSED);
				return;
			}
			roleEntity.setPartEquipId(Part, 0);
			EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
			player.getPlayerData().syncEquipInfo(equipId);
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			newAttr = roleEntity.getAttribute();
			player.getPlayerData().syncRoleInfo(roleId);
			roleEntity.notifyUpdate(true);
			equipDressBuilder.setOffEquipId(equipId);
		} else if (type == 3) {
			
			RoleEntity wearEntity =  player.getPlayerData().getRoleByEquipId(equipId);
			
			if (wearEntity != null) {
				//欲換裝備已經被其他英雄穿
				sendError(protocolId, Status.error.ROLE_EQUIP_PART_DRESSED);
				return;
			}
			
			// 驗證專武
			if (!isSoul) {
				if (!equipCfg.checkProfession(roleEntity.getItemId())) {
					sendError(protocolId, Status.error.EQUIP_PROFESSION_LIMIT);
					return;
				}
			} else {
				int equipExclusiveId = equipCfg.getRoleAttrId();
				RoleEquipCfg roleEquipCfg = ConfigManager.getInstance().getConfigByKey(RoleEquipCfg.class, equipExclusiveId);
				if (roleEquipCfg == null || roleEquipCfg.getRoleIdList().size() <= 0) {
					sendError(HP.code.EQUIP_UPGRADE_C_VALUE, error.PARAMS_INVALID);
					return;
				}
			}
			
			if (roleEntity.getPartEquipId(Part) <= 0) {
				// 如果角色身上该部位没有装备了
				sendError(protocolId, Status.error.ROLE_EQUIP_PART_NOT_DRESSED);
				return;
			}
			long oldEquipId = roleEntity.getPartEquipId(Part);
			roleEntity.setPartEquipId(Part, equipEntity.getId());
			// 刷新装备属性
			EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
			player.getPlayerData().syncEquipInfo(equipId);
			EquipEntity oldEquipEntity = player.getPlayerData().getEquipById(oldEquipId);
			if (oldEquipEntity != null) {
				EquipUtil.refreshAttribute(oldEquipEntity, player.getPlayerData());
				player.getPlayerData().syncEquipInfo(oldEquipId);
			}
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			newAttr = roleEntity.getAttribute();
			player.getPlayerData().syncRoleInfo(roleId);
			roleEntity.notifyUpdate(true);
			equipDressBuilder.setOnEquipId(equipId);
			equipDressBuilder.setOffEquipId(oldEquipId);
		}

		/**
		 * 检测是否有称号达成。"狂耀炫闪"---------------------------------------------------
		 */
		if (roleEntity.getType() == GsConst.RoleType.MAIN_ROLE) {
			int godlyCount = PlayerUtil.calRoleGodly(roleEntity, player.getPlayerData());
			int titleId = TitleCfg.getTitleIdByTypeAndCondition(GsConst.MsgType.ROLE_GODLY, godlyCount);
			if (titleId > 0 && !player.getPlayerData().getTitleEntity().contains(titleId)) {
				Msg hawkMsg = Msg.valueOf(GsConst.MsgType.ROLE_GODLY,
						GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
				hawkMsg.pushParam(titleId);
				GsApp.getInstance().postMsg(hawkMsg);
			}
		}

		equipDressBuilder.setRoleId(roleId);

		PlayerUtil.pushAllAttrChange(player, oldAttr, newAttr);

		player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());

		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_R_SUIT_COUNT,
				player.getPlayerData().getEquipSuitQualityCount(1), player.getXid());

		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_SR_SUIT_COUNT,
				player.getPlayerData().getEquipSuitQualityCount(2), player.getXid());

		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_SSR_SUIT_COUNT,
				player.getPlayerData().getEquipSuitQualityCount(3), player.getXid());

		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_UR_SUIT_COUNT,
				player.getPlayerData().getEquipSuitQualityCount(4), player.getXid());

		int minLevel = player.getPlayerData().getEquipsEnhanceMinLevel();
		QuestEventBus.fireQuestEvent(QuestEventType.EQUIP_ENHANCE_ALL_LEVEL, minLevel, player.getXid());

		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_COMMON_GOD_EQUIP_STAR_LEVEL,
				player.getPlayerData().getEquipsMaxLevel(1), player.getXid());
		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_HOUNOR_GOD_EQUIP_STAR_LEVEL,
				player.getPlayerData().getEquipsMaxLevel(2), player.getXid());

		sendProtocol(Protocol.valueOf(HP.code.EQUIP_DRESS_S_VALUE, equipDressBuilder));
	}
	/**
	 * 角色一鍵换装
	 * 
	 * @param session
	 * @param protocolId
	 * @param params
	 */
	private void onEquipOneKeyDressRole(int protocolId, HPEquipOneKeyDress DressParams) {
		
		if ((DressParams.getDressList().size() > 10)||(DressParams.getDressList().size() <= 0)) {
			// 不可超過合理裝備格數
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
		long equipId = 0;
		int roleId = 0;
		boolean isSoul = false; // 是否為賦魂行為(裝備其他的人專武)
		RoleEntity roleEntity = null;
		Attribute oldAttr = null;
		Attribute newAttr = null;
		boolean first = true;
		HPEquipOneKeyDressRet.Builder allDressBuilder = HPEquipOneKeyDressRet.newBuilder();
		ArrayList<Long> syncEquipIdList = new ArrayList<Long>();

		for (HPEquipDress params : DressParams.getDressList()) {
			equipId = params.getEquipId();

			if (first) {
				roleId = params.getRoleId();
				roleEntity = player.getPlayerData().getRoleById(roleId);
				if (roleEntity == null) {
					sendError(protocolId, Status.error.ROLE_NOT_FOUND);
					return;
				}
				
				if (!roleEntity.isHero()) {
					sendError(protocolId, error.PARAMS_INVALID);
					return;
				}
				oldAttr = roleEntity.getAttribute().clone();
				first = false;
			} else {
				// 一鍵只換相同一隻角色
				if (roleId != params.getRoleId()) {
					sendError(protocolId, error.PARAMS_INVALID);
					return;
				}
			}
			
			isSoul = params.getIsSoul(); // 是否為賦魂行為(裝備其他的人專武)
			// 1 表示穿戴 2 表示卸下 3替換
			int type = params.getType();
			if (equipId <= 0 || roleId <= 0 || type < 1 || type > 3) {
				sendError(protocolId, error.PARAMS_INVALID);
				return;
			}
			
			EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
			if (equipEntity == null) {
				sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
				return;
			}
			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
			if (equipCfg == null) {
				sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
				return;
			}
	
						
			HPEquipDressRet.Builder equipDressBuilder = HPEquipDressRet.newBuilder();
			//RoleCfg roleCfg = ConfigManager.getInstance().getConfigByKey(RoleCfg.class, roleEntity.getItemId());

			
			int Part = isSoul ? Const.equipPart.WEAPON2_VALUE : equipCfg.getPart();
			
			if (type == 1) {
				
				RoleEntity wearEntity =  player.getPlayerData().getRoleByEquipId(equipId);
				
				if (wearEntity != null) {
					// 欲換裝備已經被其他英雄穿
					sendError(protocolId, Status.error.ROLE_EQUIP_PART_DRESSED);
					return;
				}
				
				long oldEquipId = 0 ;
				
				
				
				if (!isSoul) {
					// 驗證哪個 Role ItemId
					if (!equipCfg.checkProfession(roleEntity.getItemId())) {
						sendError(protocolId, Status.error.EQUIP_PROFESSION_LIMIT);
						return;
					}
				} else {
					
					int equipExclusiveId = equipCfg.getRoleAttrId();
					RoleEquipCfg roleEquipCfg = ConfigManager.getInstance().getConfigByKey(RoleEquipCfg.class, equipExclusiveId);
					if (roleEquipCfg == null || roleEquipCfg.getRoleIdList().size() <= 0) {
						sendError(HP.code.EQUIP_UPGRADE_C_VALUE, error.PARAMS_INVALID);
						return;
					}
				}
				
				if (roleEntity.getPartEquipId(Part) > 0) {
					// 如果角色身上该部位已经有装备了
					sendError(protocolId, Status.error.ROLE_EQUIP_PART_DRESSED);
					return;
				}
				
				oldEquipId = roleEntity.getPartEquipId(Part);
				roleEntity.setPartEquipId(Part, equipEntity.getId());
				
				// 刷新装备属性
				EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
				syncEquipIdList.add(equipId);
				
				equipDressBuilder.setOnEquipId(equipId);
				equipDressBuilder.setOffEquipId(oldEquipId);
			} else if (type == 2) {
				
				long roleEquipPartId = roleEntity.getPartEquipId(Part);
				if (roleEquipPartId <= 0 || roleEquipPartId != equipId) {
					// 如果角色身上该部位没有该装备了
					sendError(protocolId, Status.error.ROLE_EQUIP_PART_NOT_DRESSED);
					return;
				}
				roleEntity.setPartEquipId(Part, 0);
				EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
				syncEquipIdList.add(equipId);

				roleEntity.notifyUpdate(true);
				equipDressBuilder.setOffEquipId(equipId);
			} else if (type == 3) {
				
				RoleEntity wearEntity =  player.getPlayerData().getRoleByEquipId(equipId);
				
				if (wearEntity != null) {
					//欲換裝備已經被其他英雄穿
					sendError(protocolId, Status.error.ROLE_EQUIP_PART_DRESSED);
					return;
				}
				
				// 驗證專武
				if (!isSoul) {
					if (!equipCfg.checkProfession(roleEntity.getItemId())) {
						sendError(protocolId, Status.error.EQUIP_PROFESSION_LIMIT);
						return;
					}
				} else {
					int equipExclusiveId = equipCfg.getRoleAttrId();
					RoleEquipCfg roleEquipCfg = ConfigManager.getInstance().getConfigByKey(RoleEquipCfg.class, equipExclusiveId);
					if (roleEquipCfg == null || roleEquipCfg.getRoleIdList().size() <= 0) {
						sendError(HP.code.EQUIP_UPGRADE_C_VALUE, error.PARAMS_INVALID);
						return;
					}
				}
				
				if (roleEntity.getPartEquipId(Part) <= 0) {
					// 如果角色身上该部位没有装备了
					sendError(protocolId, Status.error.ROLE_EQUIP_PART_NOT_DRESSED);
					return;
				}
				long oldEquipId = roleEntity.getPartEquipId(Part);
				roleEntity.setPartEquipId(Part, equipEntity.getId());
				// 刷新装备属性
				EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
				syncEquipIdList.add(equipId);;
				EquipEntity oldEquipEntity = player.getPlayerData().getEquipById(oldEquipId);
				if (oldEquipEntity != null) {
					EquipUtil.refreshAttribute(oldEquipEntity, player.getPlayerData());
					syncEquipIdList.add(oldEquipId);
				}
				
				equipDressBuilder.setOnEquipId(equipId);
				equipDressBuilder.setOffEquipId(oldEquipId);
			}
	
			equipDressBuilder.setRoleId(roleId);
			
			allDressBuilder.addDressRet(equipDressBuilder);
		}
		
		/**
		 * 检测是否有称号达成。"狂耀炫闪"---------------------------------------------------
		 */
		if (roleEntity.getType() == GsConst.RoleType.MAIN_ROLE) {
			int godlyCount = PlayerUtil.calRoleGodly(roleEntity, player.getPlayerData());
			int titleId = TitleCfg.getTitleIdByTypeAndCondition(GsConst.MsgType.ROLE_GODLY, godlyCount);
			if (titleId > 0 && !player.getPlayerData().getTitleEntity().contains(titleId)) {
				Msg hawkMsg = Msg.valueOf(GsConst.MsgType.ROLE_GODLY,
						GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
				hawkMsg.pushParam(titleId);
				GsApp.getInstance().postMsg(hawkMsg);
			}
		}
		
		player.getPlayerData().syncEquipInfo(syncEquipIdList);
		
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
		newAttr = roleEntity.getAttribute();
		player.getPlayerData().syncRoleInfo(roleId);
		roleEntity.notifyUpdate(true);
		PlayerUtil.pushAllAttrChange(player, oldAttr, newAttr);
		
		player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());

		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_R_SUIT_COUNT,
				player.getPlayerData().getEquipSuitQualityCount(1), player.getXid());

		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_SR_SUIT_COUNT,
				player.getPlayerData().getEquipSuitQualityCount(2), player.getXid());

		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_SSR_SUIT_COUNT,
				player.getPlayerData().getEquipSuitQualityCount(3), player.getXid());

		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_UR_SUIT_COUNT,
				player.getPlayerData().getEquipSuitQualityCount(4), player.getXid());

		int minLevel = player.getPlayerData().getEquipsEnhanceMinLevel();
		QuestEventBus.fireQuestEvent(QuestEventType.EQUIP_ENHANCE_ALL_LEVEL, minLevel, player.getXid());

		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_COMMON_GOD_EQUIP_STAR_LEVEL,
				player.getPlayerData().getEquipsMaxLevel(1), player.getXid());
		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_HOUNOR_GOD_EQUIP_STAR_LEVEL,
				player.getPlayerData().getEquipsMaxLevel(2), player.getXid());

		sendProtocol(Protocol.valueOf(HP.code.EQUIP_ONEKEY_DRESS_S_VALUE, allDressBuilder));
	}

	/**
	 * 装备熔炼打造
	 * 
	 * @param protocolId
	 */
	private void onEquipSmeltCreate(int protocolId) {
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		EquipEntity equipEntity = stateEntity.getEquipSmeltCreateEntity();
		ItemInfo itemInfo = stateEntity.getSmeltItemInfo();
		if (equipEntity == null && itemInfo == null) {
			// 数据初始化异常
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}
		HPEquipCreateRet.Builder equipCreateBuilder = HPEquipCreateRet.newBuilder();
		RewardInfo.Builder rewardBuilder = RewardInfo.newBuilder();
		if (equipEntity != null) {
			// 生成装备
			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
			if (equipCfg == null) {
				sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
				return;
			}
			List<ItemInfo> costItemlist = equipCfg.getcostItemList();
			
			if (costItemlist.size() > 0) { //消耗熔炼所需物品
				ConsumeItems consumeItems = ConsumeItems.valueOf();
				if (consumeItems.addConsumeInfo(player.getPlayerData(), costItemlist)) {
					if (consumeItems.checkConsume(player,protocolId)) {
						if (!consumeItems.consumeTakeAffect(player, Action.EQUIP_CREATE)) {
							return;
						}
					}
					else {
						return;
					}
				}else {
					return;
				}
			} else {
				return;
			}
			DBManager.getInstance().create(equipEntity);
			player.getPlayerData().addEquipEntity(equipEntity);
			player.getPlayerData().syncEquipInfo(equipEntity.getId());
			rewardBuilder.addShowItems(BuilderUtil.genEuiqpRewardBuilder(equipEntity));

			BehaviorLogger.log4Service(player, Source.EQUIP_ADD, Action.EQUIP_CREATE,
					Params.valueOf("itemId", equipEntity.getEquipId()), Params.valueOf("equipId", equipEntity.getId()),
					Params.valueOf("isGodly", equipEntity.getGodlyAttrId() > 0));

			BehaviorLogger.log4Platform(player, Action.EQUIP_CREATE, Params.valueOf("equipItemId", equipCfg.getId()),
					Params.valueOf("equipLevel", equipCfg.getLevel()),
					Params.valueOf("equipScore", equipEntity.getScore()),
//					Params.valueOf("costSmeltValue", costSmeltValue),
					Params.valueOf("isGodly", equipEntity.getGodlyAttrId() > 0));

		} else if (itemInfo != null) {
			return;
			// 是道具
//			ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemInfo.getItemId());
//			if (player.getSmeltValue() < itemCfg.getSmeltValue()) {
//				sendError(protocolId, Status.error.EQUIP_CREATE_SMELT_VALUE_NOT_ENOUGH);
//				return;
//			}
//			// 消耗熔炼值
//			List<ItemInfo> costItemlist = equipCfg.getcostItemList();
//			
//			if (costItemlist.size() > 0) { //消耗熔炼所需物品
//				ConsumeItems consumeItems = ConsumeItems.valueOf();
//				if (consumeItems.addConsumeInfo(player.getPlayerData(), costItemlist)) {
//					if (consumeItems.checkConsume(player)) {
//						if (!consumeItems.consumeTakeAffect(player, Action.EQUIP_CREATE)) {
//							return;
//						}
//					}
//					else {
//						return;
//					}
//				}
//			}
//			AwardItems awardItems = new AwardItems();
//			awardItems.addItem(itemInfo);
//			BehaviorLogger.log4Service(player, Source.TOOLS_ADD, Action.EQUIP_CREATE,
//					Params.valueOf("itemId", itemInfo.getItemId()));
//			rewardBuilder = awardItems.rewardTakeAffectAndPush(player, Action.EQUIP_CREATE, 1);
		}

		int needLevel = getNeedRefreshEquipLevel(player.getLevel());

		EquipForgeCfg equipForgeCfg = EquipForgeCfg.getEquipForgeCfg(needLevel);
		if (equipForgeCfg != null) {
			HPEquipSmeltInfoRet.Builder equipRefreshBuilder = HPEquipSmeltInfoRet.newBuilder();
			refreshSmeltCreate(stateEntity, equipForgeCfg, equipRefreshBuilder);
			equipRefreshBuilder.setFreeRefreshTimes(stateEntity.getEquipSmeltRefesh());
			sendProtocol(Protocol.valueOf(HP.code.EQUIP_SMELT_INFO_S_VALUE, equipRefreshBuilder));
		}

		PlayerUtil.pushRewards(player, rewardBuilder, 1);
		equipCreateBuilder.setRewards(rewardBuilder);
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_SMELT_CREATE_S, equipCreateBuilder));
	}

	/**
	 * 装备熔炼
	 * 
	 * @param session
	 * @param type
	 * @param params
	 */
	private void onEquipSmelt(int protocolId, List<Long> list) {
		// 避免装备id重复
		List<Long> equipIdList = new LinkedList<Long>();
		for (Long equipId : list) {
			if (equipId > 0 && !equipIdList.contains(equipId)) {
				equipIdList.add(equipId);

			}
		}

		if (equipIdList.size() < 0) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}

		AwardItems awardItems = new AwardItems();
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		ArrayList<Long> syncEquipIdList = new ArrayList<Long>();
		ArrayList<Integer> syncItemList = new ArrayList<Integer>();
		ConsumeItems invalidEquipConsumeItems = ConsumeItems.valueOf();
		ArrayList<EquipEntity> addEquipList = new ArrayList<EquipEntity>();

		HPEquipSmeltRet.Builder equipSmeltBuilder = HPEquipSmeltRet.newBuilder();
		equipSmeltBuilder.setGemUndress(0);

		Iterator<Long> equipIter = equipIdList.iterator();

		int smeltCount = 0;

		while (equipIter.hasNext()) {
			Long equipId = equipIter.next();
			// 添加新逻辑
			if (!smeltEquip(protocolId, equipId, true, awardItems, consumeItems, syncEquipIdList, syncItemList,
					invalidEquipConsumeItems, equipSmeltBuilder, addEquipList)) {
				// 能熔炼多少熔炼多少
				break;
			} else {
				smeltCount++;
			}
		}

		RewardInfo.Builder rewards = null;

		// 除去invalidEquipConsumeItems(仅仅为了通知客户端去除错误装备信息)
		invalidEquipConsumeItems.pushChange(player);

		// 消耗 奖励
		consumeItems.consumeTakeAffect(player, Action.EQUIP_SMELT);
		rewards = awardItems.rewardTakeAffect(player, Action.EQUIP_SMELT);

		for (EquipEntity e : addEquipList) {
			rewards.addShowItems(BuilderUtil.genEuiqpRewardBuilder(e));
			player.getPlayerData().syncEquipInfo(e.getId());
		}

		PlayerUtil.pushRewards(player, rewards, 1);

		// 同步道具
		player.getPlayerData().syncEquipInfo(syncEquipIdList);
		player.getPlayerData().syncItemInfo(syncItemList);

		// 推送熔炼任务
		QuestEventBus.fireQuestEventOneTime(QuestEventType.SMELT_EQUIP, player.getXid());

		QuestEventBus.fireQuestEvent(QuestEventType.SMELT_EQUIP_COUNT, smeltCount, player.getXid());

		// 7日之诗 装备铸造
		SevenDayQuestEventBus.fireQuestEvent(SevenDayEventType.EQUIPMENT, smeltCount, player.getXid());

		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.SMELT_EQUIP,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(smeltCount);
		GsApp.getInstance().postMsg(hawkMsg);

		sendProtocol(Protocol.valueOf(HP.code.EQUIP_SMELT_S, equipSmeltBuilder));
		return;
	}

	private void onMultiEquipSmelt(int protocolId, List<Integer> equipQualities) {
		ArrayList<Long> originalEquipIdList = new ArrayList<Long>();

		for (int equipQuality : equipQualities) {
			List<Long> idList = EquipUtil.getEquipsByQuality(equipQuality, player);
			originalEquipIdList.addAll(idList);
		}

		AwardItems awardItems = new AwardItems();
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		ArrayList<Long> syncEquipIdList = new ArrayList<Long>();
		ArrayList<Integer> syncItemList = new ArrayList<Integer>();
		ConsumeItems invalidEquipConsumeItems = ConsumeItems.valueOf();
		ArrayList<EquipEntity> addEquipList = new ArrayList<EquipEntity>();

		HPEquipSmeltRet.Builder equipSmeltBuilder = HPEquipSmeltRet.newBuilder();
		equipSmeltBuilder.setGemUndress(0);

		int smeltCount = 0;

		for (int quality : equipQualities) {
			List<Long> idList = EquipUtil.getEquipsByQuality(quality, player);

			List<Long> equipIdList = new LinkedList<Long>();

			for (Long equipId : idList) {
				if (equipId > 0 && !equipIdList.contains(equipId)) {
					equipIdList.add(equipId);
				}
			}

			if (equipIdList.size() < 0) {
				// 此品类没有
				continue;
			}

			Iterator<Long> equipIter = equipIdList.iterator();

			while (equipIter.hasNext()) {
				Long equipId = equipIter.next();
				// 添加新逻辑
				if (!smeltEquip(protocolId, equipId, originalEquipIdList.contains(equipId), awardItems, consumeItems,
						syncEquipIdList, syncItemList, invalidEquipConsumeItems, equipSmeltBuilder, addEquipList)) {
					// 能熔炼多少熔炼多少
					break;
				} else {
					smeltCount++;
				}
			}

		}

		if (smeltCount == 0) {
			sendError(protocolId, error.PARAMS_INVALID);

		}

		RewardInfo.Builder rewards = null;

		// 除去invalidEquipConsumeItems(仅仅为了通知客户端去除错误装备信息)
		invalidEquipConsumeItems.pushChange(player);

		// 消耗 奖励
		consumeItems.consumeTakeAffect(player, Action.EQUIP_SMELT);
		rewards = awardItems.rewardTakeAffect(player, Action.EQUIP_SMELT);

		for (EquipEntity e : addEquipList) {
			rewards.addShowItems(BuilderUtil.genEuiqpRewardBuilder(e));
			player.getPlayerData().syncEquipInfo(e.getId());
		}

		PlayerUtil.pushRewards(player, rewards, 1);

		// 同步道具
		player.getPlayerData().syncEquipInfo(syncEquipIdList);
		player.getPlayerData().syncItemInfo(syncItemList);

		// 推送熔炼任务
		QuestEventBus.fireQuestEventOneTime(QuestEventType.SMELT_EQUIP, player.getXid());

		QuestEventBus.fireQuestEvent(QuestEventType.SMELT_EQUIP_COUNT, smeltCount, player.getXid());

		// 7日之诗
		SevenDayQuestEventBus.fireQuestEvent(SevenDayEventType.EQUIPMENT, smeltCount, player.getXid());

		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.SMELT_EQUIP,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(smeltCount);
		GsApp.getInstance().postMsg(hawkMsg);

		sendProtocol(Protocol.valueOf(HP.code.EQUIP_SMELT_S, equipSmeltBuilder));
		return;
	}

	/**
	 * 熔炼一件装备
	 * 
	 * @return 返回值标识为熔炼是否可以继续
	 */
	private boolean smeltEquip(int protocolId, Long eid, Boolean original, AwardItems awardItems,
			ConsumeItems consumeItems, ArrayList<Long> syncEquipIdList, ArrayList<Integer> syncItemList,
			ConsumeItems invalidEquipConsumeItems, HPEquipSmeltRet.Builder equipSmeltBuilder,
			ArrayList<EquipEntity> addEquipList) {
		int smeltValue = 0;
		EquipEntity equipEntity = player.getPlayerData().getEquipById(eid);
		if (equipEntity == null) {
			// 通知客户端移除这个装备数据内存
			invalidEquipConsumeItems.addChangeInfo(changeType.CHANGE_EQUIP, eid, 0, 1);
			return true;
		}
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return false;
		}
		// 补丁: 验证套装不能熔炼
		if (equipCfg.getSuitId() > 0) {
			sendError(protocolId, Status.error.EQUIP_GODLY_SMELT_NOT_ALLOW);
			return false;
		}
		EquipSmeltCfg equipSmeltCfg = ConfigManager.getInstance().getConfigByKey(EquipSmeltCfg.class,
				equipCfg.getLevel());
		if (equipSmeltCfg == null) {
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return false;
		}
		if (player.getPlayerData().getRoleByEquipId(eid) != null) {
			sendError(protocolId, Status.error.EQUIP_HAS_DRESSED);
			return false;
		}

		// 未强化过的神器不能熔炼
		if ((equipEntity.getGodlyAttrId() > 0 && equipEntity.getStrength() <= 0)
				|| (equipEntity.getGodlyAttrId2() > 0 && equipEntity.getStrength() <= 0)) {
			// 神器不能熔炼，需要先传承
			sendError(protocolId, Status.error.EQUIP_GODLY_SMELT_NOT_ALLOW);
			return false;
		}

		// 任意可以熔炼的装备生成装备逻辑----------------------------------------------↓------------------------------------------------
		//HOH不生成裝備了
//		int currentQuality = equipCfg.getQuality();
//		int targetEquipId = 0;
//		int randValue = GuaJiRand.randInt(10000);
//		boolean isGod = false;
//		if (equipEntity.getGodlyAttrId() <= 0 && equipEntity.getGodlyAttrId2() <= 0) {
//			if (currentQuality >= Const.equipQuality.ORANGE_VALUE) {
//				// 随机出神器
//				if (randValue < equipSmeltCfg.getGodlyEquip()) {
//					// 随机出目标装备等级
//					int targetEquipLevel = 1;
//					if (player.getLevel() - equipCfg.getLevel() >= 5) {
//						// 选择三个权重筛选
//						targetEquipLevel = equipSmeltCfg
//								.selectEquipLevel(GsConst.Equip.EQUIP_SMELT_WEIGHT_SELECT_THERE);
//					} else {
//						// 选择两个权重筛选
//						targetEquipLevel = equipSmeltCfg.selectEquipLevel(GsConst.Equip.EQUIP_SMELT_WEIGHT_SELECT_TWO);
//					}
//					targetEquipId = EquipCfg.randomEquipId(targetEquipLevel, Const.equipQuality.ORANGE_VALUE);
//					isGod = true;
//				}
//			} else {
//				// 目标品质+1
//				int nextQuality = currentQuality + 1;
//				// 随机出目标装备等级
//				int targetEquipLevel = 1;
//				// 随机出神器
//				if (randValue < equipSmeltCfg.getTargetEquipRatio(nextQuality)) {
//					if (player.getLevel() - equipCfg.getLevel() >= 5) {
//						// 选择三个权重筛选
//						targetEquipLevel = equipSmeltCfg
//								.selectEquipLevel(GsConst.Equip.EQUIP_SMELT_WEIGHT_SELECT_THERE);
//					} else {
//						// 选择两个权重筛选
//						targetEquipLevel = equipSmeltCfg.selectEquipLevel(GsConst.Equip.EQUIP_SMELT_WEIGHT_SELECT_TWO);
//					}
//					targetEquipId = EquipCfg.randomEquipId(targetEquipLevel, nextQuality);
//					isGod = false;
//				}
//			}
//		}

		//////////////////////////////////////////////////////////////////// 生成装备后续处理//////////////////////////////////////
		// 如果有宝石就脱下
		if (equipEntity.isGemDressed()) {
			int count = 0;
			for (int i = 1; i <= GsConst.Equip.MAX_PUNCH_SIZE; i++) {
				int gemId = equipEntity.getGemId(i);
				if (gemId > 0) {
					equipEntity.setGemId(i, 0);
					ItemEntity itemEntity = player.getPlayerData().getItemByItemId(gemId);
					itemEntity.setItemCount(itemEntity.getItemCount() + 1);
					itemEntity.notifyUpdate(true);
					syncItemList.add(itemEntity.getId());
					count++;
				}
			}
			if (count > 0) {
				equipSmeltBuilder.setGemUndress(1);
			} else {
				equipSmeltBuilder.setGemUndress(0);
			}
		}
		// 强化材料返还
		if (equipEntity.getEquipItemInfo().size() > 0) {
			awardItems.addItemInfos(equipEntity.getEquipItemInfo());
		}

		// 清除强化材料返还
		equipEntity.clearStrengthItemInfo();

		// 神器和非神器的处理
		if (equipEntity.getGodlyAttrId() <= 0 && equipEntity.getGodlyAttrId2() <= 0) {

			consumeItems.addChangeInfo(changeType.CHANGE_EQUIP, eid, equipEntity.getEquipId(), 1);// I这里其实只是消耗，不提供广播，接口的耦合性太高

			// 是否为原始装备判断
			if (original) {
				// 这里应该是广播的
			} else {
				if (syncEquipIdList.contains(eid)) {
					syncEquipIdList.remove(eid);
					addEquipList.remove(equipEntity);
				}
			}
		} else {
			syncEquipIdList.add(eid);
			equipEntity.setStrength(0);
			equipEntity.notifyUpdate();
		}
		
		if (equipEntity.getGodlyAttrId() <= 0 && equipEntity.getGodlyAttrId2() <= 0) {
			smeltValue = equipCfg.getSmeltGain();
			if (smeltValue > 0) {
				String info = String.format("30000_101001_%d",smeltValue); //裝備粉塵
				AwardItems addItem = AwardItems.valueOf(info);
				awardItems.appendAward(addItem);
			}
			
		}
		// 生成装备进行添加列表，没生成返还熔炼值
//		if (targetEquipId > 0) {
//			EquipEntity addEquip = player.increaseEquip(targetEquipId, isGod ? 10000 : 0, Action.EQUIP_SMELT);
//			syncEquipIdList.add(addEquip.getId());
//			addEquipList.add(addEquip);
//		} else {
			// 返还熔炼值
			//if (equipEntity.getGodlyAttrId() <= 0 && equipEntity.getGodlyAttrId2() <= 0) {
//			smeltValue += equipCfg.getSmeltGain();
			//}

//			if (smeltValue > 0) {
//				Integer smeltRatio = ActivityUtil
//						.getSmeltValueActivity(player.getPlayerData().getPlayerEntity().getCreateTime());
//				if (smeltRatio != null) {
//					smeltValue = smeltValue * smeltRatio;
//				}
//				awardItems.addSmeltValue(smeltValue);
//			}
//		}

		return true;
	}

	/**
	 * 根据玩家等级生成刷新装备所需的装备等级
	 * 
	 * @param playerLevel
	 * @return
	 */
	private int getNeedRefreshEquipLevel(int playerLevel) {
		List<Integer> levelList = SysBasicCfg.getInstance().getRefreshEquipOffsetLevelList();
		List<Integer> weightList = SysBasicCfg.getInstance().getRefreshEquipWeightList();

		int level = GuaJiRand.randonWeightObject(levelList, weightList);

		if (playerLevel + level >= LevelExpCfg.getMaxLevel()) {
			return LevelExpCfg.getMaxLevel();
		}

		if (playerLevel + level <= 1) {
			return 1;
		}

		return playerLevel + level;
	}

	/**
	 * 装备打造刷新
	 * 
	 * @param protocolId
	 */
	private void onEquipSmeltRefresh(int protocolId) {
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity == null) {
			// 数据初始化存在异常
			return;
		}

		int needLevel = getNeedRefreshEquipLevel(player.getLevel());

		EquipForgeCfg equipForgeCfg = EquipForgeCfg.getEquipForgeCfg(needLevel);
		if (equipForgeCfg == null) {
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return;
		}

		MonthCardStatus monthCardStatus = ActivityUtil.getMonthCardStatus(player.getPlayerData());

		if (monthCardStatus != null) {
			if (monthCardStatus.getLeftFreeRefreshMakeEquipTimes() > 0) {
				if (monthCardStatus.expendLeftFreeRefreshMakeEquipTimes(1)) {
					// 根据规则生成
					HPEquipSmeltInfoRet.Builder equipRefreshBuilder = HPEquipSmeltInfoRet.newBuilder();
					refreshSmeltCreate(stateEntity, equipForgeCfg, equipRefreshBuilder);
					equipRefreshBuilder.setFreeRefreshTimes(stateEntity.getEquipSmeltRefesh());
					player.getPlayerData().updateActivity(Const.ActivityId.MONTH_CARD_VALUE, 0);
					player.getPlayerData().syncStateInfo();
					sendProtocol(Protocol.valueOf(HP.code.EQUIP_SMELT_INFO_S_VALUE, equipRefreshBuilder));
					BehaviorLogger.log4Platform(player, Action.EQUIP_SMELT_REFRESH, Params.valueOf("costGold", 0));
					return;
				}
			}
		}

		// 剩余免费刷新次数
		boolean isFree = false;
		if (stateEntity.getEquipSmeltRefesh() > 0) {
			isFree = true;
			stateEntity.setEquipSmeltRefesh(stateEntity.getEquipSmeltRefesh() - 1);
		} else {
			// 使用钻石刷新
			if (player.getGold() < GsConst.Equip.EQUIP_CREATE_REFRESH) {
				sendError(HP.code.EQUIP_SMELT_REFRESH_C_VALUE, Status.error.GOLD_NOT_ENOUGH);
				return;
			}
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, GsConst.Equip.EQUIP_CREATE_REFRESH).consumeTakeAffect(player,
					Action.EQUIP_SMELT_REFRESH);
		}
		// 根据规则生成
		HPEquipSmeltInfoRet.Builder equipRefreshBuilder = HPEquipSmeltInfoRet.newBuilder();
		refreshSmeltCreate(stateEntity, equipForgeCfg, equipRefreshBuilder);
		equipRefreshBuilder.setFreeRefreshTimes(stateEntity.getEquipSmeltRefesh());
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_SMELT_INFO_S_VALUE, equipRefreshBuilder));

		BehaviorLogger.log4Platform(player, Action.EQUIP_SMELT_REFRESH,
				Params.valueOf("costGold", isFree ? 0 : GsConst.Equip.EQUIP_CREATE_REFRESH));
	}

	/**
	 * 装备熔炼信息查询
	 * 
	 * @param session
	 * @param protocolId
	 */
	private void onEquipSmeltInfo(int protocolId) {
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity == null) {
			// 数据初始化存在异常
			return;
		}
		// 可选的打造装备为空
		EquipEntity equipEntity = stateEntity.getEquipSmeltCreateEntity();
		ItemInfo itemInfo = stateEntity.getSmeltItemInfo();
		HPEquipSmeltInfoRet.Builder equipSmeltInfoBuilder = HPEquipSmeltInfoRet.newBuilder();
		if (equipEntity == null && itemInfo == null) {
			int needLevel = getNeedRefreshEquipLevel(player.getLevel());
			EquipForgeCfg equipForgeCfg = EquipForgeCfg.getEquipForgeCfg(needLevel);
			if (equipForgeCfg == null) {
				sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
				return;
			}
			refreshSmeltCreate(stateEntity, equipForgeCfg, equipSmeltInfoBuilder);
		} else if (equipEntity != null) {
			equipSmeltInfoBuilder.setEquipInfo(BuilderUtil.genEquipBuilder(equipEntity));
		} else if (itemInfo != null) {
			equipSmeltInfoBuilder.setItemInfo(BuilderUtil.genItemInfoBuilder(itemInfo));
		}
		equipSmeltInfoBuilder.setFreeRefreshTimes(stateEntity.getEquipSmeltRefesh());
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_SMELT_INFO_S_VALUE, equipSmeltInfoBuilder));
	}

	/**
	 * 刷新
	 * 
	 * @param stateEntity
	 * @param equipForgeCfg
	 * @param equipRefreshBuilder
	 */
	private void refreshSmeltCreate(StateEntity stateEntity, EquipForgeCfg equipForgeCfg,
			HPEquipSmeltInfoRet.Builder equipRefreshBuilder) {
		int rewardGroupId = equipForgeCfg.randomRewardsGroupId();
		ItemInfo itemInfo = AwardUtil.randomDrop(rewardGroupId);
		stateEntity.setEquipSmeltCreateEntity(null);
		stateEntity.setSmeltItemInfo(null);
		if (itemInfo.getType() / GsConst.ITEM_TYPE_BASE == Const.itemType.EQUIP_VALUE) {
			// 是装备
			EquipEntity equipEntity = EquipUtil.generateEquip(player, itemInfo.getItemId(),
					equipForgeCfg.getGodlyRatio());
			stateEntity.setEquipSmeltCreateEntity(equipEntity);
			equipRefreshBuilder.setEquipInfo(BuilderUtil.genEquipBuilder(equipEntity));
			// add by weiyong
			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.SMELT_EQUIP_REFRESH,
					Params.valueOf("itemId", equipEntity.getEquipId()),
					Params.valueOf("attr", equipEntity.getAttribute().toString()));
		} else {
			// 是道具
			stateEntity.setSmeltItemInfo(itemInfo);
			equipRefreshBuilder.setItemInfo(BuilderUtil.genItemInfoBuilder(itemInfo));
			// add by weiyong
			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.SMELT_EQUIP_REFRESH,
					Params.valueOf("itemId", itemInfo.getItemId()));
		}
		stateEntity.notifyUpdate(true);
	}

	/**
	 * 装备传承
	 * 
	 * @param session
	 * @param protocolId
	 * @param equipExtendParams
	 */
	private void onEquipExtend(int protocolId, HPEquipExtend params) {
		// 从equipId 传承到 extendedId
		long equipId = params.getEquipId();
		long extendedId = params.getExtendedEquipId();
		if (equipId <= 0 || extendedId <= 0) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}

		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}

		EquipEntity extendedEquipEntity = player.getPlayerData().getEquipById(extendedId);
		if (extendedEquipEntity == null) {
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}

		if (equipEntity.getGodlyAttrId() <= 0 && equipEntity.getGodlyAttrId2() <= 0 && equipEntity.getStrength() <= 0
				&& equipEntity.getGemPunchCount() <= 0) {
			// 不满足传承条件
			sendError(protocolId, Status.error.EQUIP_EXTEND_NO_MEET);
			return;
		}

		// 被传承不能有神器属性
		if ((extendedEquipEntity.getGodlyAttrId() > 0 || extendedEquipEntity.getGodlyAttrId2() > 0)
				&& (equipEntity.getGodlyAttrId() > 0 || equipEntity.getGodlyAttrId2() > 0)) {
			sendError(protocolId, Status.error.EQUIP_EXTEND_TO_GOT_GOLDY_ATTR);
			return;
		}

		if (extendedEquipEntity.getStrength() > 0) {
			// 装备传承的目标已经拥有强化等级
			sendError(protocolId, Status.error.EQUIP_EXTEND_OSSESS_STRENGTH);
			return;
		}

		if (extendedEquipEntity.isGemDressed()) {
			// 装备传承的目标已经镶嵌有宝石
			sendError(protocolId, Status.error.EQUIP_EXTEND_NOT_GEM);
			return;
		}

		// max(0,(被传承装备等级+部位修正参数)^2-(传承装备等级+部位修正参数)^2)*120*传承神器所携带的神器经验值+10000
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		EquipCfg extendedEquipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class,
				extendedEquipEntity.getEquipId());

		if (equipCfg.getPart() != extendedEquipCfg.getPart()) {
			sendError(protocolId, Status.error.EQUIP_EXTEND_SAME_PART_LIMIT);
			return;
		}

		if (extendedEquipEntity.getGodlyAttrId() <= 0) {
			extendedEquipEntity.setGodlyAttrId(equipEntity.getGodlyAttrId());
			extendedEquipEntity.setStarLevel(equipEntity.getStarLevel());
			extendedEquipEntity.setStarExp(equipEntity.getStarExp());
		}

		if (extendedEquipEntity.getGodlyAttrId2() <= 0) {
			extendedEquipEntity.setGodlyAttrId2(equipEntity.getGodlyAttrId2());
			extendedEquipEntity.setStarLevel2(equipEntity.getStarLevel2());
			extendedEquipEntity.setStarExp2(equipEntity.getStarExp2());
		}

		// 传承宝石
		int equipGems[] = { equipEntity.getGem1(), equipEntity.getGem2(), equipEntity.getGem3(),
				equipEntity.getGem4() };
		// 被传承原始宝石孔
		int extendedGems[] = { extendedEquipEntity.getGem1(), extendedEquipEntity.getGem2(),
				extendedEquipEntity.getGem3(), extendedEquipEntity.getGem4() };
		for (int i = 0; i < equipGems.length; i++) {
			extendedEquipEntity.setGemId(i + 1, equipGems[i]);
		}

		for (int i = 0; i < extendedGems.length; i++) {
			equipEntity.setGemId(i + 1, extendedGems[i]);
		}

		// 传承强化等级
		extendedEquipEntity.setStrength(equipEntity.getStrength());
		extendedEquipEntity.addEquipItemCount(equipEntity.getEquipItemInfo());

		equipEntity.setGodlyAttrId(0);
		equipEntity.setStarLevel(0);
		equipEntity.setStarExp(0);
		equipEntity.setGodlyAttrId2(0);
		equipEntity.setStarLevel2(0);
		equipEntity.setStarExp2(0);
		equipEntity.clearGemId();
		equipEntity.setStrength(0);

		// add by weiyong
		BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, Action.EQUIP_EXTEND,
				Params.valueOf("fromItemId", equipEntity.getEquipId()), Params.valueOf("fromId", equipEntity.getId()),
				Params.valueOf("toItemId", extendedEquipEntity.getEquipId()),
				Params.valueOf("toId", extendedEquipEntity.getId()),
				Params.valueOf("attr", equipEntity.getAttribute().toString()),
				Params.valueOf("equipStrengthItem", equipEntity.getStrengthItemStr()));

		equipEntity.clearStrengthItemInfo();

		// 刷新属性
		EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
		EquipUtil.refreshAttribute(extendedEquipEntity, player.getPlayerData());

		RoleEntity roleEntity = player.getPlayerData().getRoleByEquipId(extendedEquipEntity.getId());
		if (roleEntity != null) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
		}

		roleEntity = player.getPlayerData().getRoleByEquipId(equipEntity.getId());
		if (roleEntity != null) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
		}

		if ((extendedEquipEntity.getGodlyAttrId() > 0) && (extendedEquipEntity.getGodlyAttrId2() > 0)) {
			// R2游戏评论
			PlayerUtil.gameComment(player);
		}
		// 数据落地
		extendedEquipEntity.notifyUpdate(true);
		equipEntity.notifyUpdate(true);

		QuestEventBus.fireQuestEventOneTime(QuestEventType.GOD_EQUIP_EXTEND_TIMES, player.getXid());

		// 同步装备信息
		player.getPlayerData().syncEquipInfo(equipEntity.getId(), extendedEquipEntity.getId());
		// 给客户端回包
		HPEquipExtendRet.Builder equipExtendBuilder = HPEquipExtendRet.newBuilder();
		equipExtendBuilder.setEquipId(equipId);
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_EXTEND_S_VALUE, equipExtendBuilder));
	}

	/**
	 * 高级装备洗练;
	 * 
	 * @param protocolId
	 * @param params
	 */
	private void onEquipSuperBaptize(int protocolId, HPEquipSuperBaptize params) {
		// 高级洗练处理流程
		long equipId = params.getEquipId();
		if (equipId <= 0) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
		// 是否有此装备;
		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}
		// 是否有此模板;
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return;
		}
		// 是否满足洗练等级
		if (equipCfg.getWashCoinCost() == 0) {
			sendError(protocolId, Status.error.BAPTIZE_MIN_LEVEL_LIMIT);
			return;
		}
		// 钻石是否足够
		int costGold = getSuperBaptizeCostGold(params);
		if (player.getGold() < costGold) {
			sendError(protocolId, Status.error.GOLD_NOT_ENOUGH);
			return;
		}
		// 是否有可洗练的二级属性
		int totalAvaliableSecAttrCount = equipEntity.getAvailableSecAttrCount();
		if (totalAvaliableSecAttrCount == 0) {
			sendError(protocolId, Status.error.EQUIP_SECONDLY_ATTR_ZERO);
			return;
		}
		// 最多可以锁定两个属性
		if (params.getLockAttributeTypesList().size() > 2) {
			sendError(protocolId, Status.error.EQUIP_LOCK_MAX);
			return;
		}
		// 获取所有需要洗练的属性类型集和二级属性总值
		List<Integer> attrTypes = new ArrayList<Integer>();
		int totalValue = getTotalSecondlyAttributeValue(params, equipEntity, attrTypes);
		int attrTypeSize = attrTypes.size();
		// 至少要有两个没锁定的属性
		if (attrTypeSize < 2) {
			sendError(protocolId, Status.error.EQUIP_UNLOCK_MIN);
			return;
		}
		// 消耗钻石
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, costGold).consumeTakeAffect(player, Action.EQUIP_SUPER_WASH);
		// 属性随机值计算
		List<Integer> attrValues = PlayerUtil.randomRoleSecondlyAttr(totalValue, attrTypeSize, true);
		Collections.sort(attrValues);
		// 根据属性类型提取有用的权重项
		List<WeightItem<Integer>> weightList = new LinkedList<>();
		for (WeightItem<Integer> item : SysBasicCfg.getInstance().getEquipBaptizeWeightItems(player.getProf())) {
			if (attrTypes.indexOf(item.getValue()) >= 0) {
				weightList.add(item);
			}
		}
		// 根据权重项,重置洗练属性类型集
		attrTypes.clear();
		for (int i = 0; i < attrTypeSize; i++) {
			int attrType = WeightUtil.random(weightList);
			Iterator<WeightItem<Integer>> iter = weightList.iterator();
			while (iter.hasNext()) {
				if (iter.next().getValue() == attrType) {
					iter.remove();
					break;
				}
			}
			attrTypes.add(attrType);
		}
		// 装备等级大于100级,需要修正耐力属性百分比
		totalValue = equipEntity.getSecondaryAttrValue1() + equipEntity.getSecondaryAttrValue2()
				+ equipEntity.getSecondaryAttrValue3() + equipEntity.getSecondaryAttrValue4();
		if (equipCfg.getLevel() > GsConst.Equip.STAMINA_PERCENT_LEVEL_LIMIT) {
			EquipUtil.revisedStaminaValue(attrTypes, attrValues, totalValue, false);
		}
		// 属性赋值
		for (int i = 0; i < attrTypes.size(); i++) {
			equipEntity.setAttr(attrTypes.get(i), attrValues.get(attrValues.size() - i - 1));
		}
		// 刷新属性
		EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());

		RoleEntity roleEntity = player.getPlayerData().getRoleByEquipId(equipId);
		if (roleEntity != null) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
		}

		equipEntity.notifyUpdate(true);
		// 同步装备信息
		player.getPlayerData().syncEquipInfo(equipId);

		QuestEventBus.fireQuestEventOneTime(QuestEventType.BAPTIZE_EQUIP_TIMES, player.getXid());

		// 7日之诗 装备洗练
		SevenDayQuestEventBus.fireQuestEventOneTime(SevenDayEventType.EQUIPSUCCINCT, player.getXid());

		QuestEventBus.fireQuestEventOneTime(QuestEventType.BAPTIZE_EQUIP_TIMES, player.getXid());

		BehaviorLogger.log4Platform(player, Action.EQUIP_SUPER_WASH, Params.valueOf("equipId", equipId),
				Params.valueOf("equipLevel", equipCfg.getLevel()), Params.valueOf("costGold", costGold));

		HPEquipBaptizeRet.Builder equipBaptizeBuilder = HPEquipBaptizeRet.newBuilder();
		equipBaptizeBuilder.setEquipId(equipId);
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_BAPTIZE_S_VALUE, equipBaptizeBuilder));
	}

	/**
	 * 获取装备高级洗练的二级属性总和;
	 * 
	 * @param params
	 * @param equipEntity
	 * @return
	 */
	private int getTotalSecondlyAttributeValue(HPEquipSuperBaptize params, EquipEntity equipEntity,
			List<Integer> unlockAttributes) {
		// 总属性计算
		List<Integer> lockAttributes = params.getLockAttributeTypesList();
		Map<Integer, Integer> secondAttributes = new HashMap<Integer, Integer>();
		if (equipEntity.getSecondaryAttrType1() > 0) {
			secondAttributes.put(equipEntity.getSecondaryAttrType1(), equipEntity.getSecondaryAttrValue1());
		}
		if (equipEntity.getSecondaryAttrType2() > 0) {
			secondAttributes.put(equipEntity.getSecondaryAttrType2(), equipEntity.getSecondaryAttrValue2());
		}
		if (equipEntity.getSecondaryAttrType3() > 0) {
			secondAttributes.put(equipEntity.getSecondaryAttrType3(), equipEntity.getSecondaryAttrValue3());
		}
		if (equipEntity.getSecondaryAttrType4() > 0) {
			secondAttributes.put(equipEntity.getSecondaryAttrType4(), equipEntity.getSecondaryAttrValue4());
		}
		// 去掉锁定的属性
		for (Integer eachType : lockAttributes) {
			if (secondAttributes.containsKey(eachType)) {
				secondAttributes.remove(eachType);
			}
		}
		// 未锁定的属性总和
		int result = 0;
		for (Integer eachKey : secondAttributes.keySet()) {
			unlockAttributes.add(eachKey);
			result += secondAttributes.get(eachKey);
		}
		return result;
	}

	/**
	 * 获取装备高级洗练钻石消耗;
	 * 
	 * @param params
	 * @return
	 */
	private int getSuperBaptizeCostGold(HPEquipSuperBaptize params) {
		return SysBasicCfg.getInstance().getEquipSuperBaptizePrice(params.getLockAttributeTypesCount());
	}

	/**
	 * 装备洗炼
	 * 
	 * @param session
	 * @param type
	 * @param equipBaptizeParams
	 */
	private void onEquipBaptize(int protocolId, HPEquipBaptize params) {
		long equipId = params.getEquipId();
		if (equipId <= 0) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}

		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return;
		}
		// 判断装备洗炼最低等级
		if (equipCfg.getWashCoinCost() == 0) {
			sendError(protocolId, Status.error.BAPTIZE_MIN_LEVEL_LIMIT);
			return;
		}
		if (player.getCoin() < equipCfg.getWashCoinCost()) {
			sendError(protocolId, Status.error.COINS_NOT_ENOUGH);
			return;
		}
		// 是否有二级属性
		int secAttrCount = equipEntity.getAvailableSecAttrCount();
		if (secAttrCount == 0) {
			sendError(protocolId, Status.error.EQUIP_SECONDLY_ATTR_ZERO);
			return;
		}
		// 获取所有需要洗练的属性类型集和二级属性总值
		List<Integer> attrTypes = equipEntity.getAvailableSecAttrTypes();
		int attrTypeSize = attrTypes.size();
		int totalValue = equipEntity.getSecondaryAttrValue1() + equipEntity.getSecondaryAttrValue2()
				+ equipEntity.getSecondaryAttrValue3() + equipEntity.getSecondaryAttrValue4();
		// 消耗金币
		ConsumeItems.valueOf(changeType.CHANGE_COIN, equipCfg.getWashCoinCost()).consumeTakeAffect(player,
				Action.EQUIP_WASH);
		// 属性随机值计算
		List<Integer> attrValues = PlayerUtil.randomRoleSecondlyAttr(totalValue, attrTypeSize, false);
		Collections.sort(attrValues);
		// 根据属性类型提取有用的权重项
		List<WeightItem<Integer>> weightList = new LinkedList<>();
		for (WeightItem<Integer> item : SysBasicCfg.getInstance().getEquipBaptizeWeightItems(player.getProf())) {
			if (attrTypes.indexOf(item.getValue()) >= 0) {
				weightList.add(item);
			}
		}
		// 根据权重项,重置洗练属性类型集
		attrTypes.clear();
		for (int i = 0; i < attrTypeSize; i++) {
			int attrType = WeightUtil.random(weightList);
			Iterator<WeightItem<Integer>> iter = weightList.iterator();
			while (iter.hasNext()) {
				if (iter.next().getValue() == attrType) {
					iter.remove();
					break;
				}
			}
			attrTypes.add(attrType);
		}
		// 装备等级大于100级,需要修正耐力属性百分比
		if (equipCfg.getLevel() > GsConst.Equip.STAMINA_PERCENT_LEVEL_LIMIT) {
			EquipUtil.revisedStaminaValue(attrTypes, attrValues, totalValue, false);
		}
		// 属性赋值
		for (int i = 0; i < attrTypes.size(); i++) {
			equipEntity.setAttr(attrTypes.get(i), attrValues.get(attrValues.size() - i - 1));
		}
		// 刷新属性
		EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());

		RoleEntity roleEntity = player.getPlayerData().getRoleByEquipId(equipId);
		if (roleEntity != null) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
		}

		equipEntity.notifyUpdate(true);
		// 同步装备信息
		player.getPlayerData().syncEquipInfo(equipId);

		QuestEventBus.fireQuestEventOneTime(QuestEventType.BAPTIZE_EQUIP_TIMES, player.getXid());

		// 7日之诗 装备洗练
		SevenDayQuestEventBus.fireQuestEventOneTime(SevenDayEventType.EQUIPSUCCINCT, player.getXid());

		BehaviorLogger.log4Platform(player, Action.EQUIP_STONE_DRESS, Params.valueOf("equipId", equipId),
				Params.valueOf("equipLevel", equipCfg.getLevel()),
				Params.valueOf("costCoins", equipCfg.getWashCoinCost()));

		HPEquipBaptizeRet.Builder equipBaptizeBuilder = HPEquipBaptizeRet.newBuilder();
		equipBaptizeBuilder.setEquipId(equipId);
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_BAPTIZE_S_VALUE, equipBaptizeBuilder));
	}

	/**
	 * 装备镶嵌宝石
	 * 
	 * @param session
	 * @param type
	 * @param equipStoneDressParams
	 */
	private void onEquipStoneDress(int protocolId, HPEquipStoneDress params) {
		long equipId = params.getEquipId(); // 装备Id
		int punchPos = params.getPunchPos();// 目标孔位
		int stoneId = params.getStoneId(); // 宝石Id
		if (equipId <= 0 || punchPos <= 0 || stoneId <= 0) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}

		ItemEntity itemEntity = player.getPlayerData().getItemByItemId(stoneId);
		if (itemEntity == null || itemEntity.getItemCount() <= 0) {
			// 宝石不存在
			sendError(protocolId, Status.error.ITEM_NOT_FOUND);
			return;
		}

		ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemEntity.getItemId());
		if (itemCfg == null) {
			// 宝石不存在
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return;
		}

		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return;
		}

		// 验证宝石是否镶嵌在符合的装备上(新宝石逻辑验证)
		List<Integer> itemList = itemCfg.getEmbedEquipIdList(); // 获取当前宝石可穿戴的装备部位
		if (!itemList.contains(equipCfg.getPart())) {
			sendError(protocolId, Status.error.GEM_EMBED_NOT_EQUIP);
			return;
		}

		RoleEntity roleEntity = player.getPlayerData().getRoleByEquipId(equipEntity.getId());
		Attribute oldAttr = null;
		if (roleEntity != null) {
			oldAttr = roleEntity.getAttribute().clone();
		}

		if (equipEntity.getGemId(punchPos) < 0 || equipEntity.getGemId(punchPos) > 0) {
			// 该位置还没有打过孔了
			sendError(protocolId, Status.error.EQUIP_PUNCH_POS_PUNCHED);
			return;
		}
		// 判断宝石状态
		if (itemEntity.getItemCount() <= 0) {
			// 宝石数量不足
			sendError(protocolId, Status.error.ITEM_NOT_FOUND);
			return;
		}
		// 判断装备身上之前是否有相同宝石类型
		for (int i = 1; i <= GsConst.Equip.MAX_PUNCH_SIZE; i++) {
			int gemId = equipEntity.getGemId(i);
			if (gemId > 0) {
				ItemCfg gemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, gemId);
				// if (gemCfg.getQuality() == itemCfg.getQuality()) {
				// // 一件装备上相同类型的宝石只能镶嵌一个
				// sendError(protocolId,
				// Status.error.EQUIP_GEM_QUALITY_SAME_EXIST);
				// return;
				// }
				// 新宝石旧宝石通用逻辑处理
				if (gemCfg.getGemType() == itemCfg.getGemType()) {
					// 一件装备上相同类型的宝石只能镶嵌一个
					sendError(protocolId, Status.error.EQUIP_GEM_QUALITY_SAME_EXIST);
					return;
				}
			}
		}

		// 镶嵌宝石
		ConsumeItems.valueOf(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), 1)
				.consumeTakeAffect(player, Action.EQUIP_STONE_DRESS);
		equipEntity.setGemId(punchPos, itemEntity.getItemId());
		// 刷新属性
		EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());

		equipEntity.notifyUpdate(true);
		itemEntity.notifyUpdate(true);

		QuestEventBus.fireQuestEventOneTime(QuestEventType.GEM_DRESS_TIMES, player.getXid());

		// 同步装备信息
		player.getPlayerData().syncEquipInfo(equipId);
		player.getPlayerData().syncItemInfo(itemEntity.getId());

		if (roleEntity != null) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
			Attribute newAttr = roleEntity.getAttribute();
			PlayerUtil.pushAllAttrChange(player, oldAttr, newAttr);
		}

		BehaviorLogger.log4Platform(player, Action.EQUIP_STONE_DRESS, Params.valueOf("equipId", equipId),
				Params.valueOf("equipLevel", equipCfg.getLevel()), Params.valueOf("stoneId", itemCfg.getId()));

		// 回复前端宝石Id 和 装备Id 让客户端自己修改一下宝石的状态
		HPEquipStoneDressRet.Builder stoneDressBuilder = HPEquipStoneDressRet.newBuilder();
		stoneDressBuilder.setEquipId(equipId);
		stoneDressBuilder.setStoneId(stoneId);
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_STONE_DRESS_S_VALUE, stoneDressBuilder));
	}

	/**
	 * 装备打孔
	 * 
	 * @param protocolId
	 * @param params
	 */
	private void onEquipPunch(int protocolId, HPEquipPunch params) {

		long equipId = params.getEquipId();
		int punchPos = params.getPunchPos();
		int costType = params.getCostType();
		// 数据校验
		if (equipId <= 0 || punchPos <= 0 || punchPos > GsConst.Equip.MAX_PUNCH_SIZE
				|| (costType == 0 && punchPos > 1)) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
		// 装备查找
		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}
		// 装备静态数据
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return;
		}
		// 装备等级10级以上才能打孔
		if (equipCfg.getLevel() < 10) {
			sendError(protocolId, Status.error.EQUIP_PUNCH_MIN_LEVEL_LIMIT);
			return;
		}
		// 开启这个孔之前上一个孔一定要开启
		if (punchPos > 1) {
			if (equipEntity.getGemId(punchPos - 1) < 0) {
				sendError(protocolId, Status.error.EQUIP_PUNCH_PRE_POS_LIMIT);
				return;
			}
		}
		// 改位置已经打过孔了
		if (equipEntity.getGemId(punchPos) >= 0) {
			sendError(protocolId, Status.error.EQUIP_PUNCH_POS_PUNCHED);
			return;
		}
		// 消耗打孔道具
		ConsumeItems consumeItems = ConsumeItems.valueOf();

		if (punchPos == 1) {
			ItemInfo costItem = ItemInfo.valueOf(equipCfg.getPunchCost());
			consumeItems.addChangeInfo(changeType.CHANGE_COIN, (int)costItem.getQuantity());
		} else {
			EquipPunchCfg punchCfg = ConfigManager.getInstance().getConfigByKey(EquipPunchCfg.class, costType);
			// 装备打孔配置静态数据
			if (punchCfg == null) {
				sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
				return;
			}
			// 消耗数据
			ItemInfo costItem = punchCfg.getCost(punchPos);
			if (costType == 1) {
				ItemEntity itemEntity = player.getPlayerData().getItemByItemId(costItem.getItemId());
				if (itemEntity == null || itemEntity.getItemCount() <= 0) {
					sendError(protocolId, Status.error.ITEM_NOT_ENOUGH);
					return;
				}
				consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(),
						costItem.getQuantity());
			} else {
				consumeItems.addChangeInfo(changeType.CHANGE_GOLD,(int)costItem.getQuantity());
			}
		}
		if (!consumeItems.checkConsume(player, protocolId)) {
			return;
		}
		consumeItems.consumeTakeAffect(player, Action.EQUIP_PUNCH);
		// 开孔
		equipEntity.setGemId(punchPos, 0);
		equipEntity.notifyUpdate(true);
		// 同步装备信息
		player.getPlayerData().syncEquipInfo(equipEntity.getId());
		// 发送回报
		HPEquipPunchRet.Builder equipPunchBuilder = HPEquipPunchRet.newBuilder();
		equipPunchBuilder.setEquipId(equipId);
		equipPunchBuilder.setPunchPos(punchPos);
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_PUNCH_S, equipPunchBuilder));
	}

	/**
	 * 装备吞噬,吞噬的规则是吞一个涨一点经验，升级要求的经验是等级数
	 * 
	 * @param session
	 * @param type
	 * @param equipSwallowParams
	 */
	private void onEquipSwallow(int protocolId, HPEquipSwallow params) {
		long equipId = params.getEquipId();
		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return;
		}
		// 优先消耗道具
		List<Integer> costItems = params.getSwallowedItemIdList();
		Map<Integer, Integer> itemCostMap = new HashMap<>();
		for (int itemId : costItems) {
			if (itemCostMap.containsKey(itemId)) {
				itemCostMap.put(itemId, itemCostMap.get(itemId) + 1);
			} else {
				itemCostMap.put(itemId, 1);
			}
		}
		int equipPart = equipCfg.getPart();
		int godlyExp = 0, godlyExp2 = 0;
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		for (int itemId : itemCostMap.keySet()) {
			ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemId);
			if (itemCfg == null) {
				sendError(protocolId, Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}

			int cost = itemCostMap.get(itemId);

			ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);
			if (itemEntity == null || itemEntity.getItemCount() < cost) {
				sendError(protocolId, Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}
			int type = itemCfg.getType();
			int equipPartLimit = itemCfg.getEquipPart();
			if (type == Const.toolType.COMMON_GODLY_EXP_VALUE) {
				godlyExp += itemCfg.getStoneExp() * cost;
				// } else if (type == Const.toolType.REPUTATION_GODLY_EXP_VALUE) {
			} else if (type == Const.toolType.REPUTATION_GODLY_EXP_VALUE
					&& (equipPartLimit == 100 || equipPart == equipPartLimit)) {
				godlyExp2 += itemCfg.getStoneExp() * cost;
			} else {
				sendError(protocolId, Status.error.ITEM_TYPE_NOT_FIT);
				return;
			}
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemId, cost);
		}

		// 避免装备id重复
		List<Long> swallowIds = new LinkedList<Long>();
		for (Long eId : params.getSwallowedEquipIdList()) {
			if (eId > 0 && !swallowIds.contains(eId)) {
				swallowIds.add(eId);
			}
		}

		if (equipId <= 0 || swallowIds.size() > 6) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}

		List<EquipEntity> canSwallowedEquipList = new LinkedList<>();
		for (Long swallowedEquipId : swallowIds) {
			if (swallowedEquipId <= 0 || swallowedEquipId == equipId) {
				sendError(protocolId, error.PARAMS_INVALID);
				return;
			}

			EquipEntity swallowedEquip = player.getPlayerData().getEquipById(swallowedEquipId);
			if (swallowedEquip == null) {
				sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
				return;
			}

			if (swallowedEquip.isGemDressed()) {
				sendError(protocolId, Status.error.EQUIP_STONE_DRESSED_VALUE);
				return;
			}

			if (swallowedEquip.getGodlyAttrId() <= 0 && swallowedEquip.getGodlyAttrId2() <= 0) {
				sendError(protocolId, Status.error.EQUIP_SWALLOW_TARGET_MUST_BE_GODLY);
				return;
			}

			if ((swallowedEquip.getGodlyAttrId() > 0 && equipEntity.getStarLevel() >= GodlyLevelExpCfg.getMaxLevel())
					|| (swallowedEquip.getGodlyAttrId2() > 0
							&& equipEntity.getStarLevel2() >= GodlyLevelExpCfg.getMaxLevel())) {
				sendError(protocolId, Status.error.GODLY_LEVEL_FULL);
				return;
			}
			if (equipEntity.getGodlyAttrId2() == 0) {
				// 只有A属性
				if (swallowedEquip.getGodlyAttrId2() > 0) {
					// 被吞噬的有B属性
					continue;
				}
				if (swallowedEquip.getGodlyAttrId() == 0) {
					continue;
				}
			} else if (equipEntity.getGodlyAttrId() == 0) {
				// 只有B属性
				if (swallowedEquip.getGodlyAttrId() > 0) {
					continue;
				}
				if (swallowedEquip.getGodlyAttrId2() == 0) {
					continue;
				}
			}

			canSwallowedEquipList.add(swallowedEquip);
		}
		// 消耗这些装备
		int starExpAdd = 0;
		int starExpAdd2 = 0;
		for (EquipEntity eqe : canSwallowedEquipList) {
			if (player.getPlayerData().getRoleByEquipId(eqe.getId()) != null) {
				sendError(protocolId, Status.error.EQUIP_HAS_DRESSED);
				return;
			}
			starExpAdd += eqe.getStarExp() + (eqe.getGodlyAttrId() > 0 ? 1 : 0);
			starExpAdd2 += eqe.getStarExp2() + (eqe.getGodlyAttrId2() > 0 ? 1 : 0);
		}
		starExpAdd += godlyExp;
		starExpAdd2 += godlyExp2;
		// 计算消耗的总金币
		int costCoin = starExpAdd * 150000;
		// 计算消耗的总金币2
		int costCoin2 = starExpAdd2 * 150000;
		consumeItems.addChangeInfo(changeType.CHANGE_COIN, (costCoin + costCoin2));
		// 返还的材料集合
		List<ItemInfo> strengthItemList = new ArrayList<>();
//		List<Long> equipIds = new ArrayList<Long>(canSwallowedEquipList.size());
		for (EquipEntity eqe : canSwallowedEquipList) {
//			if(player.getVipLevel() >= 5){
//				eqe.clearGodlyAttr();
//				equipIds.add(eqe.getId());
//				eqe.notifyUpdate();
//			}else{
			consumeItems.addChangeInfo(Const.changeType.CHANGE_EQUIP, eqe.getId(), eqe.getEquipId(), 1);
//			}
			// 添加返还的精华
			strengthItemList.addAll(eqe.getEquipItemInfo());
		}
//		if(player.getVipLevel() >= 5 && equipIds.size() > 0){
//			// 同步装备信息
//			player.getPlayerData().syncEquipInfo(equipIds.toArray(new Long[equipIds.size()]));	
//		}
		if (!consumeItems.checkConsume(player, protocolId)) {
			return;
		}
		consumeItems.consumeTakeAffect(player, Action.EQUIP_SWALLOW);

		// 先检测消耗并扣除, 再返还奖励
		if (strengthItemList.size() > 0) {
			AwardItems awardItems = new AwardItems();
			awardItems.addItemInfos(strengthItemList);
			awardItems.rewardTakeAffectAndPush(player, Action.EQUIP_SWALLOW, 1);
		}

		// 增加神器星级经验
		equipEntity.setStarExp(equipEntity.getStarExp() + starExpAdd);
		// 增加神器星级经验2
		equipEntity.setStarExp2(equipEntity.getStarExp2() + starExpAdd2);
		// 刷星神器星级等级
		equipEntity.refreshStarLevel();

		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_COMMON_GOD_EQUIP_STAR_LEVEL,
				player.getPlayerData().getEquipsMaxLevel(1), player.getXid());
		QuestEventBus.fireQuestEvent(QuestEventType.DRESS_HOUNOR_GOD_EQUIP_STAR_LEVEL,
				player.getPlayerData().getEquipsMaxLevel(2), player.getXid());

		if (equipEntity.getGodlyAttrId() > 0) {
			QuestEventBus.fireQuestEventOneTime(QuestEventType.COMMON_GOD_EQUIP_SWALLOW_TIMES, player.getXid());
		}

		if (equipEntity.getGodlyAttrId2() > 0) {
			QuestEventBus.fireQuestEventOneTime(QuestEventType.HOUNOR_GOD_EQUIP_WALLOW_TIMES, player.getXid());
		}

		/** 检测称号是否达成"十星神器"------------------------ */

		int titleId1 = TitleCfg.getTitleId(GsConst.MsgType.EQUIP_STAR, equipEntity.getStarLevel());
		int titleId2 = TitleCfg.getTitleId(GsConst.MsgType.EQUIP_STAR, equipEntity.getStarLevel2());

		if (titleId1 > 0 || titleId2 > 0) {
			Msg hawkMsg = Msg.valueOf(GsConst.MsgType.EQUIP_STAR,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
			hawkMsg.pushParam(titleId1);
			hawkMsg.pushParam(titleId2);
			GsApp.getInstance().postMsg(hawkMsg);

		}
		/** --------------------------------------- */

		equipEntity.notifyUpdate(true);
		// 同步装备信息
		player.getPlayerData().syncEquipInfo(equipId);
		// add by weiyong
		BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, Action.EQUIP_SWALLOW,
				Params.valueOf("itemId", equipEntity.getEquipId()), Params.valueOf("id", equipEntity.getId()),
				Params.valueOf("beforeExp", equipEntity.getStarExp() - starExpAdd),
				Params.valueOf("addExp", starExpAdd), Params.valueOf("afterExp", equipEntity.getStarExp()));

		BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, Action.EQUIP_SWALLOW,
				Params.valueOf("itemId", equipEntity.getEquipId()), Params.valueOf("id", equipEntity.getId()),
				Params.valueOf("beforeExp", equipEntity.getStarExp2() - starExpAdd2),
				Params.valueOf("addExp", starExpAdd2), Params.valueOf("afterExp", equipEntity.getStarExp2()));

		RoleEntity roleEntity = player.getPlayerData().getRoleByEquipId(equipEntity.getId());
		if (roleEntity != null) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
		}

		HPEquipSwallowRet.Builder equipSwallowBuilder = HPEquipSwallowRet.newBuilder();
		equipSwallowBuilder.setEquipId(equipId);
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_SWALLOW_S, equipSwallowBuilder));
	}

	/**
	 * 装备强化
	 * 
	 * @param session
	 * @param protocolId
	 * @param equipEnhanceParams
	 */
	private void onEquipEnhance(int protocolId, HPEquipEnhance equipEnhanceParams) {
		long equipId = equipEnhanceParams.getEquipId();
		//EquipEnhanceType type = equipEnhanceParams.getEquipEnhanceType();
		if (equipId < 0) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
		
		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}
		
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			sendError(protocolId, error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		
		int equipExclusiveId = equipCfg.getRoleAttrId();
		RoleEquipCfg roleEquipCfg = ConfigManager.getInstance().getConfigByKey(RoleEquipCfg.class, equipExclusiveId);
		if (roleEquipCfg == null || roleEquipCfg.getRoleIdList().size() <= 0) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
		
		if (equipEntity.getStrength() >= SysBasicCfg.getInstance().getEquipLimitLevel()) {
			sendError(protocolId, Status.error.EQUIP_STRENGTH_MAX_LEVEL);
			return;
		}

		RoleEntity roleEntity = player.getPlayerData().getRoleByEquipId(equipEntity.getId());
		Attribute oldAttr = null;
		if (roleEntity != null) {
			oldAttr = roleEntity.getAttribute().clone();
		}
		// 装备强化等级和经验判断
		int strengthLevel = equipEntity.getStrength();

		int enhanceTimes = 1;
//		if (type.equals(Const.EquipEnhanceType.EQUIP_TEN_TIMES)) {
//			enhanceTimes = 10;
//		} else {
//			enhanceTimes = 1;
//		}

		// 有效强化次数
		int strengthTimes = 0;
		// 计算下一个等级要消耗多少材料
		for (int i = 0; i < enhanceTimes; i++) {
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			strengthLevel++;
			List<ItemInfo> costItemList = EquipStrengthRatioCfg.getStrengthConstItem(equipEntity.getEquipId(),
					strengthLevel);
			if (costItemList.size() <= 0) {
				sendError(protocolId, Status.error.CONFIG_ERROR_VALUE);
				break;
			}

			if (!consumeItems.addConsumeInfo(player.getPlayerData(), costItemList)) {
				sendError(protocolId, Status.error.ITEM_NOT_ENOUGH_VALUE);
				break;
			}

			if (!consumeItems.checkConsume(player, protocolId)) {
				break;
			}

			if (equipEntity.getStrength() >= SysBasicCfg.getInstance().getEquipLimitLevel()) {
				sendError(protocolId, Status.error.EQUIP_STRENGTH_MAX_LEVEL);
				break;
			}

			// 消耗道具
			consumeItems.consumeTakeAffect(player, Action.EQUIP_EHANCE);
			// 记录强化材料消耗
			equipEntity.addEquipItemCount(costItemList);
			// 设置下一个等级
			equipEntity.setStrength(strengthLevel);
			strengthTimes++;
		}

		// 刷新装备主属性
		EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
		// 装备同步
		player.getPlayerData().syncEquipInfo(equipEntity.getId());

		equipEntity.notifyUpdate();

		if (roleEntity != null) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
			Attribute newAttr = roleEntity.getAttribute();
			PlayerUtil.pushAllAttrChange(player, oldAttr, newAttr);
		}

		int minLevel = player.getPlayerData().getEquipsEnhanceMinLevel();
		QuestEventBus.fireQuestEvent(QuestEventType.EQUIP_ENHANCE_ALL_LEVEL, minLevel, player.getXid());

		if (strengthTimes > 0) {
			Msg msg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.EQUIP_ENHANCE,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
			msg.pushParam(strengthTimes);
			GsApp.getInstance().postMsg(msg);
			// 7日之诗
			SevenDayQuestEventBus.fireQuestEvent(SevenDayEventType.EQUIPSTRAN, strengthTimes, player.getXid());
		}

		//EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		//if (equipCfg != null) {
		StringBuffer buffer = new StringBuffer();
		BehaviorLogger.log4Platform(player, Action.EQUIP_EHANCE, Params.valueOf("equipId", equipId),
				Params.valueOf("equipLevel", equipCfg.getLevel()), Params.valueOf("costItem", buffer.toString()),
				Params.valueOf("addItem", equipEntity.getStrengthItemStr()));
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.EQUIP_EHANCE,
				Params.valueOf("Id", equipId), Params.valueOf("equipId", equipEntity.getEquipId()),
				Params.valueOf("equipLevel", equipCfg.getLevel()), Params.valueOf("costItem", buffer.toString()),
				Params.valueOf("addItem", equipEntity.getStrengthItemStr()));
		//}
		// 发送回报
		HPEquipEnhanceRet.Builder equipEnhanceBuilder = HPEquipEnhanceRet.newBuilder();
		equipEnhanceBuilder.setEquipId(equipId);
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_ENHANCE_S, equipEnhanceBuilder));
	}

	private final Comparator<EquipEntity> EQUIP_ENHANCE_LEVEL_SORT = new Comparator<EquipEntity>() {

		@Override
		public int compare(EquipEntity o1, EquipEntity o2) {
			if (o2.getStrength() > o1.getStrength()) {
				return -1;
			} else {
				if (o2.getStrength() == o1.getStrength()) {
					return 0;
				}

				return 1;
			}
		}

	};

	/**
	 * 一键装备装备强化
	 * 
	 * @param session
	 * @param protocolId
	 * @param equipEnhanceParams
	 */
	private void onEquipOneKeyEnhance(int protocolId, HPEquipOneKeyEnhance equipEnhanceParams) {

		int roleId = equipEnhanceParams.getRoleId();
		RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);

		if (roleEntity == null)
			return;

		List<EquipEntity> sortEquip = new ArrayList<EquipEntity>();
		// 筛选当前强化角色身上的装备列表
		for (int part = Const.equipPart.HELMET_VALUE; part <= Const.equipPart.NECKLACE_VALUE; part++) {
			long equipId = roleEntity.getPartEquipId(part);
			if (equipId > 0 && player.getPlayerData().getEquipById(equipId) != null) {
				sortEquip.add(player.getPlayerData().getEquipById(equipId));
			}
		}
		// 身上没有可强化的设备
		if (sortEquip.size() == 0) {
			sendError(protocolId, Status.error.ITEM_NOT_ENOUGH_VALUE);
			return;
		}

		Set<Long> equipIds = new HashSet<Long>();
		// 设置
		// 按照强化等级排序
		Collections.sort(sortEquip, EQUIP_ENHANCE_LEVEL_SORT);

		EquipEntity minLevelEquip = sortEquip.get(0);
		do {
			int strengthLevel = minLevelEquip.getStrength() + 1;
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			List<ItemInfo> costItemList = EquipStrengthRatioCfg.getStrengthConstItem(minLevelEquip.getEquipId(),
					strengthLevel);

			if (costItemList.size() <= 0) {
				sendError(protocolId, Status.error.CONFIG_ERROR_VALUE);
				break;
			}

			if (!consumeItems.addConsumeInfo(player.getPlayerData(), costItemList)) {
				sendError(protocolId, Status.error.ITEM_NOT_ENOUGH_VALUE);
				break;
			}

			if (!consumeItems.checkConsume(player, protocolId)) {
				break;
			}

			if (minLevelEquip.getStrength() >= SysBasicCfg.getInstance().getEquipLimitLevel()) {
				sendError(protocolId, Status.error.EQUIP_STRENGTH_MAX_LEVEL);
				break;
			}
			Collections.sort(sortEquip, EQUIP_ENHANCE_LEVEL_SORT);
			// 消耗道具
			consumeItems.consumeTakeAffect(player, Action.EQUIP_EHANCE);
			// 记录强化材料消耗
			minLevelEquip.addEquipItemCount(costItemList);
			// 设置下一个等级
			minLevelEquip.setStrength(strengthLevel);
			if (!equipIds.contains(minLevelEquip.getId()))
				equipIds.add(minLevelEquip.getId());
			// 更新数据 及时保存强化数据
			minLevelEquip.notifyUpdate();
			minLevelEquip = sortEquip.get(0);
		} while (minLevelEquip != null && minLevelEquip.getStrength() < SysBasicCfg.getInstance().getEquipLimitLevel());

		HPEquipOneKeyEnhanceRet.Builder equipEnhanceBuilder = HPEquipOneKeyEnhanceRet.newBuilder();
		Iterator<Long> it = equipIds.iterator();
		// 通知客户端刷新属性
		while (it.hasNext()) {
			long equipId = it.next();
			equipEnhanceBuilder.addEquipId(minLevelEquip.getId());
			EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
			if (equipEntity != null) {
				// 刷新装备主属性
				EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
				// 装备同步
				player.getPlayerData().syncEquipInfo(equipEntity.getId());
				equipEnhanceBuilder.addEquipId(equipId);

			}

		}
		// 通知客户端强化了哪些等级
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_ONEKEYENHANCE_S_VALUE, equipEnhanceBuilder));

	}

	/**
	 * 装备分解(套装)
	 */
	@ProtocolHandlerAnno(code = HP.code.EQUIP_DECOMPOSE_C_VALUE)
	public void onEquipDecompose(Protocol protocol) {
		HPEquipDecompose params = protocol.parseProtocol(HPEquipDecompose.getDefaultInstance());
		List<Long> equipIds = params.getEquipIdList();
		if (equipIds.size() <= 0) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		Set<Long> equipIdSet = new HashSet<>();
		equipIdSet.addAll(equipIds);
		List<EquipEntity> equipEntities = new LinkedList<>();
		for (Long equipId : equipIdSet) {
			// 客户端数据错误
			if (equipId <= 0) {
				sendError(HP.code.EQUIP_DECOMPOSE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
				return;
			}

			EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
			// 没有找到装备
			if (equipEntity == null) {
				sendError(HP.code.EQUIP_DECOMPOSE_C_VALUE, Status.error.EQUIP_NOT_FOUND_VALUE);
				return;
			}

			// 神器不能分解
			if (equipEntity.getGodlyAttrId() > 0 || equipEntity.getGodlyAttrId2() > 0) {
				sendError(HP.code.EQUIP_DECOMPOSE_C_VALUE, Status.error.NO_GODLY_EQUIP_DEC);
				return;
			}

			// 有宝石不能被分解

			// 配置没找到
			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
			if (equipCfg == null) {
				sendError(HP.code.EQUIP_DECOMPOSE_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}

			// 不能分解属性
			if (!equipCfg.isDecomposeable()) {
				sendError(HP.code.EQUIP_DECOMPOSE_C_VALUE, Status.error.EQUIP_CAN_NOT_DECOMPOSE);
				return;
			}

			// 不是套装
			if (equipCfg.getSuitId() <= 0) {
				sendError(HP.code.EQUIP_DECOMPOSE_C_VALUE, Status.error.NO_SUIT_EQUIP_DEC);
				return;
			}

			// 正在穿着
			if (player.getPlayerData().getRoleByEquipId(equipId) != null) {
				sendError(HP.code.EQUIP_DECOMPOSE_C_VALUE, Status.error.EQUIP_HAS_DRESSED);
				return;
			}

			// 有宝石不能分解
			for (int j = 1; j <= GsConst.Equip.MAX_PUNCH_SIZE; j++) {
				int gemId = equipEntity.getGemId(j);
				if (gemId > 0) {
					sendError(HP.code.EQUIP_DECOMPOSE_C_VALUE, Status.error.EQUIP_HAS_GEM);
					return;
				}
			}

			equipEntities.add(equipEntity);
		}

		HPEquipDecomposeRet.Builder builder = HPEquipDecomposeRet.newBuilder();

		ConsumeItems consumeItems = new ConsumeItems();
		AwardItems awardItems = new AwardItems();

		List<ItemInfo> strengthItemList = new ArrayList<>();

		for (EquipEntity ee : equipEntities) {

			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, ee.getEquipId());
			if (equipCfg != null) {
				// 返还全部消耗的材料
				strengthItemList.addAll(ee.getEquipItemInfo());

				// 返还熔炼值和强化精华
				if (equipCfg.getDecomposeAwardInfo() != null) {
					awardItems.appendAward(equipCfg.getDecomposeAwardInfo());
				}
			}

			// 预消耗装备
			consumeItems.addChangeInfo(changeType.CHANGE_EQUIP, ee.getId(), ee.getEquipId(), 1);

			builder.addEquipId(ee.getId());

			QuestEventBus.fireQuestEventOneTime(QuestEventType.SUIT_DECOMPOSE_TIMES, player.getXid());

			BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.EQUIP_DECOMPOSE,
					Params.valueOf("equipId", ee.getId()), Params.valueOf("equipItemId", ee.getEquipId()));
		}

		// 消耗装备
		consumeItems.consumeTakeAffect(player, Action.EQUIP_DECOMPOSE);

		if (strengthItemList.size() > 0) {
			awardItems.addItemInfos(strengthItemList);
		}
		awardItems.rewardTakeAffectAndPush(player, Action.EQUIP_DECOMPOSE, 1);

		sendProtocol(Protocol.valueOf(HP.code.EQUIP_DECOMPOSE_S_VALUE, builder));
	}

	/**
	 * 装备进化
	 */
	@ProtocolHandlerAnno(code = HP.code.EQUIP_EVOLUTION_C_VALUE)
	public void onEquipEvolution(Protocol protocol) {
		HPEquipEvolution params = protocol.parseProtocol(HPEquipEvolution.getDefaultInstance());
		long equipId = params.getEquipId();
		if (equipId <= 0) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}

		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(protocol.getType(), Status.error.EQUIP_NOT_FOUND_VALUE);
			return;
		}

		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}

		if (equipCfg.getEvolutionTargetId() <= 0) {
			sendError(protocol.getType(), Status.error.NO_EQUIP_EVOLUTION_TARGET);
			return;
		}

		EquipCfg targetEquipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class,
				equipCfg.getEvolutionTargetId());
		if (targetEquipCfg == null) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}

		ConsumeItems consumeItems = new ConsumeItems();
		if (!consumeItems.addConsumeInfo(player.getPlayerData(), equipCfg.getEvolutionConsumeInfo())) {
			sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
			return;
		}

		if (!consumeItems.checkConsume(player, protocol.getType())) {
			return;
		}

		consumeItems.consumeTakeAffect(player, Action.EQUIP_EVOLUTION);

		equipEntity.setEquipId(targetEquipCfg.getId());

		EquipUtil.replaceEquipItemId(equipEntity, targetEquipCfg, player);

		equipEntity.notifyUpdate(false);

		// 如果穿在身上就刷新身上的属性
		RoleEntity roleEntity = player.getPlayerData().getRoleByEquipId(equipId);
		if (roleEntity != null) {
			Attribute oldAttr = roleEntity.getAttribute().clone();
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			Attribute newAttr = roleEntity.getAttribute().clone();
			PlayerUtil.pushAllAttrChange(player, oldAttr, newAttr);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
		}

		player.getPlayerData().syncEquipInfo(equipEntity.getId());

		sendProtocol(Protocol.valueOf(HP.code.EQUIP_EVOLUTION_S_VALUE,
				HPEquipEvolutionRet.newBuilder().setEquipId(equipId)));
	}

	/**
	 * 扣除经验;
	 * 
	 * @param equipEntity
	 * @param usedEquipNum
	 * @return
	 */
	private boolean deductEquipExp(EquipEntity equipEntity, Integer[] usedEquipNum, long oldEquipId) {
		int starType = 0;
		int beforeExp = 0;
		int beforeExp2 = 0;

		if (equipEntity.getStarExp() < usedEquipNum[0] || equipEntity.getStarExp2() < usedEquipNum[1]) {
			return false;
		}
		beforeExp = equipEntity.getStarExp();
		equipEntity.setStarExp(equipEntity.getStarExp() - usedEquipNum[0]);
		beforeExp2 = equipEntity.getStarExp2();
		equipEntity.setStarExp2(equipEntity.getStarExp2() - usedEquipNum[1]);
		equipEntity.refreshStarLevel();
		player.getPlayerData().syncEquipInfo(equipEntity.getId());
		equipEntity.notifyUpdate(false);
		BehaviorLogger.log4RollBack(player.getId(), Source.PLAYER_ATTR_CHANGE, Action.ROLLBACK_EQUIP_EXP,
				Params.valueOf("equipId", equipEntity.getId()), Params.valueOf("starType", starType),
				Params.valueOf("equipItemId", equipEntity.getEquipId()), Params.valueOf("beforeExp1", beforeExp),
				Params.valueOf("deductExp1", usedEquipNum[0]), Params.valueOf("afterExp1", equipEntity.getStarExp()),
				Params.valueOf("beforeExp2", beforeExp2), Params.valueOf("deductExp2", usedEquipNum[1]),
				Params.valueOf("afterExp2", equipEntity.getStarExp2()), Params.valueOf("oldEquipId", oldEquipId));
		return true;
	}

	/**
	 * 分别扣除普通神器经验和声望神器经验;
	 * 
	 * @param equipEntitys
	 * @param usedEquipNum
	 * @param oldEquipId
	 * @param playerId
	 * @return
	 */
	private boolean deductEquipExp(EquipEntity[] equipEntitys, Integer[] usedEquipNum, long oldEquipId) {
		Integer[] starExp1 = { usedEquipNum[0], 0 };
		Integer[] starExp2 = { 0, usedEquipNum[1] };
		if (deductEquipExp(equipEntitys[0], starExp1, oldEquipId)
				&& deductEquipExp(equipEntitys[1], starExp2, oldEquipId)) {
			return true;
		}
		return false;
	}

	/**
	 * 根据传入数据,回滚神器经验;
	 * 
	 * @param equipAndNum
	 */
	protected void rollbackEquip(Map<Long, Integer[]> equipAndNum) {
		List<EquipEntity> equipEntities = player.getPlayerData().getEquipEntities();
		for (EquipEntity equipEntity : equipEntities) {
			if (equipAndNum.containsKey(equipEntity.getId())) {
				if (deductEquipExp(equipEntity, equipAndNum.get(equipEntity.getId()), equipEntity.getId())) {
					equipAndNum.remove(equipEntity.getId());
				}
			}
		}
		for (long equipId : equipAndNum.keySet()) {
			EquipEntity[] equipEntitys = getTopExpEquip(equipAndNum.get(equipId));
			if (equipEntitys != null && deductEquipExp(equipEntitys, equipAndNum.get(equipId), equipId)) {
				equipAndNum.remove(equipId);
			}
		}
		StringBuilder sb = new StringBuilder();
		if (!equipAndNum.isEmpty()) {
			for (long equipId : equipAndNum.keySet()) {
				sb.append(" equipId:" + equipId + ", deductExp1:" + equipAndNum.get(equipId)[0] + ",deductExp2:"
						+ equipAndNum.get(equipId)[1] + "; ");
			}
		}
		BehaviorLogger.log4RollBack(player.getId(), Source.PLAYER_ATTR_CHANGE, Action.ROLLBACK_EQUIP_EXP,
				Params.valueOf("isSuccess", equipAndNum.isEmpty()), Params.valueOf("failEquip", sb));
	}

	/**
	 * 获得最高经验的神器;
	 * 
	 * @return
	 */
	private EquipEntity[] getTopExpEquip(Integer[] starType) {
		List<EquipEntity> equipEntities = player.getPlayerData().getEquipEntities();
		EquipEntity topEquip = equipEntities.get(0);
		EquipEntity topEquip2 = equipEntities.get(0);
		for (EquipEntity equipEntity : equipEntities) {
			if (equipEntity.getStarExp() > starType[0] || equipEntity.getStarExp2() > starType[1]) {
				if (topEquip.getStarExp() < equipEntity.getStarExp()) {
					topEquip = equipEntity;
				}
				if (topEquip2.getStarExp2() < equipEntity.getStarExp2()) {
					topEquip2 = equipEntity;
				}
			}
		}
		EquipEntity[] equips = { topEquip, topEquip2 };
		return equips;
	}

	/**
	 * 套装升级逻辑处理
	 * 
	 * @param equipId
	 */
	private void equipUpgrade(long equipId, int flag) {
		
		// 是否有该升级的装备
		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(HP.code.EQUIP_UPGRADE_C_VALUE, Status.error.EQUIP_NOT_FOUND);
			return;
		}
		// 查找当前的装备数据
		EquipCfg currentEquipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (null == currentEquipCfg) {
			sendError(HP.code.EQUIP_UPGRADE_C_VALUE, error.CONFIG_NOT_FOUND);
			return;
		}
		// 是否可以升级判断
		if (currentEquipCfg.isCanNotUpgrade()) {
			sendError(HP.code.EQUIP_UPGRADE_C_VALUE, error.EQUIP_CANNOT_UPGRADE);
			return;
		}
		
		// 檢查是不是專武
		int equipExclusiveId = currentEquipCfg.getRoleAttrId();
		RoleEquipCfg roleEquipCfg = ConfigManager.getInstance().getConfigByKey(RoleEquipCfg.class, equipExclusiveId);
		if (roleEquipCfg == null || roleEquipCfg.getRoleIdList().size() <= 0) {
			sendError(HP.code.EQUIP_UPGRADE_C_VALUE, error.PARAMS_INVALID);
			return;
		}
		
		// ---------------------------------------以下修改替换原来的upgreadeid，用currentId,为了表好配？
		// by zhangyang
		// 查找升级后的装备数据
		EquipCfg upgradeEquipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class,
				currentEquipCfg.getUpgradeId());
		if (null == upgradeEquipCfg) {
			sendError(HP.code.EQUIP_UPGRADE_C_VALUE, error.CONFIG_NOT_FOUND);
			return;
		}

		// 升级等级判断（穿不上）
//		if (upgradeEquipCfg.getLevel() > player.getLevel() + 15) {
//			sendError(HP.code.EQUIP_UPGRADE_C_VALUE, error.NOT_ENOUGH_ROLE_LEVEL);
//			return;
//		}

		// 角色等级是否满足升级判断
//		if (!this.isCanUpgrade(player.getLevel(), currentEquipCfg.getLevel())) {
//			sendError(HP.code.EQUIP_UPGRADE_C_VALUE, error.NOT_ENOUGH_ROLE_LEVEL);
//			return;
//		}
		// 材料消耗是否满足
		if (!this.isEnoughMaterial(HP.code.EQUIP_UPGRADE_C_VALUE, upgradeEquipCfg, flag)) {
			return;
		}

		// 开始套装升级处理
		this.equipUpgradeLogic(equipEntity, upgradeEquipCfg);
				
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.EQUIP_UPGRADE,
				Params.valueOf("id", equipId), Params.valueOf("sourceEquipId", currentEquipCfg.getId()),
				Params.valueOf("targetEquipId", upgradeEquipCfg.getId()));
		// 返回数据
		sendProtocol(
				Protocol.valueOf(HP.code.EQUIP_UPGRADE_S_VALUE, HPEquipUpgradeRet.newBuilder().setEquipId(equipId)));
	}

	/**
	 * 套装是否可以升级
	 * 
	 * @param roleLevel
	 * @param equipLevel
	 * @return
	 */
//	private boolean isCanUpgrade(int roleLevel, int equipLevel) {
//
//		return roleLevel + GsConst.Equip.GRADE_GAP_LIMIT >= equipLevel;
//	}
	
	/**
	 * 裝備可否再造,順便檢查材料
	 */
	
	private boolean isCanForge(EquipCfg equipCfg) {
		List<ItemInfo> Itemlist = equipCfg.getUpgradeMaterialList();
		if ((Itemlist != null )&&(Itemlist.size() > 0)) {
			for (ItemInfo aItem:Itemlist) {
				EquipCfg aCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, aItem.getItemId());
				if (aCfg == null) {
					return false;
				}
			}			
			return true;
		}
		return false;
	}

	/**
	 * 材料消耗是否满足
	 * 
	 * @param itemInfos
	 * @return
	 */
	private boolean isEnoughMaterial(int hpCode, EquipCfg equipCfg, int flag) {

		List<ItemInfo> itemInfos = equipCfg.getUpgradeMaterialList();

		boolean checkResult = false;

		ConsumeItems consumeItems = new ConsumeItems();

		// 基础消耗
		if (!consumeItems.addConsumeInfo(player.getPlayerData(), itemInfos)) {
			sendError(HP.code.ITEM_USE_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
			return checkResult;
		}

		if (!consumeItems.checkConsume(player, HP.code.ITEM_USE_C_VALUE)) {
			return checkResult;
		} else {
			int count = equipCfg.getTotalMaterialCount();
			int itemId = equipCfg.getFirstMaterialId();
			int itemId2 = equipCfg.getMixedMaterialId();

			if (count <= 0) {
				consumeItems.consumeTakeAffect(player, Action.EQUIP_UPGRADE);
				checkResult = true;
				return checkResult;
			}

			if (flag > 0)// 选用神器碎片
			{
				ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);

				if (itemEntity == null) {
					return checkResult;
				}

				if (itemEntity.getItemCount() < count)// 不够
				{
					int arg = (int)(count - itemEntity.getItemCount());

					ItemEntity item2Entity = player.getPlayerData().getItemByItemId(itemId2);

					if (item2Entity == null) {
						return checkResult;
					}

					if (item2Entity.getItemCount() < arg) {
						player.sendError(hpCode, Status.error.ITEM_NOT_ENOUGH_VALUE);

						return checkResult;
					}

					consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(),
							(int)itemEntity.getItemCount());
					consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, item2Entity.getId(), item2Entity.getItemId(),
							arg);

					consumeItems.consumeTakeAffect(player, Action.EQUIP_UPGRADE);

					checkResult = true;

					return checkResult;

				} else// 够
				{

					consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(),
							count);

					consumeItems.consumeTakeAffect(player, Action.EQUIP_UPGRADE);

					checkResult = true;

					return checkResult;
				}

			} else {
				ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);

				if (itemEntity == null) {
					return checkResult;
				}

				if (itemEntity.getItemCount() < count) {
					player.sendError(hpCode, Status.error.ITEM_NOT_ENOUGH_VALUE);

					return checkResult;
				}

				consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), count);

				consumeItems.consumeTakeAffect(player, Action.EQUIP_UPGRADE);

				checkResult = true;

				return checkResult;

			}
		}

	}

	/**
	 * 套装升级处理
	 * 
	 * @param equipEntity
	 * @param equipCfg    需要升级到的装备数据
	 */
	private void equipUpgradeLogic(EquipEntity equipEntity, EquipCfg equipCfg) {

		// 替换装备配置ID
		equipEntity.setEquipId(equipCfg.getId());
		// 刷新装备数据
		EquipUtil.replaceEquipItemId(equipEntity, equipCfg, player);
		equipEntity.notifyUpdate(false);
		// 如果穿在身上,需要刷新身上的属性
		RoleEntity roleEntity = player.getPlayerData().getRoleByEquipId(equipEntity.getId());
		if (roleEntity != null) {
			Attribute oldAttr = roleEntity.getAttribute().clone();
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			Attribute newAttr = roleEntity.getAttribute().clone();
			PlayerUtil.pushAllAttrChange(player, oldAttr, newAttr);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
		}
		// 同步装备的属性
		player.getPlayerData().syncEquipInfo(equipEntity.getId());
	}
	/**
	 * 裝備再造(合成工坊)
	 * @param protocolId
	 * @param protocol
	 */
	private void onEquipForge(int protocolId, EquipForgeReq protocol) {
		
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.workshop_Unlock)){
			sendError(protocolId, Status.error.CONDITION_NOT_ENOUGH);
			return;
		}
		
		List<ForgeInfo> infos = protocol.getInfosList();
		if (infos.size() <= 0) {
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return;
		}
		
		if (infos.size() > 99) {
			sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
			return;
		}
		
		Map <Integer,Integer> recrodItem = new HashMap <Integer,Integer>();  // 裝備變化表
		boolean succeed = false;
		ConsumeItems consumeMoney = ConsumeItems.valueOf();
		int totalpay = 0;
		int fusionTimes = 0;
		
		List<ItemInfo> allMaterial = new ArrayList<>();
		
		for (ForgeInfo ainfo:infos) {
			// 限制單一合成武器不能超過99個
			if ((ainfo.getCount() <= 0)||(ainfo.getCount() > 99)) {
				sendError(protocolId, Status.error.CONFIG_NOT_FOUND);
				return;
			}
			
			EquipCfg currentEquipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, ainfo.getEquipId());
			if (null == currentEquipCfg) {
				sendError(protocolId, error.CONFIG_NOT_FOUND);
				return;
			}
			
			// 檢查是否可合成
			if (!this.isCanForge(currentEquipCfg)){
				sendError(protocolId, error.CONFIG_NOT_FOUND);
				return;
			}
			
			int turn = ainfo.getCount();
			
			fusionTimes = fusionTimes + turn; // 合成總數
			
			List<ItemInfo> costItemlist = currentEquipCfg.getcostItemList(); // 消耗金錢
			List<ItemInfo> allcostItem = new ArrayList<>();
			
			for (ItemInfo aItem :costItemlist) {
				int mulpay = (int)aItem.getQuantity()*turn;
				allcostItem.add(ItemInfo.valueOf(aItem.getType(), aItem.getItemId(), mulpay));
				totalpay = totalpay + mulpay;
			}
			
			if (!consumeMoney.IncConsumeInfo(player.getPlayerData(), allcostItem)) {
				sendError(protocolId, error.PARAMS_INVALID);
				return;
			}
			
			List<ItemInfo> mList = currentEquipCfg.getUpgradeMaterialList();
			
			allMaterial.addAll(mList);
			
			int own = 0;
			for (int i = 0 ; i < turn ; i++) { // 再造幾次
								
				for (ItemInfo aItem :mList) {
					if (recrodItem.containsKey(aItem.getItemId())){
						own = recrodItem.get(aItem.getItemId());
						if (own >= aItem.getQuantity()) {
							own = own - (int)aItem.getQuantity(); // 扣除合成需求
							recrodItem.put(aItem.getItemId(),own); //紀錄還剩下消耗物物品
						} else {
							// 消耗品不夠送錯誤
							sendError(protocolId, error.PARAMS_INVALID);
							return;
						}
					} else {
						own = EquipUtil.getEquipsCount(aItem.getItemId(), player); // 首先找身上有沒有這個消耗物品
						if (own >= aItem.getQuantity()) {
							own = own - (int)aItem.getQuantity(); // 扣除合成需求
							recrodItem.put(aItem.getItemId(),own); //紀錄還剩下消耗物物品
						} else {
							// 消耗品不夠送錯誤
							sendError(protocolId, error.PARAMS_INVALID);
							return;
						}
					}
					
					//加入創建的裝備
					if (recrodItem.containsKey(ainfo.getEquipId())){
						own = recrodItem.get(ainfo.getEquipId());
					} else {
						own = EquipUtil.getEquipsCount(ainfo.getEquipId(), player);
					}
					recrodItem.put(ainfo.getEquipId(),own+1); // 紀錄合成出的裝備加一個
				}
			}
			succeed = true;
		}
		
		allMaterial = ItemInfo.mergeItem(allMaterial);
		
		int equipcount = 0;
		int incCount =0;
		int decCount = 0;
		int acount = 0;
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		AwardItems awards = new AwardItems();
		if (succeed) {
			for (Map.Entry<Integer, Integer> entry : recrodItem.entrySet()) {
				List<EquipEntity> equipList = player.getPlayerData().getEquipByEquipId(entry.getKey(),true); // 找裝備欄某裝備不包含已穿上的
				equipcount = equipList.size();
				// 合成模擬後的裝備大於身上的
				if (entry.getValue() > equipcount) { // 需新增
					incCount = entry.getValue() - equipcount;
			        awards.addItem(GameUtil.convertToStandardItemType(Const.itemType.EQUIP_VALUE),entry.getKey(),incCount);
				}
				// 合成模擬後裝備小於身上的
				if (entry.getValue() < equipcount) { // 需移除
					decCount = equipcount - entry.getValue();
					acount = 0;
					for (EquipEntity eEntity:equipList) {
						if (acount == decCount) {
							break;
						}
						consumeItems.addChangeInfo(changeType.CHANGE_EQUIP, eEntity.getId(), entry.getKey(), 1);
						acount++;
					}
				}
			}
			
			// 消耗金錢
			if (consumeMoney.checkConsume(player,protocolId)) {
				if (!consumeMoney.consumeTakeAffect(player, Action.EQUIP_FORGE)) {
					sendError(protocolId, error.PARAMS_INVALID);
					return;
				}
			}
			else {
				sendError(protocolId, error.PARAMS_INVALID);
				return;
			}
			
			// 消耗再造裝備材料
			if (consumeItems.checkConsume(player,protocolId)) {
				if(!consumeItems.consumeTakeAffect(player, Action.EQUIP_FORGE)) {
					sendError(protocolId, error.PARAMS_INVALID);
					return;
				}
			} else {
				sendError(protocolId, error.PARAMS_INVALID);
				return;
			}
			// 再造裝備產出
			awards.rewardTakeAffectAndPush(player, Action.EQUIP_FORGE, 2,TapDBSource.Equip_Forge,Params.valueOf("Material",allMaterial),
					Params.valueOf("totalpay", totalpay));
			
			// 推送装备再造次數
			QuestEventBus.fireQuestEvent(QuestEventType.MAKE_EQUIP, fusionTimes,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.player.getId()));
			
			// 7日之诗
			SevenDayQuestEventBus.fireQuestEvent(SevenDayEventType.EQUIP_FORGE, fusionTimes, player.getXid());
			
			Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.EQUIP_FORGE,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
			hawkMsg.pushParam(1);
			GsApp.getInstance().postMsg(hawkMsg);
			
//			Msg gMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_EQUIP_FORGE,
//					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
//			gMsg.pushParam(1);
//			GsApp.getInstance().postMsg(gMsg);
			
			
		}
	
		//流向處理
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.EQUIP_FORGE,
				Params.valueOf("totalpay", totalpay),
				Params.valueOf("awards", awards.toDbString()));
		// 返回数据
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_FORGE_S_VALUE, EquipForgeRes.newBuilder().setSuccess(succeed)));
	}
	
	/**
	 * 装备强化退回
	 * 
	 * @param session
	 * @param protocolId
	 * @param equipEnhanceParams
	 */
	private void onEquipEnhanceReset(int protocolId, HPEquipEnhanceReset equipEnhanceParams) {
		long equipId = equipEnhanceParams.getEquipId();
		//EquipEnhanceType type = equipEnhanceParams.getEquipEnhanceType();
		if (equipId < 0) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
		
		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity == null) {
			sendError(protocolId, Status.error.EQUIP_NOT_FOUND);
			return;
		}
		
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			sendError(protocolId, error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		
		int equipExclusiveId = equipCfg.getRoleAttrId();
		RoleEquipCfg roleEquipCfg = ConfigManager.getInstance().getConfigByKey(RoleEquipCfg.class, equipExclusiveId);
		if (roleEquipCfg == null || roleEquipCfg.getRoleIdList().size() <= 0) {
			sendError(protocolId, error.PARAMS_INVALID);
			return;
		}
				
		// 装备强化等级和经验判断
		
		int oldLevel = equipEntity.getStrength();
		
		if (oldLevel <= 0) {
			sendError(protocolId, Status.error.PARAMS_INVALID);
			return;
		}

		RoleEntity roleEntity = player.getPlayerData().getRoleByEquipId(equipEntity.getId());
		Attribute oldAttr = null;
		if (roleEntity != null) {
			oldAttr = roleEntity.getAttribute().clone();
		}
		
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		List<ItemInfo> itemList = ItemInfo.valueListOf(SysBasicCfg.getInstance().getResetEquip());
		boolean isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
		if (isAdd && consumeItems.checkConsume(player)) {
			if(!consumeItems.consumeTakeAffect(player, Action.EQUIP_Enhance_ReSet)) {
				player.sendError(protocolId,Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			} 
		} else {
			player.sendError(protocolId,Status.error.ITEM_NOT_ENOUGH_VALUE); 
			return; 
		}

		// 計算等级要退回多少材料
		int Level = 0;
		int resetTime = 0;
		
		AwardItems awardItems = new AwardItems();
		for (int i = 0 ; i < oldLevel ; i++ ) {
			Level++;
			List<ItemInfo> resetItemList = EquipStrengthRatioCfg.getStrengthConstItem(equipEntity.getEquipId(),Level);
			if (resetItemList.size() <= 0) {
				sendError(protocolId, Status.error.CONFIG_ERROR_VALUE);
				break;
			}
			awardItems.addItemInfos(resetItemList);
			resetTime++;
		}
		
		equipEntity.setStrength(0);
		
		awardItems.rewardTakeAffectAndPush(player, Action.EQUIP_Enhance_ReSet, 0);

		// 刷新装备主属性
		EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
		// 装备同步
		player.getPlayerData().syncEquipInfo(equipEntity.getId());

		equipEntity.notifyUpdate();

		if (roleEntity != null) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
			Attribute newAttr = roleEntity.getAttribute();
			PlayerUtil.pushAllAttrChange(player, oldAttr, newAttr);
		}

//		int minLevel = player.getPlayerData().getEquipsEnhanceMinLevel();
//		QuestEventBus.fireQuestEvent(QuestEventType.EQUIP_ENHANCE_ALL_LEVEL, minLevel, player.getXid());

//		if (strengthTimes > 0) {
//			Msg msg = Msg.valueOf(GsConst.MsgType.EQUIP_ENHANCE,
//					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
//			msg.pushParam(strengthTimes);
//			GsApp.getInstance().postMsg(msg);
//			// 7日之诗
//			SevenDayQuestEventBus.fireQuestEvent(SevenDayEventType.EQUIPSTRAN, strengthTimes, player.getXid());
//		}

		//EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		//if (equipCfg != null) {
//		StringBuffer buffer = new StringBuffer();
//		BehaviorLogger.log4Platform(player, Action.EQUIP_EHANCE, Params.valueOf("equipId", equipId),
//				Params.valueOf("equipLevel", equipCfg.getLevel()), Params.valueOf("costItem", buffer.toString()),
//				Params.valueOf("addItem", equipEntity.getStrengthItemStr()));
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.EQUIP_Enhance_ReSet,
				Params.valueOf("Id", equipId), Params.valueOf("equipId", equipEntity.getEquipId()),
				Params.valueOf("oldLevel", oldLevel), Params.valueOf("resetItem", awardItems.toString()),
				Params.valueOf("resetTime", resetTime));
		//}
		// 发送回报
		HPEquipEnhanceRet.Builder equipEnhanceBuilder = HPEquipEnhanceRet.newBuilder();
		equipEnhanceBuilder.setEquipId(equipId);
		sendProtocol(Protocol.valueOf(HP.code.EQUIP_ENHANCE_RESET_S_VALUE, equipEnhanceBuilder));
	}
	
	/**
	 * 開啟裝備相生
	 * @param protocol
	 */
	private void onEquipOpenMutual(Protocol protocol) {
		EquipOpenMutualReq req = protocol.parseProtocol(EquipOpenMutualReq.getDefaultInstance());
		//相生ID
		int fetterId = req.getMutualId();
		//相生Entity
		MutualEntity entity = player.getPlayerData().getMutualEntity();
		
		if (entity == null) {
			sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return;
		}
		//相生已經開啟
		if(entity.getOpenMutual().contains(fetterId)){
			sendError(protocol.getType(), Status.error.FETTER_IS_ACTIVED_VALUE);
			return;
		}
		//读取配置
		FetterEquipCfg mutualCfg = ConfigManager.getInstance().getConfigByKey(FetterEquipCfg.class, fetterId);
		if(mutualCfg==null){
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		//验证开启条件
		if(mutualCfg != null && mutualCfg.getArchiveIds() != null){
			for(int id : mutualCfg.getArchiveIds()){
				if (!entity.getStarMap().containsKey(id)) {
					sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
					return;
				}
			}
		}
		//发放奖励
		AwardItems awardItems = AwardItems.valueOf(mutualCfg.getAwardItem());
		awardItems.rewardTakeAffect(player, Action.OPEN_MUTUAL);
		//设置开启状态
		entity.getOpenMutual().add(fetterId);
		//entity.addalbumMap(fetterId, 1);	//測試用
		//保存DB
		entity.reConvert();
		entity.notifyUpdate();
		//刷新佣兵属性
		List<RoleEntity> activatedMercenaries = player.getPlayerData().getActiviceMercenary();
		for(RoleEntity roleEntity : activatedMercenaries){
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
		}
		player.getPlayerData().syncActivitedMercenaryRoleInfo();
		
		//推送客户端
		EquipOpenMutualResp.Builder resp = EquipOpenMutualResp.newBuilder();
		resp.setMutualId(fetterId);
		player.sendProtocol(Protocol.valueOf(HP.code.EQUIP_OPEN_MUTUAL_S, resp));
	}
	/***
	 * 裝備相生開啟資訊
	 */
	private void FetchOpenMutualInfo(Protocol protocol) {
		MutualEntity entity = player.getPlayerData().getMutualEntity();
		
		if (entity == null) {
			sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return;
		}
		//推送客户端
		OpenMutuaInfolResp.Builder resp = OpenMutuaInfolResp.newBuilder();
		for (Integer id : entity.getOpenMutual()) {
			FetterEquipCfg mutualCfg = ConfigManager.getInstance().getConfigByKey(FetterEquipCfg.class, id);
			resp.addMutualId(id);
			resp.addMinstar(mutualCfg.getGroupMinStar(entity));
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.FETCH_OPEN_MUTUAL_INFO_S, resp));
	}
}
