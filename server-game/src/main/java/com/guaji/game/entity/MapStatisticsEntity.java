package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.NewMapCfg;

@SuppressWarnings("serial")
@Entity
@Table(name = "map_statistics")
public class MapStatisticsEntity extends DBEntity {
	@Id
	@Column(name = "playerId")
	private int playerId = 0;

	@Column(name = "mapId")
	private int mapId = 0;
	
	@Column(name = "fightTimes")
	private int fightTimes = 0;
	
	@Column(name = "averageTime")
	private int averageTime = 0;
	
	@Column(name = "winRate")
	private int winRate = 0;
	
	@Column(name = "equipRate")
	private int equipRate = 0;
	
	@Column(name = "itemOneRate")
	private float itemOneRate = 0;
	
	@Column(name = "expRate")
	private int expRate = 0;
	
	@Column(name = "coinRate")
	private int coinRate = 0;
	
	@Column(name = "mapExpRatio")
	private int mapExpRatio = 0;
	
	@Column(name = "mapCoinRatio")
	private int mapCoinRatio = 0;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	public MapStatisticsEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		
		reset(null);
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getFightTimes() {
		return fightTimes;
	}

	public void setFightTimes(int fightTimes) {
		this.fightTimes = fightTimes;
	}

	public int getAverageTime() {
		return averageTime;
	}

	public void setAverageTime(int averageTime) {
		this.averageTime = averageTime;
	}

	public int getWinRate() {
		return winRate;
	}

	public void setWinRate(int winRate) {
		this.winRate = winRate;
	}

	public int getEquipRate() {
		return equipRate;
	}

	public void setEquipRate(int equipRate) {
		this.equipRate = equipRate;
	}
	
	public float getItemOneRate() {
		return this.itemOneRate;
	}

	public void setItemOneRate(float itemOneRate) {
		this.itemOneRate = itemOneRate;
	}

	public int getExpRate() {
		return expRate;
	}

	public void setExpRate(int expRate) {
		this.expRate = expRate;
	}

	public int getCoinRate() {
		return coinRate;
	}

	public void setCoinRate(int coinRate) {
		this.coinRate = coinRate;
	}

	public int getMapExpRatio() {
		return mapExpRatio;
	}

	public void setMapExpRatio(int mapExpRatio) {
		this.mapExpRatio = mapExpRatio;
	}

	public int getMapCoinRatio() {
		return mapCoinRatio;
	}

	public void setMapCoinRatio(int mapCoinRatio) {
		this.mapCoinRatio = mapCoinRatio;
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

	public void reset(NewMapCfg mapCfg) {
		averageTime = 40;
		fightTimes = 90;
		winRate = 70;
		expRate = 0;
		coinRate = 0;
		equipRate = 0;
		if (mapCfg != null) {
//			equipRate = (int)(fightTimes * winRate * 0.01f * mapCfg.getDropRate() * 0.0001f);
//			itemOneRate = (int)(fightTimes * winRate * 0.01f * mapCfg.getItemOneDropRate() * 0.0001f);
		}
	}
}
