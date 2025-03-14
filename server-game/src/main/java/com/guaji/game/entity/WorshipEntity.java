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

@Entity
@Table(name = "worship")
public class WorshipEntity extends DBEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6049074014545059773L;

	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	@Column(name = "playerId")
	private int playerId;
	
	@Column(name = "worshipStamp")
	private int worshipStamp;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = GuaJiTime.getCalendar().getTime();
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;

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

	public int getWorshipStamp() {
		return worshipStamp;
	}

	public void setWorshipStamp(int worshipStamp) {
		this.worshipStamp = worshipStamp;
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

	public boolean isTodayWoship() {
		return GuaJiTime.isSameDay(worshipStamp * 1000L, System.currentTimeMillis());
	}
}
