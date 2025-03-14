package com.guaji.game.module.activity.roulette;

import java.util.HashMap;
import java.util.Map;

import org.guaji.os.GuaJiTime;

import com.guaji.game.config.SysBasicCfg;

/**
 * 疯狂转轮盘
 */
public class RouletteStatus {
	
	/**
	 * 活动期间转盘使用次数
	 */
	private int useTime;
	
	/**
	 * 总的可用次数
	 */
	private int totalTime;
	
	/**
	 * 活动积分
	 */
	private int curCredits;
	
	/**
	 * 活动期间每天充值数额
	 */
	private Map<Integer, Integer> dayRechargeNumMap;
	
	public RouletteStatus(){
		useTime = 0;
		totalTime = 0;
		curCredits = 0;
		dayRechargeNumMap = new HashMap<Integer, Integer>();
	}
	
	/**
	 * 获取本次转盘活动总共剩余次数
	 * @return
	 */
	public int getRouletteLeftTimes() {
		return (totalTime - useTime) >= 0 ? totalTime - useTime : 0;
	}

	/**
	 * 获取当前活动积分
	 * @return
	 */
	public int getCurCredits() {
		return curCredits;
	}

	/**
	 * 增加今日充值额度和转盘次数
	 * @param rechargeNum
	 */
	public void addRechargeNum(int rechargeNum){
		int dayRechargeMaxAmount = SysBasicCfg.getInstance().getCrazyRouletteDayMaxTimes() * SysBasicCfg.getInstance().getCrazyRouletteRechargeUnit();
		int date = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		//int addTimes = 0;
		if(dayRechargeNumMap.containsKey(date)){
			int todayRecharge = dayRechargeNumMap.get(date);
			if(todayRecharge < dayRechargeMaxAmount){
				totalTime = (int)Math.floor((todayRecharge+rechargeNum) / SysBasicCfg.getInstance().getCrazyRouletteRechargeUnit());
				if(totalTime > SysBasicCfg.getInstance().getCrazyRouletteDayMaxTimes()){
					totalTime = SysBasicCfg.getInstance().getCrazyRouletteDayMaxTimes();
				}
			}
			todayRecharge += rechargeNum;
			dayRechargeNumMap.put(date, todayRecharge);
		} else {
			dayRechargeNumMap.put(date, rechargeNum);
			useTime = 0;
			totalTime = (int)Math.floor(rechargeNum / SysBasicCfg.getInstance().getCrazyRouletteRechargeUnit());
			if(totalTime > SysBasicCfg.getInstance().getCrazyRouletteDayMaxTimes()){
				totalTime = SysBasicCfg.getInstance().getCrazyRouletteDayMaxTimes();
			}
		}
		//rouletteLeftTimes += addTimes;
	}
	
	/**
	 * 扣除转盘剩余次数并增加相应积分
	 * @param times
	 */
	public boolean deductRouletteTimes(int times){
		if(times <= totalTime - useTime){
			useTime += times;
			curCredits += (SysBasicCfg.getInstance().getCrazyRouletteAddCredits() * times);
			return true;
	 	}
		return false;
	}
	
	/**
	 * 扣除活动积分
	 * @param times
	 */
	public boolean deductCredits(int credits){
		if(credits <= curCredits){
			curCredits -= credits;
			return true;
		}
		return false;
	}
		
	/**
	 * 今日充值数目
	 */
	public int getTodayRechargeNum(){
		int date = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		if(dayRechargeNumMap.containsKey(date)){
			return dayRechargeNumMap.get(date);
		}
		// 海外特殊处理，清除之前数据
//		totalTime = 0;
//		useTime = 0;
		return 0;
	}
}
