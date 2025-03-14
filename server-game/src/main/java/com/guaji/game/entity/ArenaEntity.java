package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;


/**
 * 玩家竞技场实体对象
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "arena")
public class ArenaEntity extends DBEntity {
	@Id
	@Column(name = "playerId", nullable = false)
	private int playerId = 0;
	
	@Column(name = "rank", nullable = false)
	private int rank = 0;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;
	
	public ArenaEntity(){
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public ArenaEntity(int playerId, int rank){
		this.playerId = playerId;
		this.rank = rank;
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
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
