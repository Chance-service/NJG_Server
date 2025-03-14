package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

@SuppressWarnings("serial")
@Entity
@Table(name = "camp")
public class CampEntity extends DBEntity {
	// 阵营Id
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	// 阵营Id
	@Column(name = "campId", nullable = false)
	private int campId;
	
	// 期号Id
	@Column(name = "stageId", nullable = false)
	private int stageId = 0;
	
	// 阵营积分
	@Column(name = "totalBattleScore", nullable = false)
	private int totalBattleScore = 0;
	
	// 是否胜利
	@Column(name = "isWin", nullable = false)
	private int isWin = 0;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	// 阵营总战斗力
	@Transient
	private int totalFightValue;

	// 阵营成员
	@Transient
	private List<CampWarEntity> campWarEntities;

	public CampEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.campWarEntities = new ArrayList<CampWarEntity>();
	}

	public CampEntity(int campId, int stageId) {
		this.campId = campId;
		this.stageId = stageId;
		this.totalFightValue = 0;
		this.totalBattleScore = 0;
		this.campWarEntities = new ArrayList<CampWarEntity>();
		this.createTime = GuaJiTime.getCalendar().getTime();
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getCampId() {
		return campId;
	}

	public void setCampId(int campId) {
		this.campId = campId;
	}
	
	public int getTotalBattleScore() {
		return totalBattleScore;
	}

	public int getTotalFightValue() {
		return totalFightValue;
	}
	
	public List<CampWarEntity> getCampWarEntities() {
		return campWarEntities;
	}

	public synchronized void addCampWarEntity(CampWarEntity campWarEntity) {
		campWarEntities.add(campWarEntity);
		totalFightValue += campWarEntity.getFightValue();
	}

	public synchronized void addTotalBattleScore(int addScore) {
		this.totalBattleScore += addScore;
	}

	public int getCampPlayerNum() {
		return campWarEntities.size();
	}

	public int getIsWin() {
		return isWin;
	}

	public void setIsWin(int isWin) {
		this.isWin = isWin;
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