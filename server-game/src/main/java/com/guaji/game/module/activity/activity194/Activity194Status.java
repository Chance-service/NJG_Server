package com.guaji.game.module.activity.activity194;

import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

/**
 * 賽季爬塔活動
 */
/**
 * @author Tinlin_Home
 *
 */
public class Activity194Status implements Comparable<Activity194Status> {
	/**
	 * 玩家ID
	 */
	private int playerId;
	
	/**
	 * 账号名称
	 */
	private String name;
	
	/**
	 * 排名信息(排行榜用),玩家資料為(已攻略最高樓)
	 */
	private int rank;
	/**
	 * 目前攻略樓層
	 */ 
	private int nowfloor;
	/**
	 * 活動時間索引
	 */
	private int timeIndex;
	/**
	 * 完成樓層時間
	 */
	private long doneTime;
	 /**
     * 獎勵領取紀錄
     */
    private Set<Integer> Awardrecord;
    /**
     * 可選擇樓層
     */
    private boolean chooseFloor;

	public Activity194Status() {
		this.nowfloor = 1;
        this.Awardrecord = new HashSet<>();
        this.chooseFloor = false;
        this.doneTime = 0;
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



	public int getRank() {
		return rank;
	}


	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public Set<Integer> getAwardrecord() {
		return Awardrecord;
	}


	public void setAwardrecord(int floorId) {
		this.Awardrecord.add(floorId);
	}
	
	public void clearAwardrecord() {
		this.Awardrecord.clear();
	}
	
	public int getNowfloor() {
		return nowfloor;
	}


	public void setNowfloor(int nowfloor) {
		this.nowfloor = nowfloor;
	}


	public int getTimeIndex() {
		return timeIndex;
	}


	public void setTimeIndex(int timeIndex) {
		this.timeIndex = timeIndex;
	}
	
	public long getDoneTime() {
		return doneTime;
	}


	public void setDoneTime(long doneTime) {
		this.doneTime = doneTime;
	}

	public boolean isChooseFloor() {
		return chooseFloor;
	}


	public void setChooseFloor(boolean chooseFloor) {
		this.chooseFloor = chooseFloor;
	}
	
	/**
	 * 取最大完成樓層
	 * @return
	 */
	public int getDoneFloor() {
		if (!Awardrecord.isEmpty()) {
			Integer res = Collections.max(Awardrecord);
			return res;
		}
		return 0;
	}


	@Override
	public int compareTo(Activity194Status status) {
		// 根据樓層从大到小排序
		
		if (status.getNowfloor() == this.getNowfloor()) {
			
			if (status.getDoneTime() == this.getDoneTime()) {
				return this.getPlayerId() - status.getPlayerId();
			}
			
			return (int)(this.getDoneTime() - status.getDoneTime());
		}
		
		return status.getNowfloor() - this.getNowfloor();
	}
	
}
