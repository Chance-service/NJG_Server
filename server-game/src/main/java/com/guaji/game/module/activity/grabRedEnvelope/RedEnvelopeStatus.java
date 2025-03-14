package com.guaji.game.module.activity.grabRedEnvelope;

import java.util.HashMap;
import java.util.Map;

import org.guaji.os.MyException;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;

import com.guaji.game.ServerData;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.util.GsConst;

public class RedEnvelopeStatus {
	
	/**
	 * 系统红包状态定义
	 */
	private static final int HAS_NOT_RECV = 0;
	private static final int ALREADY_RECVED = 1;
	
	/**
	 * 我的红包数量
	 */
	private int myRedEnvelope;
	
	/**
	 * 活动期间充值数额
	 */
	private int totalRechargeAmount; 
	
	/**
	 * 每日系统红包状态
	 */
	private Map<String, Integer> everyDaySysRedEnvelopeStatus = new HashMap<String, Integer>();
	
	/**
	 * 每日抢得的红包数量
	 */
	private Map<String, Integer> everyDayGrabMap = new HashMap<String, Integer>();
	
	/**
	 * 今天已抢红包数量
	 * @return
	 */
	public int getTodayGrabAmount(){
		String date = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
		if(everyDayGrabMap.containsKey(date)){
			return everyDayGrabMap.get(date);
		}else{
			return 0;
		}
	}
	
	/**
	 * 抢红包
	 */
	public void grabRedEnvelope(int grabNum){
		String date = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
		if(everyDayGrabMap.containsKey(date)){
			int grabAmount = everyDayGrabMap.get(date);
			grabAmount += grabNum;
			everyDayGrabMap.put(date, grabAmount);
		}else{
			everyDayGrabMap.put(date, grabNum);
		}
	}

	/**
	 * 我的红包数量
	 * @return
	 */
	public int getMyRedEnvelope() {
		return myRedEnvelope;
	}
	
	public void addMyRedEnvelope(int addNum){
		myRedEnvelope += addNum;
	}
	
	public int giveRedEnvelope(int giveNum){
		int realNum = Math.min(myRedEnvelope, giveNum);
		myRedEnvelope -= realNum;
		RedEnvelopeServerStatus serverStatus = ServerData.getInstance().getServerStatus(
				GsConst.ServerStatusId.RED_ENVELOPE, RedEnvelopeServerStatus.class);
		serverStatus.addServerRedEnvelopeAmount(realNum);
		return realNum;
	}
	
	public int getToaydSysRedEnvelopeStatus(){
		String date = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
		if(everyDaySysRedEnvelopeStatus.containsKey(date)){
			return everyDaySysRedEnvelopeStatus.get(date);
		}else{
			return HAS_NOT_RECV;
		}
	}
	
	public boolean recvToadySysRedEnvelope(){
		String date = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
		if(everyDaySysRedEnvelopeStatus.containsKey(date)){
			if(everyDaySysRedEnvelopeStatus.get(date) != HAS_NOT_RECV){
				return false;
			}
		}
		everyDaySysRedEnvelopeStatus.put(date, ALREADY_RECVED);
		return true;
	}
	
	/**
	 * 随机红包钻石
	 * @return
	 */
	public static int randRedEnvelopeGold(){
		// 发钻石
		int minGold = 50;
		int maxGold = 100;
		int redEnvelopeGold = minGold;
		try{
			String redEnvelopeGoldInterval = SysBasicCfg.getInstance().getRedEnvelopeGoldInterval();
			String[] goldInterval =  redEnvelopeGoldInterval.split(",");
			if(goldInterval.length == 2){
				minGold = Integer.valueOf(goldInterval[0]);
				maxGold = Integer.valueOf(goldInterval[1]);
			}
			redEnvelopeGold = GuaJiRand.randInt(minGold, maxGold);
		}catch(Exception e){
			MyException.catchException(e);
		}
		return redEnvelopeGold;
	}
	
	public int getTotalRechargeAmount() {
		return totalRechargeAmount;
	}

	/**
	 * 增加活动期间充值数额
	 * @param rechargeNum
	 */
	public void addRechargeAmount(int rechargeNum){
		totalRechargeAmount += rechargeNum;
	}
}
