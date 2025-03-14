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
 * 财富俱乐部数据
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "wealth_club")
public class WealthClubEntity extends DBEntity {

	@Id
	@Column(name = "stageId", unique = true)
	/**
	 * 活动期数
	 */
	private int stageId = 0;

	/**
	 * 财富俱乐部数据
	 */
	@Column(name = "wealthData", nullable = true)
	private String wealthData = "";

	/**
	 * 财富俱乐部数据集合
	 */
	@Transient
	private Map<String, WealthData> wealthMap = new HashMap<String, WealthData>();

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	public WealthClubEntity() {

		this.createTime = GuaJiTime.getCalendar().getTime();
	}

	public static WealthClubEntity valueOf(int stageId) {

		WealthClubEntity entity = new WealthClubEntity();
		entity.stageId = stageId;
		return entity;
	}

	public void init() {

		try {
			if (null != wealthData && !wealthData.equals("")) {
				wealthMap = GsonUtil.getJsonInstance().fromJson(wealthData, new TypeToken<HashMap<String, WealthData>>() {}.getType());
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

	public String getWealthData() {

		this.wealthData = null;
		this.wealthData = GsonUtil.getJsonInstance().toJson(this.wealthMap);
		return this.wealthData;
	}

	public void setWealthData(String wealthData) {
		this.wealthData = wealthData;
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

	public Map<String, WealthData> getWealthMap() {
		return wealthMap;
	}

	/**
	 * 取每日数据财富俱乐部数据
	 * 
	 * @param dateFormat
	 * @return
	 */
	public WealthData getWealthDataByKey(String dateFormat) {

		if (this.wealthMap.containsKey(dateFormat)) {
			return this.wealthMap.get(dateFormat);
		} else {
			WealthData data = new WealthData();
			this.wealthMap.put(dateFormat, data);
			return data;
		}
	}
	
	/**
	 * add by callan 格式转换并同步
	 * 
	 */
	public void updateWealthData()
	{
		this.wealthData = GsonUtil.getJsonInstance().toJson(this.wealthMap);
		return;
	}
}


