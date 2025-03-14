package com.guaji.cs.battle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.guaji.os.MyException;

import com.guaji.game.battle.BattleRole;
import com.guaji.game.protocol.Const.RoleActiviteState;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;

/**
 * 跨服战通用
 */
public class BattleUtil {
	
	/**
	 * 初始化跨服战
	 */
	private BattleUtil() {
	}

	/**
	 * 获取参战计算对象
	 * 
	 * @return
	 */
	public static List<BattleRole> getBattleRoleList(PlayerSnapshotInfo snapshot) {
		RoleInfo.Builder roleInfo = snapshot.getMainRoleInfo().toBuilder();
		List<BattleRole> battleRoles = new LinkedList<BattleRole>();
		// 开启战场
		if (roleInfo != null) {
			battleRoles.add(new BattleRole(snapshot.getPlayerId(), roleInfo));
			// 佣兵
//			List<RoleInfo.Builder> roleInfos = SnapShotManager.getInstance().getFightMercenaryInfo(snapshot.getPlayerId());
//			if (roleInfos == null || roleInfos.size() <= 0) {
//				return battleRoles;
//			}
//			for (RoleInfo.Builder _roleInfo : roleInfos) {
//				battleRoles.add(new BattleRole(_roleInfo));
//			}
			
			List<Integer> roleIdList = new ArrayList<Integer>(snapshot.getFightingRoleIdList());
			// 佣兵
			if (roleIdList != null && roleIdList.size() > 0) {
				for (Integer itemId : roleIdList) {
					RoleInfo.Builder _roleInfo = getActiviteRoleInfo(snapshot, itemId);
					if (_roleInfo != null) {
						battleRoles.add(new BattleRole(_roleInfo));
					}
				}
			}
		}
		return battleRoles;
	}
	
	/**
	 * 获取已激活佣兵信息
	 * 
	 * @param snapshot
	 * @param itemId
	 * @return
	 */
	public static RoleInfo.Builder getActiviteRoleInfo(PlayerSnapshotInfo snapshot, int itemId) {
		try {
			List<RoleInfo> shotList = new ArrayList<RoleInfo>(snapshot.getMercenaryInfoList());
			if (shotList == null || shotList.size() <= 0) {
				return null;
			}
			for (RoleInfo roleInfo : shotList) {
				if (itemId == roleInfo.getItemId() && roleInfo.getActiviteState() == RoleActiviteState.IS_ACTIVITE_VALUE) {
					return roleInfo.toBuilder();
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}
}
