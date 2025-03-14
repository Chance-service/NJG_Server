package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.log.Log;
import org.guaji.os.GuaJiTime;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.util.GsonUtil;

/**
 * 神器锻造数据
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "forging_equip")
public class ForgingEntity extends DBEntity {

	@Id
	@Column(name = "stageId", unique = true)
	/**
	 * 活动期数
	 */
	private int stageId = 0;

	/**
	 * 全服累计锻造次数
	 */
	@Column(name = "totalTimes", nullable = false)
	private int totalTimes = 0;

	/**
	 * 限量奖励已发放次数
	 */
	@Column(name = "prizeLimit", nullable = false)
	private String prizeLimit = "";

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	/**
	 * 限量奖励已发放次数集合
	 */
	@Transient
	private Map<String, Integer> limitMap = new HashMap<String, Integer>();

	public ForgingEntity() {

		this.createTime = GuaJiTime.getCalendar().getTime();
	}

	public static ForgingEntity valueOf(int stageId) {

		ForgingEntity entity = new ForgingEntity();
		entity.stageId = stageId;
		return entity;
	}

	public void init() {

		try {
			if (!prizeLimit.isEmpty()) {
				limitMap = GsonUtil.getJsonInstance().fromJson(this.prizeLimit, new TypeToken<HashMap<String, Integer>>() {
				}.getType());
			}
		} catch (Exception e) {
			Log.exceptionPrint(e);
		}
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(int totalTimes) {
		this.totalTimes = totalTimes;
	}

	public String getPrizeLimit() {

		prizeLimit = null;
		prizeLimit = GsonUtil.getJsonInstance().toJson(limitMap);
		return prizeLimit;
	}

	public void setPrizeLimit(String prizeLimit) {
		this.prizeLimit = prizeLimit;
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

	public Map<String, Integer> getLimitMap() {

		if (null == limitMap) {
			limitMap = new HashMap<String, Integer>();
		}
		return limitMap;
	}

}
