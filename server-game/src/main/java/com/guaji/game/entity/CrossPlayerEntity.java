package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.CrossBattleCfg;

/**
 * 跨服竞技玩家对象
 */
@Entity
@Table(name = "cross_player")
@SuppressWarnings("serial")
public class CrossPlayerEntity extends DBEntity {
	
	@Id
	@Column(name = "playerId")
	private int playerId = 0;
	
	/**
	 * 竞技积分
	 */
	private int score;
	
	/**
	 * 竞技排名
	 */
	private int rank;
	
	/**
	 * 跨服货币
	 */
	@Column(name = "crossCoin", nullable = false)
	private int crossCoin = 0;
	
	/**
	 * 挑战次数
	 */
	@Column(name = "battleTimes", nullable = false)
	private int battleTimes = 0;
	
	/**
	 * 购买挑战次数
	 */
	@Column(name = "buyTimes", nullable = false)
	private int buyTimes = 0;
	
	/**
	 * 同步数据时间
	 */
	@Column(name = "synchroTime", nullable = false)
	private long synchroTime;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	public CrossPlayerEntity() {
		createTime = GuaJiTime.getCalendar().getTime();
		battleTimes = CrossBattleCfg.getInstance().getFreeTimes();
	}
	
	/**
	 * 每日数据清理
	 */
	public void clearDayData() {
		battleTimes = CrossBattleCfg.getInstance().getFreeTimes();
		buyTimes = 0;
		this.notifyUpdate();
	}
	
	/**
	 * 赛季数据清理
	 */
	public void clearSeasonData() {
		score = 0;
		rank = 0;
		this.notifyUpdate();
	}
	
	/**
	 * 更新挑战和购买挑战次数
	 * 
	 * @param times
	 */
	public void updateTimes(int times) {
		battleTimes = battleTimes + times;
		buyTimes = buyTimes + times;
		this.notifyUpdate();
	}
	
	/**
	 * 更新刷新时间
	 */
	public void updateSynchroTime() {
		synchroTime = GuaJiTime.getMillisecond();
		this.notifyUpdate();
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getCrossCoin() {
		return crossCoin;
	}

	public void setCrossCoin(int crossCoin) {
		this.crossCoin = crossCoin;
	}

	public int getBattleTimes() {
		return battleTimes;
	}

	public void setBattleTimes(int battleTimes) {
		this.battleTimes = battleTimes;
	}

	public int getBuyTimes() {
		return buyTimes;
	}

	public void setBuyTimes(int buyTimes) {
		this.buyTimes = buyTimes;
	}

	public long getSynchroTime() {
		return synchroTime;
	}

	public void setSynchroTime(long synchroTime) {
		this.synchroTime = synchroTime;
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
}
