package com.guaji.game.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.protocol.Attribute.Attr;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.ElementAscendCfg;
import com.guaji.game.config.ElementAttrCfg;
import com.guaji.game.config.ElementCfg;
import com.guaji.game.config.ElementLevelExpCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ElementEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.ElementUtil;
import com.guaji.game.util.GJLocal;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.WeightUtil;
import com.guaji.game.util.WeightUtil.WeightItem;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.ElementOpr.HPElementAdvanced;
import com.guaji.game.protocol.ElementOpr.HPElementAdvancedRet;
import com.guaji.game.protocol.ElementOpr.HPElementDecompose;
import com.guaji.game.protocol.ElementOpr.HPElementDecomposeRet;
import com.guaji.game.protocol.ElementOpr.HPElementDress;
import com.guaji.game.protocol.ElementOpr.HPElementDressRet;
import com.guaji.game.protocol.ElementOpr.HPElementLevelUp;
import com.guaji.game.protocol.ElementOpr.HPElementLevelUpRet;
import com.guaji.game.protocol.ElementOpr.HPElementRecast;
import com.guaji.game.protocol.ElementOpr.HPElementRecastConfirm;
import com.guaji.game.protocol.ElementOpr.HPElementRecastConfirmRet;
import com.guaji.game.protocol.ElementOpr.HPElementRecastRet;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 元素模块
 * 
 * @author hawk
 */
