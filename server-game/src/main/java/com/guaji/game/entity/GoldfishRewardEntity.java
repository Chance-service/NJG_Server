package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

/**
 * 捞金鱼积分排行
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "goldfish_reward")
public class GoldfishRewardEntity extends DBEntity {

	@Id
	@Column(name = "stageId", unique = true)
	/**
	 * 活动期数
	 */
	private int stageId = 0;

	/**
	 * 是否发生过奖励了
	 */
	@Column(name = "isSendReward", nullable = false)
	private boolean isSendReward;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	
	public GoldfishRewardEntity() {

		this.createTime = GuaJiTime.getCalendar().getTime();
	}

	public static GoldfishRewardEntity valueOf(int stageId) {

		GoldfishRewardEntity entity = new GoldfishRewardEntity();
		entity.stageId = stageId;
		return entity;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public boolean isSendReward() {
		return isSendReward;
	}

	public void setSendReward(boolean isSendReward) {
		this.isSendReward = isSendReward;
	}

	@Override
	public Date getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public Date getUpdateTime() {
		return updateTime;
	}

	@Override
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public boolean isInvalid() {
		return invalid;
	}

	@Override
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

}
