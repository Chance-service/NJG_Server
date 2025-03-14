package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
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

@SuppressWarnings("serial")
@Entity
@Table(name = "campwar_auto")
public class CampWarAutoJoinEntity extends DBEntity{
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	@Column(name = "stageId", nullable = false)
	private int stageId = 0;
	
	@Column(name = "autoJoinPlayerIdsStr", nullable = false)
	private String autoJoinPlayerIdsStr = "{}";
	
	@Transient
	private ConcurrentHashMap<Integer, Integer> autoCampWarPlayerIds;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	public CampWarAutoJoinEntity(){
		this.autoCampWarPlayerIds = new ConcurrentHashMap<Integer, Integer>();
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public CampWarAutoJoinEntity(int stageId){
		this.stageId = stageId;
		this.autoCampWarPlayerIds = new ConcurrentHashMap<Integer, Integer>();
		this.createTime = GuaJiTime.getCalendar().getTime();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStageId() {
		return stageId;
	}

	public String getAutoJoinPlayerIdsStr() {
		return autoJoinPlayerIdsStr;
	}

	public void setAutoJoinPlayerIdsStr(String autoJoinPlayerIdsStr) {
		this.autoJoinPlayerIdsStr = autoJoinPlayerIdsStr;
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
	
	
	public void convertAutoJoinMap(){
		HashMap<Integer, Integer> tmpMap = GsonUtil.getJsonInstance().fromJson(this.autoJoinPlayerIdsStr, new TypeToken<HashMap<Integer, Integer>>() {}.getType());
		for(Map.Entry<Integer, Integer> entry: tmpMap.entrySet()){
			autoCampWarPlayerIds.put(entry.getKey(), entry.getValue());
		}
	}
	
	public ConcurrentHashMap<Integer, Integer> getAutoCampWarPlayerIds() {
		return autoCampWarPlayerIds;
	}

	/**
	 * 是否自动阵营战
	 * @param playerId
	 * @return
	 */
	public boolean isAutoCarmWar(int playerId){
		return autoCampWarPlayerIds.containsKey(playerId);
	}
	
	/**
	 * 取消自动阵营战
	 * @param playerId
	 */
	public void cancelAutoCarmWar(int playerId){
		if(autoCampWarPlayerIds.containsKey(playerId)){
			this.autoCampWarPlayerIds.remove(playerId);
			this.autoJoinPlayerIdsStr = GsonUtil.getJsonInstance().toJson(this.autoCampWarPlayerIds);
			notifyUpdate(true);
		}
	}
	
	/**
	 * 自动阵营战
	 */
	public void addAutoCampWar(int playerId){
		if(!autoCampWarPlayerIds.containsKey(playerId)){
			autoCampWarPlayerIds.put(playerId, 1);
			this.autoJoinPlayerIdsStr = GsonUtil.getJsonInstance().toJson(this.autoCampWarPlayerIds);
			notifyUpdate(true);
		}
	}
}
