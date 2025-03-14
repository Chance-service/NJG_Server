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
import com.guaji.game.config.DailyQuestPointCfg;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "dailyquest")
public class DailyQuestEntity extends DBEntity {
	
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
	
	@Column(name = "dailyQuestStr",columnDefinition = "varchar(1024) not null")
	private String dailyQuestStr = "";
	
	@Column(name = "dailyPointStateStr",columnDefinition = "varchar(1024) not null")
	private String dailyPointStateStr = "";
	
	@Column(name = "dailyPoint")
	private int dailyPoint;
	
	@Transient
	private Map<Integer,DailyQuestItem> dailyQuestMap ;
	@Transient
	private Map<Integer,Integer> dailyPointState ;


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
	public DailyQuestEntity()
	{
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.dailyQuestMap = new HashMap<Integer,DailyQuestItem>();
		this.dailyPointState = new HashMap<Integer,Integer>();
		
		Map<Object,DailyQuestPointCfg> map = ConfigManager.getInstance().getConfigMap(DailyQuestPointCfg.class);
		
		for(DailyQuestPointCfg dailyQuestPointCfg: map.values())
		{
			this.dailyPointState.put(dailyQuestPointCfg.getPointNumber(), 0);
		}

	}
	
	/**
	 * 字符串存储转换为内存数据
	 */
	public void convert() {
		if(this.getDailyQuestStr() != null) {
			this.dailyQuestMap = GsonUtil.getJsonInstance().fromJson(this.getDailyQuestStr(),new TypeToken<HashMap<Integer,DailyQuestItem>>() {}.getType());
		}
		
		if(this.dailyPointStateStr != null)
		{
			this.dailyPointState = GsonUtil.getJsonInstance().fromJson(this.dailyPointStateStr,new TypeToken<HashMap<Integer,Integer>>() {}.getType());
		}
	}
	
	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert()
	{
		this.setDailyQuestStr(GsonUtil.getJsonInstance().toJson(this.dailyQuestMap));
		this.dailyPointStateStr = GsonUtil.getJsonInstance().toJson(this.dailyPointState);

		return;
	}
	
	/**
	 * 0未完成 1完成未领取 2已领取
	 * @param flag
	 */
	public void modifyDailyPointState(int pointNumber,int flag)
	{
		this.dailyPointState.put(pointNumber, flag);
	}
	
	public Map<Integer,Integer> getDailyPointState()
	{
		return this.dailyPointState;
	}
	
	
	/**
	 * 获取任务数据
	 * @return
	 */
	public Map<Integer,DailyQuestItem> getDailyQuestMap()
	{
		return this.dailyQuestMap;
	}
	
	/**
	 * 添加一个任务数据
	 * 
	 */
	public void addDailyQuest(int id, DailyQuestItem item)
	{
		this.dailyQuestMap.put(id, item);
	}
	
	/**
	 * 获取任务存储字符串
	 * @return
	 */
	private String getDailyQuestStr() {
		return dailyQuestStr;
	}

	/**
	 * 设置存储字符串
	 * @param expeditionTaskStr
	 */
	private void setDailyQuestStr(String dailyQuestStr) {
		this.dailyQuestStr = dailyQuestStr;
	}
	
	/**
	 * 获取该玩家的日常任务数据存储对象
	 * @param playerId
	 * @return
	 */
	public static DailyQuestEntity valueOf(int playerId) {
		DailyQuestEntity dailyQuestEntity = new DailyQuestEntity();
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
		for(Map.Entry<Integer,DailyQuestItem> entry:this.dailyQuestMap.entrySet())
		{
			int id = entry.getValue().getId();
			DailyQuestCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(DailyQuestCfg.class, id);
			
			if(dailyQuestCfg == null){
				continue;
			}
			
			if(dailyQuestCfg.getType() == GsConst.DailyQuestType.ON_RECHARAGE)//单笔不限额
			{
				if(entry.getValue().getQuestStatus() != 1)
				{
					entry.getValue().setQuestStatus(1);
					entry.getValue().setCompleteCount(1);
					continue;
				}
			}
			
			if(dailyQuestCfg.getType() == GsConst.DailyQuestType.ON_RECHARAGE_LIMILT)//限制额度
			{
				entry.getValue().setCompleteCount(entry.getValue().getCompleteCount()+count);
				
				if(entry.getValue().getCompleteCount() >= dailyQuestCfg.getCompleteCountCfg())
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
	public DailyQuestItem getDailyQuest(int id)
	{
		return this.dailyQuestMap.get(id);
	}

	/**
	 * 重置刷新
	 */
	public void reset() 
	{
		this.dailyQuestMap.clear();
		this.dailyPoint = 0;
		this.dailyPointState.clear();
		
		Map<Object,DailyQuestPointCfg> map = ConfigManager.getInstance().getConfigMap(DailyQuestPointCfg.class);
		
		for(DailyQuestPointCfg dailyQuestPointCfg: map.values())
		{
			this.dailyPointState.put(dailyQuestPointCfg.getPointNumber(), 0);
		}

	}
	
	public int getDailyPoint()
	{
		return this.dailyPoint;
	}
	
	public void setDailyPoint(int point)
	{
		this.dailyPoint = point;
		return;
	}

}
