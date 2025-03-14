package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

/**
 * 好友实体存储
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "first")
public class FirstEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	@Column(name = "cfgMapStr",columnDefinition = "varchar(4096) not null")
	private String cfgMapStr ;
	
	@Transient
	private ConcurrentHashMap<Integer, Integer> cfgMap ;
		
	@Column(name = "createTime")
	private Date  createTime ;
	
	@Column(name = "updateTime")
	private Date  updateTime ;
	
	@Column(name = "invalid")
	private boolean invalid ;
	
	public FirstEntity() {
		this.cfgMap = new ConcurrentHashMap<Integer, Integer>();
		convertCfgMapToStr();
	}
	
	public int getId() {
		return id;
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
	 * cfgMap转化为 String
	 */
	public void convertCfgMapToStr() {
		this.cfgMapStr = GsonUtil.getJsonInstance().toJson(cfgMap);
	}
	
	public void setCfgId(int cfgId,int playerId){
		if (!cfgMap.containsKey(cfgId)) {
			cfgMap.put(cfgId,playerId);
		}
		convertCfgMapToStr();
	}
	
	public boolean isAleadyDone(int cfgid) {
		return (cfgMap.containsKey(cfgid));
	}
	
	public String getCfgMapStr() {
		return cfgMapStr;
	}
	
	public Set<Integer> getAllCfgId(){
		return new HashSet<Integer>(cfgMap.keySet());
	}
	
	public Map<Integer,Integer> getCfgMap(){
		return cfgMap;
	}

	public void setPointMapStr(String cfgMapStr) {
		this.cfgMapStr = cfgMapStr;
	}
	
	/**
	 * 数据存储转化成内存操作对象
	 */
	public void convert() {		
		this.cfgMap = new ConcurrentHashMap<>(GsonUtil.getJsonInstance().fromJson(this.cfgMapStr,new TypeToken<HashMap<Integer, Integer>>() {}.getType()));
		if (this.cfgMap == null) {
			this.cfgMap = new ConcurrentHashMap<>();
		}
	}
		
	public int getCfgSize() {
		return this.cfgMap.size();
	}

}
