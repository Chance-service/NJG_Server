package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "equip_mutual")
public class MutualEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;
	
	
	// 當前相生裝備對應星數
	@Column(name = "StarStr",columnDefinition = "varchar(2048) not null")
	private String StarStr = "";

	// 當前开启的相生
	@Column(name = "mutualStr")
	private String mutualStr = "";
		
	@Column(name = "createTime")
	private Date  createTime ;
	
	@Column(name = "updateTime")
	private Date  updateTime ;
	
	@Column(name = "invalid")
	private boolean invalid ;

	// 当前开启的缘分ID
	@Transient
	private Set<Integer> openMutual;
	@Transient
	private Map<Integer, Integer> StarMap;
		
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
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
	
	public String getMutualStr() {
		return mutualStr;
	}

	public void setMutualStr(String mutualStr) {
		this.mutualStr = mutualStr;
	}
	
	public String getStarStr() {
		return StarStr;
	}

	public void setStarStr(String StarStr) {
		this.StarStr = StarStr;
	}

	/**
	 * 字符串存储转换为内存数据
	 */
	public void convert() {
		//羁绊
		if (getMutualStr() != null) {
			openMutual = GsonUtil.getJsonInstance().fromJson(getMutualStr(),
					new TypeToken<HashSet<Integer>>() {}.getType());
		}
		if (openMutual == null) {
			openMutual = new HashSet<Integer>();
		}
		
		if (this.StarStr != null && !"".equals(this.StarStr)) {
			this.StarMap = GsonUtil.getJsonInstance().fromJson(this.StarStr,
					new TypeToken<HashMap<Integer, Integer>>() {
					}.getType());
		}
		if (this.StarMap == null) {
			this.StarMap = new HashMap<Integer, Integer>();
		}
	}

	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert() {
		setMutualStr(GsonUtil.getJsonInstance().toJson(openMutual));
		return;
	}

	public Set<Integer> getOpenMutual() {
		return openMutual;
	}

	public void setOpenMutual(Set<Integer> openMutual) {
		this.openMutual = openMutual;
		setMutualStr(GsonUtil.getJsonInstance().toJson(openMutual));
	}
	
	public Map<Integer, Integer> getStarMap() {
		return StarMap;
	}

	public void addStarMap(int key, int value) {
		if (this.StarMap.containsKey(key)) {
			this.StarMap.replace(key, value);
		} else {
			this.StarMap.put(key, value);
		}
		this.StarStr = GsonUtil.getJsonInstance().toJson(this.StarMap);
	}
		
}
