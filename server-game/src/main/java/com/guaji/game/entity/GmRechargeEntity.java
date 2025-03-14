package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

/**
 * 充值记录
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "gm_recharge")
public class GmRechargeEntity extends DBEntity {
	@Id
	@Column(name = "id", unique = true)
	private int id;
	
	@Column(name = "puid")
	private String  puid ;
	
	@Column(name = "serverId")
	private int  serverId ;
	
	@Column(name = "playerId")
	private int  playerId ;
	
	@Column(name = "goodsId")
	private int  goodsId ;
	
	@Column(name = "goodsCost")
	private float  goodsCost ;

	@Column(name = "addGold")
	private int  addGold ;
	
	@Column(name = "isFirstPay")
	private int  isFirstPay ;
	
	@Column(name = "createTime")
	private Date  createTime ;
	
	@Column(name = "updateTime")
	private Date  updateTime ;
	
	@Column(name = "invalid")
	private boolean invalid ;
	
	public GmRechargeEntity() {
		createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public float getGoodsCost() {
		return goodsCost;
	}

	public void setGoodsCost(float goodsCost) {
		this.goodsCost = goodsCost;
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
