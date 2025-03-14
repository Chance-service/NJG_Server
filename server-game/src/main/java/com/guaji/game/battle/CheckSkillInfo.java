package com.guaji.game.battle;

import java.util.HashMap;
import java.util.Map;

public class CheckSkillInfo {
	/**
	 * 技能ID
	 */
	private int skillId ;
	/**
	 * 施放技能時間
	 */
	private int addTime;
	
	/**
	 * 施放的流水ID
	 */
	private int number;
	
	/**
	 * 對象作用次數
	 */
	private Map<Integer,Integer> actionMap; // 角色Pos , 被作用次數
	
	/*
	 * 技能連擊次數
	 */
	private int SATK;

	public CheckSkillInfo(int id ,int time ,int num, int satk) {
		this.skillId = id;
		this.addTime = time;
		this.number = num;
		this.SATK = satk;
		actionMap = new HashMap<>();
	}
	
	public int getSkillId() {
		return this.skillId;
	}
	
	public int getAddTime() {
		return this.addTime;
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public int getSATK() {
		return this.SATK;
	}
	/**
	 * 取得每一位置受擊次數
	 * @param Pos
	 * @return
	 */
	public int getPosATK(int Pos) {
		if (actionMap.containsKey(Pos)) {
			return actionMap.get(Pos);
		} else {
			return 0;
		}
	}
	/**
	 * 設定一位置受擊次數
	 * @param Pos
	 * @return
	 */	
	public void setPosATK(int Pos , int atkcount) {
		actionMap.put(Pos, atkcount);
	}
	/**
	 * 取已經受擊總數
	 * @param Pos
	 * @param atkcount
	 * @return
	 */
	public int getATKTotal() {
		int total = 0;
		for (Integer akey : actionMap.keySet()) {
			total = total + actionMap.get(akey);
		}
		return total;
	}
	
}
