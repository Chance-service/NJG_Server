package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

import com.guaji.game.util.GsonUtil;


/**
 * 玩家作弊檢查實體
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cheat_check")
public class CheatCheckEntity extends DBEntity {
	@Id
	@Column(name = "playerId", nullable = false)
	private int playerId = 0;
	/**
	 * 累計次數(越級勝利次數)
	 */
	@Column(name = "total", nullable = false)
	private int total = 0;
	/**
	 * 進攻方戰力
	 */
	@Column(name = "attackPower", nullable = false)
	private int attackPower = 0;
	/**
	 * 進攻陣容
	 */
	@Column(name = "attacking", nullable = false)
	private String attacking = "";
	/**
	 * 防禦方ID
	 */
	@Column(name = "defenId", nullable = false)
	private int defenId = 0;
	
	/**
	 * 防禦方戰力
	 */
	@Column(name = "defenPower", nullable = false)
	private int defenPower = 0;
	/**
	 * 防守陣容
	 */
	@Column(name = "defensive", nullable = false)
	private String defensive = "";
	/**
	 * 是否為怪物
	 */
	@Column(name = "isMonster", nullable = false)
	private boolean isMonster = false;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;
	
	@Transient
	private List<Integer> attackList;
	
	@Transient
	private List<Integer> defenList;
	
	public CheatCheckEntity(){
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public CheatCheckEntity(int playerId, int total,int power){
		this.playerId = playerId;
		this.total = total;
		this.attackPower = power;
		
		this.defenId = 0;
		this.defenPower = 0;
		this.attackList = new ArrayList<>();
		this.defenList = new ArrayList<>();
		this.attacking =  GsonUtil.getJsonInstance().toJson(attackList);
		this.defensive =  GsonUtil.getJsonInstance().toJson(defenList);
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getAttackPower() {
		return attackPower;
	}

	public void setAttackPower(int attackPower) {
		this.attackPower = attackPower;
	}

	public int getDefenId() {
		return defenId;
	}

	public void setDefenId(int defenId) {
		this.defenId = defenId;
	}

	public int getDefenPower() {
		return defenPower;
	}

	public void setDefenPower(int defenPower) {
		this.defenPower = defenPower;
	}

	public boolean isMonster() {
		return isMonster;
	}

	public void setMonster(boolean isMonster) {
		this.isMonster = isMonster;
	}


	public void setAttackList(List<Integer> attackList) {
		this.attackList = attackList;
		if (this.attackList != null) {
			this.attacking = GsonUtil.getJsonInstance().toJson(this.attackList);
		}
	}

	public void setDefenList(List<Integer> defenList) {
		this.defenList = defenList;
		if (this.defenList != null) {
			this.defensive = GsonUtil.getJsonInstance().toJson(this.defenList);
		}
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
}
