package com.guaji.game.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.config.HaremConstCfg;
import com.guaji.game.util.GsonUtil;
import com.guaji.game.protocol.Const;

@SuppressWarnings("serial")
@Entity
@Table(name = "harem_activity")
public class HaremActivityEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;

	@Column(name = "playerId", nullable = false)
	private int playerId;

	// 普通抽卡CD时间
	@Column(name = "lastCommonFreeTime", nullable = false)
	private long lastCommonFreeTime;
	// 普通免费机会
	@Column(name = "commonFreeChance", nullable = false)
	private int commonFreeChance;
	// 历史普通抽奖总次数
	@Column(name = "commonTotalTimes", nullable = false)
	private int commonTotalTimes;
	// 单日最多抽取次数
	@Column(name = "commonDayTotalTimes", nullable = false)
	private int commonDayTotalTimes;

	// 中级CD
	@Column(name = "lastMiddleFreeTime", nullable = false)
	private long lastMiddleFreeTime;
	// 中级免费机会
	@Column(name = "middleFreeChance", nullable = false)
	private int middleFreeChance;
	// 中级抽奖总次数
	@Column(name = "middleTotalTimes", nullable = false)
	private int middleTotalTimes;

	// 高级CD
	@Column(name = "lastAdvancedFreeTime", nullable = false)
	private long lastAdvancedFreeTime;
	// 高级免费机会
	@Column(name = "advancedFreeChance", nullable = false)
	private int advancedFreeChance;
	// 高级抽奖总次数
	@Column(name = "advancedTotalTimes", nullable = false)
	private int advancedTotalTimes;

	// 限定抽奖时间
	@Column(name = "lastStrictFreeTime", nullable = false)
	private long lastStrictFreeTime;
	// 限定免费机会
	@Column(name = "strictFreeChance", nullable = false)
	private int strictFreeChance;
	// 限定抽奖总次数
	@Column(name = "strictTotalTimes", nullable = false)
	private int strictTotalTimes;

	// 新手限定结束时间
	@Column(name = "newStrictEndTime", nullable = false)
	private int newStrictEndTime;
	// 新手限定抽奖时间
	@Column(name = "lastNewStrictFreeTime", nullable = false)
	private int lastNewStrictFreeTime;
	// 新手限定免费机会
	@Column(name = "newStrictFreeChance", nullable = false)
	private int newStrictFreeChance;
	// 新手限定抽奖总次数
	@Column(name = "newStrictTotalTimes", nullable = false)
	private int newStrictTotalTimes;

	// 限购奖池结束时间
	@Column(name = "limitEndTime", nullable = false)
	private int limitEndTime;
	// 限购奖池抽奖时间
	@Column(name = "lastLimitFreeTime", nullable = false)
	private int lastLimitFreeTime;
	// 限购奖池免费机会
	@Column(name = "limitFreeChance", nullable = false)
	private int limitFreeChance;
	// 限购奖池抽奖总次数
	@Column(name = "limitTotalTimes", nullable = false)
	private int limitTotalTimes;

	// 总积分
	@Column(name = "score", nullable = false)
	private int score;

	// 兑换记录
	@Column(name = "exchangeStr", nullable = false)
	private String exchangeStr;

	// 兑换记录
	@Column(name = "exchangeNextResetTime", nullable = false)
	private long exchangeNextResetTime=0;

	@Transient
	private Map<Integer, Integer> exchangeMap = new HashMap<Integer, Integer>();

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	public HaremActivityEntity() {
		super();
	}

	public HaremActivityEntity(int playerId) {
		this.createTime = GuaJiTime.getCalendar().getTime();
		Calendar curCalendar = GuaJiTime.getCalendar();
		curCalendar.add(Calendar.MONTH, 1);
		curCalendar.set(Calendar.DAY_OF_MONTH, 1);
		curCalendar.set(Calendar.HOUR, 0);
		curCalendar.set(Calendar.MINUTE, 0);
		curCalendar.set(Calendar.SECOND, 0);
		this.exchangeNextResetTime =0;
		this.playerId = playerId;
		this.exchangeStr = "";
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

	public int getCommonFreeChance() {
		return commonFreeChance;
	}

	public void setCommonFreeChance(int commonFreeChance) {
		this.commonFreeChance = commonFreeChance;
	}

	public int getCommonTotalTimes() {
		return commonTotalTimes;
	}

	public void setCommonTotalTimes(int commonTotalTimes) {
		this.commonTotalTimes = commonTotalTimes;
	}

	public int getAdvancedFreeChance() {
		return advancedFreeChance;
	}

	public void setAdvancedFreeChance(int advancedFreeChance) {
		this.advancedFreeChance = advancedFreeChance;
	}

	public int getAdvancedTotalTimes() {
		return advancedTotalTimes;
	}

	public void setAdvancedTotalTimes(int advancedTotalTimes) {
		this.advancedTotalTimes = advancedTotalTimes;
	}

	public int getStrictFreeChance() {
		return strictFreeChance;
	}

	public void setStrictFreeChance(int strictFreeChance) {
		this.strictFreeChance = strictFreeChance;
	}

	public int getStrictTotalTimes() {
		return strictTotalTimes;
	}

	public void setStrictTotalTimes(int strictTotalTimes) {
		this.strictTotalTimes = strictTotalTimes;
	}

	public long getLastCommonFreeTime() {
		return lastCommonFreeTime;
	}

	public void setLastCommonFreeTime(long lastCommonFreeTime) {
		this.lastCommonFreeTime = lastCommonFreeTime;
	}

	public long getLastAdvancedFreeTime() {
		return lastAdvancedFreeTime;
	}

	public void setLastAdvancedFreeTime(long lastAdvancedFreeTime) {
		this.lastAdvancedFreeTime = lastAdvancedFreeTime;
	}

	public long getLastStrictFreeTime() {
		return lastStrictFreeTime;
	}

	public void setLastStrictFreeTime(long lastStrictFreeTime) {
		this.lastStrictFreeTime = lastStrictFreeTime;
	}

	public int getNewStrictEndTime() {
		return newStrictEndTime;
	}

	public void setNewStrictEndTime(int newStrictEndTime) {
		this.newStrictEndTime = newStrictEndTime;
	}

	public int getLastNewStrictFreeTime() {
		return lastNewStrictFreeTime;
	}

	public void setLastNewStrictFreeTime(int lastNewStrictFreeTime) {
		this.lastNewStrictFreeTime = lastNewStrictFreeTime;
	}

	public int getNewStrictFreeChance() {
		return newStrictFreeChance;
	}

	public void setNewStrictFreeChance(int newStrictFreeChance) {
		this.newStrictFreeChance = newStrictFreeChance;
	}

	public int getNewStrictTotalTimes() {
		return newStrictTotalTimes;
	}

	public void setNewStrictTotalTimes(int newStrictTotalTimes) {
		this.newStrictTotalTimes = newStrictTotalTimes;

	}

	public int getLimitEndTime() {
		return limitEndTime;
	}

	public void setLimitEndTime(int limitEndTime) {
		this.limitEndTime = limitEndTime;
	}

	public int getLastLimitFreeTime() {
		return lastLimitFreeTime;
	}

	public void setLastLimitFreeTime(int lastLimitFreeTime) {
		this.lastLimitFreeTime = lastLimitFreeTime;
	}

	public int getLimitFreeChance() {
		return limitFreeChance;
	}

	public void setLimitFreeChance(int limitFreeChance) {
		this.limitFreeChance = limitFreeChance;
	}

	public int getLimitTotalTimes() {
		return limitTotalTimes;
	}

	public void setLimitTotalTimes(int limitTotalTimes) {
		this.limitTotalTimes = limitTotalTimes;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getExchangeStr() {
		return exchangeStr;
	}

	public void setExchangeStr(String exchangeStr) {
		this.exchangeStr = exchangeStr;
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

	public int getCommonDayTotalTimes() {
		return commonDayTotalTimes;
	}

	public void setCommonDayTotalTimes(int commonDayTotalTimes) {
		this.commonDayTotalTimes = commonDayTotalTimes;
	}

	@Override
	public void notifyUpdate() {
		reConvert();
		super.notifyUpdate();
	}

	/**
	 * 数据类型转换
	 */
	public void coverToMap() {
		if (!StringUtils.isEmpty(exchangeStr)) {
			exchangeMap = GsonUtil.getJsonInstance().fromJson(this.exchangeStr, new TypeToken<Map<Integer, Integer>>() {
			}.getType());
		}
	}

	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert() {
		this.exchangeStr = GsonUtil.getJsonInstance().toJson(exchangeMap);
	}

	public Map<Integer, Integer> getExchangeMap() {
		return exchangeMap;
	}

	public void setExchangeMap(Map<Integer, Integer> exchangeMap) {
		this.exchangeMap = exchangeMap;
	}

	public long getLastMiddleFreeTime() {
		return lastMiddleFreeTime;
	}

	public void setLastMiddleFreeTime(long lastMiddleFreeTime) {
		this.lastMiddleFreeTime = lastMiddleFreeTime;
	}

	public int getMiddleFreeChance() {
		return middleFreeChance;
	}

	public void setMiddleFreeChance(int middleFreeChance) {
		this.middleFreeChance = middleFreeChance;
	}

	public int getMiddleTotalTimes() {
		return middleTotalTimes;
	}

	public void setMiddleTotalTimes(int middleTotalTimes) {
		this.middleTotalTimes = middleTotalTimes;
	}




	public long getExchangeNextResetTime() {
		return exchangeNextResetTime;
	}

	public void setExchangeNextResetTime(long exchangeNextResetTime) {
		this.exchangeNextResetTime = exchangeNextResetTime;
	}

	/**
	 * 清空每日免费次数
	 */
	public void clearDailyData() {
		Map<Object, HaremConstCfg> HaremConstCfgMap = ConfigManager.getInstance().getConfigMap(HaremConstCfg.class);
		for (Object haremType : HaremConstCfgMap.keySet()) {
			HaremConstCfg cfg = HaremConstCfgMap.get(haremType);
			switch ((int) haremType) {
			case Const.HaremType.HAREM_TYPE_COMMON_VALUE:
				this.commonFreeChance = cfg.getFreeChance();
				this.lastCommonFreeTime = 0;
				this.commonDayTotalTimes = 0;
				break;
			case Const.HaremType.HAREM_TYPE_ADVANCED_VALUE:
				this.advancedFreeChance = cfg.getFreeChance();
				this.lastAdvancedFreeTime = 0;
				break;
			case Const.HaremType.HAREM_TYPE_MIDDLE_VALUE:
				this.middleFreeChance = cfg.getFreeChance();
				this.lastMiddleFreeTime = 0;
				break;
			case Const.HaremType.HAREM_TYPE_STRICT_VALUE:
				this.strictFreeChance = cfg.getFreeChance();
				this.lastStrictFreeTime = 0;
				break;
			case Const.HaremType.HAREM_TYPE_NEW_STRICT_VALUE:
				this.newStrictFreeChance = cfg.getFreeChance();
				this.lastNewStrictFreeTime = 0;
				break;
			case Const.HaremType.HAREM_TYPE_LIMIT_VALUE:
				this.limitFreeChance = cfg.getFreeChance();
				this.lastLimitFreeTime = 0;
				break;
			}
		}
	}

}
