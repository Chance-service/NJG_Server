package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.util.GsonUtil;

/**
 * GVG领取城池奖励
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "gvg_reward")
public class GvgRewardEntity extends DBEntity {

	/**
	 * 账号ID
	 */
	@Id
	@Column(name = "playerId", nullable = false)
	private int playerId;
	
	/**
	 * 领取过的城池ID
	 */
	@Column(name = "received", nullable = false)
	private String received;
	
	/**
	 * 刷新时间
	 */
	@Column(name = "refreshTime", nullable = false)
	private long refreshTime;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	@Transient
	private List<Integer> receivedList = new ArrayList<Integer>();
	
	public GvgRewardEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	/**
	 * 数据初始化
	 */ 
	public void init() {
		if (received != null) {
			receivedList = GsonUtil.getJsonInstance().fromJson(received, new TypeToken<ArrayList<Integer>>() {}.getType());
		}
	}
	
	/**
	 * 数据初始化---记录
	 * 
	 * @param playerId
	 * @return
	 */
	public static GvgRewardEntity createEntity(int playerId) {
		GvgRewardEntity entity = new GvgRewardEntity();
		entity.playerId = playerId;
		entity.received = GsonUtil.getJsonInstance().toJson(entity.receivedList);
		entity.refreshTime = GuaJiTime.getNextAM0Date();
		if (DBManager.getInstance().create(entity)) {
			return entity;
		}
		return null;
	}
	
	/**
	 * 领取城池ID奖励更新
	 * 
	 * @param cityId
	 */
	public void updateReceived(int cityId) {
		receivedList.add(cityId);
		received = GsonUtil.getJsonInstance().toJson(receivedList);
		this.notifyUpdate();
	}
	
	/**
	 * 清理城池ID奖励更新
	 */
	public void clearReceived() {
		receivedList.clear();
		refreshTime = GuaJiTime.getNextAM0Date();
		received = GsonUtil.getJsonInstance().toJson(receivedList);
		this.notifyUpdate();
	}
	
	/**
	 * 领取奖励城池ID
	 * 
	 * @return
	 */
	public List<Integer> getReceivedList() {
		return receivedList;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getReceived() {
		return received;
	}

	public void setReceived(String received) {
		this.received = received;
	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
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

}
