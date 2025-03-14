package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.config.CrossBattleCfg;
import com.guaji.game.util.GsonUtil;

/**
 * 跨服竞技刷新实体对象
 */
@Entity
@Table(name = "cross_battle")
@SuppressWarnings("serial")
public class CrossBattleEntity extends DBEntity {

	@Id
	@Column(name = "id", unique = true)
	private int id = 1;

	/**
	 * 每日推送的玩家ID集合
	 */
	@Column(name = "playerIds")
	private String playerIds;

	/**
	 * 数据刷新时间
	 */
	@Column(name = "refreshTime", nullable = false)
	private long refreshTime;

	/**
	 * 赛季结束发奖时间
	 */
	@Column(name = "rewardTime", nullable = false)
	private long rewardTime;
	
	/**
	 * 当前推送阶段
	 */
	@Column(name = "pushState", nullable = false)
	private int pushState;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	@Transient
	private List<Integer> idList;

	public CrossBattleEntity() {
		idList = new ArrayList<Integer>();
		createTime = GuaJiTime.getCalendar().getTime();
	}
	
	/**
	 * 初始化每日数据刷新时间
	 */
	public void initRefreshTime() {
		// 取日期数据
		Calendar calendar = GuaJiTime.getCalendar();
		calendar.set(Calendar.MINUTE, 1);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		// 赛季最后一天
		if (dayOfWeek == CrossBattleCfg.getInstance().getEndDay()) {
			calendar.add(Calendar.DAY_OF_WEEK, 2);
			calendar.set(Calendar.HOUR_OF_DAY, CrossBattleCfg.getInstance().getStartTime());
			refreshTime = calendar.getTimeInMillis();
		}
		// 不是赛季最后一天
		if (refreshTime == 0) {
			int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
			if (hourOfDay >= CrossBattleCfg.getInstance().getStartTime()) {
				// 过了每天结算开始时间点
				calendar.add(Calendar.DAY_OF_WEEK, 1);
				calendar.set(Calendar.HOUR_OF_DAY, CrossBattleCfg.getInstance().getStartTime());
				refreshTime = calendar.getTimeInMillis();
			} else {
				calendar.set(Calendar.HOUR_OF_DAY, CrossBattleCfg.getInstance().getStartTime());
				refreshTime = calendar.getTimeInMillis();
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPlayerIds() {
		return playerIds;
	}

	public void setPlayerIds(String playerIds) {
		this.playerIds = playerIds;
	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public long getRewardTime() {
		return rewardTime;
	}

	public void setRewardTime(long rewardTime) {
		this.rewardTime = rewardTime;
	}

	public int getPushState() {
		return pushState;
	}

	public void setPushState(int pushState) {
		this.pushState = pushState;
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

	public List<Integer> getIdList() {
		return idList;
	}
	
	/**
	 * 更新每日刷新数据时间
	 */
	public void updateRefreshTime() {
		// 取日期数据
		Calendar calendar = GuaJiTime.getCalendar();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		// 赛季最后一天
		if (dayOfWeek == CrossBattleCfg.getInstance().getEndDay()) {
			this.refreshTime = this.refreshTime + 48 * 3600 * 1000L;
		} else {
			this.refreshTime = this.refreshTime + 24 * 3600 * 1000L;
		}
		this.notifyUpdate();
	}
	
	/**
	 * 更新赛季结束发奖时间
	 * 
	 * @param millisecond
	 */
	public void updateRewardTime(long millisecond) {
		this.rewardTime = millisecond + 168 * 3600 * 1000L;
		this.notifyUpdate();
	}

	/**
	 * 将字符串转成对像
	 */
	public void loadCrossBattle() {
		if (null != playerIds) {
			this.idList = GsonUtil.getJsonInstance().fromJson(playerIds, new TypeToken<List<Integer>>() {}.getType());
		}
	}

	/**
	 * 玩家是否在推送缓存数据中
	 * 
	 * @param playerId
	 * @return
	 */
	public boolean isContains(Integer playerId) {
		return idList.contains(playerId);
	}

	/**
	 * 更新推送玩家ID数据
	 * 
	 * @param idList
	 */
	public void updateIdList(List<Integer> idList) {
		this.idList.clear();
		this.idList.addAll(idList);
		this.playerIds = null;
		this.playerIds = GsonUtil.getJsonInstance().toJson(idList);
		this.notifyUpdate();
	}

}
