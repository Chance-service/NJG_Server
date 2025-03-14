package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.guaji.game.util.GsonUtil;
@SuppressWarnings("serial")
@Entity
@Table(name = "player_activity")
public class ActivityEntity<T> extends DBEntity implements Comparable<ActivityEntity<T>> {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;

	@Column(name = "playerId", nullable = false)
	private int playerId;

	@Column(name = "activityId", nullable = false)
	private int activityId;

	@Column(name = "stageId", nullable = false)
	private int stageId;

	@Column(name = "statusStr", nullable = false)
	private String statusStr;

	@Transient
	private T activityStatus = null;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	public ActivityEntity(){
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public ActivityEntity(int playerId, int activityId, int stageId){
		this.playerId = playerId;
		this.activityId = activityId;
		this.stageId = stageId;
		this.createTime = GuaJiTime.getCalendar().getTime();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}

	/**
	 * 将statusStr信息填充到活动对应的status对象中
	 * 
	 * @param activityStatus
	 *            一个实现了ActivityStatus类的子类对象
	 * @return
	 */
	public T getActivityStatus(Class<T> clazz) {
		if (this.activityStatus == null) {
//			try {
			this.activityStatus = GsonUtil.getJsonInstance().fromJson(statusStr, clazz);
//			} catch (Exception e) {
//				try {
//					Object invokeTester = clazz.newInstance();
//					Method addMethod = clazz.getMethod("add", new Class[] { String.class });
//					addMethod.invoke(invokeTester, new Object[] {statusStr});
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//			}
		}
		return this.activityStatus;
	}

	public void setActivityStatus(T t) {
		this.activityStatus = t;
		this.statusStr = GsonUtil.getJsonInstance().toJson(t);
	}
	
	public void convertStatusToStr() {
		if(activityStatus != null) {
			this.statusStr = GsonUtil.getJsonInstance().toJson(activityStatus);
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
		if (activityStatus != null) {
			convertStatusToStr();
		}
		super.notifyUpdate(async);
	}

	@Override
	public int compareTo(ActivityEntity<T> o) {
		if (this.activityId == o.activityId && this.stageId == o.stageId) {
			return 0;
		} else if (this.activityId == o.activityId) {
			return this.stageId - o.getStageId();
		} else {
			return this.activityId - o.activityId;
		}
	}
}
