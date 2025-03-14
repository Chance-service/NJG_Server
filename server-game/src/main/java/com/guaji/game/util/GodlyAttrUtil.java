package com.guaji.game.util;

import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;

public class GodlyAttrUtil {
	/**
	 * 检测装备强化等级满足等级条件
	 * 
	 * @param player
	 * @param equipId
	 * @param level
	 * @return
	 */
	public static boolean checkEquipStrengthLvl(Player player, long equipId, int level) {
		EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		if (equipEntity != null && equipEntity.getStrength() >= level) {
			return true;
		}
		return false;
	}

	/**
	 * 检测角色全身装备强化要求
	 * 
	 * @param roleEntity
	 * @param level
	 * @return
	 */
	public static boolean checkRoleEquipPartsIntensify(Player player, RoleEntity roleEntity, int level) {
		if (roleEntity.getEquip1() <= 0 || !checkEquipStrengthLvl(player, roleEntity.getEquip1(), level)) {
			return false;
		}

		if (roleEntity.getEquip2() <= 0 || !checkEquipStrengthLvl(player, roleEntity.getEquip2(), level)) {
			return false;
		}

		if (roleEntity.getEquip3() <= 0 || !checkEquipStrengthLvl(player, roleEntity.getEquip3(), level)) {
			return false;
		}

		if (roleEntity.getEquip4() <= 0 || !checkEquipStrengthLvl(player, roleEntity.getEquip4(), level)) {
			return false;
		}

		if (roleEntity.getEquip5() <= 0 || !checkEquipStrengthLvl(player, roleEntity.getEquip5(), level)) {
			return false;
		}

		if (roleEntity.getEquip6() <= 0 || !checkEquipStrengthLvl(player, roleEntity.getEquip6(), level)) {
			return false;
		}

		if (roleEntity.getEquip7() <= 0 || !checkEquipStrengthLvl(player, roleEntity.getEquip7(), level)) {
			return false;
		}

		if (roleEntity.getEquip8() <= 0 || !checkEquipStrengthLvl(player, roleEntity.getEquip8(), level)) {
			return false;
		}

		if (roleEntity.getEquip9() <= 0 || !checkEquipStrengthLvl(player, roleEntity.getEquip9(), level)) {
			return false;
		}

		if (roleEntity.getEquip10() <= 0 || !checkEquipStrengthLvl(player, roleEntity.getEquip10(), level)) {
			return false;
		}

		return true;
	}

	/**
	 * 检测神器属性是否激活
	 * 
	 * @param player
	 * @param godlyAttrCond1
	 * @return
	 */
	public static boolean checkGodlyAttrActive(Player player, EquipEntity equipEntity, int godlyAttrCond) {
		if (godlyAttrCond <= 0) {
			return false;
		}

		// 非主角不可激活
		RoleEntity roleEntity = EquipUtil.getEquipDressRole(player, equipEntity);
		if (roleEntity == null || roleEntity.getType() != Const.roleType.MAIN_ROLE_VALUE) {
			return false;
		}

		// 主角全身装备强化+1
		if (godlyAttrCond == Const.attrCond.ALL_EQUIP_INTENSIFY_1_VALUE) {
			if (checkRoleEquipPartsIntensify(player, roleEntity, 1)) {
				return false;
			}
		}

		// 主角全身装备强化+2
		if (godlyAttrCond == Const.attrCond.ALL_EQUIP_INTENSIFY_2_VALUE) {
			if (checkRoleEquipPartsIntensify(player, roleEntity, 2)) {
				return false;
			}
		}

		// 主角全身装备强化+3
		if (godlyAttrCond == Const.attrCond.ALL_EQUIP_INTENSIFY_3_VALUE) {
			if (checkRoleEquipPartsIntensify(player, roleEntity, 3)) {
				return false;
			}
		}

		return true;
	}
}
