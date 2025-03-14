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

@SuppressWarnings("serial")
@Entity
@Table(name = "profrank")
public class ProfRankEntity extends DBEntity implements Comparable<ProfRankEntity>{
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	@Column(name = "rank")
	private int rank;
	
	@Column(name = "playerId")
	private int playerId;
	
	@Column(name = "fightvalue")
	private int fightValue;
	
	@Column(name = "type")
	private int type;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime = null;
	
	@Column(name = "invalid")
	private boolean invalid;
	
	public ProfRankEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFightValue() {
		return fightValue;
	}

	public void setFightValue(int fight) {
		this.fightValue = fight;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public int compareTo(ProfRankEntity rank) {
		
		return fightValue - rank.getFightValue() != 0 ? fightValue-rank.getFightValue() : playerId - rank.getPlayerId();
	}
	
	public boolean isInvalid() {
		return invalid;
	}
	@Override
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	@Override
	public Date getCreateTime() {
		return createTime;
	}

	@Override
	public Date getUpdateTime() {
		return updateTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
