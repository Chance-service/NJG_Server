package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.guaji.game.config.WorldBossCfg;
import com.guaji.game.config.WorldBossNpcCfg;

/**
 * 世界boss基础信息
 *
 */
@Entity
@Table(name = "world_boss")
@SuppressWarnings("serial")
public class WorldBossEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;

	@Column(name = "startDate", nullable = false)
	private Date startDate;

	@Column(name = "bossNpcId")
	private int bossNpcId;

//	@Column(name = "name")
//	private String name;

//	@Column(name = "level")
//	private int level;

//	@Column(name = "clonePlayerId")
//	private int clonePlayerId;

//	@Column(name = "arenaNpcId")
//	private int arenaNpcId;

	@Column(name = "currBossHp")
	private long currBossHp;

	@Column(name = "maxBossHp")
	private long maxBossHp;

	@Column(name = "deadTime")
	private long deadTime;

	/**
	 * 最后击杀
	 */
	@Column(name = "lastKillPlayerId")
	private int lastKillPlayerId;

//	@Column(name = "hpMultiple")
//	private int hpMultiple;

//	/**
//	 * boss弱点信息
//	 */
//	@Column(name = "failingInfo")
//	private String failingInfo;

	/**
	 * boss 弱点信息<startHp,endHp,type>
	 */
	//@Transient
	//private Map<String, Integer> failingMap = new HashMap<String, Integer>();

	/**
	 * 弱点开始时间
	 */
	//@Column(name = "failingStartTime")
	//private long failingStartTime;

	/**
	 * 下次弱点刷新时间
	 */
	//@Column(name = "nextFailingTime")
	//private long nextFailingTime;

	/**
	 * 弱点是否存在
	 */
	//@Column(name = "isFailing")
	//private boolean isFailing = false;

	/**
	 * 当前弱点buff
	 */
	//@Column(name = "curFailingBuffCfg")
	//private int curFailingBuffCfg;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = GuaJiTime.getCalendar().getTime();

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	public WorldBossEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
//		this.nextFailingTime = 0;
//		this.failingStartTime = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getBossNpcId() {
		return bossNpcId;
	}

	public void setBossNpcId(int bossNpcId) {
		this.bossNpcId = bossNpcId;
	}

//	public int getClonePlayerId() {
//		return clonePlayerId;
//	}
//
//	public void setClonePlayerId(int clonePlayerId) {
//		this.clonePlayerId = clonePlayerId;
//	}

	public long getCurrBossHp() {
		return currBossHp;
	}

	public void setCurrBossHp(long currBossHp) {
		this.currBossHp = currBossHp;
	}

	public long getMaxBossHp() {
		return maxBossHp;
	}

	public void setMaxBossHp(long maxBossHp) {
		this.maxBossHp = maxBossHp;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getLastKillPlayerId() {
		return lastKillPlayerId;
	}

	public void setLastKillPlayerId(int lastKillPlayerId) {
		this.lastKillPlayerId = lastKillPlayerId;
	}

//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public int getLevel() {
//		return level;
//	}
//
//	public void setLevel(int level) {
//		this.level = level;
//	}

	public long getDeadTime() {
		return deadTime;
	}

	public void setDeadTime(Date deadTime) {
		this.deadTime = deadTime.getTime();
	}
	
	public long getRebirthTime() {
		return deadTime > 0 ? (deadTime + WorldBossCfg.getInstance().getBossRirthTime()) : (this.getEndTime() + WorldBossCfg.getInstance().getBossRirthTime())  ;
	}
	
	public Date getRebirthDate() {
		return new Date(this.getRebirthTime());
	}
	
	public long getEndTime() {
		return this.getStartDate().getTime() + WorldBossCfg.getInstance().getDuration();
	}
	
	public int getAwardsId() {
		WorldBossNpcCfg BossNpcCfgs = ConfigManager.getInstance().getConfigByKey(WorldBossNpcCfg.class,getBossNpcId());
		if (BossNpcCfgs != null) {
			return BossNpcCfgs.getawardId();
		}
		return 1;
	}

//	public int getArenaNpcId() {
//		return arenaNpcId;
//	}
//
//	public void setArenaNpcId(int arenaNpcId) {
//		this.arenaNpcId = arenaNpcId;
//	}
//
//	public int getHpMultiple() {
//		return hpMultiple;
//	}
//
//	public void setHpMultiple(int hpMultiple) {
//		this.hpMultiple = hpMultiple;
//	}

//	public String getFailingInfo() {
//		return failingInfo;
//	}

//	public void setFailingInfo(String failingInfo) {
//		this.failingInfo = failingInfo;
//	}

	public void setDeadTime(long deadTime) {
		this.deadTime = deadTime;
	}

//	public Map<String, Integer> getFailingMap() {
//		return failingMap;
//	}
//
//	public void setFailingMap(Map<String, Integer> failingMap) {
//		this.failingMap = failingMap;
//	}

	/**
	 * 判断是否结束
	 * 
	 * @return
	 */
	public boolean isEnd() {
		return GuaJiTime.getMillisecond() > this.getEndTime(); //this.getRebirthTime() > 0;
	}
	
	/**
	 * 返回弱点出现次数
	 * 
	 * @param failingStr
	 * @return
	 */
//	public int getFailingCount(String failingStr) {
//		if (failingMap == null || failingMap.size() <= 0) {
//			return 0;
//		}
//		return failingMap.get(failingStr) == null ? 0 : failingMap.get(failingStr);
//	}

	/**
	 * 数据格式化
	 */
//	public void converData() {
//		if (this.failingInfo != null && !"".equals(failingInfo) && !"null".equals(failingInfo)) {
//			this.failingMap = GsonUtil.getJsonInstance().fromJson(failingInfo, new TypeToken<Map<String, Integer>>() {
//			}.getType());
//		}
//	}

	/**
	 * 数据格式化
	 */
//	public void reConverData() {
//		this.failingInfo = GsonUtil.getJsonInstance().toJson(failingMap);
//	}

	/**
	 * 记录弱点信息
	 * 
	 * @param failingStr
	 */
//	public void addFailingMap(String failingStr) {
//		if (failingMap.containsKey(failingStr)) {
//			failingMap.put(failingStr, failingMap.get(failingStr) + 1);
//		} else {
//			failingMap.put(failingStr, 1);
//		}
//	}

//	public long getFailingStartTime() {
//		return failingStartTime;
//	}
//
//	public void setFailingStartTime(long failingStartTime) {
//		this.failingStartTime = failingStartTime;
//	}
//
//	public long getNextFailingTime() {
//		return nextFailingTime;
//	}
//
//	public void setNextFailingTime(long nextFailingTime) {
//		this.nextFailingTime = nextFailingTime;
//	}
//
//	public boolean isFailing() {
//		return isFailing;
//	}
//
//	public void setFailing(boolean isFailing) {
//		this.isFailing = isFailing;
//	}
//
//	public int getCurFailingBuffCfg() {
//		return curFailingBuffCfg;
//	}
//
//	public void setCurFailingBuffCfg(int curFailingBuffCfg) {
//		this.curFailingBuffCfg = curFailingBuffCfg;
//	}

}
