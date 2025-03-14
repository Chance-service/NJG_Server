package com.guaji.game.entity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;

/**
 * GVG开启时间
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "gvg_time")
public class GvgTimeEntity extends DBEntity {

	@Id
	@Column(name = "gvgId", nullable = false)
	private final int gvgId = 1;

	/**
	 * 是否第一次开启
	 */
	@Column(name = "isFirst", nullable = false)
	private boolean isFirst = true;

	/**
	 * 刷新时间
	 */
	@Column(name = "refreshTime", nullable = false)
	private long refreshTime;

	/**
	 * 推送GVG阶段状态
	 */
	@Column(name = "pushState", nullable = false)
	private int pushState;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	@Column(name = "resettime")
	protected long resettime;

	public GvgTimeEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.refreshTime = GuaJiTime.getNextAM0Date();
	}

	/**
	 * 数据初始化---记录
	 * 
	 * @return
	 */
	public static GvgTimeEntity createEntity() {
		GvgTimeEntity entity = new GvgTimeEntity();
		if (DBManager.getInstance().create(entity)) {
			return entity;
		}
		return null;
	}

	/**
	 * 更新第一次开启记录
	 */
	public void updateGvgTime(boolean isUpdate) {
		this.isFirst = false;
		if (isUpdate) {
			this.refreshTime = GuaJiTime.getNextAM0Date();
		} else {

			int days = GuaJiTime.getCalendar().getActualMaximum(Calendar.DATE);
			int curDay = GuaJiTime.getCalendar().get(Calendar.DAY_OF_MONTH);
			if (days == 30) {
				this.refreshTime = GuaJiTime.getDayAM0Date(2);
			} else {
				if (curDay <= 15) {
					this.refreshTime = GuaJiTime.getDayAM0Date(2);
				} else {
					this.refreshTime = GuaJiTime.getDayAM0Date(3);
				}
			}

		}
		this.notifyUpdate();
	}

	/**
	 * 更新下次重置时间
	 */
	public void updateResetTime(long resetTime) {

		this.resettime = resetTime;
		this.notifyUpdate();
	}

	/**
	 * 更新推送状态
	 */
	public void updatePushState(int pushState) {
		this.pushState = pushState;
		this.notifyUpdate();
	}

	public int getGvgId() {
		return gvgId;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public int getPushState() {
		return pushState;
	}

	public void setPushState(int pushState) {
		this.pushState = pushState;
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

	public long getResettime() {
		return resettime;
	}

	public void setResettime(long resettime) {
		this.resettime = resettime;
	}

}
