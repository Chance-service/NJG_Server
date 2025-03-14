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
 * 公会元气增加排行
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "vitality_rank")
public class VitalityRankEntity extends DBEntity {
	
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id = 0;

	/**
	 * 排行名次
	 */
	@Column(name = "rank", nullable = false)
	private int rank;

	/**
	 * 公会ID
	 */
	@Column(name = "allianceId", nullable = false)
	private int allianceId;
	
	/**
	 * 公会元气值
	 */
	@Column(name = "vitality", nullable = false)
	private int vitality;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	public VitalityRankEntity() {

		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	/**
	 * 数据初始化---记录
	 * 
	 * @param rank
	 * @param allianceId
	 * @param vitality
	 * @return
	 */
	public static VitalityRankEntity createEntity(int rank, int allianceId, int vitality) {
		
		VitalityRankEntity entity = new VitalityRankEntity();
		entity.rank = rank;
		entity.allianceId = allianceId;
		entity.vitality = vitality;
		
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

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public int getVitality() {
		return vitality;
	}

	public void setVitality(int vitality) {
		this.vitality = vitality;
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
