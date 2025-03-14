package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;

/**
 * facebook分享事件
 */
@Entity
@Table(name = "player_facebook_share")
@SuppressWarnings("serial")
public class FacebookShareEntity extends DBEntity {
	@Id
	@Column(name = "id")
	private int id = 0;

	@Column(name = "puid")
	private String puid = "";
	
	@Column(name = "serverId")
	private int serverId;

	/**
	 * 创建时间
	 */
	@Column(name = "createTime")
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	@Column(name = "updateTime")
	protected Date updateTime;
	
	/**
	 * 是否有效
	 */
	@Column(name = "invalid")
	protected boolean invalid;

	/**
	 * facebook分享次数
	 */
	@Column(name = "count")
	private int count = 0;

	public FacebookShareEntity() {
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
	

	
}
