package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.GsApp;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.RankManager;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;

/**
 * 公会系统实体
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "alliance")
public class AllianceEntity extends DBEntity {

	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id = 0;

	@Column(name = "playerId")
	private int playerId = 0;

	@Column(name = "playerName")
	private String playerName = "";

	@Column(name = "name")
	private String name;

	@Column(name = "level")
	private int level = 0;

	@Column(name = "exp")
	private int exp = 0;

	@Column(name = "joinLimit")
	private int joinLimit = 0;

	@Column(name = "notice")
	private String notice;

	@Column(name = "createAllianceTime")
	private long createAllianceTime = 0;

	/**
	 * false未开启
	 */
	@Column(name = "bossOpen")
	private boolean bossOpen;

	/**
	 * boss免费开启次数
	 */
	@Column(name = "bossOpenTime")
	private long bossOpenTime;

	/**
	 * 免费次数
	 */
	@Column(name = "bossOpenSize")
	private int bossOpenSize;

	@Column(name = "bossId")
	private int bossId = 0;

	/**
	 * 加入攻击boss者
	 */
	@Column(name = "bossJoinStr")
	private String bossJoinStr = "";

	/**
	 * <key:playerId value:伤害值>
	 */
	@Transient
	private Map<Integer, Integer> bossJoinMap;

	/**
	 * boss生命
	 */
	@Column(name = "bossHp")
	private int bossHp;

	/**
	 * boss撤退总时间
	 */
	@Column(name = "bossMaxTime")
	private long bossMaxTime;

	/**
	 * 攻击boss时间
	 */
	@Column(name = "bossAttTime")
	private long bossAttTime;

	/**
	 * boss元气值, 用于开启工会boss
	 */
	@Column(name = "bossVitality")
	private int bossVitality;

	/**
	 * 当天增加的元气
	 */
	@Column(name = "curDayAddVitality")
	private int curDayAddVitality;

	/**
	 * 昨天增加的元气
	 */
	@Column(name = "beforeDayAddVitality")
	private int beforeDayAddVitality;

	@Transient
	private Map<Integer, Integer> playerAddMap;

	/**
	 * 加成次数
	 */
	@Column(name = "bossAddProp")
	private String bossAddProp;

	/**
	 * 每天Boss开启次数
	 */
	@Column(name = "everydayBossOpenTimes")
	private int everydayBossOpenTimes;

	/**
	 * 刷新每天Boss开启次数时间
	 */
	@Column(name = "refreshTime")
	private long refreshTime;

	/**
	 * 自动开启Boss时间
	 */
	@Column(name = "automaticOpen")
	private String automaticOpen = "";

	/**
	 * 自动开启Boss时间集合
	 */
	@Transient
	private List<Long> automaticOpenList;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = GuaJiTime.getCalendar().getTime();

	@Column(name = "updateTime")
	protected Date updateTime;

	/**
	 * 0:正常,1:为删除状态
	 */
	@Column(name = "invalid")
	protected boolean invalid;

	/**
	 * 成员列表
	 */
	@Transient
	private Set<Integer> memberList;

	/**
	 * 上一次系统时间
	 */
	@Transient
	public long oldTime;

	/** 幸运值 */
	@Column(name = "luckyScore")
	protected int luckyScore;

	/** 上一次刷新幸运值时间 */
	@Column(name = "lastResetLuckyScoreTime")
	protected long lastResetLuckyScoreTime;

	@Column(name = "sendEmailNum")
	private int sendEmailNum;

	@Column(name = "sendEmailTime")
	private Date sendEmailTime;

	/**
	 * 是否勾选给会长发放邮件同意加入公会
	 */
	@Column(name = "hasCheckLeaderMail")
	protected int hasCheckLeaderMail;
	
	/**
	 * 公会是否可以改名
	 */
	@Column(name = "canChangeName")
	private boolean canChangeName = false;
	
	/**
	 * 捐献功能活跃度
	 */
	@Column(name="activeValue")
	private int activeValue = 0;
	
	
	/**
	 * 战力
	 */
	@Transient
	private int scoreValue = 0;
	
	@Transient
	private ReadWriteLock rwLock = new ReentrantReadWriteLock();
	@Transient
	private Lock rLock = rwLock.readLock();
	@Transient
	private Lock wLock = rwLock.writeLock();

	public Date getSendEmailTime() {
		return sendEmailTime;
	}

	public void setSendEmailTime(Date sendEmailTime) {
		this.sendEmailTime = sendEmailTime;
	}

	public int getSendEmailNum() {
		return sendEmailNum;
	}


	public boolean canChangeName() {
		return canChangeName;
	}

	public void setCanChangeName(boolean canChangeName) {
		this.canChangeName = canChangeName;
	}

	public void setSendEmailNum(int sendEmailNum) {
		this.sendEmailNum = sendEmailNum;
	}

	public long getLastResetLuckyScoreTime() {
		return lastResetLuckyScoreTime;
	}

	public void setLastResetLuckyScoreTime(long lastResetLuckyScoreTime) {
		this.lastResetLuckyScoreTime = lastResetLuckyScoreTime;
	}

	public AllianceEntity() {
		memberList = new HashSet<Integer>();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		if(level!=this.level) {
			postRankChangeMsg(RankType.ALLIANCE_LEVEL_RANK,this.getId(),level);
		}
		this.level = level;
		
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getJoinLimit() {
		return joinLimit;
	}

	public void setJoinLimit(int joinLimit) {
		this.joinLimit = joinLimit;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public Set<Integer> getMemberList() {
		return memberList;
	}

	public void addMember(int memberId) {
		memberList.add(memberId);
	}

	public int getBossId() {
		return bossId;
	}

	public void setBossId(int bossId) {
		this.bossId = bossId;
	}

	public String getBossJoinStr() {
		return bossJoinStr = GsonUtil.getJsonInstance().toJson(bossJoinMap);
	}

	public void setBossJoinStr(String bossJoinStr) {
		this.bossJoinStr = bossJoinStr;
	}

	public void init() {

		if (bossJoinStr != null && !"".equals(bossJoinStr) && !"null".equals(bossJoinStr)) {
			bossJoinMap = GsonUtil.getJsonInstance().fromJson(bossJoinStr, new TypeToken<ConcurrentHashMap<Integer, Integer>>() {
			}.getType());
		} else
			bossJoinMap = new ConcurrentHashMap<Integer, Integer>();

		if (bossAddProp != null && !"".equals(bossAddProp) && !"null".equals(bossAddProp)) {
			playerAddMap = GsonUtil.getJsonInstance().fromJson(bossAddProp, new TypeToken<ConcurrentHashMap<Integer, Integer>>() {
			}.getType());
		} else {
			playerAddMap = new ConcurrentHashMap<Integer, Integer>();
		}

		if (automaticOpen != null && !"".equals(automaticOpen)) {
			automaticOpenList = GsonUtil.getJsonInstance().fromJson(automaticOpen, new TypeToken<ArrayList<Long>>() {
			}.getType());
		}
		if (null == automaticOpenList) {
			automaticOpenList = new ArrayList<Long>(3);
			automaticOpenList.add(null);
			automaticOpenList.add(null);
			automaticOpenList.add(null);
		}
	}

	public Map<Integer, Integer> getBossJoinMap() {
		return bossJoinMap;
	}

	public void setBossJoinMap(Map<Integer, Integer> bossJoinMap) {
		this.bossJoinMap = bossJoinMap;
	}

	public boolean isBossOpen() {
		return bossOpen;
	}

	public void setBossOpen(boolean bossOpen) {
		this.bossOpen = bossOpen;
	}

	public int getBossOpenSize() {
		return bossOpenSize;
	}

	public void setBossOpenSize(int bossOpenSize) {
		this.bossOpenSize = bossOpenSize;
	}

	public void setBossOpenSizeAdd() {
		this.bossOpenSize++;
	}

	public long getBossMaxTime() {
		return bossMaxTime;
	}

	public void setBossMaxTime(long bossMaxTime) {
		this.bossMaxTime = bossMaxTime;
	}

	public long getBossAttTime() {
		return bossAttTime;
	}

	public void setBossAttTime(long bossAttTime) {
		this.bossAttTime = bossAttTime;
	}

	public Map<Integer, Integer> getPlayerAddMap() {
		return playerAddMap;
	}

	/**
	 * 清理加成信息
	 */
	public void clearPlayerAddInfo() {
		playerAddMap.clear();
		bossAddProp = GsonUtil.getJsonInstance().toJson(playerAddMap, new TypeToken<ConcurrentHashMap<Integer, Integer>>() {
		}.getType());
	}

	/**
	 * 清理boss加入信息
	 */
	public void clearBossJoinInfo() {
		bossJoinMap.clear();
		bossJoinStr = GsonUtil.getJsonInstance().toJson(bossJoinMap);
	}

	public void setPlayerAddMap(Map<Integer, Integer> playerAddMap) {
		this.playerAddMap = playerAddMap;
	}

	public void putPlayerAddMap(Integer playerId) {
		Integer v = playerAddMap.get(playerId);
		if (v == null)
			playerAddMap.put(playerId, 1);
		else
			playerAddMap.put(playerId, v + 1);

		bossAddProp = GsonUtil.getJsonInstance().toJson(playerAddMap, new TypeToken<ConcurrentHashMap<Integer, Integer>>() {
		}.getType());
	}

	public Integer getPlayerAddMap(Integer playerId) {
		Integer v = playerAddMap.get(playerId);
		if (v == null)
			return 0;
		return v;
	}

	public String getBossAddProp() {
		return bossAddProp = GsonUtil.getJsonInstance().toJson(playerAddMap);
	}

	public void setBossAddProp(String bossAddProp) {
		this.bossAddProp = bossAddProp;
	}

	public int getEverydayBossOpenTimes() {
		return everydayBossOpenTimes;
	}

	public void setEverydayBossOpenTimes(int everydayBossOpenTimes) {
		this.everydayBossOpenTimes = everydayBossOpenTimes;
	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public String getAutomaticOpen() {
		return automaticOpen = GsonUtil.getJsonInstance().toJson(automaticOpenList);
	}

	public void setAutomaticOpen(String automaticOpen) {
		this.automaticOpen = automaticOpen;
	}

	public List<Long> getAutomaticOpenList() {
		return automaticOpenList;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getBossHp() {
		return bossHp;
	}

	public void setBossHp(int bossHp) {
		this.bossHp = bossHp;
	}

	public long getOldTime() {
		return oldTime;
	}

	public long getCreateAllianceTime() {
		return createAllianceTime;
	}

	public void setCreateAllianceTime(long createAllianceTime) {
		this.createAllianceTime = createAllianceTime;
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

	public void setOldTime(long oldTime) {
		this.oldTime = oldTime;
	}

	public long getBossOpenTime() {
		return bossOpenTime;
	}

	public void setBossOpenTime(long bossOpenTime) {
		this.bossOpenTime = bossOpenTime;
	}

	public int getBossVitality() {
		return this.bossVitality;
	}

	/**
	 * 增加公会boss元气值
	 * 
	 * @param addVitality
	 */
	public synchronized void addBossVitality(int addVitality, Action action) {
		this.bossVitality += addVitality;
		
		
		postRankChangeMsg(RankType.ALLIANCE_VITALITY_RANK,this.getId(),this.bossVitality);
		
		// 记录今天所增加的元气
		this.curDayAddVitality += addVitality;
		GsApp.getInstance().postMsg(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.ALLIANCE_BATTLE),
				Msg.valueOf(GsConst.MsgType.ALLIANCE_VITALITY_CHANGE).pushParam(this.id, addVitality));
		notifyUpdate(true);
	}

	/**
	 * 扣除公会boss元气值
	 * 
	 * @param deductVitality
	 */
	public void deductBossVitality(int deductVitality) {
		this.bossVitality -= Math.abs(deductVitality);
		postRankChangeMsg(RankType.ALLIANCE_VITALITY_RANK,this.getId(),this.bossVitality);
		notifyUpdate(true);
	}

	/**
	 * 获取上一次时间差。。。相减 bossAtttime<0 攻击一次
	 */
	public void proAttTime() {
		if (bossAttTime > 0) {
			long rem = checkOldTime();
			bossMaxTime -= rem;
			bossAttTime -= rem;
		} else {
			oldTime = System.currentTimeMillis();
		}
	}

	/**
	 * @return 计算本次与上次时间差
	 */
	private long checkOldTime() {

		long newTime = System.currentTimeMillis();
		if (oldTime == 0) {
			oldTime = newTime;
		}
		long remTime = newTime - oldTime;
		oldTime = newTime;
		return remTime;
	}

	@Override
	public void notifyUpdate(boolean async) {

		this.getBossJoinStr();
		this.getAutomaticOpen();

		super.notifyUpdate(async);
	}

	public int getLuckyScore() {
		return luckyScore;
	}

	public void setLuckyScore(int luckyScore) {
		this.luckyScore = luckyScore;
	}

	public int getHasCheckLeaderMail() {
		return hasCheckLeaderMail;
	}

	public void setHasCheckLeaderMail(int hasCheckLeaderMail) {
		this.hasCheckLeaderMail = hasCheckLeaderMail;
	}

	public int getCurDayAddVitality() {
		return curDayAddVitality;
	}

	public void setCurDayAddVitality(int curDayAddVitality) {
		this.curDayAddVitality = curDayAddVitality;
	}

	public int getBeforeDayAddVitality() {
		return beforeDayAddVitality;
	}

	public void setBeforeDayAddVitality(int beforeDayAddVitality) {
		this.beforeDayAddVitality = beforeDayAddVitality;
	}
	
	public int getScoreValue() {
		return scoreValue;
	}

	public void setScoreValue(int scoreValue) {
		this.scoreValue = scoreValue;
	}

	/**
	 * 捐献活力值
	 * @return
	 */
	public int getActiveValue() {
		rLock.lock();
		try {
			return activeValue;
		} finally {
			rLock.unlock();
		}
		
	}
	/**
	 * 捐献活力值
	 * @param activeValue
	 */
	public void setActiveValue(int activeValue) {
		wLock.lock();
		try {
			this.activeValue = activeValue;
		} finally {
			wLock.unlock();
		}
		this.activeValue = activeValue;
	}
	
	/**
	 * @param type
	 * @param allianceId
	 * @param data
	 */
	private void postRankChangeMsg(RankType type,int allianceId,int data) {
		Msg questMsg = Msg.valueOf(GsConst.MsgType.ON_RANK_CHANGE);
		questMsg.pushParam(type);
		questMsg.pushParam(String.format("%s,%s", allianceId, data));
		GsApp.getInstance().postMsg(RankManager.getInstance().getXid(), questMsg);
	}
	
	
}
