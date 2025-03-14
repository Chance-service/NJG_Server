package com.guaji.game.module.activity.activity191;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.guaji.game.entity.DailyQuestItem;

/**
 * 免費召喚抽加強版
 */
public class Activity191Status {
	/**
	 * 已經闖過關卡
	 */ 
	private int PassStage;
	/**
	 * 	是否以初始化活動物品
	 */
	private boolean initItem;
	 /**
     * 獎勵領取紀錄
     */
    private Set<Integer> Awardrecord;
    /**
     * 每日任務紀錄
     */
	private Map<Integer,DailyQuestItem> dailyQuestMap ;
	/**
	 * 每日活動領獎紀錄
	 */
	private Set<Integer> dailyPoint ;
	/**
	 * 活動點
	 */
	private int point;


	public Activity191Status() {
		this.PassStage = 0;
		this.initItem = false;
        this.Awardrecord = new HashSet<>();
        this.dailyQuestMap = new HashMap<>();
        this.dailyPoint = new HashSet<>();
        this.point = 0;
	}


	public int getPassStage() {
		return PassStage;
	}


	public void setPassStage(int passStage) {
		PassStage = passStage;
	}


	public boolean isInitItem() {
		return initItem;
	}


	public void setInitItem(boolean initItem) {
		this.initItem = initItem;
	}


	public Set<Integer> getAwardrecord() {
		return Awardrecord;
	}


	public void setAwardrecord(Set<Integer> awardrecord) {
		Awardrecord = awardrecord;
	}


	public Map<Integer, DailyQuestItem> getDailyQuestMap() {
		return dailyQuestMap;
	}


	public void setDailyQuestMap(Map<Integer, DailyQuestItem> dailyQuestMap) {
		this.dailyQuestMap = dailyQuestMap;
	}


	public Set<Integer> getDailyPoint() {
		return dailyPoint;
	}


	public void setDailyPoint(Set<Integer> dailyPoint) {
		this.dailyPoint = dailyPoint;
	}


	public int getPoint() {
		return point;
	}


	public void setPoint(int point) {
		this.point = point;
	}
	
	/**
	 * 添加一个任务数据
	 * 
	 */
	public void addDailyQuest(int id, DailyQuestItem item)
	{
		this.dailyQuestMap.put(id, item);
	}
	
	public void Dailyreset() 
	{
		this.dailyQuestMap.clear();
		this.point = 0;
		this.dailyPoint.clear();
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
}
