package com.guaji.game.module.activity.activity175;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.os.GuaJiTime;

import com.guaji.game.entity.DailyQuestItem;

public class Activity175Status {
	/**
	 * 團隊ID
	 */
	private int team;
	/**
	 * 今日最高分數
	 */
    private int score;
    /**
       * 獲得分數時間
     */
    private long scoreTime;
    /**
       * 季賽最高分
     */
    private int maxScore;
    /**
      * 參加 賽季時間(用來重置分數)
     */
    private long joinTime;
    /**
     * 已使用免費次數
     */
    private int useFree;
    /**
     * 已使用付費次數
     */
    private int usePay;
    /**
        * 最後使用免費時間
     */
    private Date useFreeDate;
    /**
       * 遊戲終結時間
     */
    private long gameEndTime;
    /**
     * 使用增加時間
     */
    private boolean addTime;
    /**
     * 使用填充Fever bar
     */
    private boolean addbar;
    /**
     * 使用抵銷
     */
    private boolean offset;
    /**
     * 使用增益
     */
    private boolean addGain;
    /**
     * 活動任務<type,count>
     */
    private Map<Integer,Integer> MissionCounter;
    /**
     * 獎勵領取紀錄
     */
    private Map<Integer,Set<Integer>> Awardrecord;
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
	/**
	 * 
	 */
	private List<Integer> historyList;

    public Activity175Status() {
    	this.team = 0;
        this.score = 0;
        this.scoreTime = GuaJiTime.getMillisecond();
        this.maxScore = 0;
        this.joinTime = 0;
        this.useFree = 0;
        this.useFreeDate =GuaJiTime.getAM0Date();
        this.gameEndTime = 0;
        this.addTime = false;
        this.addbar = false;
        this.offset = false;
        this.addGain = false;
        this.MissionCounter = new HashMap<>();
        this.Awardrecord = new HashMap<>();
        this.dailyQuestMap = new HashMap<>();
        this.dailyPoint = new HashSet<>();
        this.point = 0;
        this.usePay = 0;
        this.historyList = new ArrayList<>();
    }
    
    
    public int getTeam() {
		return team;
	}


	public void setTeam(int team) {
		this.team = team;
	}

	public int getScore() {    	
		return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getScoreTime() {
        return scoreTime;
    }

    public void setScoreTime(long scoreTime) {
        this.scoreTime = scoreTime;
    }

	public int getMaxScore() {
		
		maxScore = 0;
		historyList.sort(Comparator.reverseOrder());
		int index = 0;
		// 最高分數為歷史分數最高前三個相加
		for (Integer score :historyList) {
			if (index >= 3) {
				break;
			}
			maxScore = maxScore + score;
			index++;
		}
		
		return maxScore;
	}

	public void addHistory(int score) {
		historyList.add(score);
	}
	
	
	
	public List<Integer> getHistoryList() {
		return historyList;
	}

	public long getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(long joinTime) {
		this.joinTime = joinTime;
	}


	public int getUseFree() {
		if (GuaJiTime.isToday(getUseFreeDate())) {
			return useFree;
		}
		return 0;
	}

	public void incUseFree() {
		this.useFree = getUseFree()+1;
		setUseFreeDate(GuaJiTime.getAM0Date());
	}


	public Date getUseFreeDate() {
		return useFreeDate;
	}


	public void setUseFreeDate(Date useFreeDate) {
		this.useFreeDate = useFreeDate;
	}


	public long getGameEndTime() {
		return gameEndTime;
	}


	public void setGameEndTime(long gameEndTime) {
		this.gameEndTime = gameEndTime;
	}


	public boolean isAddTime() {
		return addTime;
	}


	public void setAddTime(boolean addTime) {
		this.addTime = addTime;
	}


	public boolean isAddbar() {
		return addbar;
	}


	public void setAddbar(boolean addbar) {
		this.addbar = addbar;
	}


	public boolean isOffset() {
		return offset;
	}


	public void setOffset(boolean offset) {
		this.offset = offset;
	}


	public boolean isAddGain() {
		return addGain;
	}


	public void setAddGain(boolean addGain) {
		this.addGain = addGain;
	}


	public Map<Integer, Integer> getMissionCounter() {
		return MissionCounter;
	}
	
	public int getMissionCountByType(int type) {
		if (MissionCounter.containsKey(type)) {
			return MissionCounter.get(type);
		}
		return 0;
	}

	public Map<Integer, Set<Integer>> getAwardrecord() {
		return Awardrecord;
	}
	
	public boolean isAlreadyGot(int type , int count) {
		if (Awardrecord.containsKey(type)) {
			return Awardrecord.get(type).contains(count);
		}
		return false;
	}
	
	public void addAwardrecord(int type ,int count ) {
		if (Awardrecord.containsKey(type)) {
			Awardrecord.get(type).add(count);
		} else {
			Set<Integer> aSet = new HashSet<>();
			aSet.add(count);
			Awardrecord.put(type,aSet);
		}
		
	}


	public Map<Integer, DailyQuestItem> getDailyQuestMap() {
		return dailyQuestMap;
	}


	public Set<Integer> getDailyPoint() {
		return dailyPoint;
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
		this.usePay = 0;
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


	public int getUsePay() {
		return usePay;
	}


	public void setUsePay(int usePay) {
		this.usePay = usePay;
	}
	
	public void incUsePay() {
		this.usePay = this.usePay + 1 ;
	}
	
}
