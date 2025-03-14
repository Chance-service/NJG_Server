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
@Table(name = "shoot_activity")
@SuppressWarnings("serial")
public class ShootActivityEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;

	/**
	 * 上次刷新时间
	 */
	@Column(name = "shootRefreshTime")
	private int shootRefreshTime = 0;
	/**
	 * 刷新期数（1，2）
	 */
	@Column(name = "shootState")
	private int shootState = 0;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	public ShootActivityEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getShootRefreshTime() {
		return shootRefreshTime;
	}

	public void setShootRefreshTime(int shootRefreshTime) {
		this.shootRefreshTime = shootRefreshTime;
	}

	public int getShootState() {
		return shootState;
	}

	public void setShootState(int shootState) {
		this.shootState = shootState;
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
