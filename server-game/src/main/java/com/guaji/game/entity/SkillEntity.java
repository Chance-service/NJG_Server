package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

/**
 * 技能实体对象
 */
@Entity
@Table(name = "skill")
@SuppressWarnings("serial")
public class SkillEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id = 0;
	
	@Column(name = "playerId")
	private int playerId = 0;
	
	@Column(name = "roleId")
	private int roleId = 0;
	
	@Column(name = "itemId")
	private int itemId = 0;
	
	@Column(name = "skillLevel")
	private int skillLevel = 0;
	
	@Column(name = "status")
	private int status = 0;
	
	/** 经验 */
	@Column(name = "exp")
	private int exp = 0;
	
	@Transient
	private int skillEnhanceLevel = 0;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;
	
	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	public SkillEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getSkillLevel() {
		return skillLevel + skillEnhanceLevel;
	}
	
	public int getRealSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(int skillLevel) {
		this.skillLevel = skillLevel;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getSkillEnhanceLevel() {
		return skillEnhanceLevel;
	}

	public void setSkillEnhanceLevel(int skillEnhanceLevel) {
		this.skillEnhanceLevel = skillEnhanceLevel;
	}
}
