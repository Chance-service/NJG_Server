package com.guaji.game.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.BadgeCfg;
import com.guaji.game.config.BadgeGachaListCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.BadgeEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Badge.HPBadgeFusionReq;
import com.guaji.game.protocol.Badge.HPBadgeFusionRet;
import com.guaji.game.protocol.Badge.HPBadgeLockReq;
import com.guaji.game.protocol.Badge.HPBadgeRefineReq;
import com.guaji.game.protocol.Badge.HPBadgeRefineRet;
import com.guaji.game.protocol.Badge.HPMysticalDressChange;
import com.guaji.game.protocol.Badge.HPMysticalDressChangeRet;
import com.guaji.game.protocol.Badge.HPMysticalDressRemoveInfoSync;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

public class PlayerBadgeModule extends PlayerModule {
    /**
     * 构造函数
     *
     * @param player
     */
    public PlayerBadgeModule(Player player) {
        super(player);
    }


    /**
     * 玩家登陆处理(数据同步)
     */
    @Override
    protected boolean onPlayerLogin() {
        player.getPlayerData().loadBadgeEntities();
        // 同步徽章信息
        player.getPlayerData().syncBadgeInfo();
        return true;
    }


    /**
     * 徽章上下
     */
    @ProtocolHandlerAnno(code = HP.code.BADGE_DRESS_C_VALUE)
    public void badgeDress(Protocol protocol) {
        HPMysticalDressChange request = protocol.parseProtocol(HPMysticalDressChange.getDefaultInstance());
        int roleId = request.getRoleId();
        int loc = request.getLoc(); // 部位 1-4
        // 1 表示穿戴 2 表示卸下 3 表示替换
        int type = request.getType();
        if (roleId <= 0 || loc < 1 || loc > 4 || type < 1 || type > 3) {
            sendError(protocol.getType(), Status.error.PARAMS_INVALID);
        }
        // 验证角色Id
        RoleEntity roleEntity = player.getPlayerData().getRoleById(roleId);
        if (roleEntity == null) {
            sendError(protocol.getType(), Status.error.ROLE_NOT_FOUND);
            return;
        }
        
        // 部位1限制
        if (loc == 1) {        	
    		if (!FunctionUnlockCfg.checkUnlock(player,roleEntity,Const.FunctionType.rune01_Unlock)){
    			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
    			return;
    		}
        }
        
        // 部位2限制
        if (loc == 2) {
    		if (!FunctionUnlockCfg.checkUnlock(player,roleEntity,Const.FunctionType.rune02_Unlock)){
    			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
    			return;
    		}
        }
        
        // 部位3.4限制
        
    	if (loc == 3) {
    		if (!FunctionUnlockCfg.checkUnlock(player,roleEntity,Const.FunctionType.rune03_Unlock)){
    			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
    			return;
    		}
    	}
    	
    	if (loc == 4) {
    		if (!FunctionUnlockCfg.checkUnlock(player,roleEntity,Const.FunctionType.rune04_Unlock)){
    			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
    			return;
    		}
    	}
        
        
        // 主角不能佩戴
        if (roleEntity.getType() == Const.roleType.MAIN_ROLE_VALUE) {
            sendError(protocol.getType(), Status.error.BADGE_MAIN_ROLE_CANNOT_DRESS);
            return;
        }
        if (type == 1) {
            long onBadgeId = request.getOnEquipId();
            BadgeEntity onBadgeEntity = player.getPlayerData().getBadgeById(onBadgeId);
            if (onBadgeEntity == null) {
                sendError(protocol.getType(), Status.error.PARAMS_INVALID);
                return;
            }
            BadgeCfg onBadgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, onBadgeEntity.getBadgeId());
            if (onBadgeCfg == null) {
                sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
                return;
            }
            if (roleEntity.getBadgeMap().get(loc) > 0) {
                // 该部位已经有徽章
                sendError(protocol.getType(), Status.error.BADGE_LOC_ALREADY_DRESS);
                return;
            }
//            if (getLocByBadgeType(roleEntity, onBadgeCfg.getType()) > 0) {
//                // 角色身上已经有同类型的徽章
//                sendError(protocol.getType(), Status.error.BADGE_TYPE_ALREADY_DRESS);
//                return;
//            }
            RoleEntity onRoleEntity = player.getPlayerData().getRoleByBadgeId(onBadgeId);
            if (onRoleEntity != null) {
                // 其他角色已经穿戴,移除徽章
            	int part = onRoleEntity.getBadgePartById(onBadgeId);
            	onRoleEntity.setBadgePart(0, part);
            	onRoleEntity.notifyUpdate();
            	PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), onRoleEntity);
                // 同步角色信息
                player.getPlayerData().syncRoleInfo(onRoleEntity.getId());
            }
            roleEntity.setBadgePart(onBadgeId, loc);
            roleEntity.notifyUpdate();
            player.getPlayerData().syncBadgeInfo(onBadgeId);
        }
        if (type == 2) {
            long offBadgeId = request.getOffEquipId();
            BadgeEntity offBadgeEntity = player.getPlayerData().getBadgeById(offBadgeId);
            if (offBadgeEntity == null) {
                sendError(protocol.getType(), Status.error.PARAMS_INVALID);
                return;
            }
            BadgeCfg offBadgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, offBadgeEntity.getBadgeId());
            if (offBadgeCfg == null) {
                sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
                return;
            }
            if (roleEntity.getBadgeMap().get(loc) <= 0) {
                // 该部位没有徽章
                sendError(protocol.getType(), Status.error.BADGE_LOC_NOT_DRESS);
                return;
            }
            roleEntity.setBadgePart(0, loc);
            roleEntity.notifyUpdate();
                    
            player.getPlayerData().syncBadgeInfo(offBadgeId);
        }
        if (type == 3) {
            long onBadgeId = request.getOnEquipId();
            BadgeEntity onBadgeEntity = player.getPlayerData().getBadgeById(onBadgeId);
            if (onBadgeEntity == null) {
                sendError(protocol.getType(), Status.error.PARAMS_INVALID);
                return;
            }
            BadgeCfg onBadgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, onBadgeEntity.getBadgeId());
            if (onBadgeCfg == null) {
                sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
                return;
            }
            long offBadgeId = request.getOffEquipId();
            BadgeEntity offBadgeEntity = player.getPlayerData().getBadgeById(offBadgeId);
            if (offBadgeEntity == null) {
                sendError(protocol.getType(), Status.error.PARAMS_INVALID);
                return;
            }
            BadgeCfg offBadgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, offBadgeEntity.getBadgeId());
            if (offBadgeCfg == null) {
                sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
                return;
            }
            if (roleEntity.getBadgePartById(offBadgeId) != loc) {
                // 该部位没有該徽章
                sendError(protocol.getType(), Status.error.BADGE_LOC_NOT_DRESS);
                return;
            }
