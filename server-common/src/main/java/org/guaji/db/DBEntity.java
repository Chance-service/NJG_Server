package org.guaji.db;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.guaji.os.GuaJiTime;

/**
 * db实体对象基类, 对应sql需要对invalid字段进行索引创建
 */
@SuppressWarnings("serial")
public abstract class DBEntity implements Serializable {
	/**
	 * 实体状态
	 */
	private AtomicLong entityState = new AtomicLong(0);
	
	/**
	 * 获取对象创建时间
	 * @return
	 */
	public abstract Date getCreateTime();

	/**
	 * 设置对象创建时间
	 * @param createTime
	 */
	public abstract void setCreateTime(Date createTime);

	/**
	 * 获取对象更新时间
	 * @return
	 */
	public abstract Date getUpdateTime();

	/**
	 * 设置对象更新时间
	 * @param updateTime
	 */
	public abstract void setUpdateTime(Date updateTime);

	/**
	 * 判断对象是否有效
	 * @return
	 */
	public abstract boolean isInvalid();

	/**
	 * 设置对象无效
	 * @param invalid
	 */
	public abstract void setInvalid(boolean invalid);
	
	/**
	 * 获取实体状态
	 */
	public AtomicLong getEntityState() {
		return entityState;
	}
	public DBEntity() {
		setCreateTime(GuaJiTime.getCalendar().getTime());
		setUpdateTime(GuaJiTime.getCalendar().getTime());
	}
	/**
	 * 通知创建到db
	 * 
	 * @return
	 */
	public boolean notifyCreate() {		
		setCreateTime(GuaJiTime.getCalendar().getTime());
		
		return DBManager.getInstance().create(this);
	}
	
	/**
	 * 同步删除对象
	 */
	public void delete() {
		delete(true);
	}
	
	/**
	 * 删除对象(同步 or 异步)
	 */
	public void delete(boolean async) {
		setInvalid(true);
		if (async) {
			notifyUpdate(true);
		} else {
			DBManager.getInstance().delete(this);
		}
	}

	/**
	 * 通知db异步更新
	 */
	public void notifyUpdate()  {
		notifyUpdate(true);
	}
	
	/**
	 * 通知db更新(同步 or 异步更新)
	 */
	public void notifyUpdate(boolean async) {
		setUpdateTime(GuaJiTime.getCalendar().getTime());
		
		if (async) {
			DBManager.getInstance().updateAsync(this);
		} else {
			DBManager.getInstance().update(this);
		}
	}
}
