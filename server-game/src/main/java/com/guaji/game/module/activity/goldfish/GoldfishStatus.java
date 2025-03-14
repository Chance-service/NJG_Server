package com.guaji.game.module.activity.goldfish;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.guaji.os.GuaJiTime;

/**
 * 捞金鱼活动数据
 */
public class GoldfishStatus implements Comparable<GoldfishStatus> {

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
	 * 每日免费次数
	 */
	private int freeTimes;

	/**
	 * 进入奖池次数<key:奖池类型 value:进入次数>
	 */
	private Map<Integer, Integer> prizePool;

	/**
	 * 当前首次积分翻倍时间
	 */
	private long doubledDate;

	/**
	 * 获得的积分
	 */
	private int score;

	/**
	 * 获得过的鱼种
	 */
	private Set<Integer> fishId;
	

	public GoldfishStatus() {

		prizePool = new HashMap<Integer, Integer>();
		fishId = new LinkedHashSet<Integer>();
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

	public int getFreeTimes() {
		return freeTimes;
	}

	public void setFreeTimes(int freeTimes) {
		this.freeTimes = freeTimes;
	}

	public Map<Integer, Integer> getPrizePool() {
		return prizePool;
	}

	public void setPrizePool(Map<Integer, Integer> prizePool) {
		this.prizePool = prizePool;
	}

	public long getDoubledDate() {
		return doubledDate;
	}

	public void setDoubledDate(long doubledDate) {
		this.doubledDate = doubledDate;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Set<Integer> getFishId() {
		return fishId;
	}

	public void setFishId(Set<Integer> fishId) {
		this.fishId = fishId;
	}

	/**
	 * 今天第一次抽奖
	 * 
	 * @return
	 */
	public boolean isFirstTime() {

		return this.doubledDate <= GuaJiTime.getAM0Date().getTime() || this.doubledDate == 0L;
	}
	
	/**
	 * 每日要清理的数据处理
	 * 
	 * @param freeTimes
	 * @param doubledDate
	 */
	public void clearData(int freeTimes, long doubledDate) {

		this.freeTimes = freeTimes;
		this.doubledDate = doubledDate;
	}
	
	@Override
	public int compareTo(GoldfishStatus status) {
		// 根据积分从大到小排序
		if(this.score > status.getScore()){
			return -1;
		}else if(this.score < status.getScore()){
			return 1;
		}else{
			return 0;
		}
	}
}
