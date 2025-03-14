package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.util.GsonUtil;

/**
 * GVG城池奖励
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "gvg_city_reward")
public class GvgCityRewardEntity extends DBEntity {

	/**
	 * 公会ID
	 */
	@Id
	@Column(name = "allianceId", nullable = false)
	private int allianceId;
	
	/**
	 * 占有城池ID
	 */
	@Column(name = "cityIds", nullable = false)
	private String cityIds;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	@Transient
	private List<Integer> cityIdList = new ArrayList<Integer>();
	
	public GvgCityRewardEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	/**
	 * 数据初始化
	 */ 
	public void init() {
		if (cityIds != null) {
			cityIdList = GsonUtil.getJsonInstance().fromJson(cityIds, new TypeToken<ArrayList<Integer>>() {}.getType());
		}
	}
	
	/**
	 * 数据初始化---记录
	 * 
	 * @param allianceId
	 * @param cityId
	 * @return
	 */
	public static GvgCityRewardEntity createEntity(int allianceId, int cityId) {
		GvgCityRewardEntity entity = new GvgCityRewardEntity();
		entity.allianceId = allianceId;
		entity.cityIdList.add(cityId);
		entity.cityIds = GsonUtil.getJsonInstance().toJson(entity.cityIdList);
		if (DBManager.getInstance().create(entity)) {
			return entity;
		}
		return null;
	}
	
	/**
	 * 占领城池ID更新
	 * 
	 * @param cityId
	 */
	public void updateCityId(int cityId) {
		cityIdList.add(cityId);
		cityIds = GsonUtil.getJsonInstance().toJson(cityIdList);
		this.notifyUpdate();
	}
	
	/**
	 * 占有城池ID
	 * 
	 * @return
	 */
	public List<Integer> getCityIdList() {
		return cityIdList;
	}

	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public String getCityIds() {
		return cityIds;
	}

	public void setCityIds(String cityIds) {
		this.cityIds = cityIds;
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
