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

import org.guaji.config.ConfigManager;
import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.config.DailyQuestCfg;
import com.guaji.game.config.SevenDayQuestCfg;
import com.guaji.game.config.SevenDayQuestPointCfg;
import com.guaji.game.protocol.Const.QuestState;
import com.guaji.game.util.GsonUtil;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年1月24日 下午5:02:59 类说明
 */

@Entity
@Table(name = "sevendayquest")
@SuppressWarnings("serial")
public class SevenDayQuestEntity extends DBEntity {

	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;
	
	@Column(name = "awardstate")
	private boolean awardstate;

	@Column(name = "questStr")
	private String questStr = "";

	@Column(name = "pointStateStr")
	private String pointStateStr = "";

	@Column(name = "point")
	private int point;

	@Transient
	private Map<Integer, SevenDayQuestItem> questMap;
	@Transient
	private Map<Integer, Integer> pointState;

	@Override
	public Date getCreateTime() {
		// TODO Auto-generated method stub
		return createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		// TODO Auto-generated method stub
		this.createTime = createTime;

	}

	@Override
	public Date getUpdateTime() {
		// TODO Auto-generated method stub
		return this.updateTime;
	}

	@Override
	public void setUpdateTime(Date updateTime) {
		// TODO Auto-generated method stub
		this.updateTime = updateTime;

	}

	@Override
	public boolean isInvalid() {
		// TODO Auto-generated method stub
		return invalid;
	}

