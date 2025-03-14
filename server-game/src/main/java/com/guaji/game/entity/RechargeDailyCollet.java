package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

/**
 * 充值记录
 * 
 * @author xulinqs
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "recharge_dailycollet")
public class RechargeDailyCollet extends DBEntity {
	@Id
	@Column(name = "orderSerial", unique = true)
	private String orderSerial;

	@Column(name = "puid")
	private String puid;

	@Column(name = "serverId")
	private int serverId;

	@Column(name = "playerId")
	private int playerId;

	@Column(name = "goodsCost")
	private float goodsCost;

	@Column(name = "platform")
	private String platform;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

	@Transient
	private String playerName;
	
	@Transient
	private int rechageStatus;
	

	public RechargeDailyCollet() {
		platform = "default";
		createTime = GuaJiTime.getCalendar().getTime();
	}

	public String getOrderSerial() {
		return orderSerial;
	}

	public void setOrderSerial(String orderSerial) {
		this.orderSerial = orderSerial;
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}


	public float getGoodsCost() {
		return goodsCost;
	}

	public void setGoodsCost(float goodsCost) {
		this.goodsCost = goodsCost;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
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

	public int getRechageStatus() {
		return rechageStatus;
	}

	public void setRechageStatus(int rechageStatus) {
		this.rechageStatus = rechageStatus;
	}
	
}
