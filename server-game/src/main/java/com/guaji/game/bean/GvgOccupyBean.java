package com.guaji.game.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * GVG佣兵派遣
 */
public class GvgOccupyBean {

	/**
	 * 账户ID
	 */
	private int playerId;
	
	/**
	 * 佣兵ID
	 */
	private List<Integer> roleIds = new ArrayList<Integer>();
	
	public GvgOccupyBean() {

	}

	/**
	 * 添加佣兵记录
	 * 
	 * @param roleId
	 */
	public void addRoleId(Integer roleId) {
		roleIds.add(roleId);
	}
	
	/**
	 * 添加佣兵记录
	 * 
	 * @param roleIds
	 */
	public void addAllRoleId(List<Integer> roleIds) {
		this.roleIds.clear();
		this.roleIds.addAll(roleIds);
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public List<Integer> getRoleIds() {
		return roleIds;
	}

	public void setRoleMeg(List<Integer> roleIds) {
		this.roleIds = roleIds;
	}
	
	@Override
	public String toString() {
		return " playerId:" + playerId + " roleIds:" + roleIds;
	}
}
