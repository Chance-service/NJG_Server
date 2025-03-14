package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.module.activity.maidenEncounter.MaidenEncounterManager;
import com.guaji.game.util.GsonUtil;

/**
 * 少女的邂逅实体类
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "maiden_encounter_activity")
public class MaidenEncounterEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;

	/** 玩家id */
	@Column(name = "playerId", nullable = false)
	private int playerId;

	/** 活动期数 */
	@Column(name = "stageId", nullable = false)
	private int stageId;

	/** 当前交换对象ID */
	@Column(name = "currentIndex", nullable = false)
	private Integer currentIndex;

	/** 当前进度 */
	@Column(name = "progress", nullable = false)
	private String progress;

	/** 剩余免费互动次数 */
	@Column(name = "surplusFreeInteractTimes", nullable = false)
	private Integer surplusFreeInteractTimes;

	/** 剩余免费刷新次数 */
	@Column(name = "surplusFreeRefreshTimes", nullable = false)
	private Integer surplusFreeRefreshTimes;

	/** 恶魔刷新时间 */
	@Column(name = "devilRefreshTime", nullable = false)
	private Integer devilRefreshTime;

	/** 互动对象出现次数 */
	@Column(name = "maidenStage", nullable = false)
	private String maidenStage;

	/** 历史总随机次数 */
	@Column(name = "historyRandomTimes", nullable = false)
	private Integer historyRandomTimes;

	/** 交换信息 */
	@Column(name = "exchange", nullable = false)
	private String exchange;

	@Column(name = "createTime", nullable = false)
	protected Date createTime;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	@Transient
	private Map<Integer, Integer> exchangeMap;

	@Transient
	private Map<Integer, Integer> maidenStageMap;

	@Transient
	private Map<Integer, Integer> progressMap;

	public MaidenEncounterEntity() {

	}

	public MaidenEncounterEntity(int playerId, int stageId) {
		this.playerId = playerId;
		this.stageId = stageId;
		this.devilRefreshTime = 0;
		this.createTime = GuaJiTime.getCalendar().getTime();
		MaidenEncounterManager.init(this);
	}

	public Map<Integer, Integer> getMaidenStageMap() {
		return maidenStageMap = GsonUtil.getJsonInstance().fromJson(this.maidenStage, new TypeToken<Map<Integer, Integer>>() {
		}.getType());
	}

	public void setMaidenStageMap(Map<Integer, Integer> maidenStageMap) {
		this.maidenStageMap = maidenStageMap;
		this.maidenStage = GsonUtil.getJsonInstance().toJson(maidenStageMap, new TypeToken<HashMap<Integer, Integer>>() {
		}.getType());
	}

	public Map<Integer, Integer> getExchangeMap() {
		return exchangeMap = GsonUtil.getJsonInstance().fromJson(this.exchange, new TypeToken<Map<Integer, Integer>>() {
		}.getType());
	}

	public void setExchangeMap(Map<Integer, Integer> exchangeMap) {
		this.exchangeMap = exchangeMap;
		this.exchange = GsonUtil.getJsonInstance().toJson(exchangeMap, new TypeToken<HashMap<Integer, Integer>>() {
		}.getType());
	}

	public Map<Integer, Integer> getProgressMap() {
		return progressMap = GsonUtil.getJsonInstance().fromJson(this.progress, new TypeToken<Map<Integer, Integer>>() {
		}.getType());
	}

	public void setProgressMap(Map<Integer, Integer> progressMap) {
		this.progressMap = progressMap;
		this.progress = GsonUtil.getJsonInstance().toJson(progressMap, new TypeToken<HashMap<Integer, Integer>>() {
		}.getType());
	}

	@Override
	public Date getCreateTime() {
		return this.createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;

	}

	@Override
	public Date getUpdateTime() {
		return this.updateTime;
	}

	@Override
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;

	}

	@Override
	public boolean isInvalid() {
		return this.invalid;
	}

	@Override
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	/**
	 * 清空每日数据
	 */
	public void clearDailyData() {
		int freeRefreshTimes = SysBasicCfg.getInstance().getFreeRefreshTimes();
		int freeInteractTimes = SysBasicCfg.getInstance().getFreeInteractTimes();
		this.setSurplusFreeInteractTimes(freeInteractTimes);
		this.setSurplusFreeRefreshTimes(freeRefreshTimes);
		MaidenEncounterManager.initExchange(this);
	}

	@Override
	public void notifyUpdate() {
		super.notifyUpdate();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public Integer getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(Integer currentIndex) {
		this.currentIndex = currentIndex;
	}

	public Integer getSurplusFreeInteractTimes() {
		return surplusFreeInteractTimes;
	}

	public void setSurplusFreeInteractTimes(Integer surplusFreeInteractTimes) {
		this.surplusFreeInteractTimes = surplusFreeInteractTimes;
	}

	public Integer getSurplusFreeRefreshTimes() {
		return surplusFreeRefreshTimes;
	}

	public void setSurplusFreeRefreshTimes(Integer surplusFreeRefreshTimes) {
		this.surplusFreeRefreshTimes = surplusFreeRefreshTimes;
	}

	public Integer getDevilRefreshTime() {
		return devilRefreshTime;
	}

	public void setDevilRefreshTime(Integer devilRefreshTime) {
		this.devilRefreshTime = devilRefreshTime;
	}

	public Integer getHistoryRandomTimes() {
		return historyRandomTimes;
	}

	public void setHistoryRandomTimes(Integer historyRandomTimes) {
		this.historyRandomTimes = historyRandomTimes;
	}

	@Override
	public String toString() {
		return "MaidenEncounterEntity [id=" + id + ", playerId=" + playerId + ", stageId=" + stageId + ", currentIndex=" + currentIndex + ", progress=" + progress + ", surplusFreeInteractTimes=" + surplusFreeInteractTimes + ", surplusFreeRefreshTimes=" + surplusFreeRefreshTimes + ", devilRefreshTime=" + devilRefreshTime
				+ ", maidenStage=" + maidenStage + ", historyRandomTimes=" + historyRandomTimes + ", exchange=" + exchange + ", createTime=" + createTime + ", updateTime=" + updateTime + ", invalid=" + invalid + "]";
	}

}