public class PlayerElementModule extends PlayerModule {
	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerElementModule(Player player) {

		super(player);
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
	 * 玩家登陆处理(数据同步)
	 */
	@Override
	protected boolean onPlayerLogin() {
		List<ElementEntity> elementEntities = player.getPlayerData().loadElements();
		for(ElementEntity elementEntity : elementEntities) {
			ElementUtil.refreshAttribute(elementEntity);
		}
		
		//player.getPlayerData().syncDressedElementInfo();;
		return true;
	}
	
	
	@Override
	protected boolean onPlayerAssemble() {
		player.getPlayerData().syncElementInfo();
		// 检测之前是否有元素背包初始大小的设置
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if(stateEntity != null && stateEntity.getEquipBagSize() == 0) {
			stateEntity.setElementBagSize(SysBasicCfg.getInstance().getElementBagDefault());
			stateEntity.notifyUpdate(false);
		}
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

		return super.onProtocol(protocol);
	}

	/**
	 * 元素背包扩展
	 * @param protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.ELEMENT_BAG_EXTEND_C_VALUE)
	public boolean onElementBagExtend(Protocol protocol) {
		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD,SysBasicCfg.getInstance().getElementBagExtendCost());
		if(!consumeItems.checkConsume(player, HP.code.ELEMENT_BAG_EXTEND_C_VALUE)) {
			return false;
		}
		
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if(stateEntity == null) {
			return false;
		}
		consumeItems.consumeTakeAffect(player, Action.ELEMENT_BAG_EXTEND);
		
		stateEntity.setElementBagSize(stateEntity.getElementBagSize() + SysBasicCfg.getInstance().getElementBagExtendNum());
		stateEntity.notifyUpdate(true);
		// 同步
		player.getPlayerData().syncStateInfo();
		// 发送文字提示
		player.sendStatus(0, Status.error.ELEMENT_BAG_EXTEND_SUC_VALUE);
		// 记录日志
		BehaviorLogger.log4Platform(player, Action.ELEMENT_BAG_EXTEND, Params.valueOf("goldCost", SysBasicCfg.getInstance().getElementBagExtendCost()),
				Params.valueOf("afterEleBagVolume", stateEntity.getElementBagSize()));
		return true;
	}
	
	/**
	 * 元素升级
	 * @param protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.ELEMENT_LVL_UP_C_VALUE)
	public boolean onElementLvlup(Protocol protocol) {
		HPElementLevelUp params = protocol.parseProtocol(HPElementLevelUp.getDefaultInstance());
		long elementId = params.getElementId();
		List<Long> swallowEleIds = params.getSwallowEleIdsList();
		
		if(elementId <= 0 || swallowEleIds.size() == 0) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		
		ElementEntity targetElementEntity = player.getPlayerData().getElementById(elementId);
		
		if(targetElementEntity == null) {
			sendError(HP.code.ELEMENT_LVL_UP_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return false;
		}
		
		if (targetElementEntity.getLevel() >= player.getLevel()) {
			sendError(HP.code.ELEMENT_LVL_UP_C_VALUE, Status.error.ELEMENT_NOT_GREATE_THAN_PLAYER);
			return false;
		}
		
		Set<Long> swallowIds = new HashSet<>();
		swallowIds.addAll(swallowEleIds);
		List<ElementEntity> needSwallowEles = new LinkedList<>();
		
		for(Long eleId : swallowIds) {
			ElementEntity ele = player.getPlayerData().getElementById(eleId);
			if(ele == null) {
				continue ;
			}
			
			if(player.getPlayerData().getRoleByEquipId(eleId) != null) {
				continue ;
			}
			
			needSwallowEles.add(ele);
		}
		int costCoins = 0;
		int addExp = 0;
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		for(ElementEntity elementEntity : needSwallowEles) {
			ElementLevelExpCfg elementLevelExpCfg = ElementLevelExpCfg.getCfgByLevel(elementEntity.getLevel());
			if(elementLevelExpCfg != null) {
				costCoins += elementLevelExpCfg.getSwallowCoins();
				addExp += elementLevelExpCfg.getSwallowExp() * SysBasicCfg.getInstance().getElementSwallowRatio(elementEntity.getQuality());
			}
			consumeItems.addChangeInfo(changeType.CHANGE_ELEMENT, elementEntity.getId(),elementEntity.getItemId(),1);
		}
		consumeItems.addChangeInfo(changeType.CHANGE_COIN, costCoins);
		if(!consumeItems.checkConsume(player, protocol.getType())) {
			return false;
		}
		consumeItems.consumeTakeAffect(player, Action.ELEMENT_LVL_UP);
		
		int oldLevel = Math.max(1, targetElementEntity.getLevel());
		int oldExp = targetElementEntity.getExp();
		int newExp = oldExp + addExp;
		int level = oldLevel;
			// 经验不够升级
		for (; level < ElementLevelExpCfg.getMaxLevel(); level++) {
			ElementLevelExpCfg elementLevelExpCfg = ElementLevelExpCfg.getCfgByLevel(level);
			if (elementLevelExpCfg == null) {
				Log.errPrintln("cannot find element level exp config, level: " + level);
				break;
			}
			
			if (level >= player.getLevel()) {
				Log.errPrintln("cannot element levelup greate than player level : " + level);
				break;
			}
			
			int levelUpNeedExp = elementLevelExpCfg.getExp();
			if (newExp < levelUpNeedExp) {
				break;
			}
			newExp -= levelUpNeedExp;
		}
		
		targetElementEntity.setExp(newExp);
		targetElementEntity.setLevel(level);
		
		Map<Const.attr,Integer> oldElementMap = new HashMap<Const.attr,Integer>();
		for (Map.Entry<Const.attr, Integer> entry : targetElementEntity.getAttribute().getAttrMap().entrySet()) {
			oldElementMap.put(entry.getKey(),entry.getValue());
		}
		// 刷新元素属性
		ElementUtil.refreshAttribute(targetElementEntity);
		
		Map<Const.attr,Integer> newElementMap = new HashMap<Const.attr,Integer>();
		for (Map.Entry<Const.attr, Integer> entry : targetElementEntity.getAttribute().getAttrMap().entrySet()) {
			newElementMap.put(entry.getKey(),entry.getValue());
		}
				
		targetElementEntity.notifyUpdate(true);
		
		player.getPlayerData().syncElementInfo(targetElementEntity.getId());
		
		
		RoleEntity roleEntity = ElementUtil.getElementDressRole(player, targetElementEntity);
		if(roleEntity != null) {
			// 穿戴在身上
			// editby: crazyjohn 2015-11-27 去除警告
			//Attribute oldAttr = roleEntity.getAttribute().clone();
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
			//Attribute newAttr = roleEntity.getAttribute();
			//PlayerUtil.pushAttrChange(player, oldAttr, newAttr);
			PlayerUtil.pushElementAttrChange(player, oldElementMap, newElementMap);
		}
		
		BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, Action.ELEMENT_LVL_UP, 
				Params.valueOf("elementId", elementId), 
				Params.valueOf("id", targetElementEntity.getId()),
				Params.valueOf("oldLevel", oldLevel),
				Params.valueOf("oldExp", oldExp),
				Params.valueOf("newLevel", level),
				Params.valueOf("newExp", newExp),
				Params.valueOf("costElements", swallowEleIds));
		
		sendProtocol(Protocol.valueOf(HP.code.ELEMENT_LVL_UP_S_VALUE,HPElementLevelUpRet.newBuilder().setElementId(elementId)));
		return true;
	}

	/**
	 * 元素分解
	 * @param protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.ELEMENT_DECOMPOSE_C_VALUE)
	public boolean onDecompose(Protocol protocol) {
		HPElementDecompose params = protocol.parseProtocol(HPElementDecompose.getDefaultInstance());
		List<Long> eleIds = params.getElementIdsList();
		if(eleIds.size() == 0) {
			sendError(protocol.getType(), Status.error.ELEMENT_SIZE_NONE);
			return false;
		}
		
		Set<Long> eleSet = new HashSet<>();
		eleSet.addAll(eleIds);
		HPElementDecomposeRet.Builder retBuilder = HPElementDecomposeRet.newBuilder();
		AwardItems awardItems = new AwardItems();
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		for(Long eleId : eleSet) {
			ElementEntity elementEntity = player.getPlayerData().getElementById(eleId);
			if(elementEntity != null) {
				ElementAscendCfg elementAscendCfg = ElementAscendCfg.getAscendCfg(elementEntity.getQuality());
				if(elementAscendCfg != null) {
					awardItems.appendAward(elementAscendCfg.getDecomposeAwardInfo());
				}
				retBuilder.addElementIds(eleId);
				consumeItems.addChangeInfo(changeType.CHANGE_ELEMENT, elementEntity.getId(), elementEntity.getItemId(), 1);
				
				BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, Action.ELEMENT_DECOMPOSE, 
						Params.valueOf("elementId", elementEntity.getItemId()), 
						Params.valueOf("id", elementEntity.getId()),
						Params.valueOf("level", elementEntity.getLevel()),
						Params.valueOf("num", 1),
						Params.valueOf("getDec", elementAscendCfg.getDecomposeAwardInfo().toDbString()));
			}
		}
		
		consumeItems.consumeTakeAffect(player, Action.ELEMENT_DECOMPOSE);
		
		awardItems.rewardTakeAffectAndPush(player, Action.ELEMENT_DECOMPOSE,1);
		
		sendProtocol(Protocol.valueOf(HP.code.ELEMENT_DECOMPOSE_S_VALUE,retBuilder));
		return true;
	}
	
	/**
	 * 元素进阶
	 * @param protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.ELEMENT_ADVANCE_C_VALUE)
	public boolean onElementAdvance(Protocol protocol) {
		HPElementAdvanced params = protocol.parseProtocol(HPElementAdvanced.getDefaultInstance());
		long eleId = params.getElementId();
		if(eleId <= 0) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		
		ElementEntity elementEntity = player.getPlayerData().getElementById(eleId);
		if(elementEntity == null) {
			sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return false;
		}
		
		if(elementEntity.getQuality() >= Const.equipQuality.ORANGE_VALUE) {
			// 元素品质已满
			return false;
		}
		
		// 生成随机属性
		// 材料消耗
		ElementAscendCfg elementAscendCfg = ElementAscendCfg.getAscendCfg(elementEntity.getQuality());
		if(elementAscendCfg == null) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return false;
		}
		if(elementAscendCfg.getEvolutionConsumeInfo() != null) {
			ConsumeItems consumeItems = new ConsumeItems();
			if(!consumeItems.addConsumeInfo(player.getPlayerData(), elementAscendCfg.getEvolutionConsumeInfo())) {
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return false;
			};
			
			if(!consumeItems.checkConsume(player, protocol.getType())) {
				return false;
			}
			
			consumeItems.consumeTakeAffect(player, Action.ELEMENT_ADVANCED);
		}
		String oldBasicAttr = elementEntity.getBasicAttr().toString();
		String oldExtraAttr = elementEntity.getExtraAttr().toString();
		ElementUtil.advanceElement(elementEntity);
		int oldQuality = elementEntity.getQuality();
		elementEntity.setQuality(oldQuality + 1);

		ElementUtil.refreshAttribute(elementEntity);
		
		player.getPlayerData().syncElementInfo(elementEntity.getId());
		
		elementEntity.notifyUpdate(true);
		
		RoleEntity roleEntity = ElementUtil.getElementDressRole(player, elementEntity);
		if(roleEntity != null) {
			// 穿戴在身上
			Attribute oldAttr = roleEntity.getAttribute().clone();
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
			Attribute newAttr = roleEntity.getAttribute();
			PlayerUtil.pushAttrChange(player, oldAttr, newAttr);
		}
		
		sendProtocol(Protocol.valueOf(HP.code.ELEMENT_ADVANCE_S_VALUE, HPElementAdvancedRet.newBuilder().setElementId(eleId)));
		
		BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, Action.ELEMENT_ADVANCED, 
				Params.valueOf("elementId", elementEntity.getItemId()), 
				Params.valueOf("id", elementEntity.getId()),
				Params.valueOf("oldQuality", oldQuality),
				Params.valueOf("newQuality", elementEntity.getQuality()),
				Params.valueOf("oldBasicAttr", oldBasicAttr),
				Params.valueOf("oldExtraAttr", oldExtraAttr),
				Params.valueOf("newBasicAttr", elementEntity.getBasicAttr()),
				Params.valueOf("newExtraAttr", elementEntity.getExtraAttr()));
		
		return false;
	}
	
	/**
	 * 元素穿戴
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.ELEMENT_DRESS_C_VALUE)
	public boolean onElementDress(Protocol protocol) {
		HPElementDress params = protocol.parseProtocol(HPElementDress.getDefaultInstance());
		long eleId = params.getElementId();
		int index = params.getIndex() - 1;
		if(index < 0 || index >= SysBasicCfg.getInstance().getMaxElementSize()) {
			sendError(HP.code.ELEMENT_DRESS_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		
		RoleEntity mainRole = player.getPlayerData().getMainRole();
		if(mainRole == null) {
			return false;
		}
		Attribute oldAttr = mainRole.getAttribute().clone();
		if(eleId == 0) {
			// 表示卸下
			if(mainRole.getElementByIndex(index) > 0) {
				mainRole.setElementId(index,0L);
			};
		} else {
			ElementEntity elementEntity = player.getPlayerData().getElementById(eleId);
			if(elementEntity == null) {
				sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
				return false;
			}
			
			if (elementEntity.getLevel() > player.getLevel()) {
				sendError(HP.code.ELEMENT_DRESS_C_VALUE, Status.error.NOT_LEVEL_OVER_LIMIT);
				return false;
			}
			
			if(index > 6) {
				// 职业限制的格子
				ElementCfg elementCfg = ConfigManager.getInstance().getConfigByKey(ElementCfg.class, elementEntity.getItemId());
				if(elementCfg == null) {
					sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
					return false;
				}
				
				if (player.getLevel() < SysBasicCfg.getInstance().getProfElementLevelLimit()) {
					sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return false;
				}
				
				if(elementCfg.getProfLimit() != 0 && elementCfg.getProfLimit() != player.getProf()) {
					sendError(protocol.getType(), Status.error.PROF_NOT_FIT);
					return false;
				}
			}
			
			mainRole.setElementId(index,eleId);
		}
		
		mainRole.notifyUpdate(true);
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), mainRole);
		player.getPlayerData().syncRoleInfo(mainRole.getId());
		Attribute newAttr = mainRole.getAttribute();
		PlayerUtil.pushAttrChange(player, oldAttr, newAttr);
		HPElementDressRet.Builder re = HPElementDressRet.newBuilder();
		re.setElementId(eleId);
		re.setIndex(index);
		sendProtocol(Protocol.valueOf(HP.code.ELEMENT_DRESS_S_VALUE, re));
		return true;
	}
	
	/**
	 * 元素重铸
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.ELEMENT_RECAST_C_VALUE)
	public boolean onElementRecast(Protocol protocol) {
		HPElementRecast params = protocol.parseProtocol(HPElementRecast.getDefaultInstance());
		long elementId = params.getElementId();
		int attrId = params.getAttrId();
		int type = params.getType();
		if(elementId <= 0 || attrId <= 0 || type < 1 || type > 2) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		
		ElementEntity elementEntity = player.getPlayerData().getElementById(elementId);
		if(elementEntity == null) {
			sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return false;
		}
		
		ElementCfg elementCfg = ConfigManager.getInstance().getConfigByKey(ElementCfg.class, elementEntity.getItemId());
		if(elementCfg == null) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return false;
		}
		
		//  重铸消耗
		ConsumeItems consumeItems = new ConsumeItems();
		if(type == 2) {
			// 高级重铸
			consumeItems.addChangeInfo(changeType.CHANGE_GOLD, SysBasicCfg.getInstance().getRecastGoldCost());
		}
		if(elementCfg.getRecastConsumeInfo() != null) {
			if(!consumeItems.addConsumeInfo(player.getPlayerData(), elementCfg.getRecastConsumeInfo())) {
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return false;
			};
			
			if(!consumeItems.checkConsume(player, protocol.getType())) {
				return false;
			}
		}
		
		List<WeightItem<Integer>> extraRandomList = new LinkedList<>(); 
		if(type == 1) {
			extraRandomList.addAll(elementCfg.getRecastCommonPool());
		} else if(type == 2) {
			extraRandomList.addAll(elementCfg.getRecastAdvancePool());
		}
		
		if(extraRandomList.size() == 0) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return false;
		}
		
		Iterator<WeightItem<Integer>> iter = extraRandomList.iterator();
		while(iter.hasNext()) {
			WeightItem<Integer> item = iter.next();
			ElementAttrCfg elementAttrCfg = ConfigManager.getInstance().getConfigByKey(ElementAttrCfg.class, item.getValue());
			Attribute extraAttr = elementEntity.getExtraAttr();
			Attribute basicAttr = elementEntity.getBasicAttr();
			if(elementAttrCfg == null || (extraAttr.containsAttr(elementAttrCfg.getAttrId()) && elementAttrCfg.getAttrId() != attrId) || basicAttr.containsAttr(elementAttrCfg.getAttrId())) {
				iter.remove();
			}
		}
		
		if(extraRandomList.size() < 3) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return false;
		}
		
		consumeItems.consumeTakeAffect(player, Action.ELEMENT_RECAST);
		
		List<Integer> attrIds = new LinkedList<>();
		List<Integer> attrValues = new LinkedList<>();
		int index = 0;
		for(int i=0; i< extraRandomList.size();i++) {
			Integer attrCfgId = WeightUtil.random(extraRandomList);	
			ElementAttrCfg elementAttrCfg = ConfigManager.getInstance().getConfigByKey(ElementAttrCfg.class, attrCfgId);
			if(elementAttrCfg != null) {
				attrIds.add(elementAttrCfg.getAttrId());
				attrValues.add(elementAttrCfg.getAttrValue());
				if (index ++ > 2) {
					break;
				}
			}
			extraRandomList.remove(attrCfgId.intValue());
		}
		String oldBasicAttr = elementEntity.getBasicAttr().toString();
		String oldExtraAttr = elementEntity.getExtraAttr().toString();
		elementEntity.setRecastAttrIds(attrIds);
		elementEntity.setRecastAttrValues(attrValues);
		elementEntity.setRecastAttrId(attrId);
		
		elementEntity.notifyUpdate(true);
		
		HPElementRecastRet.Builder recastBuilder = HPElementRecastRet.newBuilder();
		recastBuilder.setElementId(elementId);
		recastBuilder.setAttrId(attrId);
		for(int i=0;i<attrIds.size();i++) {
			recastBuilder.addAttrs(Attr.newBuilder().setAttrId(attrIds.get(i)).setAttrValue(attrValues.get(i)));
		}
		sendProtocol(Protocol.valueOf(HP.code.ELEMENT_RECAST_S_VALUE, recastBuilder));
		
		BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, Action.ELEMENT_RECAST, 
				Params.valueOf("elementId", elementEntity.getItemId()), 
				Params.valueOf("id", elementEntity.getId()),
				Params.valueOf("oldBasicAttr", oldBasicAttr),
				Params.valueOf("oldExtraAttr", oldExtraAttr),
				Params.valueOf("newBasicAttr", elementEntity.getBasicAttr()),
				Params.valueOf("newExtraAttr", elementEntity.getExtraAttr()));
		
		return true;
	}
	
	/**
	 * 重铸确认
	 * @param protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.ELEMENT_RECAST_CONFIRM_C_VALUE)
	public boolean onRecastConfirm(Protocol protocol) {
		HPElementRecastConfirm params = protocol.parseProtocol(HPElementRecastConfirm.getDefaultInstance());
		long elementId = params.getElementId();
		int index = params.getIndex() - 1;
		if(elementId <= 0 || index < 0) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		
		ElementEntity elementEntity = player.getPlayerData().getElementById(elementId);
		if(elementEntity == null) {
			sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return false;
		}
		
		ElementCfg elementCfg = ConfigManager.getInstance().getConfigByKey(ElementCfg.class, elementEntity.getItemId());
		if(elementCfg == null) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return false;
		}
		
		if (GJLocal.isLocal(GJLocal.KOREAN) && (params.getType() == 2)){
			//  重铸消耗
			ConsumeItems consumeItems = new ConsumeItems();
			consumeItems.addChangeInfo(changeType.CHANGE_GOLD, SysBasicCfg.getInstance().getRecastGoldCost());
			consumeItems.consumeTakeAffect(player, Action.ELEMENT_RECAST);
		}
		
		
		elementEntity.removeExtraAttr(elementEntity.getRecastAttrId());
		
		int attrId = elementEntity.getRecastAttrIds().get(index);
		int attrValue = elementEntity.getRecastAttrValues().get(index);
		
		elementEntity.addExtraAttr(attrId, attrValue);
		
		
		// 清除重铸状态
		elementEntity.clearRecastInfo();
		
		ElementUtil.refreshAttribute(elementEntity);
		player.getPlayerData().syncElementInfo(elementEntity.getId());
		elementEntity.notifyUpdate(true);

		RoleEntity roleEntity = ElementUtil.getElementDressRole(player, elementEntity);
		if(roleEntity != null) {
			// 穿戴在身上
			Attribute oldAttr = roleEntity.getAttribute().clone();
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			roleEntity.notifyUpdate(true);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
			Attribute newAttr = roleEntity.getAttribute();
			PlayerUtil.pushAttrChange(player, oldAttr, newAttr);
		}
		
		sendProtocol(Protocol.valueOf(HP.code.ELEMENT_RECAST_CONFIRM_S_VALUE,HPElementRecastConfirmRet.newBuilder().setElementId(elementId)));
		return true;
	}
	
}
