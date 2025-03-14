package com.guaji.game.entity;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

@Entity
@Table(name = "server_timelimit")
@SuppressWarnings("serial")
public class ServerTimeLimitEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "stageId", unique = true)
	private int stageId ;
	
	@Transient
	private Map<Long, Map<Integer, Integer>> buyMap ;
	
	@Column(name = "buyMapStr")
	private String buyMapStr = null;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;
	
	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	public ServerTimeLimitEntity(){
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.buyMap = new ConcurrentHashMap<>();
		this.buyMapStr = GsonUtil.getJsonInstance().toJson(this.buyMap);
	}
	
	public void addTodayBuyTime(int cfgId, int count) {
		long curTime = GuaJiTime.getAM0Date().getTime();
		Map<Integer, Integer> buyTimesMap = buyMap.get(curTime);
		if(buyTimesMap == null) {
			buyTimesMap = new ConcurrentHashMap<>(); 
			buyMap.put(curTime, buyTimesMap);
		}
		
		if(!buyTimesMap.containsKey(cfgId)) {
			buyTimesMap.put(cfgId, count);
		}else{
			buyTimesMap.put(cfgId, buyTimesMap.get(cfgId) + count);
		}
		
		this.buyMapStr = GsonUtil.getJsonInstance().toJson(this.buyMap);
	}
	
	public void convertData() {
		this.buyMap = GsonUtil.getJsonInstance().fromJson(this.buyMapStr,new TypeToken<ConcurrentHashMap<Long, ConcurrentHashMap<Integer, Integer>>>() {}.getType());
	}
	
	public int getTodayBuyTimes(int cfgId) {
		long curTime = GuaJiTime.getAM0Date().getTime();
		Map<Integer, Integer> buyTimesMap = buyMap.get(curTime);
		if(buyTimesMap == null) {
			return 0;
		}
		
		if(buyTimesMap.containsKey(cfgId)) {
			return  buyTimesMap.get(cfgId);
		}
		
		return 0;
	}
	
	public int getTotalBuyTimes(int cfgId) {
		int count = 0;
		for(Map<Integer,Integer> buyTimesMap : buyMap.values()) {
			if(buyTimesMap.containsKey(cfgId)) {
				count += buyTimesMap.get(cfgId);
			}
		}
		return count;
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

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

}
