package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

/**
 * GVG城池争夺日志
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "gvg_log")
public class GvgLogEntity extends DBEntity {

	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	/**
	 * 攻击城池公会名称
	 */
	@Column(name = "attackerName", nullable = false)
	private String attackerName;
	
	/**
	 * 防御者公会名称
	 */
	@Column(name = "defenderName")
	private String defenderName;
	
	/**
	 * 是否是反攻战
	 */
	@Column(name = "isFightback")
	private boolean isFightback;
	
	/**
	 * 争夺结果
	 */
	@Column(name = "result", nullable = false)
	private int result;
	
	/**
	 * 争夺的城池ID
	 */
	@Column(name = "cityId", nullable = false)
	private int cityId;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	public GvgLogEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	/**
	 * 数据初始化---记录
	 * 
	 * @param attackerName
	 * @param defenderName
	 * @param result
	 * @param cityId
	 * @return
	 */
	public static GvgLogEntity createEntity(String attackerName, String defenderName, boolean isFightback, int result, int cityId) {
		GvgLogEntity entity = new GvgLogEntity();
		entity.attackerName = attackerName;
		entity.defenderName = defenderName;
		entity.isFightback = isFightback;
		entity.result = result;
		entity.cityId = cityId;
		if (DBManager.getInstance().create(entity)) {
			return entity;
		}
		return null;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAttackerName() {
		return attackerName;
	}

	public void setAttackerName(String attackerName) {
		this.attackerName = attackerName;
	}

	public String getDefenderName() {
		return defenderName;
	}

	public void setDefenderName(String defenderName) {
		this.defenderName= defenderName;
	}

	public boolean isFightback() {
		return isFightback;
	}

	public void setFightback(boolean isFightback) {
		this.isFightback = isFightback;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
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
