package com.guaji.game.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.util.GsonUtil;

/**
 * 角色基础数据
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "skin")

public class RoleSkinEntity extends DBEntity {
	/**
	 * 玩家id;
	 */
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "assigned")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "playerId", nullable = false)
	private int playerId;
	/**
	 * 皮膚信息;
	 */
	@Column(name = "skinStr", nullable = false, columnDefinition ="varchar(4096)")
	private String skinStr;
		
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	@Transient
	private Set<Integer> skinSet;
	
	public RoleSkinEntity() {
		
	}
	
	public RoleSkinEntity(int playerId) {
		this.playerId = playerId;
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.updateTime = GuaJiTime.getCalendar().getTime();
		this.invalid = false;
		this.skinSet = new HashSet<Integer>();
		this.skinStr = GsonUtil.getJsonInstance().toJson(skinSet);
	}
	
	/**
	 * 字符串存储转换为内存数据
	 */
	public void convert() {
		
		if (getSkinStr() != null) {
			this.skinSet = GsonUtil.getJsonInstance().fromJson(getSkinStr(),
					new TypeToken<HashSet<Integer>>() {}.getType());
		}
		if (this.skinSet == null) {
			this.skinSet = new HashSet<Integer>();
		}
	}
	
	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert() {
		setSkinStr(GsonUtil.getJsonInstance().toJson(skinSet));
		return;
	}

	@Override
	public Date getCreateTime() {
		// TODO Auto-generated method stub
		return createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getUpdateTime() {
		// TODO Auto-generated method stub
		return updateTime;
	}

	@Override
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInvalid() {
		// TODO Auto-generated method stub
		return invalid;
	}

	@Override
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
		// TODO Auto-generated method stub
		
	}
	
	public String getSkinStr() {
		return this.skinStr;
	}
	
	public void setSkinStr(String aStr) {
		this.skinStr = aStr;
	}
	
	public Set<Integer> getSkinSet() {
		return this.skinSet;
	}
	
	public boolean haveSkin(int skinId) {
		return this.skinSet.contains(skinId);
	}
	
	public void setSkinSet(int skinId) {
		if (!this.skinSet.contains(skinId)) {
			skinSet.add(skinId);
			reConvert();
		}
	}
	

}