	@Override
	public void setInvalid(boolean invalid) {
		// TODO Auto-generated method stub
		this.invalid = invalid;

	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getQuestStr() {
		return questStr;
	}

	public void setQuestStr(String questStr) {
		this.questStr = questStr;
	}

	public String getPointStateStr() {
		return pointStateStr;
	}

	public void setPointStateStr(String pointStateStr) {
		this.pointStateStr = pointStateStr;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public boolean isAwardstate() {
		return awardstate;
	}

	public void setAwardstate(boolean awardstate) {
		this.awardstate = awardstate;
	}

	public Map<Integer, SevenDayQuestItem> getQuestMap() {
		return questMap;
	}

	public void setQuestMap(Map<Integer, SevenDayQuestItem> questMap) {
		this.questMap = questMap;
	}

	public Map<Integer, Integer> getPointState() {
		return pointState;
	}

	public void setPointState(Map<Integer, Integer> pointState) {
		this.pointState = pointState;
	}

	/**
	 * 构造初始化
	 */
	public SevenDayQuestEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.questMap = new HashMap<Integer, SevenDayQuestItem>();
		this.pointState = new HashMap<Integer, Integer>();
		this.awardstate=false;
		
		Map<Object, SevenDayQuestCfg> configMap = ConfigManager.getInstance().getConfigMap(SevenDayQuestCfg.class);
		for (SevenDayQuestCfg cfg : configMap.values()) {
			if (questMap.containsKey(cfg.getId())) {
				continue;
			}
			SevenDayQuestItem questItem = new SevenDayQuestItem();
			questItem.setId(cfg.getId());
			questItem.setStatus(QuestState.ING_VALUE);
			questItem.setFinishNum(0);
			questItem.initCounter(this);
			questMap.put(cfg.getId(), questItem);
		}

		Map<Object, SevenDayQuestPointCfg> map = ConfigManager.getInstance().getConfigMap(SevenDayQuestPointCfg.class);

		for (SevenDayQuestPointCfg dailyQuestPointCfg : map.values()) {
			if(dailyQuestPointCfg.getPointNumber()>0)
				this.pointState.put(dailyQuestPointCfg.getPointNumber(), 0);
			
			
		}

	}

	/**
	 * 字符串存储转换为内存数据
	 */
	public void convert() {

		if (this.getQuestStr() != null) {
			this.questMap = GsonUtil.getJsonInstance().fromJson(this.getQuestStr(),
					new TypeToken<HashMap<Integer, SevenDayQuestItem>>() {
					}.getType());
		}

		if (this.pointStateStr != null) {
			this.pointState = GsonUtil.getJsonInstance().fromJson(this.pointStateStr,
					new TypeToken<HashMap<Integer, Integer>>() {
					}.getType());
		}
		
		for (SevenDayQuestItem questItem : this.questMap.values()) {
			questItem.initCounter(this);
		}
	}

	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert() {
		
		this.setQuestStr(GsonUtil.getJsonInstance().toJson(this.questMap));
		this.pointStateStr = GsonUtil.getJsonInstance().toJson(this.pointState);

		return;
	}

	/**
	 * 查漏补缺从数据表
	 */
	private void fixQuestFromConfig() {

		Map<Object, SevenDayQuestCfg> configMap = ConfigManager.getInstance().getConfigMap(SevenDayQuestCfg.class);

		boolean flag = false;

		for (SevenDayQuestCfg cfg : configMap.values()) {
			if (questMap.containsKey(cfg.getId())) {
				continue;
			}

			SevenDayQuestItem questItem = new SevenDayQuestItem();
			questItem.setId(cfg.getId());
			if(cfg.getNeedCount()==0)
				questItem.setStatus(QuestState.FINISHED_VALUE);
			else
				questItem.setStatus(QuestState.ING_VALUE);
			
			questItem.setFinishNum(0);
			questItem.initCounter(this);
			questMap.put(cfg.getId(), questItem);

			flag = true;
		}

		if (flag) {
			this.reConvert();
		}
		return;
	}

	// 加载任务
	public void loadQuest() {
		this.convert();
		fixQuestFromConfig();
		return;
	}

	public void update() {
		reConvert();
		this.notifyUpdate(false);
		return;
	}
	/**
	 * 0未完成 1完成未领取 2已领取
	 * 
	 * @param flag
	 */
	public void modifyPointState(int pointNumber, int flag) {
		this.pointState.put(pointNumber, flag);
	}


	/**
	 * 添加一个任务数据
	 * 
	 */
	public void addDailyQuest(int id, SevenDayQuestItem item) {
		this.questMap.put(id, item);
	}

	/**
	 * 获取该玩家的日常任务数据存储对象
	 * 
	 * @param playerId
	 * @return
	 */
	public static SevenDayQuestEntity valueOf(int playerId) {
		SevenDayQuestEntity dailyQuestEntity = new SevenDayQuestEntity();
		dailyQuestEntity.playerId = playerId;
		dailyQuestEntity.reConvert();

		return dailyQuestEntity;
	}

	/**
	 * 离线数据更新
	 * 
	 * @param count
	 * @return
	 */
	public boolean offlineOnRecharge(int count) {
		for (Map.Entry<Integer, SevenDayQuestItem> entry : this.questMap.entrySet()) {
			int id = entry.getValue().getId();
			
			DailyQuestCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(DailyQuestCfg.class, id);

			if (dailyQuestCfg == null) {
				continue;
			}

			if (dailyQuestCfg.getType() == 2)// 单笔不限额
			{
				if (entry.getValue().getStatus() != 1) {
					entry.getValue().setStatus(1);
					entry.getValue().setFinishNum(0);
					continue;
				}
			}

			if (dailyQuestCfg.getType() == 3)// 限制额度
			{
				entry.getValue().setFinishNum(entry.getValue().getFinishNum() + count);

				if (entry.getValue().getFinishNum() >= dailyQuestCfg.getCompleteCountCfg()) {
					entry.getValue().setStatus(1);
				}

				continue;

			}

		}

		this.reConvert();
		this.notifyUpdate(false);

		return true;
	}

	/**
	 * 获取某个对象
	 * 
	 * @param id
	 * @return
	 */
	public SevenDayQuestItem getQuest(int id) {
		return this.questMap.get(id);
	}

}
