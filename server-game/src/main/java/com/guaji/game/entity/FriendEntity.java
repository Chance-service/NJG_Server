package com.guaji.game.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsonUtil;

/**
 * 好友实体存储
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "friend")
public class FriendEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;
	
	@Column(name = "friendIds")
	private String friendIds;

	@Transient
	private Set<Integer> friendIdSet;
	
	@Column(name = "applyFriendIds",columnDefinition = "varchar(2048) not null")
	private String applyFriendIds;
	
	@Transient
	private Set<Integer> applyFriendIdSet;
	
	@Column(name = "shieldMapStr",columnDefinition = "varchar(2048) not null")
	private String shieldMapStr ;
	
	@Transient
	private Map<Integer, Date> shieldMap ;
	
	@Column(name = "pointMapStr",columnDefinition = "varchar(2048) not null")
	private String pointMapStr ;
	
	@Transient
	private ConcurrentHashMap<Integer, Integer> pointMap ;
	
	@Column(name = "dailyMsgPlayerStr",columnDefinition = "varchar(2048) not null")
	private String dailyMsgPlayerStr ;
	
	@Transient
	private Set<Integer> dailyMsgPlayerId ;
	
	@Column(name = "dailyGiftStr",columnDefinition = "varchar(2048) not null")
	private String dailyGiftStr ;
	
	@Transient
	private Set<Integer> dailyGiftPlayerId ;
	
	@Column(name="bindingState", nullable = false)
	private byte bindingState;
	
	@Column(name = "createTime")
	private Date  createTime ;
	
	@Column(name = "updateTime")
	private Date  updateTime ;
	
	@Column(name = "invalid")
	private boolean invalid ;
	
	public FriendEntity() {
		this.friendIdSet = new HashSet<>();
		this.applyFriendIdSet = new LinkedHashSet<>();
		this.shieldMap = new HashMap<Integer, Date>();
		this.dailyMsgPlayerId = new HashSet<>();
		this.dailyGiftPlayerId = new HashSet<>();
		this.pointMap = new ConcurrentHashMap<Integer, Integer>();
		this.shieldMapStr = GsonUtil.getJsonInstance().toJson(shieldMap);
		this.pointMapStr = GsonUtil.getJsonInstance().toJson(pointMap);
		this.dailyMsgPlayerStr = GsonUtil.getJsonInstance().toJson(dailyMsgPlayerId);
		this.dailyGiftStr = GsonUtil.getJsonInstance().toJson(dailyGiftPlayerId);
		convertFriendSetToStr();
		convertApplyFriendSetToStr();
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public Set<Integer> getFriendIdSet() {
		return friendIdSet;
	}

	public void setFriendIdSet(Set<Integer> friendIdSet) {
		this.friendIdSet = friendIdSet;
	}
	
	public byte getBindingState() {
		return bindingState;
	}

	public void setBindingState(byte bindingState) {
		this.bindingState = bindingState;
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
	public int getFriendCount() {
		return friendIdSet.size();
	}

	public void addFriendId(int targetId) {
		this.friendIdSet.add(targetId);
		convertFriendSetToStr();
	}
	
	/**
	 * playerIdSet 转化为 String
	 */
	public void convertFriendSetToStr() {
		this.friendIds = GameUtil.join(friendIdSet, ",");
	}

	/**
	 * 删除好友
	 * @param targetId
	 */
	public void removeFriend(int targetId) {
		this.friendIdSet.remove(Integer.valueOf(targetId));
		convertFriendSetToStr();
	}

	public boolean contains(int playerId) {
		return this.friendIdSet.contains(playerId);
	}
	
	/**
	 * applyFriendIdSet 转化为 String
	 */
	public void convertPointMapToStr() {
		this.pointMapStr = GsonUtil.getJsonInstance().toJson(pointMap);
	}
	
	public void addPoint(int playerId , int point){
		if (pointMap.containsKey(playerId)) {
			pointMap.replace(playerId, pointMap.get(playerId)+point);
		} else {
			pointMap.put(playerId, point);
		}
		convertPointMapToStr();
	}
	
	public void removePoint(int playerId) {
		pointMap.remove(playerId);
		convertPointMapToStr();
	}
	
	public void clearPoint() {
		pointMap.clear();
		convertPointMapToStr();
	}
	
	public int getGiftPoint(int playerId) {
		if (pointMap.containsKey(playerId)) {
			return pointMap.get(playerId);
		}
		return 0;
	}
	
	public Map<Integer,Integer> getGiftPoint() {
		return pointMap;
	}
	
	/**
	 * 设置屏蔽关系
	 * @param playerId
	 */
	public void setShield(int playerId){
		Calendar calendar = GuaJiTime.getCalendar();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		shieldMap.put(playerId, calendar.getTime());
	}
	
	public void cancelShield(int playerId) {
		shieldMap.remove(playerId);
	}
	
	
	public Set<Integer> getAllShieldIds() {
		return this.shieldMap.keySet();
	}

	/**
	 * 是否屏蔽
	 * @param playerId
	 * @return
	 */
	public boolean isShield(int playerId) {
		if(shieldMap.containsKey(playerId)) {
			Date endShieldDate = shieldMap.get(playerId);
			if(endShieldDate != null) {
				if(GuaJiTime.getMillisecond() < endShieldDate.getTime()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 是否允许今日发言
	 * @param playerId
	 * @return
	 */
	public boolean isDailySendMsgAllow(int playerId) {
		if(!dailyMsgPlayerId.contains(playerId) && dailyMsgPlayerId.size() >= 50) {
			return false;
		}
		return true;
	}

	public String getShieldMapStr() {
		return shieldMapStr;
	}

	public void setShieldMapStr(String shieldMapStr) {
		this.shieldMapStr = shieldMapStr;
	}
	
	public String getPointMapStr() {
		return pointMapStr;
	}

	public void setPointMapStr(String pointMapStr) {
		this.pointMapStr = pointMapStr;
	}
	
	/**
	 * 数据存储转化成内存操作对象
	 */
	public void convert() {
		this.friendIdSet.clear();
		this.shieldMap.clear();
		this.dailyMsgPlayerId.clear();
		
		String[] friendIdStrs = friendIds.split(",");
		for(String friendIdStr : friendIdStrs) {
			if(friendIdStr.length() > 0) {
				this.friendIdSet.add(Integer.valueOf(friendIdStr));
			}
		}
		
		applyFriendIdSet.clear();
		String[] applyFriendIdStrs = applyFriendIds.split(",");
		for(String friendIdStr : applyFriendIdStrs) {
			if(friendIdStr.length() > 0) {
				this.applyFriendIdSet.add(Integer.valueOf(friendIdStr));
			}
		}
		
		this.shieldMap = GsonUtil.getJsonInstance().fromJson(this.shieldMapStr,new TypeToken<HashMap<Integer, Long>>() {}.getType());
		
		this.pointMap = new ConcurrentHashMap<>(GsonUtil.getJsonInstance().fromJson(this.pointMapStr,new TypeToken<HashMap<Integer, Integer>>() {}.getType()));
		if (pointMap == null) {
			this.pointMap = new ConcurrentHashMap<>();
		}
		
		this.dailyMsgPlayerId = GsonUtil.getJsonInstance().fromJson(this.dailyMsgPlayerStr,new TypeToken<HashSet<Integer>>() {}.getType());
		if(this.dailyMsgPlayerId == null) {
			this.dailyMsgPlayerId = new HashSet<>();
		}
		
		this.dailyGiftPlayerId = GsonUtil.getJsonInstance().fromJson(this.dailyGiftStr,new TypeToken<HashSet<Integer>>() {}.getType());
		if(this.dailyGiftPlayerId == null) {
			this.dailyGiftPlayerId = new HashSet<>();
		}
		
		//fix
		if (this.friendIdSet.contains(0)) {
			this.friendIdSet.remove(0);
			convertFriendSetToStr();
			notifyUpdate();
		}
	}
	
	/**
	 * 添加今日发言对象
	 * @param playerId
	 */
	public void addMsgPlayerId(int playerId) {
		this.dailyMsgPlayerId.add(playerId);
		
		this.dailyMsgPlayerStr = GsonUtil.getJsonInstance().toJson(this.dailyMsgPlayerId);
	}
	
	/**
	 * 添加今日送禮對象
	 * @param playerId
	 */
	public void addGiftPlayerId(int playerId) {
		this.dailyGiftPlayerId.add(playerId);
		
		this.dailyGiftStr = GsonUtil.getJsonInstance().toJson(this.dailyGiftPlayerId);
	}
	
	public boolean isAlreadyGift(int playerId) {
		return dailyGiftPlayerId.contains(playerId);
	}

	/**
	 * 刷新状态
	 */
	public void refresh() {
		boolean changed = false;
		Iterator<Entry<Integer, Date>> iter = this.shieldMap.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<Integer, Date> entry = iter.next();
			Date endShieldDate = entry.getValue();
			if(endShieldDate != null) {
				if(GuaJiTime.getMillisecond() < endShieldDate.getTime()) {
					iter.remove();
					changed = true;
				}
			}
		}
		this.shieldMapStr = GsonUtil.getJsonInstance().toJson(shieldMap);
		if (changed) {
			this.notifyUpdate(true);
		}
	}

	public String getDailyMsgPlayerStr() {
		return dailyMsgPlayerStr;
	}

	public void setDailyMsgPlayerStr(String dailyMsgPlayerStr) {
		this.dailyMsgPlayerStr = dailyMsgPlayerStr;
	}
	
	public String getDailyGiftStr() {
		return dailyGiftStr;
	}

	public void setDailyGiftStr(String dailyGiftStr) {
		this.dailyGiftStr = dailyGiftStr;
	}

	public int getShieldSize() {
		return this.shieldMap.size();
	}
	
	public int getPointSize() {
		return this.pointMap.size();
	}

	public void reset() {
		this.dailyMsgPlayerId.clear();
		this.dailyMsgPlayerStr = GsonUtil.getJsonInstance().toJson(this.dailyMsgPlayerId);
		this.dailyGiftPlayerId.clear();
		this.dailyGiftStr = GsonUtil.getJsonInstance().toJson(this.dailyGiftPlayerId);
		clearPoint();
	}
	
	/**
	 * applyFriendIdSet 转化为 String
	 */
	public void convertApplyFriendSetToStr() {
		this.applyFriendIds = GameUtil.join(applyFriendIdSet, ",");
	}
	
	public void addApplyFriendId(int targetId) {
		this.applyFriendIdSet.add(targetId);
		convertApplyFriendSetToStr();
	}
	/**
	 * 删除申请好友
	 * @param targetId
	 */
	public boolean removeApplyFriend(int targetId) {
		boolean succ = this.applyFriendIdSet.remove(Integer.valueOf(targetId));
		convertApplyFriendSetToStr();
		return succ;
	}
	public boolean isApplyFriend(int playerId) {
		return this.applyFriendIdSet.contains(playerId);
	}
	public Set<Integer> getApplyFriendIdSet() {
		return applyFriendIdSet;
	}
	public boolean isFriend(int playerId) {
		return this.friendIdSet.contains(playerId);
	}
	public int getapplyFriendCount() {
		return applyFriendIdSet.size();
	}
}
