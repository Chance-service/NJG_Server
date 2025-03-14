package com.guaji.game.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

/**
 * GVG公会数据记录
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "gvg_alliance")
public class GvgAllianceEntity extends DBEntity  {
	
	/**
	 * 公会ID
	 */
	@Id
	@Column(name = "allianceId", unique = true)
	private int allianceId;
	
	@Column(name = "allianceLevel", nullable = false)
	private int allianceLevel;
	
	/**
	 * 宣战次数
	 */
	@Column(name = "declareTimes", nullable = false)
	private int declareTimes;
	
	/**
	 * 元气增加量
	 */
	@Column(name = "addCount")
	private int addCount;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	public GvgAllianceEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	/**
	 * 数据初始化
	 * 
	 * @param allianceId
	 * @param addCount
	 * @return
	 */
	public static GvgAllianceEntity valueOf(int allianceId, int addCount) {
		GvgAllianceEntity entity = new GvgAllianceEntity();
		entity.allianceId = allianceId;
		entity.addCount = addCount;
		
		return entity;
	}
	
	/**
	 * 数据初始化---记录
	 * 
	 * @param allianceId
	 * @param addCount
	 * @return
	 */
	public static GvgAllianceEntity createEntity(int allianceId, int addCount, int allianceLevel) {
		try {
			List<GvgAllianceEntity> list = DBManager.getInstance().query("from GvgAllianceEntity where allianceId= ? and invalid = 0", allianceId);
			if(list!=null&&list.size()>0){
				return list.get(0);
			}
			GvgAllianceEntity entity = new GvgAllianceEntity();
			entity.allianceId = allianceId;
			entity.addCount = addCount;
			entity.allianceLevel = allianceLevel;
			if (DBManager.getInstance().create(entity)) {
				return entity;
			}
		} catch (Exception e) {
			// TODO: handle exception
			MyException.catchException(e);
		}
		return null;
	}

	/**
	 * 元气增加更新
	 * 
	 * @param addCount
	 */
	public void updateAddCount(int addCount) {
		this.addCount += addCount;
		this.notifyUpdate(false);
	}
	
	/**
	 * 宣战次数更新
	 * 
	 * @param addCount
	 */
	public void updateDeclareTimes() {
		this.declareTimes ++;
		this.notifyUpdate(false);
	}
	
	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public int getDeclareTimes() {
		return declareTimes;
	}

	public void setDeclareTimes(int declareTimes) {
		this.declareTimes = declareTimes;
	}

	public int getAddCount() {
		return addCount;
	}

	public void setAddCount(int addCount) {
		this.addCount = addCount;
	}
	
	public int getAllianceLevel() {
		return allianceLevel;
	}

	public void setAllianceLevel(int allianceLevel) {
		this.allianceLevel = allianceLevel;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + allianceId;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final GvgAllianceEntity other = (GvgAllianceEntity) obj;
		if (allianceId != other.allianceId){
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return this.allianceId + " " + this.addCount + " " + this.declareTimes;
	}
}
