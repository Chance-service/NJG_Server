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
import com.guaji.game.config.WeeklyQuestCfg;
import com.guaji.game.config.WeeklyQuestPointCfg;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "weeklyquest")
public class WeeklyQuestEntity extends DBEntity {
	
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;
	
	@Column(name = "createTime")
	private Date  createTime ;
	
	@Column(name = "updateTime")
	private Date  updateTime ;
	
	@Column(name = "invalid")
	private boolean invalid ;
	
	@Column(name = "QuestStr" ,columnDefinition = "varchar(1024) not null")
	private String QuestStr = "";
	
	@Column(name = "PointStateStr",columnDefinition = "varchar(1024) not null")
	private String PointStateStr = "";
	
	@Column(name = "weeklyPoint")
	private int weeklyPoint;
	
	@Transient
	private Map<Integer,WeeklyQuestItem> QuestMap ;
	@Transient
	private Map<Integer,Integer> PointState ;


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
	
	/**
	 * 构造初始化
	 */
	public WeeklyQuestEntity()
	{
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.QuestMap = new HashMap<Integer,WeeklyQuestItem>();
		this.PointState = new HashMap<Integer,Integer>();
		
		Map<Object,WeeklyQuestPointCfg> map = ConfigManager.getInstance().getConfigMap(WeeklyQuestPointCfg.class);
		
		for(WeeklyQuestPointCfg QuestPointCfg: map.values())
		{
			this.PointState.put(QuestPointCfg.getPointNumber(), 0);
		}

	}
	
	/**
	 * 字符串存储转换为内存数据
	 */
	public void convert() {
		if(this.getQuestStr() != null) {
			this.QuestMap = GsonUtil.getJsonInstance().fromJson(this.getQuestStr(),new TypeToken<HashMap<Integer,WeeklyQuestItem>>() {}.getType());
		}
		
		if(this.PointStateStr != null)
		{
			this.PointState = GsonUtil.getJsonInstance().fromJson(this.PointStateStr,new TypeToken<HashMap<Integer,Integer>>() {}.getType());
		}
	}
	
	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert()
	{
		this.setQuestStr(GsonUtil.getJsonInstance().toJson(this.QuestMap));
		this.PointStateStr = GsonUtil.getJsonInstance().toJson(this.PointState);

		return;
	}
	
	/**
	 * 0未完成 1完成未领取 2已领取
	 * @param flag
	 */
	public void modifyPointState(int pointNumber,int flag)
	{
		this.PointState.put(pointNumber, flag);
	}
	
	public Map<Integer,Integer> getPointState()
	{
		return this.PointState;
	}
	
	
	/**
	 * 获取任务数据
	 * @return
	 */
	public Map<Integer,WeeklyQuestItem> getQuestMap()
	{
		return this.QuestMap;
	}
	
	/**
	 * 添加一个任务数据
	 * 
	 */
	public void addQuest(int id, WeeklyQuestItem item)
	{
		this.QuestMap.put(id, item);
	}
	
	/**
	 * 获取任务存储字符串
	 * @return
	 */
	private String getQuestStr() {
		return QuestStr;
	}

	/**
	 * 设置存储字符串
	 * @param expeditionTaskStr
	 */
	private void setQuestStr(String QuestStr) {
		this.QuestStr = QuestStr;
	}
	
	/**
	 * 获取该玩家的日常任务数据存储对象
	 * @param playerId
	 * @return
	 */
	public static WeeklyQuestEntity valueOf(int playerId) {
		WeeklyQuestEntity dailyQuestEntity = new WeeklyQuestEntity();
		dailyQuestEntity.playerId = playerId;
		dailyQuestEntity.reConvert();
		
		return dailyQuestEntity;
	}
	
	/**
	 * 离线数据更新
	 * @param count
	 * @return
	 */
	public boolean offlineOnRecharge(int count) 
	{						
		for(Map.Entry<Integer,WeeklyQuestItem> entry:this.QuestMap.entrySet())
		{
			int id = entry.getValue().getId();
			WeeklyQuestCfg QuestCfg = ConfigManager.getInstance().getConfigByKey(WeeklyQuestCfg.class, id);
			
			if(QuestCfg == null){
				continue;
			}
			
			if(QuestCfg.getType() == GsConst.DailyQuestType.ON_RECHARAGE)//单笔不限额
			{
				if(entry.getValue().getQuestStatus() != 1)
				{
					entry.getValue().setQuestStatus(1);
					entry.getValue().setCompleteCount(1);
					continue;
				}
			}
			
			if(QuestCfg.getType() == GsConst.DailyQuestType.ON_RECHARAGE_LIMILT)//限制额度
			{
				entry.getValue().setCompleteCount(entry.getValue().getCompleteCount()+count);
				
				if(entry.getValue().getCompleteCount() >= QuestCfg.getCompleteCountCfg())
				{
					entry.getValue().setQuestStatus(1);
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
	 * @param id
	 * @return
	 */
	public WeeklyQuestItem getQuest(int id)
	{
		return this.QuestMap.get(id);
	}

	/**
	 * 重置刷新
	 */
	public void reset() 
	{
		this.QuestMap.clear();
		this.weeklyPoint = 0;
		this.PointState.clear();
		
		Map<Object,WeeklyQuestPointCfg> map = ConfigManager.getInstance().getConfigMap(WeeklyQuestPointCfg.class);
		
		for(WeeklyQuestPointCfg dailyQuestPointCfg: map.values())
		{
			this.PointState.put(dailyQuestPointCfg.getPointNumber(), 0);
		}

	}
	
	public int getPoint()
	{
		return this.weeklyPoint;
	}
	
	public void setPoint(int point)
	{
		this.weeklyPoint = point;
		return;
	}

}
