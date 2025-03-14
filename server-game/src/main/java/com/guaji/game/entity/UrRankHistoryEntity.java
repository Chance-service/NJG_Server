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

@Entity
@Table(name = "ur_rank_history")
@SuppressWarnings("serial")
public class UrRankHistoryEntity extends DBEntity {

	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private long id= 0;

	@Column(name = "stageId", nullable = false)
	protected int stageId = 0;

	@Column(name = "ranklist")
	protected String ranklist = "";

	@Column(name = "awardlist")
	protected String awardlist = "";

	@Column(name = "createTime", nullable = false)
	protected Date createTime = GuaJiTime.getCalendar().getTime();

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	
	public UrRankHistoryEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();

	}
	
	

	public UrRankHistoryEntity(int stageId, String ranklist, String awardlist) {
		super();
		this.stageId = stageId;
		this.ranklist = ranklist;
		this.awardlist = awardlist;
	}



	public int getStageId() {
		return stageId;
	}




	public void setStageId(int stageId) {
		this.stageId = stageId;
	}




	public String getRanklist() {
		return ranklist;
	}




	public void setRanklist(String ranklist) {
		this.ranklist = ranklist;
	}




	public String getAwardlist() {
		return awardlist;
	}




	public void setAwardlist(String awardlist) {
		this.awardlist = awardlist;
	}




	@Override
	public Date getCreateTime() {
		return this.createTime;
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

	
}
