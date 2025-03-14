package com.guaji.game.module.activity.activity193;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.guaji.game.util.GsConst.SingleBossQuestType;

/**
 * 單人強敵活動
 */
public class Activity193Status implements Comparable<Activity193Status> {
	/**
	 * 玩家ID
	 */
	private int playerId;
	
	/**
	 * 账号名称
	 */
	private String name;
	
	/**
	 * 排名信息
	 */
	private int rank;
	/**
	 * 目前攻略等級
	 */ 
	private int nowLv;
    /**
     * 已挑戰次數
     */
    private int count;
	/**
	 * 	是否以初始化活動物品
	 */
	private boolean initItem;
	 /**
     * 獎勵領取紀錄
     */
    private Set<Integer> Awardrecord;

    private Map<Integer,Long> questRecord;

	public Activity193Status() {
		this.nowLv = 1;
		this.count = 0;
        this.Awardrecord = new HashSet<>();
        this.questRecord = new HashMap<>();
	}
	
	

	public int getNowLv() {
		return nowLv;
	}



	public void setNowLv(int nowLv) {
		this.nowLv = nowLv;
	}



	public int getCount() {
		return count;
	}



	public void setCount(int count) {
		this.count = count;
	}
	
	public void addCount() {
		this.count = this.count + 1 ;
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
	
	public boolean isInitItem() {
		return initItem;
	}


	public void setInitItem(boolean initItem) {
		this.initItem = initItem;
	}

	public long getScore() {
		return getQuestRecord(SingleBossQuestType.MaxScore);
	}
	
	public void setScore(long score) {
		this.setQuestRecord(SingleBossQuestType.MaxScore,score);
	}
	
	public void addTotalScore(long score) {
		this.setQuestRecord(SingleBossQuestType.TotalScore,getQuestRecord(SingleBossQuestType.TotalScore)+score);
	}
	
	public void addChanllengeTime() {
		this.setQuestRecord(SingleBossQuestType.ChanllengeTime,getQuestRecord(SingleBossQuestType.ChanllengeTime)+1);
	}

	public Set<Integer> getAwardrecord() {
		return Awardrecord;
	}


	public void setAwardrecord(Set<Integer> awardrecord) {
		Awardrecord = awardrecord;
	}

	public void setQuestRecord(int type,long value) {
		if (questRecord.containsKey(type)) {
			questRecord.put(type, value);
		} else {
			questRecord.put(type, value);
		}
	}
	
	public long getQuestRecord(int type) {
		if (questRecord.containsKey(type)) {
			return questRecord.get(type);
		} else {
			return 0;
		}
	}
	
	public void Dailyreset() 
	{
		this.count = 0;
	}
	
	@Override
	public int compareTo(Activity193Status status) {
		// 根据积分从大到小排序
		if(this.getScore() > status.getScore()){
			return -1;
		}else if(this.getScore() < status.getScore()){
			return 1;
		}else{
			return 0;
		}
	}
	
}
