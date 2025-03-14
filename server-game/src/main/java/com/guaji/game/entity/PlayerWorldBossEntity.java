package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

/**
 * 角色世界boss
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "player_world_boss")
public class PlayerWorldBossEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id = 0;

	@Column(name = "worldBossId")
	private int worldBossId = 0;

	@Column(name = "playerId")
	private int playerId = 0;

	@Column(name = "allianceId")
	private int allianceId = 0;

	@Column(name = "attackTimes")
	private int attackTimes = 0;
	/**
	 * 累積傷害
	 */
	@Column(name = "harm")
	private long harm = 0;
	/**
	 * 最新攻擊傷害
	 */
	@Column(name = "hurt")
	private int hurt = 0;
	/**
	 * 是否攻擊 1.是  0.否
	 */
	@Column(name = "Attack")
	private int Attack = 0;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = GuaJiTime.getCalendar().getTime();

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
		
	public boolean getAttack() {
		return Attack == 1;
	}
	
	public void setAttack(int value) {
		this.Attack = value;
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

	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
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

	public int getWorldBossId() {
		return worldBossId;
	}

	public void setWorldBossId(int worldBossId) {
		this.worldBossId = worldBossId;
	}

	public long getHarm() {
		return harm;
	}

	public void setHarm(long harm) {
		this.harm = harm;
	}
	
	public int getHurt() {
		return hurt;
	}

	public void setHurt(int ahurt) {
		this.hurt = ahurt;
		if (this.hurt != 0) {
			setAttack(1);
		}
	}

	public void addHarm(long harm) {
		this.harm += harm;
	}

	public int getAttackTimes() {
		return attackTimes;
	}

	public void setAttackTimes(int attackTimes) {
		this.attackTimes = attackTimes;
	}

	public void incAttackTimes() {
		this.attackTimes++;
	}

}
