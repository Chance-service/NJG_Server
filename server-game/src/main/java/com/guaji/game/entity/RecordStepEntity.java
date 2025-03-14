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

/**
 * 記錄登入步驟
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "record_step")
public class RecordStepEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id = 0;
	/**
	 * 裝置
	 */
	@Column(name = "device", nullable = false)
	private String device = "";
	/**
	 * 平台
	 */
	@Column(name = "platform",nullable = false)
	private String platform = "" ;
	/**
	 * step 紀錄位置
	 */
	@Column(name = "step", nullable = false)
	private int step = 0;
	/**
	 * 寫入本地DB更新時間
	 */
	@Column(name = "updateTime")
	private Date  updateTime ;
	
	@Column(name = "createTime")
	private Date  createTime ;
	
	@Column(name = "invalid")
	private boolean invalid ;
	
	public RecordStepEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public RecordStepEntity(String device,String platform,int step) {
		this.device = device;
		this.platform = platform;
		this.step = step;
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}


	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
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