//            int roleLoc = getLocByBadgeType(roleEntity, onBadgeCfg.getType());
//            if (roleLoc != 0 && roleLoc != loc) {
//                // 角色身上已经有同类型的徽章
//                sendError(protocol.getType(), Status.error.BADGE_TYPE_ALREADY_DRESS);
//                return;
//            }
            RoleEntity onRoleEntity = player.getPlayerData().getRoleByBadgeId(onBadgeId);
            if (onRoleEntity != null) {
                // 其他角色已经穿戴,替換脫下來的裝備
            	int part = onRoleEntity.getBadgePartById(onBadgeId);
            	onRoleEntity.setBadgePart(offBadgeId,part);
            	onRoleEntity.notifyUpdate();
            	PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), onRoleEntity);
                // 同步角色信息
                player.getPlayerData().syncRoleInfo(onRoleEntity.getId());
            }
            roleEntity.setBadgePart(onBadgeId, loc);
            roleEntity.notifyUpdate();
            player.getPlayerData().syncBadgeInfo(onBadgeId, offBadgeId);
        }

        Attribute oldAttr = roleEntity.getAttribute().clone();
        // 刷新属性
        PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
        // 同步角色信息
        player.getPlayerData().syncRoleInfo(roleEntity.getId());

        Attribute newAttr = roleEntity.getAttribute();
        PlayerUtil.pushAllAttrChange(player,oldAttr,newAttr);



        HPMysticalDressChangeRet.Builder builder = HPMysticalDressChangeRet.newBuilder();
        builder.setSuccess(true);
        sendProtocol(Protocol.valueOf(HP.code.BADGE_DRESS_S_VALUE, builder));
    }

    /**
     * 徽章升级
     */
