package com.guaji.game.soul;

import java.util.LinkedList;
import java.util.List;

/**
 * 佣兵碎片结构体
 * @author Melvin.Mao
 * @date Jun 15, 2017 10:57:43 AM
 */
public class RoleSoulInfo {
	/**
	 * 种类
	 */
	int type;
	/**
	 * id
	 */
	int roleId;
	/**
	 * 数量
	 */
	int count;

	public RoleSoulInfo() {
		super();
	}

	public RoleSoulInfo(int type, int roleId, int count) {
		super();
		this.type = type;
		this.roleId = roleId;
		this.count = count;
	}

	public RoleSoulInfo(String info) {
		initByString(info);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public RoleSoulInfo clone() {
		RoleSoulInfo ret = new RoleSoulInfo(type, roleId, count);
		return ret;
	}

	@Override
	public String toString() {
		return String.format("%d_%d_%d", type, roleId, count);
	}

	public static RoleSoulInfo valueOf(int type, int roleId, int count) {
		return new RoleSoulInfo(type, roleId, count);
	}

	public boolean initByString(String info) {
		if (info != null && info.length() > 0 && !info.equals("0") && !info.equals("none")) {
			String[] roleSouls = info.split("_");
			if (roleSouls.length < 3) {
				return false;
			}
			type = Integer.parseInt(roleSouls[0]);//商店里 货币类型
			roleId = Integer.parseInt(roleSouls[1]);//商店里为价格
			count = Integer.parseInt(roleSouls[2]);//商店里为折扣
			return true;
		}
		return false;
	}

	public static RoleSoulInfo valueOf(String info) {
		RoleSoulInfo roleSoulInfo = new RoleSoulInfo();
		if (roleSoulInfo.initByString(info)) {
			return roleSoulInfo;
		}
		return null;
	}

	public static List<RoleSoulInfo> valueListOf(String info) {
		List<RoleSoulInfo> roleSouls = new LinkedList<>();
		String[] ss = info.split(",");
		for (String s : ss) {
			RoleSoulInfo roleSoulInfo = valueOf(s);
			if (roleSoulInfo != null) {
				roleSouls.add(roleSoulInfo);
			}
		}
		return roleSouls;
	}

}
