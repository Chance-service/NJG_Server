package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.db.DBManager;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：May 5, 2019 4:41:31 PM 类说明
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "gvg_history_rank")
public class GvgHistoryRankEntity extends DBEntity {

	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private long id = 0;

	@Column(name = "rank")
	private int rank = 0;

	@Column(name = "allianceName")
	private String allianceName;

	@Column(name = "masterName")
	private String masterName;

	@Column(name = "allianceId")
	private int allianceId;

	@Column(name = "level")
	private int level;

	@Column(name = "holdCityInfo")
	private String holdCityInfo;

	@Column(name = "Score")
	private int Score;

	@Column(name = "createTime")
	protected Date createTime;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getAllianceName() {
		return allianceName;
	}

	public void setAllianceName(String allianceName) {
		this.allianceName = allianceName;
	}

	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public String getHoldCityInfo() {
		return holdCityInfo;
	}

	public void setHoldCityInfo(String holdCityInfo) {
		this.holdCityInfo = holdCityInfo;
	}

	public int getScore() {
		return Score;
	}

	public void setScore(int score) {
		Score = score;
	}

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @param allianceName 帮会名称
	 * @param masterName   帮主名称
	 * @param allianceId   帮会id
	 * @param citysInfo    所占城池数量信息
	 * @param rank         排名
	 * @param score        分值
	 * @return
	 */
	public static GvgHistoryRankEntity createEntity(String allianceName, String masterName, int allianceId,
			String citysInfo, int rank, int score, int level) {
		GvgHistoryRankEntity entity = new GvgHistoryRankEntity();
		entity.rank = rank;
		entity.allianceId = allianceId;
		entity.allianceName = allianceName;
		entity.Score = score;
		entity.holdCityInfo = citysInfo;
		entity.masterName = masterName;
		entity.level=level;
		if (DBManager.getInstance().create(entity)) {
			return entity;
		}
		return null;
	}

}
