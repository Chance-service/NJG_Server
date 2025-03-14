package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 跨服竞技配置数据
 * 
 * @author Nannan.Gao
 * @date 2017-8-11 14:25:38
 */
@ConfigManager.KVResource(file = "xml/crossBattle.cfg")
public class CrossBattleCfg extends ConfigBase {

	private static CrossBattleCfg instance = null;
	
	/**
	 * 跨服战是否开启
	 */
	private final boolean crossserverOpen;
	
	/**
	 * 玩家开放等级限定
	 */
	private final int openLevel;

	/**
	 * 竞技场排行前X名
	 */
	private final int arenaRank;

	/**
	 * 每日免费
	 */
	private final int freeTimes;
	
	/**
	 * 初始分数值
	 */
	private final int scoreInit;

	/**
	 * 战斗数据记录条数
	 */
	private final int battleTotal;

	/**
	 * 同步时间间隔
	 */
	private final int synchroTime;

	/**
	 * 赛季开始星期几（注：星期日为1、以此类推）
	 */
	private final int startDay;

	/**
	 * 赛季结束星期几（注：星期日为1、以此类推）
	 */
	private final int endDay;

	/**
	 * 每天结算开始时间点
	 */
	private final int startTime;

	/**
	 * 每天结算结束时间点
	 */
	private final int endTime;
	
	/**
	 * 排行榜显示条数
	 */
	private final int rankShows;
	
	/**
	 * 差值最高分
	 */
	private final int scoreMax;

	/**
	 * 高低积分差百分比
	 */
	private final float percentage;
	
	/**
	 * 战斗胜利后获得积分
	 */
	private final int winScore;

	/**
	 * 挑战失败积分
	 */
	private final int chanllengedFailedScore;
	
	/**
	 * 被挑战失败积分
	 */
	private final int beChanllengedFailedScore;
	
	/**
	 * 积分相差
	 */
	private final int scoreDifference;
	
	/**
	 * 购买挑战次数消耗
	 */
	private final String buyTimesCost;
	/**
	 * 战败后扣除的积分
	 */
	private final int failedScore;
	
	/**
	 * 购买消耗数据
	 */
	private List<Integer> buyCostList = new ArrayList<Integer>();
	
	public CrossBattleCfg() {
		crossserverOpen = false;
		openLevel = 50;
		arenaRank = 20;
		freeTimes = 10;
		scoreInit = 500;
		battleTotal = 20;
		synchroTime = 60000;
		startDay = 2;
		endDay = 7;
		startTime = 21;
		endTime = 23;
		rankShows = 50;
		scoreMax = 20;
		percentage = 0.25f;
		winScore = 20;
		chanllengedFailedScore = 0;
		beChanllengedFailedScore = 20;
		scoreDifference = 200;
		buyTimesCost = "";
		failedScore = 20;
		instance = this;
	}
	
	public static CrossBattleCfg getInstance() {
		return instance;
	}

	@Override
	protected boolean assemble() {
		String[] buyTimes = buyTimesCost.split(",");
		for (String buyCost : buyTimes) {
			buyCostList.add(Integer.parseInt(buyCost));
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 购买挑战次数消耗数据
	 * 
	 * @param buyTimes
	 * @param times
	 * @return
	 */
	public int getCost(int buyTimes, int times) {
		int totalCost = 0;
		for (int value = 0; value < times; value++) {
			int index = buyTimes + value;
			if (index >= buyCostList.size()) {
				index = buyCostList.size() - 1;
			}
			totalCost += buyCostList.get(index);
		}
		return totalCost;
	}
	
	public Boolean getCrossserverOpen() {
		return crossserverOpen;
	}
	
	public int getOpenLevel() {
		return openLevel;
	}

	public int getArenaRank() {
		return arenaRank;
	}

	public int getFreeTimes() {
		return freeTimes;
	}

	public int getScoreInit() {
		return scoreInit;
	}

	public int getBattleTotal() {
		return battleTotal;
	}

	public int getSynchroTime() {
		return synchroTime;
	}

	public int getStartDay() {
		return startDay;
	}

	public int getEndDay() {
		return endDay;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public int getRankShows() {
		return rankShows;
	}

	public int getScoreMax() {
		return scoreMax;
	}

	public float getPercentage() {
		return percentage;
	}

	public int getWinScore() {
		return winScore;
	}

	public int getBeChanllengedFailedScore() {
		return beChanllengedFailedScore;
	}

	public int getScoreDifference() {
		return scoreDifference;
	}

	public String getBuyTimesCost() {
		return buyTimesCost;
	}
	
	public int getFailedScore() {
		return failedScore;
	}

	public int getChanllengedFailedScore() {
		return chanllengedFailedScore;
	}
}