//    @ProtocolHandlerAnno(code = HP.code.BADGE_UPGRADE_C_VALUE)
//    public void badgeUpgrade(Protocol protocol) {
//        HPMysticalDressAbsorbReq hpBadgeUpgrade = protocol.parseProtocol(HPMysticalDressAbsorbReq.getDefaultInstance());
//        long badgeId = hpBadgeUpgrade.getId();
//        int roleId = hpBadgeUpgrade.getRoleId();
//        List<Long> swallowedBadgeIdList = hpBadgeUpgrade.getDressIdsList();
//
//        //region 验证参数
//        if (badgeId <= 0 || swallowedBadgeIdList.size() <= 0 || swallowedBadgeIdList.size() > 6) {
//            sendError(protocol.getType(), Status.error.PARAMS_INVALID);
//            return;
//        }
//        // 验证是否有该徽章
//        BadgeEntity upgradeBadgeEntity = player.getPlayerData().getBadgeById(badgeId);
//        if (upgradeBadgeEntity == null) {
//            sendError(protocol.getType(), Status.error.PARAMS_INVALID);
//            return;
//        }
//        BadgeCfg upgradeBadgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, upgradeBadgeEntity.getBadgeId());
//        if (upgradeBadgeCfg == null) {
//            sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
//            return;
//        }
//        //endregion
//
//        // region 验证待吸收徽章
//        // id 去重
//        Set<Long> swallowedIdSet = new HashSet<>(swallowedBadgeIdList);
//        Set<BadgeEntity> swallowedEntitySet = new HashSet<>();
//        for (Long swallowedId : swallowedIdSet) {
//            // 验证是否包括待升级徽章
//            if (swallowedId <= 0 || swallowedId == badgeId) {
//                sendError(protocol.getType(), Status.error.PARAMS_INVALID);
//                return;
//            }
//            // 验证是否有该徽章
//            BadgeEntity swallowedEntity = player.getPlayerData().getBadgeById(swallowedId);
//            if (swallowedEntity == null) {
//                sendError(protocol.getType(), Status.error.PARAMS_INVALID);
//                return;
//            }
//            BadgeCfg swallowedCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, swallowedEntity.getBadgeId());
//            if (swallowedCfg == null) {
//                sendError(protocol.getType(), Status.error.PARAMS_INVALID);
//                return;
//            }
//            // 验证是否被穿戴
//            RoleEntity roleItemEntity = player.getPlayerData().getRoleByBadgeId(swallowedId);
//            if (roleItemEntity != null) {
//                sendError(protocol.getType(), Status.error.BADGE_OTHER_ROLE_DRESS);
//                return;
//            }
//            swallowedEntitySet.add(swallowedEntity);
//        }
//        // endregion
//
//        // 验证穿戴角色
//        RoleEntity roleEntity = player.getPlayerData().getRoleByBadgeId(badgeId);
//        if (roleEntity != null) {
//            if (roleEntity.getId() != roleId) {
//                // 参数roleId 与 穿戴该徽章的角色不符
//                sendError(protocol.getType(), Status.error.PARAMS_INVALID);
//                return;
//            }
//        }
//
//        // 验证最大等级
//        if (upgradeBadgeEntity.getLevel()>=upgradeBadgeCfg.getMaxLevel()){
//            sendError(protocol.getType(), Status.error.BADGE_MAX_LEVEL);
//        }
//
//        // region 吸收升级
//        int sumExp = upgradeBadgeEntity.getLevelExp();
//        for (BadgeEntity swallowedEntity : swallowedEntitySet) {
//            if (swallowedEntity.getLevelExp() > 0) {
//                sumExp += swallowedEntity.getLevelExp();
//            } else {
//                BadgeCfg swallowedCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, swallowedEntity.getBadgeId());
//                sumExp += swallowedCfg.getBasicExp();
//            }
//            // 消耗徽章
//            player.consumeBadge(swallowedEntity.getId(), Action.BADGE_UPGRADE);
//        }
//
//        // 更新徽章等级
//        int level = upgradeBadgeCfg.getLevelByExp(sumExp);
//        upgradeBadgeEntity.setLevelExp(sumExp);
//        upgradeBadgeEntity.setLevel(level);
//        upgradeBadgeEntity.notifyUpdate();
//        // endregion
//
//        // 刷新属性
//        if (roleEntity != null) {
//            Attribute oldAttr = roleEntity.getAttribute().clone();
//            // 刷新属性
//            PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
//            // 同步角色信息
//            player.getPlayerData().syncRoleInfo(roleEntity.getId());
//            Attribute newAttr = roleEntity.getAttribute();
//            PlayerUtil.pushAttrChange(player,oldAttr,newAttr);
//        }
//
//        // 同步删除已经吸收的徽章
//        syncRemoveBadgeInfo(swallowedIdSet);
//
//        // 同步徽章信息
//        player.getPlayerData().syncBadgeInfo(badgeId);
//
//        HPMysticalDressAbsorbRet.Builder builder = HPMysticalDressAbsorbRet.newBuilder();
//        builder.setSuccess(true);
//        sendProtocol(Protocol.valueOf(HP.code.BADGE_UPGRADE_S_VALUE, builder));
//    }
    
    /**
     * 徽章合成
     */
    @ProtocolHandlerAnno(code = HP.code.BADGE_FUSION_C_VALUE)
    public void badgeFusion(Protocol protocol) {
    	
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.badge_Unlock)){
			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
			return;
		}
		
		HPBadgeFusionReq hpBadgeFusion = protocol.parseProtocol(HPBadgeFusionReq.getDefaultInstance());
		List<Long> BadgeIdList = hpBadgeFusion.getFusionIdsList();
		Set<Long> FusionIdSet = new HashSet<>(BadgeIdList);
		Set<BadgeEntity> fusionEntitySet = new HashSet<>();
		//验证参数
		if ( FusionIdSet.size() != 5) {
		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		    return;
		}
		int target = 0;
		int comparaRank = 0;
		List<Integer> targetList = new ArrayList<>();
		for (Long fusionId : FusionIdSet) {
		      // 验证是否包括待升级徽章
		  if (fusionId <= 0) {
		      sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		      return;
		  }
		  // 验证是否有该徽章
		  BadgeEntity badgeEntity = player.getPlayerData().getBadgeById(fusionId);
		  if (badgeEntity == null) {
		      sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		      return;
		  }
		  
		  if (badgeEntity.getFusionLock() == 1) {
			  // 合成已上鎖
		      sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		      return;
		  }
		  
		  BadgeCfg badgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, badgeEntity.getBadgeId());
		  if (badgeCfg == null) {
		      sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		      return;
		  }
		  
		  if (comparaRank != 0) { //被合成徽章都必須同一等級類型
			  if (badgeCfg.getRank() != comparaRank) { 
			      sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			      return;
			  }
		  } else {
			  comparaRank = badgeCfg.getRank();
		  }
		  
		  target = badgeCfg.getAfterId();
		  
		  BadgeCfg targetCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, target);
		  // 驗證有無下一級
		  if (targetCfg == null) {
			  // 跳回原來等級,不報錯
			  //target = badgeCfg.getAfterId();
		      sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		      return;
		  }
		  
		  targetList.add(target);
		  
		  // 验证是否被穿戴
		  RoleEntity roleItemEntity = player.getPlayerData().getRoleByBadgeId(fusionId);
		  if (roleItemEntity != null) {
		      sendError(protocol.getType(), Status.error.BADGE_OTHER_ROLE_DRESS);
		      return;
		  }
		  fusionEntitySet.add(badgeEntity);
		}
		
	    for (BadgeEntity swallowedEntity : fusionEntitySet) {
		      // 消耗徽章
		      player.consumeBadge(swallowedEntity.getId(), Action.BADGE_FUSION);
	    }
	    // 同步删除已经吸收的徽章
	    syncRemoveBadgeInfo(FusionIdSet);
	    
		try {
			int index = GuaJiRand.randInt(0,targetList.size()-1);
			target = targetList.get(index);
		} catch (Exception e) {
			MyException.catchException(e);
		}
	    
		List<Long> BeforId = new ArrayList<>();
		for (BadgeEntity badgeEntity :player.getPlayerData().getBadgeEntities()) {
			BeforId.add(badgeEntity.getId());
		}
	    AwardItems awards = new AwardItems();
	    awards.addItem(GameUtil.convertToStandardItemType(Const.itemType.BADGE_VALUE),target,1);
	    awards.rewardTakeAffectAndPush(player, Action.BADGE_FUSION, 0,TapDBSource.Badge_Fusion);
	    
	    List<Long> afterId = new ArrayList<>();
	    for (BadgeEntity badgeEntity :player.getPlayerData().getBadgeEntities()) {
	    	afterId.add(badgeEntity.getId());
	    }
	    afterId.removeAll(BeforId);
	    long BadgeIndex = 0;
	    if (afterId.size() > 0) { // 比對前後badgeIndex變化找出新產出的Index
	    	BadgeIndex = afterId.get(0);
	    }
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.BADGE_FUSION,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(1);
		GsApp.getInstance().postMsg(hawkMsg);
	    
	    //target
	    HPBadgeFusionRet.Builder builder = HPBadgeFusionRet.newBuilder();
	    builder.setSuccess(true);
	    builder.setAward(String.valueOf(BadgeIndex));
	    sendProtocol(Protocol.valueOf(HP.code.BADGE_FUSION_S_VALUE, builder));
	    
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.BADGE_FUSION,
				Params.valueOf("BadgeIndex", BadgeIndex),
				Params.valueOf("target", target));
  }
	

    /**
     * 删除徽章信息
     *
     * @param removeIds
     */
    public void syncRemoveBadgeInfo(Set<Long> removeIds) {
        HPMysticalDressRemoveInfoSync.Builder builder = HPMysticalDressRemoveInfoSync.newBuilder();
        for (Long removeId : removeIds) {
            builder.addRemIds(removeId);
        }
        sendProtocol(Protocol.valueOf(HP.code.BADGE_REMOVE_SYNC_S_VALUE, builder));
    }

    @ProtocolHandlerAnno(code = HP.code.BADGE_BAG_EXTEND_C_VALUE)
    public void badgeBagExtend(Protocol protocol) {
        StateEntity stateEntity = player.getPlayerData().getStateEntity();
        if (stateEntity == null) {
            // 数据初始化存在异
            return;
        }
        if (stateEntity.getLeftBadgeBagExtendTimes() <= 0) {
            // 装备背包扩展次数已满
            sendError(protocol.getType(), Status.error.EQUIP_BAG_EXTEND_TIMES_FULL);
            return;
        }
        int goldCost = SysBasicCfg.getInstance().getEquipExtendGoldCost(stateEntity.getEquipBagExtendTimes());
        if (player.getGold() < goldCost) {
            sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH);
            return;
        }

        player.consumeGold(goldCost, BehaviorLogger.Action.BADGE_BAG_EXTEND);

        ConsumeItems consumeItems = ConsumeItems.valueOf(Const.changeType.CHANGE_GOLD, goldCost);
        consumeItems.pushChange(player);

        stateEntity.setBadgeBagSize(stateEntity.getBadgeBagSize() + SysBasicCfg.getInstance().getBadgeExtendSize());
        stateEntity.setBadgeBagExtendTimes(stateEntity.getBadgeBagExtendTimes() + 1);

        stateEntity.notifyUpdate(true);
        player.getPlayerData().syncStateInfo();

        player.sendStatus(0, Status.error.EQUIP_BAG_EXTEND_SUC_VALUE);


        BehaviorLogger.log4Platform(player, Action.EQUIP_BAG_EXTEND, BehaviorLogger.Params.valueOf("goldCost", goldCost),
                Params.valueOf("afterBagVolume", stateEntity.getEquipBagSize()));
    }
    /**
     * 	徽章精煉
     * @param protocol
     */
    @ProtocolHandlerAnno(code = HP.code.BADGE_REFINE_C_VALUE)
    public void badgeRefine(Protocol protocol) {
    	HPBadgeRefineReq request = protocol.parseProtocol(HPBadgeRefineReq.getDefaultInstance());
    	
    	long badgeId = request.getBadgeId();
    	
    	int action = request.getAction();
    	
		// 验证是否有该徽章
		BadgeEntity badgeEntity = player.getPlayerData().getBadgeById(badgeId);
		if (badgeEntity == null) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		    return;
		}
		
		BadgeCfg badgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, badgeEntity.getBadgeId());
		if (badgeCfg == null) {
		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		    return;
		}
		
        RoleEntity onRoleEntity = player.getPlayerData().getRoleByBadgeId(badgeId);
        
        int slotId = -1;
        
        List<Integer> locklist = new ArrayList<>();
        
        if (action == 0) { // 轉換精煉資料
    		if (badgeEntity.getRefineList().size() != badgeCfg.getSlots()) {
    			// 沒有精煉資料
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		List<Integer> skillList = new ArrayList<>();
    		for (int rId : badgeEntity.getRefineList()) {
    			    			
    			int refineId = rId > GsConst.BADGE_LOCK_MASK ? (rId % GsConst.BADGE_LOCK_MASK) : rId;
    			
    			BadgeGachaListCfg rcfg = ConfigManager.getInstance().getConfigByKey(BadgeGachaListCfg.class, refineId);
    			if (rcfg == null) {
        		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
        		    return;
    			}
    			skillList.add(refineId);
    		}
    		badgeEntity.setSkillList(skillList);
    		//badgeEntity.setRefineList(emptryList);
    		badgeEntity.notifyUpdate();
    	} else if (action == 1) { // 一般精煉
    		
    		if ((badgeEntity.getRefineList().size() > 0)&&(badgeEntity.getRefineList().size() != badgeCfg.getSlots())) {
    			// 沒有精煉資料
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		
    		int lockNum = 0;
    		int number = 1;
    		for (Integer rId :badgeEntity.getRefineList()) {
    			if (rId > GsConst.BADGE_LOCK_MASK) {
    				locklist.add(number);
    				lockNum++;
    			}
    			number++;
    		}
    		
    		if (lockNum >= badgeCfg.getSlots()) {
    			// 槽位檢查異常
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		
    		int costNum = SysBasicCfg.getInstance().getRefineStoneList(lockNum);
 			ItemInfo stoneInfo =  ItemInfo.valueOf(SysBasicCfg.getInstance().getRefineStone());
 			stoneInfo.setQuantity(stoneInfo.getQuantity()*costNum);
    		
    		if (lockNum > 0) { // 鎖定孔位
    			
    			Map<Integer,Integer> lockMap = new HashMap<>(); // <slotNum,cfgId>
    			

        		// check slotNumber
        		for (int slotNum : locklist ) {
        			if ((slotNum <=0) || (slotNum > badgeCfg.getSlots())) {
            		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            		    return;
        			}
        			int lockId = badgeEntity.getRefineList().get(slotNum-1);
        			int cfgId = lockId % GsConst.BADGE_LOCK_MASK;
        			BadgeGachaListCfg bglcfg = ConfigManager.getInstance().getConfigByKey(BadgeGachaListCfg.class, cfgId);
            		if (bglcfg == null) {
	        		    sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
	        		    return;
        			}
            		
            		lockMap.put(slotNum, lockId);
        		}
        		
    			boolean isCost = false;
    			ConsumeItems consumeItems = ConsumeItems.valueOf();
    			List<ItemInfo> itemInfoList = new ArrayList<>();//ItemInfo.valueListOf(costItems);
    			itemInfoList.add(stoneInfo);
    			isCost = consumeItems.addConsumeInfo(player.getPlayerData(),itemInfoList); 
    			if (isCost && consumeItems.checkConsume(player)) { 
    				if(!consumeItems.consumeTakeAffect(player, Action.BADGE_REFINE)) {
	        		    sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
	        		    return;
    				}
    			} else {
    				sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
    				return; 
    			}
        		        		
        		List<Integer> newList = BadgeGachaListCfg.getRandomBadage(badgeCfg.getSkillpool(), badgeCfg.getSlots(), lockMap);
        		
        		if (newList.size() == badgeCfg.getSlots()) {
            		badgeEntity.setRefineList(newList);
            		badgeEntity.notifyUpdate();
        		} else {
        		    sendError(protocol.getType(), Status.error.CONFIG_ERROR);
        		    return;
        		} 
    		} else {
     			
    			if (badgeEntity.getRefineList().size() > 0) {
	    			boolean isCost = false;
	    			ConsumeItems consumeItems = ConsumeItems.valueOf();
	    			List<ItemInfo> itemInfoList = new ArrayList<>();//ItemInfo.valueListOf(costItems);
	    			itemInfoList.add(stoneInfo);
	    			isCost = consumeItems.addConsumeInfo(player.getPlayerData(),itemInfoList); 
	    			if (isCost && consumeItems.checkConsume(player)) { 
	    				if(!consumeItems.consumeTakeAffect(player, Action.BADGE_REFINE)) {
		        		    sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
		        		    return;
	    				}
	    			} else {
	    				sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
	    				return; 
	    			}
    			}
    			List<Integer> addList = BadgeGachaListCfg.getRandomBadage(badgeCfg.getSkillpool(), badgeCfg.getSlots(),null);
        		if (addList.size() == badgeCfg.getSlots()) {
            		badgeEntity.setRefineList(addList);
            		if (badgeEntity.getSkillList().size() == 0) {
            			badgeEntity.setSkillList(addList);
            		} 
            		badgeEntity.notifyUpdate();
        		} else {
        		    sendError(protocol.getType(), Status.error.CONFIG_ERROR);
        		    return;
        		}
        		
    		}
    		
    	} else if (action == 2) { // 特殊精煉
    		slotId = request.getSlotId();
    		
    		if (badgeCfg.getUnlocksp() == 0) {
    			//此符文不能特殊精煉
    			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    			return;
    		}
    		
    		if ((slotId <= 0) || (slotId > badgeCfg.getSlots())) {
    			// 槽位檢查異常
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		if (badgeEntity.getRefineList().size() != badgeCfg.getSlots()) {
    			// 沒有精煉資料
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		
    		int cfgId = badgeEntity.getRefineList().get(slotId-1);
    		
    		if (cfgId > GsConst.BADGE_LOCK_MASK) {
    			// 鎖定狀態不能特殊精煉
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		
    		BadgeGachaListCfg bglcfg = ConfigManager.getInstance().getConfigByKey(BadgeGachaListCfg.class, cfgId);
    		
    		if (bglcfg == null) {
    		    sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
    		    return;
    		}
    		
    		Set<Integer> typeSet =  new HashSet<>();
    		
    		for (int aId :badgeEntity.getRefineList()) {
    			int reId = (aId > GsConst.BADGE_LOCK_MASK) ? aId % GsConst.BADGE_LOCK_MASK : aId;
    			BadgeGachaListCfg acfg = ConfigManager.getInstance().getConfigByKey(BadgeGachaListCfg.class, reId);
    			if (acfg != null) {
    				typeSet.add(acfg.getType());
    			}
    		}
    		
    		int  newcfgId = BadgeGachaListCfg.getChoseRefine(badgeCfg.getSkillpool(),bglcfg.getRare(),typeSet);
    		
    		if (newcfgId == -1) {
    		    sendError(protocol.getType(), Status.error.CONFIG_ERROR_VALUE);
    		    return;
    		}
    		
    		ItemInfo maxInfo =  ItemInfo.valueOf(SysBasicCfg.getInstance().getRefineStoneMax());
			boolean isCost = false;
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			List<ItemInfo> itemInfoList = new ArrayList<>();//ItemInfo.valueListOf(costItems);
			itemInfoList.add(maxInfo);
			isCost = consumeItems.addConsumeInfo(player.getPlayerData(),itemInfoList); 
			if (isCost && consumeItems.checkConsume(player)) { 
				if(!consumeItems.consumeTakeAffect(player, Action.BADGE_REFINE)) {
        		    sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
        		    return;
				}
			} else {
				sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			}
    		
    		List<Integer> fixList = new ArrayList<>(badgeEntity.getRefineList());
    		fixList.set(slotId-1,newcfgId);
    		badgeEntity.setRefineList(fixList);
    		badgeEntity.notifyUpdate();
    		
    	} else if (action == 3) { // 鎖洞孔位
    		locklist = request.getLockSlotList();
    		if (locklist.size() <= 0) {
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		    		
    		int lockNum = 0; // 已上鎖的孔數量
    		for (Integer rid :badgeEntity.getRefineList()) {
    			if (rid > GsConst.BADGE_LOCK_MASK) {
    				lockNum++;
    			}
    		}
    		
    		if ((lockNum + locklist.size()) >= badgeCfg.getSlots()){
				//超過可以鎖定數量
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		
    		int useNum = 0; // 符石鎖使用數量
    		for (Integer aId : locklist) {
    			if ((aId > badgeCfg.getSlots()) || (aId <= 0)) {
    				// 孔位超出範圍
        		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
        		    return;
    			}
    			// 檢查是否重複鎖
    			if (badgeEntity.getRefineList().get(aId-1) > GsConst.BADGE_LOCK_MASK) {
    				//已鎖定
        		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
        		    return;
    			}
    			
    			lockNum++;
    			
    			useNum = useNum + SysBasicCfg.getInstance().getRefineLockList(lockNum-1);
    		}
    		
    		
    		ItemInfo lockInfo =  ItemInfo.valueOf(SysBasicCfg.getInstance().getRefineLock());
    		
 			lockInfo.setQuantity(lockInfo.getQuantity()*useNum);
 			
			boolean isCost = false;
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			List<ItemInfo> itemInfoList = new ArrayList<>();//ItemInfo.valueListOf(costItems);
			itemInfoList.add(lockInfo);
			isCost = consumeItems.addConsumeInfo(player.getPlayerData(),itemInfoList); 
			if (isCost && consumeItems.checkConsume(player)) { 
				if(!consumeItems.consumeTakeAffect(player, Action.BADGE_REFINE)) {
        		    sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
        		    return;
				}
			} else {
				sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			}
			
			for (Integer aId : locklist) {
				int idx = aId - 1;
				if (badgeEntity.getRefineList().get(idx) < GsConst.BADGE_LOCK_MASK) {
					int lockId = badgeEntity.getRefineList().get(idx) + GsConst.BADGE_LOCK_MASK;
					badgeEntity.setRefineId(idx,lockId);
				}
			}
			badgeEntity.notifyUpdate();
    	} else if (action == 4) { // 解鎖孔位
    		locklist = request.getLockSlotList();
    		
    		if (locklist.size() <= 0) {
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		
    		int lockNum = 0; // 已上鎖的孔數量
    		for (Integer rid :badgeEntity.getRefineList()) {
    			if (rid > GsConst.BADGE_LOCK_MASK) {
    				lockNum++;
    			}
    		}
    		
    		if (lockNum  <= 0){
				//沒有上鎖的孔
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		
    		for (Integer aId : locklist) {
    			if ((aId > badgeCfg.getSlots()) || (aId <= 0)) {
    				// 孔位超出範圍
        		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
        		    return;
    			}
    			// 檢查是否重複鎖
    			if (badgeEntity.getRefineList().get(aId-1) < GsConst.BADGE_LOCK_MASK) {
    				//未上鎖
        		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
        		    return;
    			}
    		}
    		
			for (Integer aId : locklist) {
				int idx = aId - 1;
				if (badgeEntity.getRefineList().get(idx) > GsConst.BADGE_LOCK_MASK) {
					int lockId = badgeEntity.getRefineList().get(idx) % GsConst.BADGE_LOCK_MASK;
					badgeEntity.setRefineId(idx,lockId);
				}
			}
			badgeEntity.notifyUpdate();
    		
    	}else if (action == 5) {
    		if (badgeEntity.getSkillList().size() != badgeCfg.getSlots()) {
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		
    		if (badgeEntity.getRefineList().size() != badgeCfg.getSlots()) {
    		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
    		    return;
    		}
    		
    		List<Integer> fixList = new ArrayList<>();
    		
    		for (int idx = 0; idx < badgeCfg.getSlots(); idx++) {
    			int reId = badgeEntity.getRefineList().get(idx);
    			int skId = badgeEntity.getSkillList().get(idx);
    			if (reId > GsConst.BADGE_LOCK_MASK) {
    				fixList.add(reId);
    			} else {
    				fixList.add(skId);
    			}
    		}
    		
    		badgeEntity.setRefineList(fixList);
    		badgeEntity.notifyUpdate();
    	}else {
    	
			// 協定異常
		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		    return;
    	}
    	
    	player.getPlayerData().syncBadgeInfo(badgeId);
    	
        if (onRoleEntity != null) {
            // 有角色穿著 同步角色信息
            player.getPlayerData().syncRoleInfo(onRoleEntity.getId());
        }
        
        HPBadgeRefineRet.Builder builder = HPBadgeRefineRet.newBuilder();
        builder.setAction(action);
        builder.setBadgeId(badgeId);
        builder.addAllRefineId(badgeEntity.getRefineList());

	    sendProtocol(Protocol.valueOf(HP.code.BADGE_REFINE_S_VALUE, builder));
	    
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.BADGE_REFINE,
				Params.valueOf("badgeId", badgeId),
				Params.valueOf("action", action),
				Params.valueOf("slotId", slotId),
				Params.valueOf("locklist", locklist),
				Params.valueOf("RefineList", badgeEntity.getRefineList()));
    }
    
    /**
     *	 徽章上鎖
     */
    @ProtocolHandlerAnno(code = HP.code.BADGE_LOCK_C_VALUE)
    public void badgeLock(Protocol protocol) {
    	HPBadgeLockReq request = protocol.parseProtocol(HPBadgeLockReq.getDefaultInstance());
    	long badgeId = request.getBadgeId();
    	    	
		// 验证是否有该徽章
		BadgeEntity badgeEntity = player.getPlayerData().getBadgeById(badgeId);
		if (badgeEntity == null) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		    return;
		}
		
		BadgeCfg badgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, badgeEntity.getBadgeId());
		if (badgeCfg == null) {
		    sendError(protocol.getType(), Status.error.PARAMS_INVALID);
		    return;
		}
		
		int lock = badgeEntity.getFusionLock() == 0 ? 1 : 0;
		
		badgeEntity.setFusionLock(lock);
		badgeEntity.notifyUpdate(true);
		
		// 同步徽章信息
		player.getPlayerData().syncBadgeInfo(badgeId);
		
        HPMysticalDressChangeRet.Builder builder = HPMysticalDressChangeRet.newBuilder();
        builder.setSuccess(true);
		
		sendProtocol(Protocol.valueOf(HP.code.BADGE_LOCK_S_VALUE,builder));
		
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.BADGE_LOCK,
				Params.valueOf("Index", badgeId),
				Params.valueOf("badgeId", badgeCfg.getId()),
				Params.valueOf("lock", lock),
				Params.valueOf("RefineList", badgeEntity.getRefineList()));
		
    }
}
