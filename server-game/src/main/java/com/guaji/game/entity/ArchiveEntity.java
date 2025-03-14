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
@Table(name = "player_archive")
public class ArchiveEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	// 当前开启的缘分
	@Column(name = "fetterStr")
	private String fetterStr = "";
	
	// 当前开启的缘分進度
	@Column(name = "albumStr")
	private String albumStr = "";
	
	@Column(name = "createTime")
	private Date  createTime ;
	
	@Column(name = "updateTime")
	private Date  updateTime ;
	
	@Column(name = "invalid")
	private boolean invalid ;

	// 当前开启的缘分ID
	@Transient
	private Set<Integer> openFetters;
	
	@Transient
	private Map<Integer, Integer> albumMap;
		
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
	
	public String getFetterStr() {
		return fetterStr;
	}

	public void setFetterStr(String fetterStr) {
		this.fetterStr = fetterStr;
	}
	
	public String getalbumStr() {
		return albumStr;
	}

	public void setalbumStr(String albumStr) {
		this.albumStr = albumStr;
	}

	/**
	 * 字符串存储转换为内存数据
	 */
	public void convert() {
		//羁绊
		if (getFetterStr() != null) {
			openFetters = GsonUtil.getJsonInstance().fromJson(getFetterStr(),
					new TypeToken<HashSet<Integer>>() {}.getType());
		}
		if (openFetters == null) {
			openFetters = new HashSet<Integer>();
		}
		
		if (this.albumStr != null && !"".equals(this.albumStr)) {
			this.albumMap = GsonUtil.getJsonInstance().fromJson(this.albumStr,
					new TypeToken<HashMap<Integer, Integer>>() {
					}.getType());
		}
		if (this.albumMap == null) {
			this.albumMap = new HashMap<Integer, Integer>();
		}
	}

	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert() {
		setFetterStr(GsonUtil.getJsonInstance().toJson(openFetters));
		return;
	}

	public Set<Integer> getOpenFetters() {
		return openFetters;
	}

	public void setOpenFetters(Set<Integer> openFetters) {
		this.openFetters = openFetters;
	}
	
	public Map<Integer, Integer> getalbumMap() {
		return albumMap;
	}

	public void addalbumMap(int key, int value) {
		this.albumMap.put(key, value);
		this.albumStr = GsonUtil.getJsonInstance().toJson(this.albumMap);
	}
	
}
