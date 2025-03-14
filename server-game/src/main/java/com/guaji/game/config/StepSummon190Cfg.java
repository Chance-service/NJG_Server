package com.guaji.game.config;

import java.util.HashMap;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.util.GsConst;

@ConfigManager.XmlResource(file = "xml/StepSummon190.xml", struct = "map")
public class StepSummon190Cfg extends ConfigBase {
	/**
	 * @Fields id :可視為天數
	 */
	@Id
	private final int id;
	/**
	 * 種類
	 */
	private final int type;
	/**
	 * 獎勵
	 */
	private final String awards;
	/*
	 * 
	 * 需要消費幾刺
	 */
	private final int needcount;
	/**
	 * 最表定最大天數
	 */
	private static Map<Integer,Integer> maxDay ;
	/**
	 * 有幾個類型
	 */
	private static int maxType ;
	
	public StepSummon190Cfg() {
		this.id = 0;
		this.type =0;
		this.awards = null;
		this.needcount = 0;
		maxType = 0;
		maxDay = new HashMap<>();
	}
	
	@Override
	protected boolean assemble() {
		
		if (type > maxType) {
			maxType = type;
		}
		
		// record max day
		int day = (this.id % GsConst.SUMMON_TYPE_BASE);
		if (maxDay.containsKey(this.type)) {
			if (day > maxDay.get(this.type)) {
				maxDay.put(this.type,day);
			}
		} else {
			maxDay.put(this.type,day);
		}		
		return true;

	}

	public String getAwards() {
		return awards;
	}
	
	/**
	 * 最大天數
	 * @return
	 */
	public static int getMaxDay(int type) {
		if (maxDay.containsKey(type)) {
			return maxDay.get(type);
		}
		return 0;
	}
	
	public static int getMaxType() {
		return maxType;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getNeedcount() {
		return needcount;
	}
	
}
