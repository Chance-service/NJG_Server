package com.guaji.game.config;

import java.util.Set;
import java.util.TreeSet;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/alliance.xml", struct = "map")
public class AllianceCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	protected final int level;
	protected final int expStart;
	protected final int expEnd;
	protected final int popSize;
	protected final String name;
	protected final int contribution;
	/**
	 * BOSS存在时间
	 */
	protected final String dayOfWeekStr;
	protected final int time;
	protected final int bossId;
	protected final int bossHp;
	protected final int openBossVitality;
	
	private Set<Integer> dayOfWeekSet = new TreeSet<Integer>();

 	public AllianceCfg() {
		this.id = 0;
		this.level = 0;
		this.expStart = 0;
		this.expEnd = 0;
		this.popSize = 0;
		this.name = "";
		this.contribution = 0;
		this.dayOfWeekStr = "1,2,3,4,5,6,7";
		this.time = 0;
		this.bossId = 0;
		this.bossHp = 0;
		this.openBossVitality = 0;
	}

	public int getPopSize() {
		return popSize;
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public int getExpStart() {
		return expStart;
	}

	public int getExpEnd() {
		return expEnd;
	}

	public String getName() {
		return name;
	}

	public int getContribution() {
		return contribution;
	}

	public int getTime() {
		return time;
	}

	public int getBossId() {
		return bossId;
	}

	public int getBossHp() {
		return bossHp;
	}

	public int getOpenBossVitality() {
		return openBossVitality;
	}
	/**
	 * 获取改玩法开放星期
	 * @return
	 */
	public Set<Integer> getDayOfWeekSet() {
		Set<Integer> copy = new TreeSet<Integer>();
		copy.addAll(dayOfWeekSet);
		return copy;
	}

	@Override
	protected boolean assemble() {
		String[] days = dayOfWeekStr.split(",");
		for(String ss:days){
			if(ss.trim().equals("")){
				continue;
			}
			dayOfWeekSet.add(Integer.parseInt(ss));
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
