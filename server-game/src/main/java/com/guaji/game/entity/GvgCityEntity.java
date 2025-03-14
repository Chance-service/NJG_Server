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
import com.guaji.game.bean.GvgOccupyBean;
import com.guaji.game.util.GsonUtil;

/**
 * GVG城池
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "gvg_city")
public class GvgCityEntity extends DBEntity {

	/**
	 * 城池ID
	 */
	@Id
	@Column(name = "cityId", nullable = false)
	private int cityId;

	/**
	 * 城池持有公会ID
	 */
	@Column(name = "holderId", nullable = false)
	private int holderId;

	/**
	 * 对城池宣战的公会ID
	 */
	@Column(name = "marauderId", nullable = false)
	private int marauderId;

	/**
	 * 攻击该城池公会ID
	 */
	@Column(name = "attackerIds", nullable = false)
	private String attackerIds;

	/**
	 * 防御者ID集
	 */
	@Column(name = "defenderIds", nullable = false)
	private String defenderIds;

	/**
	 * 是否反攻
	 */
	@Column(name = "isFightback", nullable = false)
	private boolean isFightback = false;

	/**
	 * 反攻结束时间点
	 */
	@Column(name = "fightbackTime", nullable = false)
	private long fightbackTime;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	@Transient
	private List<GvgOccupyBean> attackerList = new ArrayList<GvgOccupyBean>();

	@Transient
	private List<GvgOccupyBean> defenderList = new ArrayList<GvgOccupyBean>();

	public GvgCityEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}

	/**
	 * 数据初始化
	 */
	public void init() {
		if (attackerIds != null) {
			attackerList = GsonUtil.getJsonInstance().fromJson(attackerIds, new TypeToken<ArrayList<GvgOccupyBean>>() {}.getType());
		}

		if (defenderIds != null) {
			defenderList = GsonUtil.getJsonInstance().fromJson(defenderIds, new TypeToken<ArrayList<GvgOccupyBean>>() {}.getType());
		}
	}

	/**
	 * 数据初始化---记录
	 * 
	 * @param cityId
	 * @param holderId
	 * @param marauderId
	 * @return
	 */
	public static GvgCityEntity createEntity(int cityId, int holderId, int marauderId) {
		GvgCityEntity entity = new GvgCityEntity();
		entity.cityId = cityId;
		entity.holderId = holderId;
		entity.marauderId = marauderId;
		entity.attackerIds = GsonUtil.getJsonInstance().toJson(entity.attackerList);
		entity.defenderIds = GsonUtil.getJsonInstance().toJson(entity.defenderList);

		if (DBManager.getInstance().create(entity)) {
			return entity;
		}
		return null;
	}

	/**
	 * 对城池宣战的公会ID更新
	 * 
	 * @param marauderId
	 */
	public void updateMarauderId(int marauderId) {
		this.marauderId = marauderId;
		this.notifyUpdate();
	}

	/**
	 * 添加攻击佣兵
	 * 
	 * @param occupyBean
	 */
	public void addAttacker(GvgOccupyBean occupyBean) {
		attackerList.add(occupyBean);
		this.notifyUpdate();
	}

	/**
	 * 移除攻击佣兵
	 */
	public void removeAttacker() {
		this.attackerList.remove(0);
		this.notifyUpdate();
	}

	/**
	 * 清理攻击佣兵
	 */
	public void clearAttacker() {
		attackerList.clear();
		this.notifyUpdate();
	}

	/**
	 * 添加防御佣兵
	 * 
	 * @param occupyBean
	 */
	public void addDefender(GvgOccupyBean occupyBean) {
		defenderList.add(occupyBean);
		this.notifyUpdate();
	}

	/**
	 * 清理防御佣兵
	 */
	public void clearDefender() {
		defenderList.clear();
		this.notifyUpdate();
	}

	/**
	 * 移除防御佣兵
	 */
	public void removeDefender() {
		this.defenderList.remove(0);
		this.notifyUpdate();
	}

	/**
	 * 更新反攻宣战数据
	 */
	public void updateIsFightBack() {
		this.isFightback = true;
		this.notifyUpdate();
	}

	public List<GvgOccupyBean> getAttackerList() {
		return attackerList;
	}

	public void setAttackerList(List<GvgOccupyBean> attackerList) {
		this.attackerList = attackerList;
	}

	public List<GvgOccupyBean> getDefenderList() {
		return defenderList;
	}

	public void setDefenderList(List<GvgOccupyBean> defenderList) {
		this.defenderList = defenderList;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getHolderId() {
		return holderId;
	}

	public void setHolderId(int holderId) {
		this.holderId = holderId;
	}

	public int getMarauderId() {
		return marauderId;
	}

	public void setMarauderId(int marauderId) {
		this.marauderId = marauderId;
	}

	public String getAttackerIds() {
		return attackerIds;
	}

	public void setAttackerIds(String attackerIds) {
		this.attackerIds = attackerIds;
	}

	public String getDefenderIds() {
		return defenderIds;
	}

	public void setDefenderIds(String defenderIds) {
		this.defenderIds = defenderIds;
	}

	public boolean isFightback() {
		return isFightback;
	}

	public void setFightback(boolean isFightback) {
		this.isFightback = isFightback;
	}

	public long getFightbackTime() {
		return fightbackTime;
	}

	public void setFightbackTime(long fightbackTime) {
		this.fightbackTime = fightbackTime;
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

	@Override
	public void notifyUpdate() {
		this.attackerIds = GsonUtil.getJsonInstance().toJson(attackerList);
		this.defenderIds = GsonUtil.getJsonInstance().toJson(defenderList);
		super.notifyUpdate();
	}

}
