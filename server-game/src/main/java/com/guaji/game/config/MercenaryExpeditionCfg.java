package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/mercenaryExpedition.xml", struct = "map")
public class MercenaryExpeditionCfg extends ConfigBase {

	@Id
	protected final int id;
	/**
	 * 任务星级
	 */
	protected final int starLevel;
	/**
	 * 需要佣兵等级
	 */
	protected final int mercenaryStar;
	/**
	 * 需要英雄屬性
	 */
	protected final int mercenaryAttr;
	/**
	 * 需要英雄職業
	 */
	protected final int mercenaryclass;
	/**
	 * 任务奖励
	 */
	protected final String award;
	/**
	 * 完成任务需要时间
	 */
	protected final int aliveTime;
	/**
	 * 权重1
	 */
	protected final int weight1;
	/**
	 * 权重2
	 */
	protected final int weight2;
	/**
	 * 权重3
	 */
	protected final int weight3;
	/**
	 * 权重4
	 */
	protected final int weight4;
	/**
	 * 权重5
	 */
	protected final int weight5;
	/**
	 * 权重6
	 */
	protected final int weight6;
	/**
	 * 权重7
	 */
	protected final int weight7;
	/**
	 * 权重8
	 */
	protected final int weight8;
	/**
	 * 权重9
	 */
	protected final int weight9;
	/**
	 * 权重10
	 */
	protected final int weight10;
	/**
	 * 任务名索引
	 */
	protected final String name;

	public MercenaryExpeditionCfg() {
		id = 0;
		starLevel = 0;
		mercenaryStar = 0;
		mercenaryAttr = 0;
		mercenaryclass = 0;
		award = null;
		aliveTime = 0;
		weight1 = 0;
		weight2 = 0;
		weight3 = 0;
		weight4 = 0;
		weight5 = 0;
		weight6 = 0;
		weight7 = 0;
		weight8 = 0;
		weight9 = 0;
		weight10 = 0;
		name = null;
	}

	/**
	 * 获取任务id
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * 获取任务星级
	 * 
	 * @return
	 */
	public int getStarLevel() {
		return starLevel;
	}

	/**
	 * 获取需求佣兵星级
	 */
	public int getRoleStar() {
		return mercenaryStar;
	}

	/**
	 * 获取任务奖励
	 * 
	 * @return
	 */
	public String getAward() {
		return award;
	}

	/**
	 * 获取任务完成需要时间
	 * 
	 * @return
	 */
	public int getAliveTime() {
		return aliveTime;
	}

	/**
	 * 获取第一权重
	 * 
	 * @return
	 */
	public int getWeight1() {
		return weight1;
	}

	/**
	 * 获取第二权重
	 * 
	 * @return
	 */
	public int getWeight2() {
		return weight2;
	}

	/**
	 * 获取第三权重
	 * 
	 * @return
	 */
	public int getWeight3() {
		return weight3;
	}

	/**
	 * 获取第四权重
	 * 
	 * @return
	 */
	public int getWeight4() {
		return weight4;
	}

	/**
	 * 获取第五权重
	 * 
	 * @return
	 */
	public int getWeight5() {
		return weight5;
	}

	/**
	 * 获取第六权重
	 * 
	 * @return
	 */
	public int getWeight6() {
		return weight6;
	}

	/**
	 * 获取第三权重
	 * 
	 * @return
	 */
	public int getWeight7() {
		return weight7;
	}

	/**
	 * 获取第三权重
	 * 
	 * @return
	 */
	public int getWeight8() {
		return weight8;
	}

	/**
	 * 获取第三权重
	 * 
	 * @return
	 */
	public int getWeight9() {
		return weight9;
	}

	/**
	 * 获取第三权重
	 * 
	 * @return
	 */
	public int getWeight10() {
		return weight10;
	}

	public int getMercenaryStar() {
		return mercenaryStar;
	}
	
	public int getmercenaryAttr() {
		return mercenaryAttr;
	}
	/**
	 * 取得所需職業
	 * @return
	 */
	public int getmercenaryClass() {
		return mercenaryclass;
	}

	/**
	 * 任务名字索引
	 */
	public String getName() {
		return name;
	}

	@Override
	protected boolean assemble() {
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
