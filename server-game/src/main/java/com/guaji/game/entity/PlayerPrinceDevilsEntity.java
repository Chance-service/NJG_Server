package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.config.PrinceDevilsCostCfg;
import com.guaji.game.util.GsonUtil;

/**
 * 魔王宝藏
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "player_prince_devils")
public class PlayerPrinceDevilsEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	/** 积分 */
	@Column(name = "score")
	private int score;

	/** 积分兑换次数信息 */
	@Column(name = "scoreExchangeStr")
	private String scoreExchangeStr;
	
	/** 兑换次数信息 */
	@Transient
	private Map<Integer, Integer> scoreExchangeMap;

	/** 更新积分时间 */
	@Column(name = "refreshTime")
	private long refreshTime;

	/** 领取信息 */
	@Column(name = "rewardInfoStr")
	private String rewardInfoStr;
	
	@Transient
	private Map<Integer, String> rewardInfoMap;

	/** 活动期数 */
	@Column(name = "stageId")
	private int stageId;

	/** 开启宝藏次数 */
	@Column(name = "count")
	private int count;
	
	/** 免费次数刷新时间 */
	@Column(name = "freeTime")
	private int freeTime;

	@Column(name="poolRateStr")
	private String poolRateStr;
	/**大奖池概率*/
	@Transient
	private TreeMap<Integer,Integer> poolRateMap;
	/**读写分离锁*/
	@Transient
	private ReadWriteLock ratelock = new ReentrantReadWriteLock();
	@Transient
	private Lock rLock = ratelock.readLock();
	@Transient
	private Lock wLock = ratelock.writeLock();
	/** 幸运奖励次数 */
	@Column(name = "luckCount")
	private int luckCount;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

	public PlayerPrinceDevilsEntity() {
		this.rewardInfoMap = new HashMap<Integer, String>();
		this.scoreExchangeMap = new HashMap<Integer, Integer>();
		this.poolRateMap = new TreeMap<Integer,Integer>();
		this.score = 0;
		this.refreshTime = 0L;
		this.stageId = 0;
		this.count = 0;
		this.rewardInfoStr = GsonUtil.getJsonInstance().toJson(this.rewardInfoMap);
		this.scoreExchangeStr = GsonUtil.getJsonInstance().toJson(this.scoreExchangeMap);
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.luckCount = 0;
		this.freeTime = 0;
		this.poolRateStr = GsonUtil.getJsonInstance().toJson(poolRateMap);
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

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public String getRewardInfo() {
		return rewardInfoStr;
	}

	public void setRewardInfo(String rewardInfo) {
		this.rewardInfoStr = rewardInfo;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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

	/**
	 * 添加打开宝藏奖励
	 * 
	 * @param index
	 * @param item
	 */
	public void addRewardInfo(int index, String item) {
		rewardInfoMap.put(index, item);
		this.rewardInfoStr = GsonUtil.getJsonInstance().toJson(this.rewardInfoMap);
	}

	/**
	 * 增加兑换次数
	 * 
	 * @param id
	 * @param count
	 */
	public void addExchageInfo(int id, int count) {
		scoreExchangeMap.put(id, count);
		this.scoreExchangeStr = GsonUtil.getJsonInstance().toJson(this.scoreExchangeMap);
	}

	public int getLuckCount() {
		return luckCount;
	}

	public void setLuckCount(int luckCount) {
		this.luckCount = luckCount;
	}

	public int getFreeTime() {
		return freeTime;
	}

	public void setFreeTime(int freeTime) {
		this.freeTime = freeTime;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	/**
	 * 触发幸运奖励时重置领奖信息
	 */
	public void resetReward() {
		luckCount=0;
		rewardInfoMap.clear();
		this.rewardInfoStr = GsonUtil.getJsonInstance().toJson(this.rewardInfoMap);
	}
	
	/**
	 * 新一期清除兑换次数
	 */
	public void resetExchangeCount() {
		scoreExchangeMap.clear();
		this.scoreExchangeStr = GsonUtil.getJsonInstance().toJson(this.scoreExchangeMap);
	}

	public void resetPoolRate(){
		wLock.lock();
		try {
			poolRateMap.clear();
			this.poolRateStr = GsonUtil.getJsonInstance().toJson(this.poolRateMap);
		} finally {
			wLock.unlock();
		}
		
	}
	
	public Map<Integer, String> getRewardInfoMap() {
		return this.rewardInfoMap;
	}

	/**
	 * 获取已兑换次数
	 * @param id
	 * @return
	 */
	public int getScoreExchangeCount(int id) {
		if(scoreExchangeMap.containsKey(id)){
			return scoreExchangeMap.get(id);
		}
		return 0;
	}

	public void setScoreExchangeMap(Map<Integer, Integer> scoreExchangeMap) {
		this.scoreExchangeMap = scoreExchangeMap;
	}

	/**
	 * 下期活动开启时重置
	 * 
	 * @param closeTime
	 */
	public void resetInfo(int stageId) {
		this.score = 0;
		this.refreshTime = 0L;
		this.stageId = stageId;
		this.count = 0;
		this.luckCount = 0;
		this.freeTime = 0;
		resetReward();
		resetExchangeCount();
		resetPoolRate();
	}
	

	public void addSearchTimes(boolean luck) {
		count += 1;
		// 暴击后，点击次数清零
		if (luck) {
			luckCount += 1;
		}
	}
	/**
	 * 通过随机到的概率获取奖池ID
	 * @param rate
	 * @return
	 */
	public int getPoolIDByRate(int rate){
		if(poolRateMap==null||poolRateMap.size()==0){
			initPoolRate();
		}
		rLock.lock();
		try {
			Set<Integer> keySet = poolRateMap.keySet();
			int lastValue = 0;
			for(Integer key:keySet){
				Integer value = poolRateMap.get(key);
				if(rate>lastValue&&rate<value){
					return key;
				}
				lastValue = value;
			}
			return poolRateMap.firstKey();
		} finally {
			rLock.unlock();
		}
		
	}
	/***
	 * 初始化大奖池概率
	 */
	private void initPoolRate(){
		wLock.lock();
		try {
			poolRateMap.clear();
			PrinceDevilsCostCfg config = ConfigManager.getInstance().getConfigByIndex(PrinceDevilsCostCfg.class, 0);
			TreeMap<Integer, Integer> rateMap = config.getPoolRate();
			Set<Integer> keySet = rateMap.keySet();
			for(Integer key:keySet){
				Integer value = rateMap.get(key);
				poolRateMap.put(key, value);
			}
			poolRateStr = GsonUtil.getJsonInstance().toJson(this.poolRateMap);
		} finally {
			wLock.unlock();
		}
		
	}
	/**
	 * 更新概率
	 */
	public void updatePoolRate(){
		//最高奖励衰减，衰减出来的数值平均增加给其他奖项
		wLock.lock();
		try {
			PrinceDevilsCostCfg config = ConfigManager.getInstance().getConfigByIndex(PrinceDevilsCostCfg.class, 0);
			int damping = config.getTopAwardRateDamping();
			if(damping==0){
				return;
			}
			int limit = config.getTopAwardMinRateLimit();
			int curTopRate = poolRateMap.get(poolRateMap.firstKey());
			if(curTopRate<=limit){
				return;
			}
			//计算衰减值分布
			int size = poolRateMap.size();
			if(size<=1){
				return;
			}
			size = size-1;
			int appeand = damping/size;
			int lastKey = 0;
			for(int i = 1;i<=size;i++){
				lastKey = poolRateMap.higherKey(lastKey);
				int value = poolRateMap.get(lastKey);
				value+=appeand*i;
				poolRateMap.put(lastKey, value);
			}
			this.poolRateStr = GsonUtil.getJsonInstance().toJson(this.poolRateMap);
		} finally {
			wLock.unlock();
		}
	}

	/**
	 * 组装数据;
	 */
	public void convert() {
		this.rewardInfoMap.clear();
		this.rewardInfoMap = GsonUtil.getJsonInstance().fromJson(this.rewardInfoStr, new TypeToken<Map<Integer, String>>() {
		}.getType());

		this.scoreExchangeMap.clear();
		this.scoreExchangeMap = GsonUtil.getJsonInstance().fromJson(this.scoreExchangeStr, new TypeToken<Map<Integer, Integer>>() {
		}.getType());
		this.poolRateMap.clear();
		Map<Integer,Integer> dbPoolRateMap = GsonUtil.getJsonInstance().fromJson(this.poolRateStr,  new TypeToken<Map<Integer, Integer>>() {
		}.getType());
		this.poolRateMap.putAll(dbPoolRateMap);
	}
}