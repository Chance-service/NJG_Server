package com.guaji.game.util;

import java.util.ArrayList;
import java.util.List;

import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ElementEntity;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.FormationEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.RoleRingEntity;
import com.guaji.game.entity.SkillEntity;
import com.guaji.game.entity.TitleEntity;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Player.PlayerInfo;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;

public class QuickPhotoUtil {
    /**
     * 生成在线玩家快照对象
     *
     * @param player
     * @return
     */
    public static PlayerSnapshotInfo.Builder genOnlineQuickPhoto(PlayerData playerData) {

        if (playerData.getPlayerEntity() == null)
            return null;

        PlayerSnapshotInfo.Builder playerSnapshot = PlayerSnapshotInfo.newBuilder();
        playerSnapshot.setVersion(SysBasicCfg.getInstance().getPlayerSnapShotVersion());
        playerSnapshot.setPlayerId(playerData.getId());
        if (playerData.getPlayerEntity() != null && playerData.getPlayerEntity().getLogoutTime() != null) {
            playerSnapshot.setLastLogoutTime((int) (playerData.getPlayerEntity().getLogoutTime().getTime() / 1000));
        }

        PlayerInfo.Builder playerInfo = BuilderUtil.genPlayerBuilder(playerData.getPlayerEntity(), playerData,
                playerData.getPlayerTalentEntity().getTalentNum());
        playerSnapshot.setPlayerInfo(playerInfo);

        // 主角信息
        RoleEntity mainRoleEntity = playerData.getMainRole();
        RoleInfo.Builder mainRoleInfo = BuilderUtil.genRoleBuilder(playerData, mainRoleEntity,
                playerData.getEquipEntities(), playerData.getSkillEntities(), playerData.getElementEntities(),playerData.getBadgeEntities());
        playerSnapshot.setMainRoleInfo(mainRoleInfo);
        
        // 競技場出战佣兵阵容信息
        FormationEntity formationEntity = playerData.getFormationByType(GsConst.FormationType.FormationEnd);
        if (formationEntity != null) {
            List<Integer> fightRoleList = formationEntity.getFightingArray();
            playerSnapshot.addAllFightingRoleId(playerData.getHeroIdtoItemId(fightRoleList));
        }

        // 佣兵信息
        for (RoleEntity role : playerData.getMercenary()) {
            if (role == null) {
                continue;
            }

            if (role.getRoleState() == Const.RoleActiviteState.CAN_ACTIVITE_VALUE
                    || role.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
            	
            	RoleInfo.Builder roleInfo = BuilderUtil.genRoleBuilder(playerData, role,
                        playerData.getEquipEntities(), playerData.getSkillEntities(), playerData.getElementEntities(),playerData.getBadgeEntities());
            	if (formationEntity != null) { // 為競技隊伍,設定競技隊伍屬性
            		if (formationEntity.getFightingArray().contains(role.getId())) {
            			roleInfo.setAttribute(BuilderUtil.genAttributeBuilder(role.getArenaAttr()));
            		}
            	}
                playerSnapshot.addMercenaryInfo(roleInfo);
            }
            
        }

        // 技能详情
        attachSkillInfo(playerData, playerSnapshot, playerData.getSkillEntities());

        // 工会信息
        attachAllianceInfo(playerData, playerSnapshot, playerData.getPlayerAllianceEntity());

        // 装备信息
        attachEquipInfo(playerData, playerSnapshot, playerData.getEquipEntities());

        // 元素信息
        attachElementInfo(playerData, playerSnapshot, playerData.getElementEntities());

        // 称号信息
        attachTitleInfo(playerData, playerSnapshot, playerData.getTitleEntity());

        // 光环信息
        attachRingInfo(playerData, playerSnapshot, playerData.getRingInfoEntities());

        // 刷新玩家属性战力信息（未进行属性更新，也需要刷新战力）
        PlayerUtil.refreshFightValue(playerData);

        return playerSnapshot;
    }

