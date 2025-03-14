package com.guaji.game.soul;

import com.guaji.game.entity.RoleEntity;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;

/**
 * 消耗佣兵碎片
 * @author Melvin.Mao
 * @date Jun 15, 2017 11:28:22 AM
 */
public class ConsumeRoleSoul {

	public static ConsumeRoleSoul valueOf() {
		return new ConsumeRoleSoul();
	}

	/**
	 * 检测佣兵碎片消耗
	 * @param player
	 * @param roleSoulInfo 消耗的佣兵碎片结构体
	 */
	public int checkConsume(Player player, RoleSoulInfo roleSoulInfo) {
		RoleEntity roleEntity = player.getPlayerData().getMercenaryByItemId(roleSoulInfo.getRoleId());
		// 判断佣兵存不存在
		if (null == roleEntity) {
			return Status.error.MERCENARY_NOT_FOUND_VALUE;
		}
		// 判断佣兵状态，没有激活才可以消耗
		if (roleEntity.getRoleState() != Const.RoleActiviteState.NOT_ACTIVITE_VALUE) {
			return Status.error.ROLE_STATUS_ERROR_VALUE;
		}
		// 检测数量
		int consumeSoulCount = roleSoulInfo.getCount();
		if (roleEntity.getSoulCount() < consumeSoulCount) {
			return Status.error.ROLE_SOUL_LACK_VALUE;
		}
		return 0;
	}
	

	/**
	 * 消耗佣兵碎片
	 * @param player
	 * @param roleSoulInfo 消耗的佣兵碎片结构体
	 */
	public void consumeRoleSoul(Player player,RoleSoulInfo roleSoulInfo,Action action) {
		RoleEntity roleEntity = player.getPlayerData().getMercenaryByItemId(roleSoulInfo.getRoleId());
		roleEntity.setSoulCount(roleEntity.getSoulCount() - roleSoulInfo.getCount());
		roleEntity.notifyUpdate();
		// 记录佣兵碎片消耗BI平台日志
		BehaviorLogger.log4Platform(player, action, 
				Params.valueOf("roleId", roleEntity.getItemId()), 
				Params.valueOf("roleAttr", Const.playerAttr.ROLE_SOUL_VALUE), 
				Params.valueOf("sub", roleSoulInfo.getCount()), 
				Params.valueOf("after", roleEntity.getSoulCount()));
	}
}
