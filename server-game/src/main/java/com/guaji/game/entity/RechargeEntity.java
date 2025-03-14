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
 * 充值记录
 * 
 * @author xulinqs
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "recharge")
public class RechargeEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id = 0;
	
	@Column(name = "orderSerial", unique = true)
	private String orderSerial;

	@Column(name = "puid")
	private String puid;

	@Column(name = "serverId")
	private int serverId;

	@Column(name = "playerId")
	private int playerId;

	@Column(name = "goodsId")
	private int goodsId;

	@Column(name = "goodsCount")
	private float goodsCount;

	@Column(name = "goodsCost")
	private float goodsCost;

	@Column(name = "currency")
	private String currency;

	@Column(name = "addGold")
	private int addGold;

	@Column(name = "isFirstPay")
	private int isFirstPay;

	@Column(name = "level")
	private int level;

	@Column(name = "vipLevel")
	private int vipLevel;

	@Column(name = "device")
	private String device;

	@Column(name = "platform")
	private String platform;

	@Column(name = "isTest")
	private boolean isTest;

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
	
	@Transient
	private String deviceInfo;
	

	public RechargeEntity() {
		currency = "RMB";
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

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public float getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(float goodsCount) {
		this.goodsCount = goodsCount;
	}

	public float getGoodsCost() {
		return goodsCost;
	}

	public void setGoodsCost(float goodsCost) {
		this.goodsCost = goodsCost;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getAddGold() {
		return addGold;
	}

	public void setAddGold(int addGold) {
		this.addGold = addGold;
	}

	public int getIsFirstPay() {
		return isFirstPay;
	}

	public void setIsFirstPay(int isFirstPay) {
		this.isFirstPay = isFirstPay;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
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

	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isTest() {
		return isTest;
	}

	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}

	public int getRechageStatus() {
		return rechageStatus;
	}

	public void setRechageStatus(int rechageStatus) {
		this.rechageStatus = rechageStatus;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
}