    private static void attachSkillInfo(PlayerData playerData, PlayerSnapshotInfo.Builder playerSnapshot,
                                        List<SkillEntity> skillEntities) {
        if (skillEntities != null) {
            for (int i = 0; i < skillEntities.size(); i++) {
                playerSnapshot.addSkillInfo(BuilderUtil.genSkillBuilder(skillEntities.get(i)));
            }
        }
    }

    private static void attachAllianceInfo(PlayerData playerData, PlayerSnapshotInfo.Builder playerSnapshot,
                                           PlayerAllianceEntity allianceEntity) {
        if (allianceEntity != null) {
            playerSnapshot.setAllianceInfo(BuilderUtil.genAllianceBuilder(allianceEntity));
        }
    }

    private static void attachEquipInfo(PlayerData playerData, PlayerSnapshotInfo.Builder playerSnapshot,
                                        List<EquipEntity> equipEntities) {
        if (equipEntities != null) {
            List<Long> equipIdList = new ArrayList<Long>();
            // 主角身上的装备详情
            for (int i = Const.equipPart.HELMET_VALUE; i <= Const.equipPart.NECKLACE_VALUE; i++) {
                equipIdList.add(playerData.getMainRole().getPartEquipId(i));
            }

            // 佣兵身上装备部位信息
            int parts[] = {Const.equipPart.HELMET_VALUE, Const.equipPart.RING_VALUE, Const.equipPart.CUIRASS_VALUE,
                    Const.equipPart.WEAPON1_VALUE, Const.equipPart.WEAPON2_VALUE, Const.equipPart.LEGGUARD_VALUE};

            // 佣兵身上的装备详情
            for (int i = 0; i < parts.length; i++) {
                for (RoleEntity mercenaryEntity : playerData.getMercenary()) {
                    equipIdList.add(mercenaryEntity.getPartEquipId(parts[i]));
                }
            }

            attachEquipInfoInner(playerSnapshot, equipEntities, equipIdList);
        }
    }

    private static void attachElementInfo(PlayerData playerData, PlayerSnapshotInfo.Builder playerSnapshot,
                                          List<ElementEntity> elementEntities) {
        if (elementEntities != null) {
            for (ElementEntity elementEntity : elementEntities) {
                if (playerData.getMainRole().checkElementInDress(elementEntity.getId())) {
                    playerSnapshot.addElementInfo(BuilderUtil.genElementBuilder(elementEntity));
                }
            }
        }
    }

    /**
     * 拆出来预防 JVM 内编译错误
     *
     * @param playerSnapshot
     * @param equipEntities
     * @param equipIdList
     */
    private static void attachEquipInfoInner(PlayerSnapshotInfo.Builder playerSnapshot, List<EquipEntity> equipEntities,
                                             List<Long> equipIdList) {

        if (equipEntities == null)
            return;
        
        for (EquipEntity equipEntity : equipEntities) {
            if (equipIdList.contains(equipEntity.getId())) {
                playerSnapshot.addEquipInfo(BuilderUtil.genEquipBuilder(equipEntity));
            }
        }
      
    }

    private static void attachTitleInfo(PlayerData playerData, PlayerSnapshotInfo.Builder playerSnapshot,
                                        TitleEntity titleEntity) {
        if (titleEntity != null) {
            playerSnapshot.setTitleInfo(BuilderUtil.genTitleBuilder(titleEntity));
        }
    }

    private static void attachRingInfo(PlayerData playerData, PlayerSnapshotInfo.Builder playerSnapshot,
                                       List<RoleRingEntity> roleRingEntities) {
        if (roleRingEntities != null && roleRingEntities.size() > 0) {
            for (int i = 0; i < roleRingEntities.size(); i++) {
                playerSnapshot.addRingInfos(BuilderUtil.genRingInfoBuilder(roleRingEntities.get(i)));
            }
        }
    }
}
