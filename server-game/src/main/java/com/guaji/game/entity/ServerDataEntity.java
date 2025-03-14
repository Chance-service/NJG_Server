package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "serverdata")
public class ServerDataEntity<T> extends DBEntity {
	@Id
	@Column(name = "id", nullable = false)
	private int id;

	@Column(name = "statusStr", nullable = false)
	private String statusStr;

	@Transient
	private T serverStatus = null;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;
	
	public ServerDataEntity(){
		createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public ServerDataEntity(int serverDataId){
		this.id = serverDataId;
		createTime = GuaJiTime.getCalendar().getTime();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 将statusStr信息填充到对应的serverStatus对象中
	 */
	public T getServerStatus(Class<T> clazz) {
		if (this.serverStatus == null) {
			this.serverStatus = GsonUtil.getJsonInstance().fromJson(statusStr, clazz);
		}
		return this.serverStatus;
	}

	public void setServerStatus(T t) {
		this.serverStatus = t;
		this.statusStr = GsonUtil.getJsonInstance().toJson(t);
	}

	public void convertStatusToStr() {
		if (serverStatus != null) {
			this.statusStr = GsonUtil.getJsonInstance().toJson(serverStatus);
		}
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
	
	/**
	 * 通知db更新(异步更新)
	 */
	@Override
	public void notifyUpdate(boolean async) {
		if (serverStatus != null) {
			convertStatusToStr();
		}
		super.notifyUpdate(async);
	}
}
