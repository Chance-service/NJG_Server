package com.guaji.game.config;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 性奴小學戰隊Skill表
 */
@ConfigManager.XmlResource(file = "xml/LTSkill.xml", struct = "map")
public class LittleTestSkillCfg extends ConfigBase {
	
	public class ArgData{
		
		private Map<Integer,Integer> ArgMap;
		private int times;
		
		public ArgData() {
			ArgMap = new HashMap<>();
			times = 0;
		}
		
		public int getArg(int key) {
			if (ArgMap.containsKey(key)) {
				return ArgMap.get(key);
			}
			return 0;
		}
		
		public void setArg(int key,int value) {
			ArgMap.put(key, value);
		}
		
		public int gettimes() {
			return times;
		}
		
		public void settimes(int count) {
			this.times = count;
		}
	}
	/**
	 * BuffID定義
	 */
	public static final int Buff_AddSpeed = 1;			// 增加速度
	public static final int Buff_AddAtk = 2; 			// 增加攻擊
	public static final int Buff_DecHurt = 3; 			// 減少被擊傷害
	public static final int Buff_AddHP = 4;				// 恢復血量
	public static final int Buff_AddFinalSpeed = 5;		// 增加最終速度
	public static final int Buff_DecAtk = 101;			// 減少攻擊力
	public static final int Buff_AddHurt = 102;			// 增加被擊傷害
	public static final int Buff_DecSpeed = 103;		// 減少速度
	public static final int Buff_DecFinalSpeed = 104;	// 減少最終速度
	
	public static final int Buff_Arg1 = 1;
	public static final int Buff_Arg2 = 2;
	public static final int Buff_Arg3 = 3;
	
	public static final int target_self = 1;
	public static final int target_enemy = 2;
	public static final int target_all = 3;
	/**
	 * 角色ID
	 */
	@Id
	private final int ID;
	/**
	 * 計算公式
	 */
	private final int SF;
	/**
	 * 技能機率
	 */
	private final int Rate;
	/**
	 * BUFFID
	 */
	private final String BUFFID;
	/**
	 * 技能作用回合數
	 */
	private final String Times;
	/**
	 * 變數1
	 */
	private final String Arg1;
	/**
	 * 變數2
	 */
	private final String Arg2;
	/**
	 * 變數3
	 */
	private final String Arg3;
	/**
	 * 變數物件化 , key:Buffid, ArgDat
	 */
	private final Map <Integer,ArgData> BuffArgMap;
		
	public LittleTestSkillCfg() {
		ID = 0;
		SF = 0;
		Rate = 0;
		BUFFID = "";
		Times = "";
		Arg1 = "";
		Arg2 = "";
		Arg3 = "";
		BuffArgMap = new HashMap<>();
	}

	public int getID() {
		return ID;
	}
	
	public int getSF() {
		return SF;
	}
	
	public int getRate() {
		return Rate;
	}
	
	public String getBUFFID() {
		return BUFFID;
	}
	
	public String getArg1() {
		return Arg1;
	}
	
	public String getArg2() {
		return Arg2;
	}
	
	public String getArg3() {
		return Arg3;
	}
	
	public Map<Integer,ArgData> getBuffArgMap(){
		return BuffArgMap;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	@Override
	protected boolean assemble() {

		List <Integer> BuffList = new ArrayList<>();
		List <Integer> TimesList = new ArrayList<>();
		List <Integer> Arg1List = new ArrayList<>();
		List <Integer> Arg2List = new ArrayList<>();
		List <Integer> Arg3List = new ArrayList<>();
		
		String[] Buffs = BUFFID.split(",");
		for (String abuff : Buffs ) {
			BuffList.add(Integer.parseInt(abuff));
		}
		
		String[] TimeStr = Times.split(",");
		for (String StrVal : TimeStr ) {
			TimesList.add(Integer.parseInt(StrVal));
		}
		
		if (!Arg1.equals("0")){
			String[] conuts = Arg1.split(",");
			for (String StrVal : conuts ) {
				Arg1List.add(Integer.parseInt(StrVal));
			}
		}
		if (!Arg2.equals("0")){
			String[] conuts = Arg2.split(",");
			for (String StrVal : conuts ) {
				Arg2List.add(Integer.parseInt(StrVal));
			}
		}
		if (!Arg3.equals("0")){
			String[] conuts = Arg3.split(",");
			for (String StrVal : conuts ) {
				Arg3List.add(Integer.parseInt(StrVal));
			}
		}
		
		int index = 0;
		for(Integer bufid :BuffList) {
			ArgData argdata = new ArgData();
			if (TimesList.size() == BuffList.size()) {
				argdata.settimes(TimesList.get(index));
			}
			if (Arg1List.size() == BuffList.size()) {
				argdata.setArg(Buff_Arg1,Arg1List.get(index));
			}
			if (Arg2List.size() == BuffList.size()) {
				argdata.setArg(Buff_Arg2,Arg2List.get(index));
			}
			if (Arg3List.size() == BuffList.size()) {
				argdata.setArg(Buff_Arg3,Arg2List.get(index));
			}
			BuffArgMap.put(bufid,argdata);
			index++;
		}
		
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
}
