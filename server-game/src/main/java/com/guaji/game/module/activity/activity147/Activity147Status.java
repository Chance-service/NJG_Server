package com.guaji.game.module.activity.activity147;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.guaji.game.config.SysBasicCfg;

import java.util.HashSet;

public class Activity147Status {
	/**
	 * 免费刷新使用时间点
	 */
	private Map<Integer,Integer> ReFreeTimeMap;
	/***
	 * 各輪盤累積幸運值
	 */
	private Map<Integer,Integer> luckyMap;
	/***
	 * 各輪盤領獎順序
	 */
	private Map<Integer,Integer> takeMap;
	/**
	 * 最高獎項列領取表 false未領 ture 已領
	 */
	private Map<Integer,HashMap<String, Boolean>> awardTake;
	/**
	 * 最高獎項分區表
	 */
	private Map<Integer,HashMap<Integer,String>> areaAward;
	/**
	 * 各個實際轉輪獎品列表
	 * 
	 */
	private Map<Integer,HashSet<Integer>> CfgIdxList;
	/**
	 * 星輪的免費抽數
	 */
	private int freeDraw;
	
	public Activity147Status() {
		this.ReFreeTimeMap = new HashMap<Integer,Integer>();
		this.luckyMap= new HashMap<Integer,Integer>();
		this.takeMap = new HashMap<Integer,Integer>();
		this.awardTake= new HashMap<Integer,HashMap<String,Boolean>>();
		this.areaAward =  new HashMap<Integer,HashMap<Integer,String>>();
		this.CfgIdxList = new HashMap<Integer,HashSet<Integer>>();
		this.freeDraw = SysBasicCfg.getInstance().getWishingFreeDraw();
	}

	public int getRefreshFreeTime(int type) {
		if (ReFreeTimeMap.containsKey(type)){
			return ReFreeTimeMap.get(type);
		}
		return 0;
	}

	public void setRefreshFreeTime(int type,int RefreshFreeTime) {
		if (ReFreeTimeMap.containsKey(type)) {
			ReFreeTimeMap.replace(type,RefreshFreeTime);
		} else {
			ReFreeTimeMap.put(type,RefreshFreeTime);
		}
	}
	
	public  Map<Integer,Integer> getLuckyMap(){
		return luckyMap;
	}
	
	public int getTake(int type) {
		if (takeMap.containsKey(type)) {
			return takeMap.get(type);
		}
		return 0;
	}

	public void setTake(int type,int value) {
		if (takeMap.containsKey(type)) {
			takeMap.replace(type,value);
		} else {
			takeMap.put(type,value);
		}
	}
	
	public void incTake(int type,int value) {
		if (takeMap.containsKey(type)) {
			takeMap.put(type,takeMap.get(type)+value);
		} else {
			takeMap.put(type,value);
		}
	}
	
	public int getLucky(int type) {
		if (luckyMap.containsKey(type)) {
			return luckyMap.get(type);
		}
		return 0;
	}
	
	public void setLucky(int type,int value) {
			luckyMap.put(type,value);
	}
	
	public void incLucky(int type,int value) {
		if (luckyMap.containsKey(type)) {
			luckyMap.put(type,luckyMap.get(type)+value);
		} else {
			luckyMap.put(type,value);
		}
	}
	
	public void decLucky(int type,int value) {
		if (luckyMap.containsKey(type)) {
			luckyMap.put(type,Math.max(luckyMap.get(type)-value,0));
		} else {
			luckyMap.put(type,0);
		}
	}
	
	public void clearAwardTake(int type) {
		if (awardTake.containsKey(type)) {
			awardTake.get(type).clear();
		}
	}
	
	public void setAwradTake(int type ,String awradStr,boolean siw) {
		if (awardTake.containsKey(type)) {
			if (awardTake.get(type).containsKey(awradStr)) {
				awardTake.get(type).replace(awradStr, siw);
			} else {
				awardTake.get(type).put(awradStr,siw);
			}
		} else {
			HashMap<String,Boolean> aMap = new HashMap<String ,Boolean>();
			aMap.put(awradStr,siw);
			awardTake.put(type,aMap);
		}
			
	}
	
	public Map<String,Boolean> getAwradTakeMap(int type) {
		if (awardTake.containsKey(type)) {
			return awardTake.get(type);
		}
		return null;
	}
	
	public void clearAreaAward(int type) {
		if (areaAward.containsKey(type)) {
			areaAward.get(type).clear();
		}
	}
	
	public void setAreaAward(int type ,int area,String awradStr) {
		if (areaAward.containsKey(type)) {
			if (areaAward.get(type).containsKey(area)) {
				areaAward.get(type).replace(area, awradStr);
			} else {
				areaAward.get(type).put(area,awradStr);
			}
		} else {
			HashMap<Integer,String> aMap = new HashMap<Integer,String>();
			aMap.put(area,awradStr);
			areaAward.put(type,aMap);
		}
			
	}
	
	public Map<Integer,String> getAreaAwardMap(int type) {
		if (areaAward.containsKey(type)) {
			return areaAward.get(type);
		}
		return null;
	}
	
	
	public Set<Integer> getCfgIdxList(int type){
		if (CfgIdxList.containsKey(type)) {
			return CfgIdxList.get(type);
		}
		return null;
	}
	
	public void setCfgIdxList(int type,HashSet<Integer> aSet){
		if (CfgIdxList.containsKey(type)) {
			CfgIdxList.replace(type,aSet);
		} else {
			CfgIdxList.put(type,aSet);
		}
	}
	
	public boolean gameEnd(int type) {
		if ((getCfgIdxList(type) == null) || (getCfgIdxList(type).size() == 0))
		{
			return true;
		}
		
		return false;
	}
	
	public int getFreeDraw() {
		return freeDraw;
	}
	
	public void setFreeDraw(int value) {
		this.freeDraw = value;
	}
}
